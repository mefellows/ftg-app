(ns re-navigate.shared.navigation
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [re-navigate.events]
            [clojure.data :as d]
            [re-navigate.shared.ui :refer [app-registry text scroll image view md-icon-toggle md-button md-switch theme]]
            [re-navigate.subs]))
(js* "/* @flow */")

(def ReactNative (js/require "react-native"))
(def react-navigation (js/require "react-navigation"))
(def add-navigation-helpers (.-addNavigationHelpers react-navigation))
(def stack-navigator (.-StackNavigator react-navigation))
(def tab-navigator (.-TabNavigator react-navigation))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def logo-img (js/require "./images/cljs.png"))
(defn random-colou
  []
  (js* "'#'+('00000'+(Math.random()*(1<<24)|0).toString(16)).slice(-6)"))

(defn nav-wrapper [component title]
  (let [comp (r/reactify-component
               (fn [{:keys [navigation]}]
                 [component (-> navigation .-state js->clj)]))]
    (aset comp "navigationOptions" #js {"title" title})
    comp))


; (def resd-comp (nav-wrapper resd #(str "Card "
;                                        (aget % "state" "params" "number"))))

; (def app-root-comp (nav-wrapper app-root "Welcome"))
;
; (def stack-router {:Home {:screen app-root-comp}
;                    :Card {:screen resd-comp}})
;
;
; (def sn (r/adapt-react-class (stack-navigator (clj->js stack-router))))
;
; (defn card-start [] (let [nav-state (subscribe [:nav/stack-state "Index"])]
;                       (fn []
;                         (js/console.log @nav-state)
;                         [sn {:navigation (add-navigation-helpers
;                                            (clj->js
;                                              {"dispatch" #(do
;                                                             (js/console.log "EVENT" %)
;                                                             (dispatch [:nav/js [% "Index"]]))
;                                               "state"    (clj->js @nav-state)}))}])))

; (def tab-router {:Index    {:screen (nav-wrapper card-start "Index")}
;                  :Settings {:screen (nav-wrapper settings "Settings")}})
;
;
;
; (defn tab-navigator-inst []
;   (tab-navigator (clj->js tab-router) (clj->js {:order            ["Index" "Settings"]
;                                                 :initialRouteName "Index"})))
;
; (defn get-state [action]
;   (-> (tab-navigator-inst)
;       .-router
;       (.getStateForAction action)))
;
; (defonce tn
;   (let [tni (tab-navigator-inst)]
;     (aset tni "router" "getStateForAction" #(let [new-state (get-state %)]
;                                               (js/console.log "STATE" % new-state)
;                                                              (dispatch [:nav/set new-state])
;                                                              new-state) #_(do (js/console.log %)
;                                                                                                                                         #_(get-state %)))
;     (r/adapt-react-class tni)))
;
; (defn start []
;   (let [nav-state (subscribe [:nav/tab-state])]
;     (fn []
;       [tn])
;     )
;   )
;
; (defn init []
;   (dispatch-sync [:initialize-db])
;   (.registerComponent app-registry "ReNavigate" #(r/reactify-component start)))
