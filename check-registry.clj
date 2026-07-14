(require '[clojure.edn :as edn])

(let [data (edn/read-string (slurp "resources/kotoba/occupation/registry.edn"))
      occupations-str (:kotoba.occupation/occupations data)
      occupations (edn/read-string occupations-str)]
  (println "Total occupations:" (count occupations))
  ; Find 3113
  (if-let [entry (first (filter #(= (:id %) "3113") occupations))]
    (do
      (println "Found 3113:")
      (println entry))
    (println "3113 not found - need to add it"))
  (println)
  ; Also find 3112
  (if-let [entry (first (filter #(= (:id %) "3112") occupations))]
    (do
      (println "Found 3112:")
      (println entry))
    (println "3112 not found")))
