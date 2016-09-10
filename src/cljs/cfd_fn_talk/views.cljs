(ns cfd-fn-talk.views
  (:require [re-frame.core :as re-frame]
            [cfd-fn-talk.db :as db]
            [cljsjs.firebase]))

(defn card [title]
  [:div.column
   [:p.notification.is-warning title]
   [:p.notification.is-warning.purple
    {:id       (gensym (str (name title) "-"))
     :on-click (fn [e] (prn (-> e .-target .-id)))}
    "$200"]
   [:p.notification.is-warning.purple
    {:id       (gensym (str (name title) "-"))
     :on-click (fn [e] (prn (-> e .-target .-id)))}
    "$400"]
   [:p.notification.is-warning.purple
    {:id       (gensym (str (name title) "-"))
     :on-click (fn [e] (prn (-> e .-target .-id)))}
    "$800"]
   [:p.notification.is-warning.purple
    {:id       (gensym (str (name title) "-"))
     :on-click (fn [e] (prn (-> e .-target .-id)))}
    "$1000"]])

(defn main-panel []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:section
       [:h1.title @name]
       [:div.columns
        (for [c (:categories db/default-db)]
          ^{:key c}
          [card c])]])))
