(ns re-navigate.shared.screens.preferences
  (:require [re-frame.core :as rf]
            [re-navigate.shared.ui :as ui]
            [re-navigate.shared.styles :refer [styles]]
            [clojure.walk :refer [keywordize-keys]]
            [print.foo :as pf :include-macros true]))

(defn filtered-preferences [preferences action]
  (->> (keywordize-keys preferences)
    (remove #(not= (:type %1) action))))
