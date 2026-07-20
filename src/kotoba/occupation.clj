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

(defn- gap-shape
  "Classify a human-required gap's shape from :duration/:location alone
  (ADR-2607202500 routing table -- 4 branches, all handled by this ONE
  classifier feeding the ONE fn the ADR's :referral-draft-contract names,
  `human-gap-referral-draft`; :reason and :urgency do not affect routing).
  Anything not matching a specific branch -- missing/unrecognized :duration
  or :location, or an explicit \"widen reach\" case -- falls through to
  :public-recruit-reach, the safe default that just widens candidate reach
  on the public board before any private-desk match is attempted."
  [{:keys [duration location]}]
  (cond
    (and (= duration :recurring) (= location :on-site)) :on-site-recurring
    (= duration :permanent)                             :permanent-role
    (or (= duration :one-off) (= location :remote))      :one-off-remote-or-cognitive
    :else                                                :public-recruit-reach))

(defn- target-actor-for
  "Map a gap-shape to the cloud-itonami-isic-* staffing/matching actor that
  owns that employment relationship, and why (ADR-2607202500 routing table)."
  [shape]
  (condp = shape
    :on-site-recurring            {:target-actor "cloud-itonami-isic-7820"
                                    :routing-reason "on-site-recurring -> temporary staffing agency is employer of record"}
    :permanent-role                {:target-actor "cloud-itonami-isic-7810"
                                    :routing-reason "permanent-role -> permanent placement agency"}
    :one-off-remote-or-cognitive   {:target-actor "cloud-itonami-isic-8299"
                                    :routing-reason "one-off-remote-or-cognitive -> BPO/task-matching, contracted independent operators"}
    :public-recruit-reach          {:target-actor "cloud-itonami-isic-6399"
                                    :routing-reason "public-recruit-reach -> public meta job board"}))

(defn human-gap-referral-draft
  "Turn a :human-required gap into a referral-draft record for one of the
  cloud-itonami-isic-{6399,7810,7820,8299} staffing/matching actors
  (ADR-2607202500, generalizing the human-carried handoff rule
  ADR-2607131000 already established for isic-6399<->7810).

  `isco` is the ISCO-08 unit group code whose robot actor hit the gap.
  `gap` is `{:task string, :reason (:missing-technology|:no-automation-path|:other),
  :duration (:one-off|:recurring|:permanent), :location (:on-site|:remote),
  :urgency keyword}`.

  Returns a DRAFT RECORD ONLY -- it never makes a network call, never
  invokes another actor, and never carries any person's PII (name/email/
  phone/address): both `gap` and the returned map only ever carry the
  fields documented here. The isco actor's own governor decides WHEN to
  emit :human-required and calls this fn to shape the draft, then writes
  it to its OWN ledger only; the target actor's own governor takes over
  independently once a human carries the draft into that actor's intake.

  :draft-id is a fresh unique id per call (java.util.UUID/randomUUID) --
  each call represents a distinct real-world gap-detection occurrence
  (even for the same isco+task, e.g. a recurring gap detected again in a
  later week), so the id must not be a deterministic function of the
  input alone or two genuinely distinct draft occurrences would collide
  in the ledger. The fleet's no-random-uuid/no-wall-clock determinism
  discipline applies to Workflow orchestration scripts, not to plain
  library code like this fn.

  :drafted-at-state is always the constant :referral-drafted -- the
  ledger-state term precedent ADR-2607131000 established (\"6399's
  ledger shows referral-drafted\"), marking the state of the REFERRAL
  DRAFT process itself, not the ISCO occupation's own maturity tier
  (already available, unchanged, via :occupation-context's :maturity
  key)."
  [isco gap]
  (let [occupation (get-occupation isco)
        shape (gap-shape gap)
        {:keys [target-actor routing-reason]} (target-actor-for shape)]
    {:isco (str isco)
     :business-id (:business-id occupation)
     :draft-id (str (java.util.UUID/randomUUID))
     :task (:task gap)
     :target-actor target-actor
     :routing-reason routing-reason
     :occupation-context (execution-plan isco)
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
