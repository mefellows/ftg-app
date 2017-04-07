(ns re-navigate.shared.components.navigation
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [clojure.data :as d]
            [re-navigate.shared.screens.edit-incident :refer [edit-incident-form]]
            [re-navigate.shared.screens.incidents :refer [incidents]]
            [re-navigate.shared.screens.students :refer [students]]
            [re-navigate.shared.screens.classrooms :refer [classrooms]]
            [re-navigate.shared.screens.preferences :refer [preferences]]
            [re-navigate.shared.ui :refer [app-registry text scroll image view md-icon-toggle md-button md-switch theme]]))

(def ReactNative (js/require "react-native"))
(def react-navigation (js/require "react-navigation"))
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

(def tab-router {:Index          {:screen (nav-wrapper incidents "Incidents")}
                 :Preferences    {:screen (nav-wrapper preferences "Preferences")}
                 :Students       {:screen (nav-wrapper students "Students")}
                 :Classrooms     {:screen (nav-wrapper classrooms "Classrooms")}
                 :Edit           {:screen (nav-wrapper edit-incident-form "Create/Edit Incident")}})

; TODO: take events / functions to dynamically add screens (e.g. during startup)
(defn drawer-navigator-inst []
  (drawer-navigator (clj->js tab-router) (clj->js {:order            ["Index" "Preferences" "Students" "Classrooms" "Edit"]
                                                   :initialRouteName "Index"})))

; is this basically a redux-like reducer?
; https://github.com/react-community/react-navigation/blob/master/docs/guides/Redux-Integration.md
(defn get-state [action]
  (-> (drawer-navigator-inst)
      .-router
      (.getStateForAction action)))

(defonce tab-navigator
  (let [tni (drawer-navigator-inst)]
    (r/adapt-react-class tni)))
