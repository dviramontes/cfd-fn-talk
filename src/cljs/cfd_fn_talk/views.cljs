(ns cfd-fn-talk.views
  (:require cljsjs.firebase
            cljsjs.jquery
            [re-frame.core :as re-frame]
            [cfd-fn-talk.db :as db]
            [cfd-fn-talk.firebase :refer [firebase-db-ref ref-for-path]]
            [reagent.core :as reagent :refer [atom]]))

(defn card-opts [title index]
  (let [name (str (name title) "-" index)
        read-ref (ref-for-path (str "game-state/" name))
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
                       write-ref (ref-for-path (str "game-state/" name))]
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
  (let [presence-ref (ref-for-path "presence")
        _ (.on presence-ref "value"
               (fn [snapshot]
                 (let [snapshot->clj (-> snapshot .val (js->clj :keywordize-keys true))]
                   (re-frame/dispatch [:set-firebase-player-count snapshot->clj]))))
        name (re-frame/subscribe [:name])
        game-state (re-frame/subscribe [:game-state])
        player-name (re-frame/subscribe [:player-name])
        player-count (re-frame/subscribe [:player-count])]
    (reagent/create-class
      {:component-did-mount
       (fn []
         (when (nil? @player-name)
           (re-frame/dispatch [:set-firebase-player-presence])))
       :reagent-render
       (fn []
         (when-not (empty? @game-state)
           (doseq [g @game-state]
             (let [id (js/$ (str "#" (:card g)))]
               (.data id "taken" (:taken g))
               (if (:taken g)
                 (.fadeTo id "fast" 0.25)
                 (.fadeTo id "fast" 1)))))
         [:section
          [:h1.title @name]
          [:div.columns
           (for [c (:categories db/default-db)]
             ^{:key c} [card c])]
          [:button.btn.btn-lg.btn-danger
           {:on-click #(let [reset-ref (ref-for-path "game-state")]
                        (.remove reset-ref (fn [e]
                                             (if e
                                               (prn e)
                                               (prn "reset db success")))))}

           "reset board!"]
          [:h3.player-name
           (str "player name: " @player-name " | # of players: " @player-count)]])})))