(ns cfd-fn-talk.handlers
  (:require [re-frame.core :as re-frame]
            [cfd-fn-talk.db :as db]))

(re-frame/reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(re-frame/reg-event-db
  :update-game-state
  (re-frame/path [:game-state])
  (fn [game [_ state]]
    (if (= (count game) 32)
      (let [index (.indexOf (map :card game) (:card state))]
        (assoc game index state))
      (conj game state))))