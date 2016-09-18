(ns cfd-fn-talk.handlers
  (:require
    cljsjs.chance
    [re-frame.core :as re-frame]
    [cfd-fn-talk.db :as db]
    [cfd-fn-talk.firebase :refer [am-i-online? ref-for-path]]))

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

(re-frame/reg-event-db
  :set-firebase-player-presence
  (re-frame/path [:player-name])
  (fn [_ [_ _]]
    (let [gen-user-id (.string js/chance #js {:length 3 :pool "fun123"})
          am-i-online-ref am-i-online?
          player-ref (ref-for-path (str "presence/" gen-user-id))
          _ (.on am-i-online-ref "value"
                 (fn [snapshot]
                   (when (.val snapshot)
                     (do
                       (-> player-ref
                           .onDisconnect
                           .remove)
                       (.set player-ref true)))))]
      gen-user-id)))

(re-frame/reg-event-db
  :set-firebase-player-count
  (re-frame/path [:player-count])
  (fn [_ [_ players-state]]
    (-> players-state
        keys
        count)))