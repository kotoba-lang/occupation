(ns kotoba.occupation
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [kotoba.occupation.wave :as wave]
            [kotoba.technology :as technology]))

(def registry-resource "kotoba/occupation/registry.edn")

;; registry.edn is stored as Datomic/Datascript tx-data (a single-entity
;; vector, see scripts/edn-datomize.cljs `wrap-generic`) rather than a raw map,
;; so it is directly transactable/queryable. Keys that already carried their
;; own namespace (e.g. :kotoba.registry/id) were left untouched; only the
;; genuinely bare :occupations key was promoted under this ns. `registry`
;; reconstitutes the original bare-keyed map (with :occupations un-blobbed
;; back into its live vector-of-maps) so every downstream fn below keeps
;; working against the exact pre-datomize shape.
(def ^:private wrap-ns "kotoba.occupation")

(defn- unblob [v]
  (if (string? v)
    (try (let [parsed (edn/read-string v)] (if (coll? parsed) parsed v))
         (catch Exception _ v))
    v))

(defn- reconstitute-entity [tx-data]
  (into {}
        (map (fn [[k v]]
               (let [bare? (= (namespace k) wrap-ns)]
                 [(if bare? (keyword (name k)) k) (unblob v)])))
        (dissoc (first tx-data) :db/id)))

(defn registry []
  (reconstitute-entity (edn/read-string (slurp (io/resource registry-resource)))))

(defn occupations
  ([] (:occupations (registry)))
  ([reg] (:occupations reg)))

(defn by-id
  ([] (by-id (registry)))
  ([reg] (into {} (map (juxt :id identity) (occupations reg)))))

(defn get-occupation
  ([isco] (get-occupation (registry) isco))
  ([reg isco] (get (by-id reg) (str isco))))

(defn required-technologies
  ([isco] (required-technologies (registry) isco))
  ([reg isco] (:required-technologies (get-occupation reg isco))))

(defn optional-technologies
  ([isco] (optional-technologies (registry) isco))
  ([reg isco] (:optional-technologies (get-occupation reg isco))))

(defn technology-stack
  "Resolve the required technology records for an ISCO-08 occupation."
  ([isco] (technology-stack (registry) isco))
  ([reg isco]
   (technology/stack (required-technologies reg isco))))

(defn readiness
  "Return an execution-readiness summary for an ISCO-08 unit group and available technology IDs."
  [isco available-tech-ids]
  (let [occupation (get-occupation isco)
        required (set (:required-technologies occupation))
        available (set available-tech-ids)
        missing (set/difference required available)]
    {:isco (str isco)
     :business-id (:business-id occupation)
     :ready? (empty? missing)
     :required required
     :available available
     :missing missing
     :operating-states (:operating-states occupation)}))

(defn execution-plan
  "Data contract cloud-itonami-isco can expose in business state."
  [isco]
  (let [occupation (get-occupation isco)
        stack (technology-stack isco)]
    {:isco (str isco)
     :business-id (:business-id occupation)
     :occupation (:name occupation)
     :maturity (:maturity occupation)
     :wave (wave/wave-of isco)
     :required-technologies (:required-technologies occupation)
     :optional-technologies (:optional-technologies occupation)
     :operating-states (:operating-states occupation)
     :ui-ready? (some :ui? stack)
     :export-ready? (some :export? stack)
     :technology-stack (mapv #(select-keys % [:id :name :layer :capabilities :repos :contracts :ui? :export?])
                             stack)}))

(defn maturity
  "Return the maturity level of an ISCO-08 entry: :spec (registry only),
  :blueprint (blueprint repo published), or :implemented (source actor exists).
  Defaults to :spec when unset."
  [isco]
  (let [occupation (get-occupation isco)]
    (or (:maturity occupation)
        (cond
          (:implemented? occupation) :implemented
          (:repo occupation)         :blueprint
          :else                      :spec))))

(defn maturity-summary
  "Aggregate maturity counts across all occupations."
  []
  (let [occs (occupations)]
    {:total       (count occs)
     :spec        (count (filter #(= :spec (maturity (:id %))) occs))
     :blueprint   (count (filter #(= :blueprint (maturity (:id %))) occs))
     :implemented (count (filter #(= :implemented (maturity (:id %))) occs))}))

(defn maturity-roadmap
  "Return the next maturity step for an ISCO-08 entry: :spec→:blueprint→:implemented,
  with the action required to advance and whether a capability lib with UI/export
  already backs it (so the spec entry can be promoted cheaply)."
  [isco]
  (let [occupation (get-occupation isco)
        level (maturity isco)
        stack (technology-stack isco)
        ui? (some :ui? stack)
        export? (some :export? stack)
        has-repo (boolean (:repo occupation))]
    {:isco (str isco)
     :maturity level
     :wave (wave/wave-of isco)
     :next-step (condp = level
                  :spec        :blueprint
                  :blueprint   :implemented
                  :implemented nil)
     :next-action (condp = level
                    :spec        "publish a blueprint repo (scaffold + blueprint.edn + docs)"
                    :blueprint   "implement the actor (source + tests)"
                    :implemented "at maturity ceiling")
     :ui-ready? ui?
     :export-ready? export?
     :has-repo has-repo}))

(defn- gap-target
  "Routing table from ADR-2607202500: gap shape -> staffing/matching actor.
  Anything that doesn't cleanly match one-off/remote, on-site/recurring or
  permanent falls through to the public job-board actor (widen reach first)."
  [{:keys [duration location]}]
  (cond
    (and (= duration :one-off) (= location :remote))
    ["cloud-itonami-isic-8299"
     "independent contracted operator, task-based, no employer-of-record"]

    (and (= location :on-site) (= duration :recurring))
    ["cloud-itonami-isic-7820"
     "employer-of-record dispatch; tenure-limit-gate/wage-compliance-gate already enforce dispatch law"]

    (= duration :permanent)
    ["cloud-itonami-isic-7810"
     "one-time placement-fee agency model fits a durable headcount gap"]

    :else
    ["cloud-itonami-isic-6399"
     "public job-board reach as a precursor to a private-desk match"]))

(defn human-gap-referral-draft
  "Turn a human-required automation gap for an ISCO-08 occupation into a
  referral-draft record naming which existing cloud-itonami staffing/matching
  actor a human operator should carry it to. Produces a DRAFT only -- never
  calls, invokes, or writes to any other actor's store (ADR-2607131000 /
  ADR-2607202500 invariant).

  :draft-id is a fresh unique id per call (java.util.UUID/randomUUID) -- each
  call represents a distinct real-world gap-detection occurrence (even for
  the same isco+task, e.g. a recurring gap detected in two different weeks),
  so the id must not be a deterministic function of the input alone or two
  genuinely distinct draft occurrences would collide in the ledger. The
  no-random-uuid/no-wall-clock determinism discipline elsewhere in this
  fleet applies to Workflow orchestration scripts, not to plain library
  code like this fn.

  :drafted-at-state is always the constant :referral-drafted (the ledger
  state term precedent ADR-2607131000 established: \"6399's ledger shows
  referral-drafted\") -- it marks the state of the REFERRAL DRAFT process,
  not the ISCO occupation's own maturity tier (that is already available,
  unchanged, via :occupation-context's :maturity key)."
  [isco gap]
  (let [{:keys [task]} gap
        plan (execution-plan isco)
        [target reason-text] (gap-target gap)]
    {:isco (:isco plan)
     :business-id (:business-id plan)
     :draft-id (str (java.util.UUID/randomUUID))
     :task task
     :target-actor target
     :routing-reason reason-text
     :occupation-context plan
     :drafted-at-state :referral-drafted}))

(defn wave-maturity-summary
  "Aggregate maturity counts per reverse-topological rollout wave
  (ADR-2607121000, `kotoba.occupation.wave`): for each wave 0-4, how
  many registry entries sit at :spec / :blueprint / :implemented.
  Occupation counterpart of `kotoba.industry/wave-maturity-summary`."
  []
  (into (sorted-map)
        (map (fn [[w entries]]
               [w {:total       (count entries)
                   :spec        (count (filter #(= :spec (maturity (:id %))) entries))
                   :blueprint   (count (filter #(= :blueprint (maturity (:id %))) entries))
                   :implemented (count (filter #(= :implemented (maturity (:id %))) entries))}]))
        (group-by #(wave/wave-of (:id %)) (occupations))))
