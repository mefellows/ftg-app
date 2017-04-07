(ns re-navigate.shared.screens.edit-student
  (:require-macros [natal-shell.data-source :as ds])
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [re-navigate.shared.styles :refer [styles]]
            [clojure.string :as str]
            [re-navigate.shared.ui :as ui]
            [re-navigate.shared.components.incident-list :refer [incident-list render-incident-row list-view-ds footer]]))

(def edit-student
  (fn [nav]
   (r/create-class
     {:reagent-render
      (fn [props]
        (let [student (rf/subscribe [:current-student])
              incidents (rf/subscribe [:current-student-incidents])
              classroom (rf/subscribe [:current-student-classroom])
              loading (rf/subscribe [:sync])
              date (new js/Date (:date_of_birth @student))]
              [ui/view {:flex 1 :flex-direction "column"}
                [ui/header nav "View Student"]
                [ui/view {:flex 9}
                  [ui/scroll {:style (:first-item styles)}
                   [ui/view {:style (:readonly-form styles)}
                     [ui/view {:style (:readonly-container styles)}
                      [ui/text {:style (:readonly-label styles)}
                         "Name"]
                      [ui/text {:style (:readonly-value styles)}
                         (str (:first_name @student) " " (:last_name @student))]]
                     [ui/view {:style (:readonly-container styles)}
                      [ui/text {:style (:readonly-label styles)}
                         "Gender"]
                      [ui/text {:style (:readonly-value styles)}
                         (:gender @student)]]
                     [ui/view {:style (:readonly-container styles)}
                      [ui/text {:style (:readonly-label styles)}
                         "Classroom"]
                      [ui/text {:style (:readonly-value styles)}
                         (:name @classroom)]]
                      [ui/text {:style (:readonly-section-title styles)}
                         "Incidents"]]
                      [incident-list nav @incidents @loading]]]]))})))
