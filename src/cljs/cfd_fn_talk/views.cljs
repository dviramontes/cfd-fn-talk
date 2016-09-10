(ns cfd-fn-talk.views
  (:require [re-frame.core :as re-frame]
            [cfd-fn-talk.db :as db]
            [cljsjs.firebase]
            [cfd-fn-talk.config :refer [ref-for-path firebase-db-ref]]))

(defn card-opts [title index]
  (let [name (str (name title) "-" index)
        read-path (ref-for-path (str "jeopardy/" name))
        _ (.on read-path "value"
               (fn [snapshot]
                 (let [snapshot->clj (-> snapshot .val (js->clj :keywordize-keys true))]
                   (prn (assoc (or snapshot->clj {:taken false})
                          :card name)))))]
    {:id       name
     :on-click (fn [_]
                 (let [write-path (ref-for-path (str "jeopardy/" name))]
                   (.set write-path (clj->js {:taken false}))))}))

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
