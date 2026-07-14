(require '[clojure.edn :as edn])

(let [registry-file "resources/kotoba/occupation/registry.edn"
      content (slurp registry-file)
      registry (edn/read-string content)]
  (println "Registry keys:" (keys registry))
  (println "Occupations value type:" (type (:kotoba.occupation/occupations registry)))
  (let [occ-str (:kotoba.occupation/occupations registry)]
    (println "Occupations string length:" (count occ-str))
    (println "First 200 chars:" (subs occ-str 0 200))))
