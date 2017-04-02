(ns re-navigate.db
  (:require [clojure.spec :as s]))

;; spec of app-db
;; Fetched from: https://github.com/react-community/react-navigation/blob/c37ad8a0a924d13f3897bc72fbda52aac76904b6/src/TypeDefinition.js

(s/def :nav.route/key keyword?)
(s/def :nav.route/routeName keyword?)
(s/def :nav.route/path keyword?)
(s/def :nav.route/param (s/or :str string? :num number?))
(s/def :nav.route/params (s/map-of keyword? :nav.route/param))
(s/def :nav/route (s/keys :req [:nav.route/key :nav.route/routeName]
                          :opt [:nav.route/path :nav.route/params]))
(s/def :nav.state/routes (s/coll-of :nav/route :kind vector?))
(s/def :nav.state/index integer?)
(s/def :nav/tab-state (s/keys :req [:nav.state/index :nav.state/routes]))

; (def user {:username s/Str}
                  ;  :password (s/maybe s/Str)})

(s/def ::app-db
  (s/keys :req                          [:nav/tab-state]
          :user                         s/Any
          :students                     [s/Any]
          :teachers                     [s/Any]
          :classrooms                   [s/Any]
          :incidents                    [s/Any]
          :contacts                     [s/Any]
          :locations                    [s/Any]
          :actions                      [s/Any]
          :summary                      [s/Any]
          :preferences                  [s/Any]
          :current-incident             s/Any
          :current-student              s/Any
          :current-preference           s/Any
          :current-student-incidents    s/Any
          :current-student-classroom    s/Any
          :current-classroom            s/Any
          :authenticated                s/Bool
          :sync                         s/Bool
          :nav-screen                   s/Str
          :current-page                 s/Keyword))

;; initial state of app-db
(def app-db {:nav/tab-state   #:nav.state{:index  0
                                          :routes [#:nav.route{:key :IndexKey :routeName :Index}
                                                   #:nav.route{:key :SettingsKey :routeName :Settings}]}
             :nav/stack-state #:nav.routeName {:Index #:nav.state {:index  0
                                                                   :routes [#:nav.route {:key :Home :routeName :Home}]}}
            ;  :students [{:id 1 :first_name "billy" :last_name "silly"} {:id 2 :first_name "jane" :last_name "austin"}]
            ;  :preferences [ { :id 1 :type "contact" :school_id 0 :value "m@onegeek.com.au" }, { :id 15 :type "location" :school_id 0 :value "Green turf" }, { :id 16 :type "location" :school_id 0 :value "Four square courts" }, { :id 18 :type "location" :school_id 0 :value "Adventure playground" }, { :id 19 :type "location" :school_id 0 :value "Senior playground" }, { :id 20 :type "location" :school_id 0 :value "Junior playground" }, { :id 21 :type "location" :school_id 0 :value "BBQ area - playground" }, { :id 22 :type "location" :school_id 0 :value "Basketball court" }, { :id 23 :type "location" :school_id 0 :value "Footy oval" }, { :id 24 :type "location" :school_id 0 :value "BBQ area" }, { :id 25 :type "location" :school_id 0 :value "3/4 decking" }, { :id 26 :type "location" :school_id 0 :value "Other" }, { :id 27 :type "action" :school_id 0 :value "Resolved through discussion" }, { :id 28 :type "action" :school_id 0 :value "Refer to BEs" }, { :id 29 :type "action" :school_id 0 :value "Walk with teacher" }, { :id 30 :type "action" :school_id 0 :value "Sent to office" }, { :id 31 :type "action" :school_id 0 :value "Other" }, { :id 32 :type "summary" :school_id 0 :value "Verbal dispute" }, { :id 33 :type "summary" :school_id 0 :value "Physical dispute" }, { :id 34 :type "summary" :school_id 0 :value "Unsafe / rough play" }, { :id 35 :type "summary" :school_id 0 :value "No hat" }, { :id 36 :type "summary" :school_id 0 :value "Other" }, { :id 17 :type "location" :school_id 0 :value "Gully" } ]
            ;  :current-incident {:description "test" :students [1] :follow_up true}
             })
