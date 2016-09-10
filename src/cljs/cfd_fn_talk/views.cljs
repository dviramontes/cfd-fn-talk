(ns cfd-fn-talk.views
  (:require [re-frame.core :as re-frame]
            [cfd-fn-talk.db :as db]))

(defn card [title]
  [:div {:class "column"}
   [:p.notification.is-warning title]])

(defn main-panel []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:section
       [:h1.title @name]
       [:div {:class "columns"}
        (for [c (:categories db/default-db)]
          ^{:key (gensym "card-")}
          [card c])]])))
