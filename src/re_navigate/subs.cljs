(ns re-navigate.subs
  (:require [re-frame.core :refer [reg-sub]]))

(.log js/console "here")

(reg-sub
  :nav/tab-state
  (fn [db _]
    (:nav/tab-state db)))

(reg-sub
  :nav/stack-state
  (fn [db [_ route-name]]
    (get-in db [:nav/stack-state (keyword "nav.routeName" route-name)])))

(reg-sub
  :get-greeting
  (fn [db _]
    (:greeting db)))

(reg-sub
  :current-incident
  (fn [db _]
    (:current-incident db)))

(reg-sub
 :students
 (fn [db _]
   (:students db)))

(reg-sub
 :preferences
 (fn [db _]
   (:preferences db)))
