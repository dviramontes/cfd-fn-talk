(ns cfd-fn-talk.handlers
    (:require [re-frame.core :as re-frame]
              [cfd-fn-talk.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))
