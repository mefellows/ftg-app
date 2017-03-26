(ns re-navigate.events
  (:require
    [re-frame.core :refer [reg-event-db after reg-event-fx debug dispatch dispatch-sync]]
    [clojure.spec :as s]
    [re-navigate.config :refer [env]]
    [clojure.string :as str]
    [ajax.core :refer [GET]]
    [re-navigate.shared.ui :as ui]
    [re-navigate.db :as db :refer [app-db]]))

;; -- Interceptors ------------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/blob/master/docs/Interceptors.md
;;
(defn check-and-throw
  "Throw an exception if db doesn't have a valid spec."
  [spec db [event]]
  (when-not (s/valid? spec db)
    (let [explain-data (s/explain-data spec db)]
      (throw (ex-info (str "Spec check after " event " failed: " explain-data) explain-data)))))

(def validate-spec
  (if goog.DEBUG
    (after (partial check-and-throw ::db/app-db))
    []))

(defn dec-to-zero
  "Same as dec if not zero"
  [arg]
  (if (< 0 arg)
    (dec arg)
    arg))

(defn log-ex
  [handler]
  (fn log-ex-handler
    [db v]
    (try
        (handler db v)        ;; call the handler with a wrapping try
        (catch :default e     ;; ooops
          (do
            (.error js/console e.stack)   ;; print a sane stacktrace
            (throw e))))))

(def standard-interceptors [validate-spec debug])

;; -- Handlers --------------------------------------------------------------

(reg-event-db
  :initialize-db
  standard-interceptors
  (fn [_ _]
    app-db))

(reg-event-db
 :set-greeting
 standard-interceptors
 (fn [db [_ value]]
   (assoc db :greeting value)))

;; -- API handlers ---------------------------------------------------

(reg-event-db
 :set-current-incident
 standard-interceptors
 (fn [db [_ value]]
   (assoc db :current-incident value)))

(defn find-student-classroom "Finds a classroom in the given db by a students' id" [db id]
  (let [classrooms (:classrooms db)
    classroom (first
               (->> classrooms
                    (filter
                      (fn [classroom]
                        (some #{id} (:students classroom))))))]
    (if-not (nil? classroom)
      (let []
        (assoc db :current-student-classroom classroom))
      db)))

(defn find-classroom "Finds a classroom in the given db by id" [db id]
  (let [classrooms (:classrooms db)
    classroom (first
               (->> classrooms
                    (filter
                      (fn [classroom]
                        (= (:id classroom) id)))))]
    (if-not (nil? classroom)
      (let []
        (dispatch [:nav/push :edit-classroom])
        (assoc db :current-student-classroom classroom))
      nil)))

(defn find-student "Finds a student in the given db by id" [db id]
  (let [students (:students db)
        incidents (:incidents db)
        student-incidents (->> incidents
                               (filter
                                 (fn [incident]
                                   ; return true where student in (:students incident)
                                   (not (empty? (->> (:students incident)
                                                    (filter
                                                      (fn [student]
                                                        (= (:id student) id)))))))))

        student (first
                   (->> students
                        (filter
                          (fn [student]
                            (= (:id student) id)))))]
        (if-not (nil? student)
          (let []
            (dispatch [:nav/push :edit-student])
            (-> db
              (assoc :current-student-incidents student-incidents)
              (assoc :current-student student)))
          nil)))

(defn find-incident "Finds an incident in the given db by id" [db id]
  (let [incidents (:incidents db)
    incident (first
               (->> incidents
                    (filter
                      (fn [incident]
                        (= (:id incident) id)))))]
    (if-not (nil? incident)
      (let []
        (dispatch [:nav/push :edit-incident])
        (assoc db :current-incident incident))
      nil)))

(defn find-preference "Finds an preference in the given db by id" [db id]
  (let [preferences (:preferences db)
    preference (first
               (->> preferences
                    (filter
                      (fn [preference]
                        (= (:id preference) id)))))]
    (if-not (nil? preference)
      (let []
        (dispatch [:nav/push :edit-preference])
        (assoc db :current-preference preference))
      nil)))

(defn update-incident "Finds and updates an incident in the db (uses local-id)" [db incident]
  (let [incidents (:incidents db)]
    (->>
      incidents
      (mapv #(let []
        (if (= (:local_id %1) (:local_id incident)) incident %))))))

(reg-event-db
  :initialize-db
  standard-interceptors
  (fn [_]
    app-db))

(def standard-middlewares  [standard-interceptors log-ex])

(reg-event-db
 :process-students-res
 standard-interceptors
 (fn
   ;; store the response of fetching the phones list in the phones attribute of the db
   [db [_ response]]
   (print response)
   (if-not (nil? response)
    (assoc db :students response)
    (assoc db :students []))))

(reg-event-db
 :process-classrooms-res
 standard-interceptors
 (fn
   ;; store the response of fetching the phones list in the phones attribute of the db
   [db [_ response]]
   (print response)
   (if-not (nil? response)
    (assoc db :classrooms response)
    (assoc db :classrooms []))))

(reg-event-db
  :load-students
  standard-interceptors
  (fn [db _]
    (ajax.core/GET (str (:hostname env) "/students")
     {
      :response-format :json
      :keywords? true
      :handler #(dispatch [:process-students-res %1])
      :error-handler #(dispatch-sync [:bad-response %1])})
   db)) ; <- DAH! Must return the state!!

(reg-event-db
  :load-teachers
  standard-interceptors
  (fn [db _]
    (ajax.core/GET (str (:hostname env) "/teachers")
     {
      :response-format :json
      :keywords? true
      :handler #(dispatch [:process-teachers-res %1])
      :error-handler #(dispatch-sync [:bad-response %1])})
   db)) ; <- DAH! Must return the state!!

(reg-event-db
  :load-classrooms
  standard-interceptors
  (fn [db _]
    (ajax.core/GET (str (:hostname env) "/classes")
     {
      :response-format :json
      :keywords? true
      :handler #(dispatch [:process-classrooms-res %1])
      :error-handler #(dispatch-sync [:bad-response %1])})
   db)) ; <- DAH! Must return the state!!

(reg-event-db
 :process-teachers-res
 standard-interceptors
 (fn
   ;; store the response of fetching the phones list in the phones attribute of the db
   [db [_ response]]
   (print response)
   (if-not (nil? response)
    (assoc db :teachers response)
    (assoc db :teachers []))))

(reg-event-db
  :load-teachers
  standard-interceptors
  (fn [db _]
    (ajax.core/GET (str (:hostname env) "/teachers")
     {
      :response-format :json
      :keywords? true
      :handler #(dispatch [:process-teachers-res %1])
      :error-handler #(dispatch-sync [:bad-response %1])})
   db)) ; <- DAH! Must return the state!!

; Fetch a single incident from the local database (does not make API call)
(reg-event-db
  :incident-load
  standard-interceptors
  (fn [db [_ id]]
    (find-incident db id)))

; Fetch a single preference from the local database (does not make API call)
(reg-event-db
  :preference-load
  standard-interceptors
  (fn [db [_ id]]
    (find-preference db id)))

; Fetch a single student from the local database (does not make API call)
(reg-event-db
  :student-load
  standard-interceptors
  (fn [db [_ id]]
    (-> db
      (find-student id)
      (find-student-classroom id))))

(reg-event-db
  :incident-res
  standard-interceptors
  (fn [db [_ incident]]
    (let [incident (if (map? incident) [(:image incident)] incident)]
      (-> db
          (assoc-in [:incident-query :incident] incident)
          (assoc-in [:incident-query :loading?] false)))))

(reg-event-db
  :login
  standard-interceptors
  (fn [db [_ user]]
    (let [password (:password user)]
      (if (= password "gullynorth")
        (let []
          (dispatch [:nav/push :incidents])
          (assoc db :user user))
        (let []
          (js/alert "Invalid password :(")
          db)
          ))))

; Sync all lookup lists. These are not managed as carefully as incidents
; so all they do is replace what is currently in storage.
(defn sync-config []
  (dispatch [:load-students])
  (dispatch [:load-incidents])
  (dispatch [:load-classrooms])
  (dispatch [:load-teachers])
  (dispatch [:load-preferences]))

(reg-event-db
  :sync-complete
  standard-interceptors
  (fn [db [_ res records]]
    (print res)
    (sync-config)
    (when-not (nil? res)
      ; update all incidents -> no longer dirty!
      (merge (:incidents db) (->> records
        (map (fn [i]
          (assoc i :synchronised true))))))
    (assoc db :sync false)))

(reg-event-db
  :sync-fail
  standard-interceptors
  (fn [db [_ res]]
    (print res)
    (ui/alert (str "Synchronise failed: " res))
    (assoc db :sync false)))

(defn sync-records [records]
  (js/console.log "sync records: " (clj->js records))
  (ajax.core/POST (str (:hostname env) "/sync")
   {
    :format (ajax.core/json-request-format)
    :response-format (ajax.core/json-response-format {:keywords? true})
    :params (clj->js records)
    :handler #(dispatch [:sync-complete %1 records])
    :error-handler #(dispatch-sync [:sync-fail %1])}))

(defn save-preference [preference]
  (js/console.log "saving preference: " (clj->js preference))
  (let [method (if (nil? (:id preference))
                          ajax.core/POST
                          ajax.core/PUT)]
                          (method (str (:hostname env) "/preferences")
                          {
                            :format (ajax.core/json-request-format)
                            :response-format (ajax.core/json-response-format {:keywords? true})
                            :params (clj->js (assoc preference :school_id (:school-id env)))
                            :handler #(ui/alert "Saved!")
                            :error-handler #(ui/alert "Error saving!")})))

; New Handlers
(reg-event-db
  :save-preference
  standard-interceptors
  (fn [db [_ preference]]
    (save-preference preference)
    (assoc db :current-preference preference)))

; New Handlers
(reg-event-db
  :synchronise
  standard-interceptors
  (fn [db [_]]
    (ui/alert "Synchronising...")
    (sync-records (let [incidents (:incidents db)]
      (->> incidents
        (filterv #(let []
            (= (:synchronised %1) false))))))
    (assoc db :sync true)))

(reg-event-db
  :bad-response
  standard-interceptors
  (fn [db [_ body]]
    (print "error: " body)
    db))

(reg-event-db
 :process-incidents-res
 standard-interceptors
 (fn
   [db [_ response]]
   (print response)
   (assoc db :incidents response)))

(reg-event-db
 :process-preferences-res
 standard-interceptors
 (fn
   [db [_ response]]
   (print response)
   (assoc db :preferences response)))

(reg-event-db
  :load-incidents
  standard-interceptors
  (fn [db _]
    (ajax.core/GET (str (:hostname env) "/incidents")
     {
      :response-format :json
      :keywords? true
      :handler #(dispatch [:process-incidents-res %1])
      :error-handler #(dispatch-sync [:bad-response %1])})
   db)) ; <- DAH! Must return the state!!

(reg-event-db
  :load-preferences
  standard-interceptors
  (fn [db _]
    (ajax.core/GET (str (:hostname env) "/school/" (:school-id env) "/preferences")
     {
      :response-format :json
      :keywords? true
      :handler #(dispatch [:process-preferences-res %1])
      :error-handler #(dispatch-sync [:bad-response %1])})
   db)) ; <- DAH! Must return the state!!

 (reg-event-db
   :create-incident
   standard-interceptors
   (fn [db [_]]
      (dispatch [:nav/push :edit-incident])
      (assoc db :current-incident {})))

 (reg-event-db
   :create-preference
   standard-interceptors
   (fn [db [_]]
      (dispatch [:nav/push :edit-preference])
      (assoc db :current-preference {})))

(defn get-incident-by-local-id "Finds an incident in the db by its local-id" [db local-id]
  (let [incidents (:incidents db)]
    (first (->>
      incidents
      (filter #(let []
        (= (:local_id %1) local-id)))))))

 ; NOTE: handle updates to unsynced records -> currently only adds new.
 (reg-event-db
   :save-incident
   standard-interceptors
   (fn [db [_ incident]]
     (print "Saving local incident: " incident)
     (let [incidents (:incidents db)
           id (:id incident)
           local-id (:local_id incident)
           updated-incident (assoc incident :synchronised false)]
           (print "updated-incident" updated-incident)
       (if (nil? (get-incident-by-local-id db local-id))
         (let []
           ; Add a new incident locally.
           (print "INSERTING a new incident")
           (->> updated-incident
             (conj incidents)
             (assoc db :incidents)))
           (let []
             ; find and update an existing incident
             (print "UPDATING an existing incident")
              (->> updated-incident
                   (update-incident db)
                   (assoc db :incidents)))))))

;; -- Navigation handlers ---------------------------------------------------

;; TODO: Review which events are used!!

(reg-event-db
  :nav/navigate
  standard-interceptors
  (fn [db [_ [value route-name]]]
    (-> db
        (update-in [:nav/stack-state
                    (keyword "nav.routeName" route-name)
                    :nav.state/routes]
                   #(conj % value))
        (update-in [:nav/stack-state
                    (keyword "nav.routeName" route-name)
                    :nav.state/index]
                   inc))))

(defn nav-val->route
  [nav-val route-name]
  (let [route-name (.-routeName nav-val)
        params     (.-params nav-val)]
    [(merge #:nav.route{:routeName (keyword route-name)
                        :key       (->> route-name (str "scene_") keyword)}
            (if params {:nav.route/params params})) route-name]))

(defn tab-val->route
  [nav-val]
  (let [route-name (.-routeName nav-val)
        params     (.-params nav-val)]
    (merge #:nav.route{:routeName (keyword route-name)
                       :key       (->> route-name (str "scene_") keyword)}
           (if params {:nav.route/params params}))))

(reg-event-db
  :nav/back
  standard-interceptors
  (fn [db [_ route-name]]
    (let [route-key (keyword "nav.routeName" route-name)]
      (-> db
          (update-in [:nav/stack-state route-key :nav.state/index] dec-to-zero)
          (update-in [:nav/stack-state route-key :nav.state/routes] pop)))))

(reg-event-db
  :nav/reset
  standard-interceptors
  (fn [db [_ route-name]]
    (let [route-key   (keyword "nav.routeName" route-name)
          first-route (-> db :nav/stack-state route-key :nav.state/routes first)]
      (-> db
          (assoc-in [:nav/stack-state route-key :nav.state/routes] [first-route])
          (assoc-in [:nav/stack-state route-key :nav.state/index] 0)))))

(defn position
  [pred coll]
  (first
    (keep-indexed (fn [idx x]
                    (when (pred x)
                      idx))
                  coll)))

(reg-event-db
  :nav/set-tab
  standard-interceptors
  (fn [db [_ tab]]
    (let [old-idx (get-in db [:nav/tab-state :nav.state/index])
          idx     (position #(do
                               (= tab (name (:nav.route/routeName %))))
                            (get-in db [:nav/tab-state :nav.state/routes]))]
      ; (js/console.log (js/Date.) "SETTING TAB " tab idx old-idx)
      (assoc-in db [:nav/tab-state :nav.state/index] idx))))

(reg-event-db
  :nav/set
  standard-interceptors
  (fn [db [_ nav]]
    (js/console.log "GOT NAV" nav)
    (assoc-in db [:nav/tab-state :nav.state/index] (.-index nav))))

(reg-event-fx
  :nav/js-tab
  standard-interceptors
  (fn [{:keys [db]} [_ tab-val]]
    ; (js/console.log "JS TAB NAV" (js->clj tab-val))
    {:dispatch (case (.-type tab-val)
                 "Back" [:nav/back]
                 "Navigate" [:nav/set #:nav.state{:index ()} (.-routeName tab-val)])
     :db       db}))


(reg-event-fx
  :nav/js
  standard-interceptors
  (fn [{:keys [db]} [_ [nav-val route-name]]]
    ; (js/console.log [nav-val route-name])
    ; (js/console.log "JS NAV" (js->clj nav-val))
    {:dispatch (case (.-type nav-val)
                 "Back" [:nav/back route-name]
                 "Navigate" [:nav/navigate (nav-val->route nav-val route-name)])
     :db       db}))

; EXPERIMENTAL NAVIGATION
(reg-event-db
 :navigate-to
 standard-interceptors
 (fn [db [_ value]]
   (js/console.log "navigating somewhere:" value)
   (assoc db :nav-screen value)))
