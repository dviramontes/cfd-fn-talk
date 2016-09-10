(ns cfd-fn-talk.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [cfd-fn-talk.core-test]))

(doo-tests 'cfd-fn-talk.core-test)
