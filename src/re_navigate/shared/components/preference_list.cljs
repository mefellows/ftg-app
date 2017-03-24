(ns re-navigate.ios.components.preference-list
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

(defn submit [id]
  (rf/dispatch [:preference-load id] ))

(defn render-preference-row [{:keys [type value id] :as preference}]
  [ui/touchable-highlight {:style       (:listview-row styles)
                           :on-press    #(submit id)
                           :underlay-color "#efefef"
                           :active-opacity .9}
    [ui/view {:style       (:listview-row styles)}
      [ui/view {:style (:listview-rowcontent styles)}
          [ui/text {}
            (str value)]]
      [ui/view {:style (:listview-rowaction styles)}
        [ui/text {} " > "]]]])

(defn footer [loading?]
  (when loading?
    [ui/view
     {:style (:listview-row-footer styles)}
     [ui/activity-indicator
      {:style (:indicator styles)}]]))

(defn preference-list [preferences loading?]
  (if (not-empty preferences)
    (let []
       [ui/list-view (merge
                       {:dataSource    (ds/clone-with-rows list-view-ds preferences)
                        :render-row    (comp r/as-element render-preference-row u/js->cljk)
                        :render-footer (comp r/as-element (partial footer loading?))}
                       {})])
   [ui/scroll
    {:style (:container styles)}
    [ui/view
     {:style (:listview-rowcontent styles)}
     [ui/text {}
       "No preferences huh? Time to create some!"]]]))
