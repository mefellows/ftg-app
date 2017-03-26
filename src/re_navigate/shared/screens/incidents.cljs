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
            [ui/view
              ; Header: button + title
              [ui/view {:height 20
                        :background-color "#"
                        :border-bottom-width 1
                        :flex 10
                        :flex-direction "row"
                        :border-color "#aaaaaa"
                        :margin-top 40
                        :margin-bottom 40}
                  [ui/view {:flex 1
                            :text-align-vertical "center"
                            :height 20}
                    [ui/material-icon-button {:name "menu"
                                              :background-color "#ffffff"
                                              :color "#aaaaaa"
                                              :border-radius 0
                                              :on-press (fn []
                                                (js/console.log "drawer press?" navigation)
                                                ; (-> navigation (.dispatch (navigate-action "Settings"))))}]]
                                                (-> navigation (.navigate "DrawerOpen")))}]]
                                                ; (-> navigation (.navigate "Settings")))}]]
                  [ui/view {:flex 8 :align-items "center"}
                    [ui/text {:text-align "center"
                              ; :height 20
                            }
                             "This is the title"]]
                 [ui/view {:flex 1
                           :text-align-vertical "center"
                           :height 20}
                   [ui/material-icon-button {:name "refresh"
                                             :background-color "#ffffff"
                                             :color "#aaaaaa"
                                             :border-radius 0
                                             :on-press (fn []
                                               (js/console.log "synchronise")
                                               (rf/dispatch [:synchronise]))}]]
                             ]
              [ui/view
                [incident-list-view @incidents @loading]]])))
