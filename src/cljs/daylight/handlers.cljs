(ns daylight.handlers
    (:require [re-frame.core :as re-frame]
              [daylight.db :as db]
              [ajax.core :refer [GET]]
              [daylight.calc :as calc]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(defn set-pos-inner [db [_ newlon newlat]]
  (assoc db :lon newlon
            :lat newlat

            ;; https://en.wikipedia.org/wiki/Dawn#/media/File:Twilight_subcategories.svg
            :sunsets [(calc/calculate-sunsets newlon newlat 108) ;; astronomical dusk
                      (calc/calculate-sunsets newlon newlat 102) ;; nautical dusk
                      (calc/calculate-sunsets newlon newlat 96) ;; civil dusk
                      (calc/calculate-sunsets newlon newlat 90)] ;; sunset
            :sunrises [(calc/calculate-sunrises newlon newlat 108)
                       (calc/calculate-sunrises newlon newlat 102)
                       (calc/calculate-sunrises newlon newlat 96)
                       (calc/calculate-sunrises newlon newlat 90)]))

(re-frame/register-handler
 :set-pos
 set-pos-inner)

; (re-frame/register-handler
;  :handle-response
;  (fn [db [_ data]]
;    (let [features (get data "features")]
;      (println (count features))
;      (assoc db :loading? false
;                :features features))))

; (re-frame/register-handler
;  :handle-error
;  (fn [db [_ x]]
;    (.log js/console (str (:status x) ": " (:status-text x)))
;    (assoc db :loading? false)))

; (re-frame/register-handler
;  :get-timezones
;  (fn [db _]
;    (GET
;      "/data/timezones.geojson"
;      {:handler       #(re-frame/dispatch [:handle-response %1])
;       :error-handler #(re-frame/dispatch [:handle-error %1])
;       :response-format :json})
;    (assoc db :loading? true)))
