(ns cfd-fn-talk.handlers
  (:require
    cljsjs.chance
    [re-frame.core :as re-frame]
    [cfd-fn-talk.db :as db]
    [cfd-fn-talk.firebase :refer [am-i-online? ref-for-path]]
    [ajax.core :refer [GET]]))

(re-frame/reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(re-frame/reg-event-db
  :update-game-state
  (re-frame/path [:game-state])
  (fn [game [_ state]]
    (if (= (count game) 24)
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

(re-frame/reg-event-db
  :set-most-recent-card
  (re-frame/path [:most-recent-card])
  (fn [_ [_ most-recent-state]]
    most-recent-state))

(re-frame/reg-event-db
  :get-canvas-card-data
  (fn [db [_ card-name]]
    (let [format ".html"
          found (get (:card-pointers db) card-name)
          name (str found format)]
      (if found
        (GET name
             {:handler       #(re-frame/dispatch [:set-card-in-view %1])
              :error-handler #(re-frame/dispatch [:set-card-in-view nil])})
        (re-frame/dispatch [:set-card-in-view nil]))
      db)))

(re-frame/reg-event-db
  :set-card-in-view
  (re-frame/path [:card-in-view])
  (fn [_ [_ card-data]]
    card-data))

(re-frame/reg-event-db
  :set-card-in-view-locked
  (re-frame/path [:card-in-view-locked])
  (fn [_ [_ state]]
    state))

(re-frame/reg-event-db
  :reset-game-state
  (re-frame/path [:game-state])
  (fn [_ [_ _]]
    (let [reset-ref (ref-for-path "game-state")]
      (.remove reset-ref (fn [e]
                           (if e
                             (prn e)
                             (prn "reset db success")))))
    []))