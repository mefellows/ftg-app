(ns yimp.core.test-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [yimp.core-test]))

(doo-tests 'yimp.core-test)
