(ns re-navigate.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :nav/tab-state
  (fn [db _]
    (:nav/tab-state db)))

(reg-sub
  :nav/stack-state
  (fn [db [_ route-name]]
    (get-in db [:nav/stack-state (keyword "nav.routeName" route-name)])))

(reg-sub        ;; a new subscription handler
 :students             ;; usage (subscribe [:students])
 (fn [db]
   ;; extracts the students property from the db
   (:students db)))

(reg-sub        ;; a new subscription handler
 :preferences             ;; usage (subscribe [:preferences])
 (fn [db]
   ;; extracts the preferences property from the db
   (:preferences db)))

(reg-sub        ;; a new subscription handler
 :current-student-incidents             ;; usage (subscribe [::current-student-incidents])
 (fn [db]
   ;; extracts the :current-student-incidents property from the db
   (:current-student-incidents db)))

(reg-sub        ;; a new subscription handler
 :current-student-classroom             ;; usage (subscribe [::current-student-classroom])
 (fn [db]
   ;; extracts the :current-student-classroom property from the db
   (:current-student-classroom db)))

(reg-sub        ;; a new subscription handler
 :teachers             ;; usage (subscribe [:teachers])
 (fn [db]
   ;; extracts the teachers property from the db
   (:teachers db)))

(reg-sub        ;; a new subscription handler
 :incidents             ;; usage (subscribe [:incidents])
 (fn [db]
   ;; extracts the incidents property from the db
   (:incidents db)))

(reg-sub        ;; a new subscription handler
 :classes             ;; usage (subscribe [:classes])
 (fn [db]
   ;; extracts the classes property from the db
   (:classes db)))

(reg-sub        ;; a new subscription handler
 :classrooms             ;; usage (subscribe [:classrooms])
 (fn [db]
   ;; extracts the classrooms property from the db
   (:classrooms db)))

(reg-sub
  :current-page
  (fn [db _]
    (reg-sub
      (:current-page db))))

(reg-sub
  :current-classroom
  (fn [db _]
      (:current-classroom db)))

(reg-sub
  :menu-selected
  (fn [db _]
      (:menu-selected db)))

(reg-sub
  :incident
  (fn [db _]
      (let [{:keys [images loading?]} (:incidents-query @db)]
        [images loading?])))

(reg-sub
  :detail
  (fn [db _]
      (print "some reaction")))

(reg-sub
  :user
  (fn [db _]
      (:user db)))

(reg-sub
  :current-incident
  (fn [db _]
      (:current-incident db)))

(reg-sub
  :current-preference
  (fn [db _]
      (:current-preference db)))

(reg-sub
  :current-student
  (fn [db _]
      (:current-student db)))

(reg-sub
  :sync
  (fn [db _]
      (:sync db)))

(reg-sub
  :nav-screen
  (fn [db _]
    (:nav-screen db)))
