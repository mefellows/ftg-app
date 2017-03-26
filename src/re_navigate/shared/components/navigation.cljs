(ns re-navigate.shared.components.navigation
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [clojure.data :as d]
            [re-navigate.shared.screens.edit-incident :refer [edit-incident-form]]
            [re-navigate.shared.screens.incidents :refer [incidents]]
            [re-navigate.shared.ui :refer [app-registry text scroll image view md-icon-toggle md-button md-switch theme]]))

(def ReactNative (js/require "react-native"))
(def react-navigation (js/require "react-navigation"))
(def add-navigation-helpers (.-addNavigationHelpers react-navigation))
(def stack-navigator (.-StackNavigator react-navigation))
(def drawer-navigator (.-DrawerNavigator react-navigation))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def logo-img (js/require "./images/cljs.png"))

; Automatically add header here?
(defn nav-wrapper [component title]
  (let [comp (r/reactify-component
               (fn [{:keys [navigation]}]
                 [component navigation (-> navigation .-state js->clj)]))]
    (aset comp "navigationOptions" #js {"title" title})
    (aset "navigationOptions" "drawer" #js {"label" title})
    comp))

(def tab-router {
                 :Index    {:screen (nav-wrapper incidents "Incidents")}
                 :Settings {:screen (nav-wrapper edit-incident-form "Create")}})

; TODO: take events / functions to dynamically add screens (e.g. during startup)

(defn drawer-navigator-inst []
  (drawer-navigator (clj->js tab-router) (clj->js {:order            ["Index" "Settings"]
                                                   :initialRouteName "Index"})))

(defn get-state [action]
  (-> (drawer-navigator-inst)
      .-router
      (.getStateForAction action)))

(defonce tab-navigator
  (let [tni (drawer-navigator-inst)]
    (aset tni "onNavigationStateChange" #(js/console.log "ON NAV STATE"))
    ; (aset tni "onNavigationStateChange" (fn [old new] js/console.log "navigation state change. Old: " old ", new: " new))
    (aset tni "router" "getStateForAction" #(let [new-state (get-state %)]
                                              (js/console.log "STATE" % new-state)
                                                             (dispatch [:nav/set new-state])
                                                             new-state) #_(do (js/console.log %)
                                                                                                                                        #_(get-state %)))
    (r/adapt-react-class tni)))
