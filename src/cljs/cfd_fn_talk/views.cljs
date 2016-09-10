(ns cfd-fn-talk.views
  (:require [re-frame.core :as re-frame]
            [cfd-fn-talk.db :as db]
            [cljsjs.firebase]
            [cfd-fn-talk.config :refer [ref-for-path firebase-db-ref]]))

(defn card-opts [title index]
  (let [name (str (name title) "-" index)]
    {:id       name
     :on-click (fn [e]
                 (let [path (ref-for-path (str "jeopardy/" name))]
                   (.set path
                         (clj->js {:taken false})))
                 (prn (-> e .-target .-id)))}))

(defn card-contents [title index amount]
  [:p.notification.is-warning.purple
   (card-opts title index) amount])

(defn card [title]
  (let [amounts ["$200" "$400" "$800" "$1000"]]
    [:div.column
     [:p.notification.is-warning title]
     (map-indexed (fn [index amount]
                    ^{:key index}
                    [card-contents title index amount]) amounts)]))

(defn main-panel []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:section
       [:h1.title @name]
       [:div.columns
        (for [c (:categories db/default-db)]
          ^{:key c}
          [card c])]])))
