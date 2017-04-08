(ns yimp.shared.screens.preferences
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [yimp.shared.ui :as ui]
            [yimp.shared.styles :refer [styles]]
            [yimp.shared.components.preference-list :refer [preference-list]]
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
              [ui/header nav "Preferences"]
              [ui/view {:flex 9}
                 [ui/scroll {:style (:first-item styles)}
                   [ui/view {:style (:readonly-form styles)}
                    [ui/text {:style (:readonly-section-title styles)}
                       "Contacts"]]
                       [ui/view {:style (:readonly-container styles)}
                    [ui/text {:style (:readonly-value styles)}
                       "The following people will be notified daily at 5pm of all yard incidents"]]
                    [preference-list nav (filtered-preferences @preferences "contact") @loading]
                   [ui/view {:style (:readonly-form styles)}
                    [ui/text {:style (:readonly-section-title styles)}
                       "Summaries"]]
                       [ui/view {:style (:readonly-container styles)}
                    [ui/text {:style (:readonly-value styles)}
                       "Incident summaries for pre-population in yard incidents"]]
                    [preference-list nav (filtered-preferences @preferences "summary") @loading]
                   [ui/view {:style (:readonly-form styles)}
                    [ui/text {:style (:readonly-section-title styles)}
                       "Locations"]]
                       [ui/view {:style (:readonly-container styles)}
                    [ui/text {:style (:readonly-value styles)}
                       "Locations for pre-population in yard incidents"]]
                    [preference-list nav (filtered-preferences @preferences "location") @loading]
                    [ui/view {:style (:readonly-form styles)}
                     [ui/text {:style (:readonly-section-title styles)}
                        "Actions"]]
                        [ui/view {:style (:readonly-container styles)}
                     [ui/text {:style (:readonly-value styles)}
                        "Action taken for pre-population in yard incidents"]]
                        [preference-list nav (filtered-preferences @preferences "action") @loading]
                    ]]
                    (let [component (ui/floating-action-button (fn []
                                      (rf/dispatch-sync [:clear-current-preference])
                                      (-> nav (.navigate "Preference"))))]
                     [component
                       [ui/text {:style {:font-size 24
                                           :font-weight "400"
                                           :color "#FFF"}}
                                           "+"]])
                    ])))
