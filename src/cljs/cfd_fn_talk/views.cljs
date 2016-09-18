(ns cfd-fn-talk.views
  (:require cljsjs.firebase
            cljsjs.jquery
            [re-com.core :refer [modal-panel]]
            [re-frame.core :as re-frame]
            [cfd-fn-talk.db :as db]
            [cfd-fn-talk.firebase :refer [firebase-db-ref ref-for-path]]
            [reagent.core :as reagent :refer [atom]]))

(def show-modal? (atom false))

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
                       write-ref (ref-for-path (str "game-state/" name))
                       most-recent-write-ref (ref-for-path "game-state/most-recent")]
                   (.set most-recent-write-ref
                         (clj->js {name (not card-state)}))
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

(defn most-recent-card-btn [most-recent-card admin?]
  (fn []
    (let [most-recent (or @most-recent-card "{:card \"name\" :taken false}")]
      (when admin?
        [:button.btn.btn-lg.btn-info
         {:on-click #(reset! show-modal? true)}
         (str most-recent)]))))


(defn reset-game-state-btn [admin?]
  (fn []
    (when admin?
      [:section
       [:button.btn.btn-lg.btn-danger
        {:on-click #(let [reset-ref (ref-for-path "game-state")]
                     (.remove reset-ref (fn [e]
                                          (if e
                                            (prn e)
                                            (prn "reset db success")))))}
        "reset board!"]])))

(defn main-panel []
  (let [admin? (.getItem js/localStorage "jeopardy-admin")
        presence-ref (ref-for-path "presence")
        most-recent-ref (ref-for-path "game-state/most-recent")
        _ (.on presence-ref "value"
               (fn [snapshot]
                 (let [snapshot->clj (-> snapshot .val (js->clj :keywordize-keys true))]
                   (re-frame/dispatch [:set-firebase-player-count snapshot->clj]))))
        _ (.on most-recent-ref "value"
               (fn [snapshot]
                 (let [snapshot->clj (-> snapshot .val (js->clj :keywordize-keys true))]
                   (re-frame/dispatch [:set-most-recent-card snapshot->clj]))))
        name (re-frame/subscribe [:name])
        game-state (re-frame/subscribe [:game-state])
        player-name (re-frame/subscribe [:player-name])
        player-count (re-frame/subscribe [:player-count])
        most-recent-card (re-frame/subscribe [:most-recent-card])]
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
         [:div
          [:section.hero.is-large
           [:div.hero-head
            [:header.nav
             [:div.container.has-text-centered
              [:p.title.main-title @name]
              (when @show-modal?
                [modal-panel
                 :wrap-nicely? false
                 :backdrop-on-click #(reset! show-modal? false)
                 :child [:span "Please wait for 3 seconds" [:br] "(or click on backdrop)"]])]]]

           [:div.hero-body
            [:div.container
             [:p.title (str "player name: " @player-name)]]
            [:div.container.has-text-centered
             [:p.subtitle (str "# of players: " @player-count)]
             [:div.columns
              (for [c (:categories db/default-db)]
                ^{:key c} [card c])]]]

           [:div.hero-foot
            [:nav.tabs.is-boxed.is-fullwidth
             [:di.container
              [:ul
               [:li.is-active
                [reset-game-state-btn admin?]]
               [:li
                [most-recent-card-btn most-recent-card admin?]]
               [:li
                [:a "FP talk"]]
               [:li
                [:a "david viramontes"]]
               [:li
                [:a {:href "http://www.codefordenver.org/"} "CFD"]]]]]]]])})))