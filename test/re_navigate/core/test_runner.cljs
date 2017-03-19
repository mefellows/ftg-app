(ns re-navigate.core.test-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [re-navigate.core-test]))

(doo-tests 're-navigate.core-test)
