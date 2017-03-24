(ns re-navigate.shared.screens.incidents
  (:require [re-frame.core :as rf]
            [re-navigate.shared.ui :as ui]
            [re-navigate.shared.components.incident-list :refer [incident-list-view]]
            [re-navigate.shared.styles :refer [styles]]
            [clojure.walk :refer [keywordize-keys]]
            [print.foo :as pf :include-macros true]))

(defn incidents []
   (fn []
      (let [incidents (rf/subscribe [:incidents])
            loading (rf/subscribe [:sync])]
            [ui/view
              [incident-list-view @incidents @loading]])))
