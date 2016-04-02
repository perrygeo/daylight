(ns daylight.db)

(def default-db
  {:name "re-frame"
   :lat 0
   :lon 0
   :loading? false
   :features []
   :sunsets [[] [] [] []]
   :sunrises [[] [] [] []] })
