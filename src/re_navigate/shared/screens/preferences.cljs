(ns re-navigate.shared.screens.preferences
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [re-navigate.shared.ui :as ui]
            [re-navigate.shared.styles :refer [styles]]
            [re-navigate.shared.components.preference-list :refer [preference-list]]
            [clojure.walk :refer [keywordize-keys]]))

(defn filtered-preferences [preferences action]
  (->> (keywordize-keys preferences)
    (remove #(not= (:type %1) action))))

(defn preferences []
   (fn [nav]
      (let [preferences (rf/subscribe [:preferences])
            this (r/current-component)
            props (r/props this)
            loading (rf/subscribe [:sync])]
            [ui/view {:flex 1
                      :flex-direction "column"}
              [ui/header nav "Incidents"]
              [ui/view {:flex 9}
                [preference-list @preferences @loading]]])))
