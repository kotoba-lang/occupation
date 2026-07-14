(require '[clojure.edn :as edn])

(let [registry-file "resources/kotoba/occupation/registry.edn"
      content (slurp registry-file)
      tx-data (edn/read-string content)
      entity (first tx-data)
      occ-str (:kotoba.occupation/occupations entity)
      occupations (edn/read-string occ-str)
      
      ; Check a few entries to understand the structure
      _ (println "Sample ID types:")
      _ (doall (take 3 (map #(println "  " (:id %) "type:" (type (:id %))) occupations)))
      
      ; Update 3113 (try both string and int)
      updated-occupations
        (mapv (fn [occ]
                (if (or (= (:id occ) "3113") (= (:id occ) 3113))
                  (assoc occ
                    :maturity :implemented
                    :required-technologies [:robotics :identity :survey-forms :dmn :audit-ledger]
                    :operating-states [:intake :record :inspect :flag :schedule :audit]
                    :repo "https://github.com/cloud-itonami/cloud-itonami-isco-3113"
                    :business-id "cloud-itonami-isco-3113")
                  occ))
              occupations)
      
      updated-entity (assoc entity :kotoba.occupation/occupations (pr-str updated-occupations))
      output-edn (pr-str [updated-entity])]
  
  (println "Total:" (count occupations))
  (spit registry-file output-edn)
  (println "Registry updated!")
  
  (let [verify-content (slurp registry-file)
        verify-tx (edn/read-string verify-content)
        verify-entity (first verify-tx)
        verify-occ-str (:kotoba.occupation/occupations verify-entity)
        verify-occ (edn/read-string verify-occ-str)
        found-3113 (first (filter #(or (= (:id %) "3113") (= (:id %) 3113)) verify-occ))]
    (println "Verification - total:" (count verify-occ))
    (when found-3113
      (println "3113 updated successfully:")
      (println "  maturity:" (:maturity found-3113))
      (println "  repo:" (:repo found-3113)))))
