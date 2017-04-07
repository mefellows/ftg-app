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
          :nav-screen                   s/Any
          :login-form                   s/Any
          :current-page                 s/Keyword))

;; initial state of app-db
(def app-db {:nav/tab-state   #:nav.state{:index  0
                                          :routes [#:nav.route{:key :IndexKey :routeName :Index}
                                                   #:nav.route{:key :SettingsKey :routeName :Settings}]}
             :nav/stack-state #:nav.routeName {:Index #:nav.state {:index  0
                                                                   :routes [#:nav.route {:key :Home :routeName :Home}]}}
             :nav-screen {:type "Navigation/INIT"}
             :user (if goog.DEBUG
                    {:id 0
                     :username "Stephanie Massarotti"
                     :password "gullynorth"}
                    nil)})
