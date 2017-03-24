(ns re-navigate.shared.main
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [re-navigate.events]
            [re-navigate.shared.screens.edit-incident :refer [edit-incident-form]]
            [re-navigate.shared.screens.incidents :refer [incidents]]
            [clojure.data :as d]
            [re-navigate.shared.ui :refer [app-registry text scroll image view md-icon-toggle md-button md-switch theme floating-action-button]]
            [re-navigate.subs]))
(js* "/* @flow */")

(def ReactNative (js/require "react-native"))

(def react-navigation (js/require "react-navigation"))
(def add-navigation-helpers (.-addNavigationHelpers react-navigation))
(def stack-navigator (.-StackNavigator react-navigation))
(def tab-navigator (.-TabNavigator react-navigation))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def logo-img (js/require "./images/cljs.png"))
(defn random-color
  []
  (js* "'#'+('00000'+(Math.random()*(1<<24)|0).toString(16)).slice(-6)"))

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

; existing
(defn resd [props]
  (let [number (-> props (get "params") (get "number"))
        route-name "Index"]
    [view {:style {:align-items      "center"
                   :justify-content  "center"
                   :flex             1
                   :background-color (random-color)}}
     [view {:style {:background-color "rgba(256,256,256,0.5)"
                    :margin-bottom    20}}
      [text {:style (style :title)} "Card numero uno " number]]
     [touchable-highlight
      {:style    (style :button)
       :on-press #(dispatch
                    [:nav/navigate
                     [#:nav.route {:key       (keyword (str number))
                                   :routeName :Card
                                   :params    {:number (inc number)}}
                      route-name]])}
      [text {:style (style :button-text)} "Next"]]
     [touchable-highlight {:on-press #(dispatch [:nav/reset route-name])
                           :style    (style :button)}
      [text {:style (style :button-text)} "RESET"]]]))

(defn app-root [{:keys [navigation]}]
  [view {:style {:flex-direction   "column"
                 :flex             1
                 :padding          40
                 :align-items      "center"
                 :background-color (random-color)}}
   [text {:style (style :title)} "Hejsan"]
   [image {:source logo-img
           :style  {:width 80 :height 80 :margin-bottom 30}}]
   [touchable-highlight {:style    (style :button)
                         :on-press #(dispatch
                                      [:nav/navigate
                                       [#:nav.route {:key       :0
                                                     :routeName :Card
                                                     :params    {:number 1}}
                                        "Index"]])}
    [text {:style (style :button-text)} "press me"]]
    (let [component (floating-action-button (fn [] (js/console.log "action button!")))]
     [component
       [text {:style {:font-size 24
         :font-weight "400"
         :color "#FFF"}}
         "+"]])
    ])


(defn nav-wrapper [component title]
  (let [comp (r/reactify-component
               (fn [{:keys [navigation]}]
                 [component (-> navigation .-state js->clj)]))]
    (aset comp "navigationOptions" #js {"title" title})
    comp))

(def resd-comp (nav-wrapper resd #(str "Card "
                                       (aget % "state" "params" "number"))))

(def app-root-comp (nav-wrapper app-root "Welcome"))

(def stack-router {:Home {:screen app-root-comp}
                   :Card {:screen resd-comp}})


(def sn (r/adapt-react-class (stack-navigator (clj->js stack-router))))

(defn card-start [] (let [nav-state (subscribe [:nav/stack-state "Index"])]
                      (fn []
                        (js/console.log @nav-state)
                        [sn {:navigation (add-navigation-helpers
                                           (clj->js
                                             {"dispatch" #(do
                                                            (js/console.log "EVENT" %)
                                                            (dispatch [:nav/js [% "Index"]]))
                                              "state"    (clj->js @nav-state)}))}])))

(def tab-router {
                ;  :Index    {:screen (nav-wrapper card-start "Incidents")}
                 :Index    {:screen (nav-wrapper incidents "Incidents")}
                 :Settings {:screen (nav-wrapper edit-incident-form "Create")}})
                ;  :Settings {:screen (nav-wrapper settings "Settings")}})



(defn tab-navigator-inst []
  (tab-navigator (clj->js tab-router) (clj->js {:order            ["Index" "Settings"]
                                                :initialRouteName "Index"})))

(defn get-state [action]
  (-> (tab-navigator-inst)
      .-router
      (.getStateForAction action)))

(defonce tn
  (let [tni (tab-navigator-inst)]
    (aset tni "router" "getStateForAction" #(let [new-state (get-state %)]
                                              (js/console.log "STATE" % new-state)
                                                             (dispatch [:nav/set new-state])
                                                             new-state) #_(do (js/console.log %)
                                                                                                                                        #_(get-state %)))
    (r/adapt-react-class tni)))

(defn start []
  (let [nav-state (subscribe [:nav/tab-state])]
    (fn []
      [tn])
    )
  )

(defn init []
  (dispatch-sync [:initialize-db])
  (dispatch-sync [:load-teachers])
  (.registerComponent app-registry "ReNavigate" #(r/reactify-component start))
  (dispatch [:load-students])
  (dispatch [:load-classrooms])
  (dispatch [:load-preferences])
  (dispatch [:load-incidents]))
