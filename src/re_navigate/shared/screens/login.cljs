(ns re-navigate.shared.screens.login
  (:require [re-frame.core :as rf :refer [subscribe]]
            [reagent.core :as r]
            [re-navigate.shared.styles :refer [styles]]
            [clojure.walk :refer [keywordize-keys]]
            [print.foo :as pf :include-macros true]
            [re-navigate.shared.ui :as ui]
            [re-navigate.subs]
            [clojure.string :as str]))

(def t (js/require "tcomb-form-native"))
(def Form (r/adapt-react-class (.-Form t.form)))
(def s (.-stylesheet (.-Form t.form)))
(def _ (js/require "lodash"))

(defn valid-form? [props]
  (let [validation-result (.validate (-> props
    (aget "refs")
    (aget "form")))]
    (empty? (js->clj (aget validation-result "errors")))))

(defn on-submit [props user]
  (when (valid-form? props)
    (let [value (-> user js->clj keywordize-keys)]
      (rf/dispatch [:login value]))))

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
    :order [:username :password]
    :fields {:password {:secure-text-entry true}}})

(defn extract-teacher-enum [teacher]
  (let [] {(:id teacher) (str (:first_name teacher) " " (:last_name teacher))}))

(defn Teachers []
  (let [teachers (rf/subscribe [:teachers])]
    (->> @teachers
         (filter #(let [] (> (:id %1) 0)))
         (mapv extract-teacher-enum)
         (flatten)
         (into {})
         (clj->js)
         (t.enums))))

(defn User []
  (let [obj {:username (Teachers)
             :password t.String}]
               (t.struct (clj->js obj))))

(defn login []
 (let [value (subscribe [:login-form])
       this (r/current-component)]
    [ui/view {:style (:form-container styles)}
     [ui/scroll
      {:style (:scroll-container styles)}
      [Form {:ref "form"
             :type (User)
             :value @value
             :options options
             :on-change #(rf/dispatch [:set-login-form %1])}]
      [ui/touchable-highlight {:on-press    #(on-submit this @value)
                               :style       {:background-color (ui/colour :orange800) :padding 10 :margin-bottom 20 :border-radius 5}}
        [ui/text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Login"]]]]))
