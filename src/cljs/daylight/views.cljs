(ns daylight.views
    (:require [re-frame.core :as re-frame]
              [reagent.core :as reagent]
              [cljsjs.leaflet]
              [re-com.core :as re-com]))

(defn scale-lin [x lower upper]
  (let [scaled (/ (- x lower) (- upper lower))]
    (cond
      (< scaled 0) 0
      (> scaled 1) 1
      :default scaled)))

(def width 400)
(def height 200)

(defn scale-data [width height ymin ymax i sun-hour]
  (let [xmin 1
        xmax 365]
    (clojure.string/join "," [
                              (* width (scale-lin i xmin xmax))
                              (- height (* height (scale-lin sun-hour ymin ymax)))])))

(def scale (partial scale-data width height 0 24))

(defn daylight-chart [i data]
  (let [[sunrise sunset] data
        sunrise-seq (map-indexed scale sunrise)
        sunset-seq (map-indexed scale sunset)
        points (clojure.string/join " " (concat sunrise-seq
                                                (reverse sunset-seq)))]
     [:polyline
       {:class (str "chart-line" i)
        :points points}]))


(defn daylight-charts [sunrises sunsets]
  (let [sun-vectors (map vector sunrises sunsets)
        charts (map-indexed daylight-chart sun-vectors)]
     [:svg
       {:viewBox
        (clojure.string/join " " [0 0 width height])
        :class "chart"}
      (nth charts 0)
      (nth charts 1)
      (nth charts 2)
      (nth charts 3)
      [:polyline
         {:class "ref-line"
          :points (clojure.string/join " " [(scale 0 9) (scale 365 9)])}]
      [:polyline
         {:class "ref-line"
          :points (clojure.string/join " " [(scale 0 17) (scale 365 17)])}]
      ]))

(defn daylight-chart-container []
  (let [sunsets (re-frame/subscribe [:sunsets])
        sunrises (re-frame/subscribe [:sunrises])
        pos (re-frame/subscribe [:pos])]
    (fn []
      [:div
        {:class "chart-container"}
        [daylight-charts @sunrises @sunsets]
        [:img {:src "data/sun_angles.svg"
               :width 400 :height 200
               :class "sunangle"}]
        [:p (str "Latitude " (.toFixed (:lat @pos) 2) " | "
                 "Longitude " (.toFixed (:lon @pos) 2))]])))


(defn mount-leaflet []
  (let [layer (.tileLayer js/L "http://{s}.tile.osm.org/{z}/{x}/{y}.png")
        lmap (.setView (.map js/L "map") #js [0 0] 2)]
    (.addTo layer lmap)
    (.on lmap "mousemove"
         (fn [e]
           (re-frame/dispatch [:set-pos
                               (-> e .-latlng .-lng)
                               (-> e .-latlng .-lat)])))))

(defn update-leaflet [x]
  ;; todo update map with features
  (println x))

(defn leaflet-inner []
  (let [test (atom :woot)]
    (reagent/create-class
      {:reagent-render (fn [] [:div#map])
       :component-did-mount mount-leaflet
       :component-did-update update-leaflet
       :display-name "leaflet-inner"})))

(defn leaflet-map []
  (let [features (re-frame/subscribe [:features])]
    (fn []
      [leaflet-inner @features])))

(defn main-panel []
  [:div
   {:class "container"}
   [leaflet-map]
   [daylight-chart-container]])
