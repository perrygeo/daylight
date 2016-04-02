(ns daylight.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [daylight.handlers]
              [daylight.subs]
              [daylight.calc]
              [daylight.routes :as routes]
              [daylight.views :as views]
              [daylight.config :as config]))

(when config/debug?
  (println "dev mode"))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init [] 
  (routes/app-routes)
  ; (re-frame/dispatch [:get-timezones])
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root))

