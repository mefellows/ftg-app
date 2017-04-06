(ns re-navigate.shared.screens.incidents
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [re-navigate.shared.ui :as ui]
            [re-navigate.shared.components.incident-list :refer [incident-list-view]]
            [re-navigate.shared.styles :refer [styles]]))

(defn incidents []
   (fn [nav]
      (let [incidents (rf/subscribe [:incidents])
            this (r/current-component)
            props (r/props this)
            loading (rf/subscribe [:sync])]
            [ui/view {:flex 1
                      :flex-direction "column"}
              [ui/header nav "Incidents"]
              [ui/view {:flex 9}
                [incident-list-view nav @incidents @loading]]])))
