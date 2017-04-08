(ns yimp.core-test
  (:require [cljs.test :refer-macros [deftest is testing async]]
            [yimp.utils :refer [foobar]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
