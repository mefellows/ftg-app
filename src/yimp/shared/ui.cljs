(ns yimp.shared.ui
  (:require [reagent.core :as r]
            [yimp.shared.colours :refer [colours]]
            [clojure.string :as str]
            [yimp.utils :as u]
            [medley.core :as m]
            [re-frame.core :as rf]
            [camel-snake-kebab.core :as cs :include-macros true]))

(enable-console-print!)

(def react-native (js/require "react-native"))
(def platform (.-Platform react-native))

(defn get-platform
  []
  (.-OS platform))

(defn ios?
  []
  (= "ios" (get-platform)))

(defn android?
  []
  (= "android" (get-platform)))

(def colour colours)
(def text (r/adapt-react-class (.-Text react-native)))
(def view (r/adapt-react-class (.-View react-native)))
(def scroll (r/adapt-react-class (.-ScrollView react-native)))
(def Image (.-Image react-native))
(def image (r/adapt-react-class Image))
(def ActivityIndicator (.-ActivityIndicator react-native))
(def activity-indicator (r/adapt-react-class ActivityIndicator))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight react-native)))
(def list-view (r/adapt-react-class (.-ListView react-native)))
(def app-registry (.-AppRegistry react-native))
(defn alert [title] (.alert (.-Alert react-native) title))

; stylesheet

(def EStyleSheet (aget (js/require "react-native-extended-stylesheet") "default"))

(defn build-stylesheet
  ([] (build-stylesheet {}))
  ([vals]
   (.build EStyleSheet (clj->js vals))))

(defn create-stylesheet [styles]
  (-> (m/map-vals #(u/apply-if map? (partial m/map-keys cs/->camelCase) %) styles)
      clj->js
      (->> (.create EStyleSheet))
      u/obj->hash-map))

(build-stylesheet)

;; icon
(def font-awesome (js/require "react-native-vector-icons/FontAwesome"))
(def icon (r/adapt-react-class font-awesome))
(def icon-button (r/adapt-react-class (.-Button font-awesome)))

(def material-icons (js/require "react-native-vector-icons/MaterialIcons"))
(def material-icon (r/adapt-react-class material-icons))
(def material-icon-button (r/adapt-react-class (.-Button material-icons)))

;; app intro
(def app-intro (r/adapt-react-class (.-default (js/require "react-native-app-intro"))))

; Components
(def material-kit (js/require "react-native-material-kit"))
(def MDButton (.-MKButton material-kit))
(def MDIconToggle (.-MKIconToggle material-kit))
(def MDSwitch (.-MKSwitch material-kit))
(def get-theme (.-getTheme material-kit))
(def theme material-kit)
(def md-button (r/adapt-react-class MDButton))
(def md-icon-toggle (r/adapt-react-class MDIconToggle))
(def md-switch (r/adapt-react-class MDSwitch))
(def coloured-button (.coloredButton MDButton))

(defn floating-action-button
  [handler]
  (-> MDButton
      (.accentColoredFlatButton)
      (.withBackgroundColor (:teal400 colours))
      (.withMaskColor "transparent")
      (.withStyle #js {:position "absolute"
                       :right 16
                       :bottom 16
                       :width 50
                       :height 50
                       :borderRadius 28})
      (.withOnPress handler)
      (.build)
      (r/adapt-react-class)))

; Header: button + title
(defn header [navigation title]
  (let [nav-screen (rf/subscribe [:nav-screen])]
    [view {:border-bottom-width 1
          :border-bottom-color "#eeeeee"
          :color "#fff"
          :flex .6
          :flex-direction "row"
          :background-color (colour :orange800)
          :margin-top (if (android?) 0 40)
          :padding-top (if (android?) 15 15)
          :margin-bottom 0}
       [view {:flex (if (android?) 1 1.5) :text-align-vertical "center" :height 26 }
         [material-icon-button {:name "menu"
                                :background-color (colour :orange800)
                                :color "#fff"
                                :size 24
                                :border-radius 0
                                :height 26
                                :margin-left (if (android?) 0 10)
                                :on-press #(-> navigation (.navigate "DrawerOpen"))}]]
       [view {:flex (if (android?) 7 7) :align-items "center" :text-align-vertical "center" :height 26}
         [text {:style {:text-align "center" :color "#fff" :font-size 18 :font-weight "600"}}
           title]]
       [view {:flex (if (android?) 1 1.5) :text-align-vertical "center" :height 26}
         [material-icon-button {:name "refresh"
                                :background-color (colour :orange800)
                                :color "#fff"
                                :border-radius 0
                                :size 24
                                :margin-left (if (android?) 0 10)
                                :on-press #(rf/dispatch [:synchronise])}]]]))
