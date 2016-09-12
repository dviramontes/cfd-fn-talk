(ns cfd-fn-talk.views
  (:require cljsjs.firebase
            cljsjs.jquery
            [re-frame.core :as re-frame]
            [cfd-fn-talk.db :as db]
            [cfd-fn-talk.config :refer [firebase-db-ref]]
            [reagent.core :as reagent :refer [atom]]))

(defn ref-for-path
  "Returns a Firebase ref for the node at the given
  path string relative to firebase-db-ref.
  -- Tyler Perkins"
  [rel-path]
  (.child firebase-db-ref rel-path))

(defn card-opts [title index]
  (let [name (str (name title) "-" index)
        read-ref (ref-for-path (str "jeopardy/" name))
        _ (.on read-ref "value"
               (fn [snapshot]
                 (let [snapshot->clj (-> snapshot .val (js->clj :keywordize-keys true))]
                   (re-frame/dispatch [:update-game-state
                                       (assoc
                                         (or snapshot->clj {:taken false})
                                         :card name)]))))]
    {:id       name
     :on-click (fn [e]
                 (let [id (js/$ (str "#" (-> e .-target .-id)))
                       card-state (.data id "taken")
                       write-ref (ref-for-path (str "jeopardy/" name))]
                   (.set write-ref
                         (clj->js {:taken (not card-state)}))))}))

(defn card-contents [title amount]
  [:p.notification.is-warning.purple
   (card-opts title amount) (str "$" amount)])

(defn card [title]
  (let [amounts [200 400 800 1000]]
    [:div.column
     [:p.notification.is-warning title]
     (map (fn [amount]
            ^{:key (gensym "card-")}
            [card-contents title amount]) amounts)]))

(defn main-panel []
  (let [name (re-frame/subscribe [:name])
        game-state (re-frame/subscribe [:game-state])]
    (reagent/create-class
      {:reagent-render
       (fn []
         (when-not (empty? @game-state)
           (doseq [g @game-state]
             (let [id (js/$ (str "#" (:card g)))]
               (.data id "taken" (:taken g))
               (if (:taken g)
                 (.fadeTo id "fast" 0.5)
                 (.fadeTo id "fast" 1)))))
         [:section
          [:h1.title @name]
          [:div.columns
           (for [c (:categories db/default-db)]
             ^{:key c} [card c])]])})))
