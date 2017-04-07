(ns re-navigate.shared.components.student-list
  (:require-macros [natal-shell.data-source :as ds])
  (:require [re-navigate.shared.ui :as ui]
            [re-navigate.shared.styles :refer [styles]]
            [re-navigate.utils :as u]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [print.foo :as pf :include-macros true]
            [re-navigate.config :as cfg]
            [re-navigate.config :refer [app-name]]))

(def list-view-ds (ds/data-source {:rowHasChanged #(not= %1 %2)}))

(defn render-student-row [nav]
  (fn [{:keys [first_name last_name id] :as student}]
  [ui/touchable-highlight {:style       (:listview-row styles)
                           :on-press    (fn []
                                          (rf/dispatch-sync [:student-load id])
                                          ; (rf/dispatch [:set-nav "student"]))
                                          (-> nav (.navigate "Student")))
                           :underlay-color "#efefef"
                           :active-opacity .9}
    [ui/view {:style       (:listview-row styles)}
      [ui/view {:style (:listview-rowcontent styles)}
          [ui/text {}
            (str first_name " " last_name)]]
      [ui/view {:style (:listview-rowaction styles)}
        [ui/text {} " > "]]]]))

(defn footer [loading?]
  (when loading?
    [ui/view
     {:style (:listview-row-footer styles)}
     [ui/activity-indicator
      {:style (:indicator styles)}]]))

(defn student-list [nav students loading?]
  (if (not-empty students)
    (let []
      [ui/scroll
       {:style (:listview-row styles)}
       [ui/list-view (merge
                       {:dataSource    (ds/clone-with-rows list-view-ds students)
                        :render-row    (comp r/as-element (render-student-row nav) u/js->cljk)
                        :style         (merge-with (:container styles) (:first-item styles))
                        :render-footer (comp r/as-element (partial footer loading?))}
                       {})]])
   [ui/scroll
    {:style (:container styles)}
    [ui/view
     {:style (:listview-rowcontent styles)}
     [ui/text {}
       "No students huh? You must have a _boring_ school!"]]]))
