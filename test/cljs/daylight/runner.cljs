(ns daylight.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [daylight.core-test]))

(doo-tests 'daylight.core-test)
