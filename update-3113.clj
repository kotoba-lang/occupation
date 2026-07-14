(require '[clojure.edn :as edn]
         '[clojure.string :as str])

(let [registry-file "resources/kotoba/occupation/registry.edn"
      content (slurp registry-file)
      registry (edn/read-string content)
      occupations-str (:kotoba.occupation/occupations registry)
      occupations (edn/read-string occupations-str)
      
      ; Update 3113 entry
      updated-occupations 
        (mapv (fn [occ]
                (if (= (:id occ) 3113)
                  (assoc occ 
                    :maturity :implemented
                    :required-technologies [:robotics :identity :survey-forms :dmn :audit-ledger]
                    :operating-states [:intake :record :inspect :flag :schedule :audit]
                    :repo "https://github.com/cloud-itonami/cloud-itonami-isco-3113"
                    :business-id "cloud-itonami-isco-3113")
                  occ))
              occupations)]
  
  (println "Total occupations:" (count updated-occupations))
  (println "3113 entry after update:")
  (println (first (filter #(= (:id %) 3113) updated-occupations)))
  
  ; Create new registry with updated occupations
  (let [updated-registry (assoc registry :kotoba.occupation/occupations (str updated-occupations))
        output (str "[" (pr-str updated-registry) "]")]
    
    ; Write back
    (spit registry-file output)
    (println "\nRegistry file updated successfully!")
    
    ; Verify by reading back
    (let [verify-content (slurp registry-file)
          verify-registry (edn/read-string verify-content)
          verify-occupations (edn/read-string (:kotoba.occupation/occupations verify-registry))]
      (println "Verification - total after write:" (count verify-occupations))
      (println "3113 after write:"
        (first (filter #(= (:id %) 3113) verify-occupations))))))
