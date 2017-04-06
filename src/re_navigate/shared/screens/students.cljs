(ns re-navigate.shared.screens.students
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [re-navigate.shared.ui :as ui]
            [re-navigate.shared.styles :refer [styles]]
            [re-navigate.shared.components.student-list :refer [student-list]]))

(defn students []
   (fn [nav]
      (let [students (rf/subscribe [:students])
            this (r/current-component)
            props (r/props this)
            loading (rf/subscribe [:sync])]
            [ui/view {:flex 1
                      :flex-direction "column"}
              [ui/header nav "Students"]
              [ui/view {:flex 9}
                [student-list (sort-by :first_name @students) @loading]]])))