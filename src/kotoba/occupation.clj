(ns kotoba.occupation
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [kotoba.technology :as technology]))

(def registry-resource "kotoba/occupation/registry.edn")

;; registry.edn is stored as Datomic/Datascript tx-data (a single-entity
;; vector, see scripts/edn-datomize.bb `wrap-generic`) rather than a raw map,
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
