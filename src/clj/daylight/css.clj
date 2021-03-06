(ns daylight.css
  (:require [garden.def :refer [defstyles]]))

(def chart-width 400)
(def chart-height 200)

(defstyles screen
  [:body {:color "red"
          :margin 0
          :padding 0}]

  [:.chart-container 
   {:position "absolute"
    :bottom 0
    :width "100%"
    :right 0
    :padding "12px"
    :background-color "white"}]
  [:.sunangle
   {:width 400
    :min-height 200
    :float "right"}]
  [:.chart
   {:height (str chart-height "px")
    :width (str chart-width "px")
    :background-color "#1A1F26"
    :opacity 1.0}]
  [:.chart-line3 {:fill "#D8E7FF" :opacity 1.0}]
  [:.chart-line2 {:fill "#809CCF" :opacity 1.0}]
  [:.chart-line1 {:fill "#4168B6" :opacity 1.0}]
  [:.chart-line0 {:fill "#21355E" :opacity 1.0}]
  [:.ref-line {:fill "none"
              :stroke "grey"
              :stroke-dasharray "5,5"
              :stroke-width "1"}]
  [:.container {:position "absolute"
                :width "100%"
                :min-height "100%"
                :left 0
                :top 0 }]
  [:#map {:position "absolute"
          :top 0
          :left 0
          :width "100%"
          :min-height "100%"
          }]
  [:.level1 {:color "green"}])
