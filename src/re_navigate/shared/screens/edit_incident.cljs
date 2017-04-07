(ns re-navigate.shared.screens.edit-incident
  (:require [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [reagent.core :as r]
            [clojure.walk :refer [keywordize-keys]]
            [re-navigate.shared.styles :refer [styles]]
            [clojure.string :as str]
            ; [re-navigate.subs]
            ; [re-navigate.events]
            [re-navigate.shared.screens.preferences :refer [filtered-preferences]]
            [re-navigate.shared.ui :refer [app-registry text scroll image view md-icon-toggle md-button md-switch theme touchable-highlight floating-action-button header colour]]))

(defn valid-form? [props]
  (js/console.log "validating form")
  (let [validation-result (.validate (-> props
                                         (aget "refs")
                                         (aget "form")))]
  (empty? (js->clj (aget validation-result "errors")))))



; sanitises dates etc.
(defn sanitise-form [incident]
  (let [start_time (:start_time incident)
        end_time (:end_time incident)
        updated (-> incident
          (assoc :start_time (js->clj (if (nil? start_time) (new js/Date) (new js/Date start_time))))
          (assoc :end_time (js->clj (if (nil? end_time) (new js/Date) (new js/Date end_time)))))]
          (js/console.log "sanitised incident object to: " (clj->js updated))
        (keywordize-keys updated)))

; This converts into the appropriate clojure structures for API submission
(defn on-submit [props nav]
  (js/console.log "on submit")
  (when (valid-form? props)
    (let [i (subscribe [:current-incident])
          incident (sanitise-form @i)
          ; turns it BACK into a clj object.
          ; TODO: move this out of thsi class??
          students (into [] (map (fn [i] {:id (int i)}) (:students incident)))
          updated (-> incident
            (assoc :students students)
            (assoc :start_time (.toISOString (new js/Date (:start_time incident))))
            (assoc :end_time (.toISOString (new js/Date (:end_time incident)))))]
            (js/console.log "original incident object: " (clj->js incident))
            (js/console.log "converted incident object to: " (clj->js updated))
      (dispatch [:save-incident (js->clj updated)])
      (-> nav (.navigate "Index")))))

(defn sanitise-and-validate-form [props incident]
  (if (valid-form? props)
    (let [updated (sanitise-form incident)]
    updated)
    incident))

; save without validating, but cleanses
(defn save [incident]
  (js/console.log "Saving incident:" (clj->js incident))
  (dispatch [:set-current-incident (js->clj incident)]))

(def t (js/require "tcomb-form-native"))
(def Form (r/adapt-react-class (.-Form t.form)))
(def s (.-stylesheet (.-Form t.form)))
(def _ (js/require "lodash"))

; See https://github.com/gcanti/tcomb-form-native/blob/master/lib/stylesheets/bootstrap.js
; for more you can modify.
(def form-style
  (let [stylesheet (_.cloneDeep s)
  updated (-> stylesheet
    (.-controlLabel)
    (.-normal)
    (aset "color" "#444444"))]
    stylesheet))

(def text-area-style
  (let [stylesheet (_.cloneDeep form-style)
        updated (-> stylesheet
                    (.-textbox)
                    (.-normal)
                    (aset "height" 150))]
        stylesheet))

(def options
  {:stylesheet form-style
    :order [:summary :location :description :action_taken :students :follow_up :start_time :end_time]
    :fields {:id {:hidden true}
    :students {:item {:label " "
                      :order "asc"}}
    :description {:stylesheet text-area-style
                  :multiline true}}})

(defn extract-student-enum [student]
  (let [] {(:id student) (str (:first_name student) " " (:last_name student))}))

(defn extract-preference-enum [preference]
  (let [] {(:value preference) (str (:value preference))}))

(defn Student []
  (let [students (subscribe [:students])]
    (->> @students
         (filter #(let [] (> (:id %1) 0)))
         (mapv extract-student-enum)
         (flatten)
         (into {})
         (clj->js)
         (t.enums))))

 (defn Preference [type current-value]
   (let [preferences (subscribe [:preferences])
         val (if-not (nil? current-value)
              current-value
              "")]
     (->> (filtered-preferences @preferences type)
          (mapv extract-preference-enum)
          (flatten)
          (cons {(str val) (str val)})
          (into {})
          (clj->js)
          (t.enums))))

(defn incident [val]
  (let [new? (nil? (:id val))
        current-incident (subscribe [:current-incident])
        obj {:start_time t.Date
             :end_time t.Date
             :summary (Preference "summary" (:summary @current-incident))
             :students (t.list (Student))
             :description (t.maybe t.String)
             :location (Preference "location" (:location @current-incident))
             :follow_up (t.maybe t.Boolean)
             :action_taken (Preference "action" (:action_taken @current-incident))
             }]
    (if-not new?
      (t.struct (clj->js (assoc obj :id t.Number)))
      (t.struct (clj->js obj)))))

(def style
  {
   :title       {:font-size   30
                 :font-weight "100"
                 :margin      20
                 :text-align  "center"}
   :button      {:background-color (colour :orange800)
                 :padding          10
                 :margin-bottom    20
                 :border-radius    5}
   :button-text {:color       "white"
                 :text-align  "center"
                 :font-weight "bold"}
   })

; ; TODO: Move state into GLOBAL app state, not confined to component
(defn edit-incident []
 (fn [nav]
  (r/create-class
    {:reagent-render
     (fn [props]
       (this-as this
        (let [current-incident (subscribe [:current-incident])
              start_time (:start_time @current-incident)
              end_time   (:end_time @current-incident)
              students   (:students @current-incident)
              id         (:id @current-incident)
              local_id   (:local_id @current-incident)

              ; Convert string to Date objects, and extract student id's
              updated (-> @current-incident
                  (assoc :start_time (js->clj (if (nil? start_time) (new js/Date) (new js/Date start_time))))
                  (assoc :end_time (js->clj (if (nil? end_time) (new js/Date) (new js/Date end_time)))))]
          [view {:flex 1 :flex-direction "column"}
            [header nav "Edit Incident"]
            [view {:flex 9}
              [scroll {:style (:scroll-container styles)}
               [Form {:ref "form"
                      :type (incident updated)
                      :value (clj->js updated)
                      :options options
                      :on-change #(save %1)}]
                [touchable-highlight
                  {:style      (style :button)
                  :disabled?   #(not (valid-form? this))
                  :on-press    #(on-submit this nav)}
                  [text {:style (style :button-text)} "Save Incident"]]]]])))})))
