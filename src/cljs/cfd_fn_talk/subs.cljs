(ns cfd-fn-talk.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
  :game-state
  (fn [db]
    (:game-state db)))

(re-frame/reg-sub
  :player-name
  (fn [db]
    (:player-name db)))

(re-frame/reg-sub
  :player-count
  (fn [db]
    (:player-count db)))

(re-frame/reg-sub
  :most-recent-card
  (fn [db]
    (:most-recent-card db)))

(re-frame/reg-sub
  :card-in-view
  (fn [db]
    (:card-in-view db)))

(re-frame/reg-sub
  :card-in-view-locked
  (fn [db]
    (:card-in-view-locked db)))