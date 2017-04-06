(ns re-navigate.shared.screens.classrooms
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [re-navigate.shared.ui :as ui]
            [re-navigate.shared.styles :refer [styles]]
            [re-navigate.shared.components.classroom-list :refer [classroom-list]]))

(defn classrooms []
   (fn [nav]
      (let [classrooms (rf/subscribe [:classrooms])
            this (r/current-component)
            props (r/props this)
            loading (rf/subscribe [:sync])]
            [ui/view {:flex 1
                      :flex-direction "column"}
              [ui/header nav "Classrooms"]
              [ui/view {:flex 9}
                [classroom-list @classrooms @loading]]])))
