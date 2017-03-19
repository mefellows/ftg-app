(ns re-navigate.shared.screens.edit-incident
  (:require [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [reagent.core :as r]
            [clojure.walk :refer [keywordize-keys]]
            [re-navigate.shared.styles :refer [styles]]
            [clojure.string :as str]
            [re-navigate.subs]
            [re-navigate.events]
            [re-navigate.shared.screens.preferences :refer [filtered-preferences]]
            [re-navigate.shared.ui :refer [app-registry text scroll image view md-icon-toggle md-button md-switch theme touchable-highlight floating-action-button]]))

; Settings
(def card-style (.-cardStyle (-> theme .getTheme)))
(def card-title-style (.-cardTitleStyle (-> theme .getTheme)))
(def card-content-style (.-cardContentStyle (-> theme .getTheme)))
(def card-image-style (.-cardImageStyle (-> theme .getTheme)))
(def card-menu-style (.-cardMenuStyle (-> theme .getTheme)))
(def card-action-style (.-cardActionStyle (-> theme .getTheme)))

(js/console.log card-style)
(js/console.log card-title-style)
(js/console.log card-content-style)

(defn settings []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [scroll {:style {:flex 1}}
        [view {:style { :flex 1 :alignItems "stretch" :backgroundColor "#F5FCFF" :padding 20 :marginTop 0 }}
          [view {:style card-title-style}
            [image {:source {:uri "http://www.getmdl.io/assets/demos/welcome_card.jpg"}
                    :style  card-image-style}]
            [text {:style card-title-style} "Welcome"]
            [view {}
              [text {:style card-content-style} "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris sagittis pellentesque lacus eleifend lacinia..."]]
            [view {:style card-menu-style}
              [md-icon-toggle {:styles {:margin-top 10}
                          :on-checked-change #(js/console.log "changed toggle")
                          :on-press #(js/console.log "pressed toggle")}
                [text {:style { :fontSize 16 :fontStyle "italic" :fontWeight "bold" :color "#616161" }
                       :state_checked true} "T"]
                [text {:style {:color (.-primaryColor (-> theme .getTheme))}} "T"]]
              [md-switch {:styles {}
                          :on-checked-change #(js/console.log "changed toggle")
                          :on-press #(js/console.log "pressed toggle")}]
              [text { :style {:textAlign "center" :color "#cccccc" :marginTop 0 :marginBottom 0 :fontSize 12 :fontWeight "300"} } "cheese"]]
            [view {:style card-action-style}
              [text { } @greeting]]]]])))

(defn valid-form? [props] true)

; TODO: Need to get to local/global props to get the 'validation result'

; (defn valid-form? [props]
;   (js/console.log "valid form?")
;   (let [validation-result
;     (.validate (-> props
;                                          (aget "refs")
;                                          (aget "form")))]
;   (empty? (js->clj (aget validation-result "errors")))))

(defn sanitise-form [incident]
  (if (valid-form? incident)
    (js/console.log (clj->js incident))
    (let [value (keywordize-keys (:value incident))
          start_time (:start_time value)
          end_time (:end_time value)
          students (into [] (map (fn [i] {:id (int i)}) (:students value)))
          updated (-> value
            (assoc :students students)
            (assoc :start_time (.toISOString (new js/Date start_time)))
            (assoc :end_time (.toISOString (new js/Date end_time))))]
            (js/console.log "converted incident object to: " (clj->js updated))
          updated))
          incident)

(defn save [incident]
  (let [updated (sanitise-form incident)]
    (js/console.log "saving!")
    (dispatch [:set-current-incident updated])))

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

; ; TODO: Move state into GLOBAL app state, not confined to component
;
(def style
  {
   :title       {:font-size   30
                 :font-weight "100"
                 :margin      20
                 :text-align  "center"}
   :button      {:background-color "#999"
                 :padding          10
                 :margin-bottom    20
                 :border-radius    5}
   :button-text {:color       "white"
                 :text-align  "center"
                 :font-weight "bold"}
   })

(defn edit-incident-form []
  (let [current-incident (subscribe [:current-incident])
        start_time (:start_time @current-incident)
        end_time   (:end_time @current-incident)
        students   (:students @current-incident)
        id         (:id @current-incident)
        local_id   (:local_id @current-incident)
    ; Convert string to Date objects, and extract student id's
    updated (-> @current-incident
        (assoc :students (into [] (map #(:id %1) students)))
        (assoc :local_id (if (and (nil? id) (nil? local_id))
                             (.now js/Date)
                             (if-not (nil? local_id) local_id id)))
        (assoc :start_time (js->clj (if (nil? start_time) (new js/Date) (new js/Date start_time))))
        (assoc :end_time (js->clj (if (nil? end_time) (new js/Date) (new js/Date end_time)))))]
    (fn []
      [scroll {:style {:flex 1}}
        [view {:style { :flex 1 :alignItems "stretch" :backgroundColor "#F5FCFF" :padding 20 :marginTop 0 }}
          [Form {:ref "form"
             :type (incident @current-incident)
             :value @current-incident
             :options options
             :on-change #(save %1)
           }]
           [touchable-highlight
             {:style    (style :button)
             :on-press    #(dispatch [:nav/set-tab "Index"])}
             [text {:style (style :button-text)} "Save Incident"]]]])))
