(ns re-navigate.shared.main
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [re-navigate.events]
            [re-navigate.shared.screens.edit-incident :refer [edit-incident-form]]
            [re-navigate.shared.screens.incidents :refer [incidents]]
            [re-navigate.shared.components.navigation :refer [tab-navigator]]
            [clojure.data :as d]
            [re-navigate.shared.ui :refer [app-registry text scroll image view md-icon-toggle md-button md-switch theme floating-action-button]]
            [re-navigate.subs]))

(js* "/* @flow */")

(defn start []
  (let [nav-state (subscribe [:nav/tab-state])]
    (fn []
      [tab-navigator])))

(defn init []
  (dispatch-sync [:initialize-db])
  (dispatch-sync [:load-teachers])
  (.registerComponent app-registry "ReNavigate" #(r/reactify-component start))
  (dispatch [:load-students])
  (dispatch [:load-classrooms])
  (dispatch [:load-preferences])
  (dispatch [:load-incidents]))
