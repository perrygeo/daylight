(ns daylight.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
 :sunsets
 (fn [db]
   (reaction (:sunsets @db))))

(re-frame/register-sub
 :sunrises
 (fn [db]
   (reaction (:sunrises @db))))

(re-frame/register-sub
 :features 
 (fn [db]
   (reaction (:features @db))))

(re-frame/register-sub
 :pos
 (fn [db]
   (reaction (select-keys @db [:lon :lat]))))
