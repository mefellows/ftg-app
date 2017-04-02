(ns re-navigate.shared.screens.incidents
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [re-navigate.shared.ui :as ui]
            [re-navigate.shared.components.incident-list :refer [incident-list-view]]
            [re-navigate.shared.styles :refer [styles]]
            [clojure.walk :refer [keywordize-keys]]
            [print.foo :as pf :include-macros true]))

(defn incidents []
   (fn [navigation]
      (let [incidents (rf/subscribe [:incidents])
            this (r/current-component)
            props (r/props this)
            loading (rf/subscribe [:sync])]
            [ui/view {:flex 1
                      :flex-direction "column"}
              ; Header: button + title
              [ui/view {:border-bottom-width 0
                        :border-bottom-color "#aaaaaa"
                        :flex .4
                        :flex-direction "row"
                        :background-color "#eeeeee"
                        :margin-top 40
                        :padding-top 10
                        :margin-bottom 40}
                  [ui/view {:flex 1
                            :text-align-vertical "center"
                            :height 18}
                    [ui/material-icon-button {:name "menu"
                                              :background-color "#eee"
                                              :color "#aaaaaa"
                                              :border-radius 0
                                              :on-press (fn []
                                                (js/console.log "drawer press?" navigation)
                                                (-> navigation (.navigate "DrawerOpen")))}]]
                  [ui/view {:flex 7 :align-items "center" :height 18}
                    [ui/text {:text-align "center"
                            }
                             "This is the title"]]
                 [ui/view {:flex 1
                           :text-align-vertical "center"
                           :height 18}
                   [ui/material-icon-button {:name "refresh"
                                             :background-color "#eee"
                                             :color "#aaaaaa"
                                             :border-radius 0
                                             :on-press (fn []
                                               (js/console.log "synchronise")
                                               (rf/dispatch [:synchronise]))}]]
                             ]
              [ui/view {:flex 9}
                [incident-list-view @incidents @loading]]])))
