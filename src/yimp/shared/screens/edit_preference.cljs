(ns yimp.shared.screens.edit-preference
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [clojure.walk :refer [keywordize-keys]]
            [yimp.shared.styles :refer [styles]]
            [clojure.string :as str]
            [yimp.shared.ui :as ui]))

(defn valid-form? [props]
  (let [validation-result (.validate (-> props
                                         (aget "refs")
                                         (aget "form")))]
  (empty? (js->clj (aget validation-result "errors")))))

(defn on-submit [props preference nav]
  (when (valid-form? props)
   (let [value (-> preference js->clj keywordize-keys)]
         (rf/dispatch [:save-preference value])
         (-> nav (.navigate "Preferences")))))

(def t (js/require "tcomb-form-native"))
(def Form (r/adapt-react-class (.-Form t.form)))
(def s (.-stylesheet (.-Form t.form)))
(def _ (js/require "lodash"))

; See https://github.com/gcanti/tcomb-form-native/blob/master/lib/stylesheets/bootstrap.js
; for more you can modify.
(def form-style
  (let [stylesheet (_.cloneDeep s)
  updated (-> stylesheet
    (.-controlLabel)
    (.-normal)
    (aset "color" "#444444"))]
    stylesheet))

 (def options
   {:stylesheet form-style
    :order [:id :type :value]
    :fields {:id {:hidden true}}})

(defn Types []
  (->> [{:action "Action"} {:summary "Summary" } {:location "Location"} {:contact "Contact"}]
         (flatten)
         (into {})
         (clj->js)
         (t.enums)))

(defn preference [new?]
  (let [obj {:type (Types)
             :value t.String}]
    (if-not new?
      (t.struct (clj->js (assoc obj :id t.Number)))
      (t.struct (clj->js obj)))))

(def edit-preference
  (fn [nav]
   (r/create-class
     {:reagent-render
      (fn [props]
        (this-as this
          (let [current-preference (rf/subscribe [:current-preference])]
               (js/console.log "Value => " (clj->js @current-preference))
               [ui/view {:flex 1 :flex-direction "column" :padding-bottom 20}
                 [ui/header nav "Edit Preference"]
                 [ui/view {:flex 9}
                   [ui/scroll {:style (:scroll-container styles)}
                    [Form {:ref "form"
                           :type (preference (nil? (:id @current-preference)))
                           :value (clj->js @current-preference)
                           :options options
                           :on-change #(rf/dispatch [:set-current-preference %1])}]
                    [ui/touchable-highlight {
                      :on-press    #(on-submit this @current-preference nav)
                      :style       {:background-color (ui/colour :orange800) :padding 10 :margin-bottom 20 :border-radius 5}
                      :is-disabled #(not (valid-form? props))}
                     [ui/text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Save Preference"]]]]])))})))
