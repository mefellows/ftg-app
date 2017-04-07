(ns re-navigate.shared.components.incident-list
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

(defn render-incident-row [nav]
  (fn [{:keys [summary id] :as incident}]
    (js/console.log "render-incident-row" nav incident)
    (let [date (new js/Date (:start_time incident))]
      [ui/touchable-highlight {:style       (:listview-row styles)
                               :on-press    (fn []
                                              (rf/dispatch-sync [:incident-load id])
                                              (-> nav (.navigate "Edit")))
                               :underlay-color "#efefef"
                               :active-opacity .9}
        [ui/view {:style       (:listview-row styles)}
          [ui/view {:style (:listview-rowcontent styles)}
              [ui/text {:style (:listview-rowcontent-attribute styles)}
                  (str (.getUTCDate date) "/" (+ 1(.getUTCMonth date)) " " (.getUTCFullYear date))]
              [ui/text {:style (:listview-rowcontent-inner styles)}
                summary]]
          [ui/view {:style (:listview-rowaction styles)}
            [ui/text {} " > "]]]])))

(defn footer [loading?]
  (when loading?
    [ui/view
     {:style (:listview-row-footer styles)}
     [ui/activity-indicator
      {:style (:indicator styles)}]]))

; TODO: add touchable highlight and click-through
(defn incident-list [nav incidents loading?]
  (if (not-empty incidents)
    (let []
       [ui/list-view (merge
                       {:dataSource    (ds/clone-with-rows list-view-ds incidents)
                        :render-row    (comp r/as-element (render-incident-row nav) u/js->cljk)
                        :style         (merge-with (:container styles) {})
                        :render-footer (comp r/as-element (partial footer loading?))}
                       {})])
   [ui/scroll
    {:style (:container styles)}
    [ui/view
     {:style (:listview-rowcontent styles)}
     [ui/text {}
"No incidents huh? You must have a nice school!"]]]))

(defn incident-list-view [nav incidents loading?]
  (let []
    [ui/view {}
      [ui/scroll {:style (merge-with (:listview-row styles) (:first-item styles))}
        [incident-list nav incidents loading?]]
      (let [component (ui/floating-action-button (fn []
                        (rf/dispatch-sync [:clear-current-incident])
                        (-> nav (.navigate "Edit"))))]
       [component
         [ui/text {:style {:font-size 24
                             :font-weight "400"
                             :color "#FFF"}}
                             "+"]])]))
