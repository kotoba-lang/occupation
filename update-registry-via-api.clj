(require '[kotoba.occupation :as occ]
         '[clojure.edn :as edn])

; Read the registry
(let [registry (occ/registry)
      occupations-data (:kotoba.occupation/occupations registry)
      occupations (occ/occupations registry)]
  
  (println "Registry structure:")
  (println "  Type of occupations-data:" (type occupations-data))
  (println "  Count of occupations:" (count occupations))
  
  (when-let [occ-3113 (occ/get-occupation "3113")]
    (println "\nBefore update:")
    (println "  3113:" occ-3113)))
