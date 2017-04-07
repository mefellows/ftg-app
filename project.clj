(defproject re-navigate "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.9.0-alpha10"]
                           [org.clojure/clojurescript "1.9.198"]
                           [binaryage/devtools "0.9.0"]
                           [print-foo-cljs "2.0.0"]
                           [camel-snake-kebab "0.4.0"]
                           [medley "0.8.1"]
                           [com.rpl/specter "0.10.0"]
                           [funcool/tubax "0.2.0"]
                           [natal-shell "0.2.1"]
                           [reagent "0.6.0" :exclusions [cljsjs/react cljsjs/react-dom cljsjs/react-dom-server]]
                           [re-frame "0.8.0"]]
            :plugins [[lein-cljsbuild "1.1.4"]
            [lein-doo "0.1.6"]
                      [lein-figwheel "0.5.8"]]
            :clean-targets ["target/" "index.ios.js" "index.android.js"]
            :aliases {"prod-build" ^{:doc "Recompile code with prod profile."}
                                   ["do" "clean"
                                    ["with-profile" "prod" "cljsbuild" "once" ]]}
            :profiles {:dev  {:dependencies [[figwheel-sidecar "0.5.8"]
                                             [com.cemerick/piggieback "0.2.1"]]
                              :source-paths ["src" "env/dev"]
                              :doo {:build "test"}
                              :cljsbuild    {:builds [{:id           "ios"
                                                       :source-paths ["src" "env/dev"]
                                                       :figwheel     true
                                                       :compiler     {:output-to     "target/ios/not-used.js"
                                                                      :main          "env.ios.main"
                                                                      ;:preloads      [devtools.preload]
                                                                      :output-dir    "target/ios"
                                                                      :optimizations :none}}
                                                      {:id            "test"
                                                       :source-paths ["src" "test"]
                                                                      :compiler {:output-to "target/resources/test.js"
                                                                                 :output-dir "target/test/"
                                                                                 :main re-navigate.core.test-runner
                                                                                 :optimizations :none
                                                                                 :pretty-print true
                                                                                 :source-map false}}
                                                      {:id           "android"
                                                       :source-paths ["src" "env/dev"]
                                                       :figwheel     true
                                                       :compiler     {:output-to     "target/android/not-used.js"
                                                                      :main          "env.android.main"
                                                                      :output-dir    "target/android"
                                                                      :optimizations :none}}]}
                              :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}
                       :prod {:cljsbuild {:builds [{:id           "ios"
                                                    :source-paths ["src" "env/prod"]
                                                    :compiler     {:output-to          "index.ios.js"
                                                                   :main               "env.ios.main"
                                                                   :output-dir         "target/ios"
                                                                   :static-fns         true
                                                                   :optimize-constants true
                                                                   :optimizations      :simple
                                                                   :closure-defines    {"goog.DEBUG" false}}}
                                                   {:id           "android"
                                                    :source-paths ["src" "env/prod"]
                                                    :compiler     {:output-to          "index.android.js"
                                                                   :main               "env.android.main"
                                                                   :output-dir         "target/android"
                                                                   :static-fns         true
                                                                   :optimize-constants true
                                                                   :optimizations      :simple
                                                                   :closure-defines    {"goog.DEBUG" false}}}]}}})
