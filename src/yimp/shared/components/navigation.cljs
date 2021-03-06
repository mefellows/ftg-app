(ns yimp.shared.components.navigation
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [clojure.data :as d]
            [yimp.shared.screens.edit-incident :refer [edit-incident]]
            [yimp.shared.screens.edit-student :refer [edit-student]]
            [yimp.shared.screens.incidents :refer [incidents]]
            [yimp.shared.screens.students :refer [students]]
            [yimp.shared.screens.classrooms :refer [classrooms]]
            [yimp.shared.screens.preferences :refer [preferences]]
            [yimp.shared.screens.edit-preference :refer [edit-preference]]
            [yimp.shared.ui :refer [app-registry text scroll image view md-icon-toggle md-button md-switch theme]]))

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
                 :Student       {:screen (nav-wrapper edit-student "Create/Edit Student")}
                 :Preference     {:screen (nav-wrapper edit-preference "Create/Edit Preference")}
                 :Edit           {:screen (nav-wrapper edit-incident "Create/Edit Incident")}})

; TODO: take events / functions to dynamically add screens (e.g. during startup)
; TODO: add stack navigator on top of these screens - e.g. edit incident, preference etc.
(defn drawer-navigator-inst []
  (drawer-navigator (clj->js tab-router) (clj->js {:order            ["Index" "Preferences" "Students" "Edit" "Preference" "Student"]
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
