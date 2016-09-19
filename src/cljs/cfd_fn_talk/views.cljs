(ns cfd-fn-talk.views
  (:require cljsjs.firebase
            cljsjs.jquery
            [re-com.core :refer [modal-panel v-box]]
            [re-frame.core :as re-frame]
            [cfd-fn-talk.db :as db]
            [cfd-fn-talk.firebase :refer [firebase-db-ref ref-for-path]]
            [reagent.core :as reagent :refer [atom]]))

(def show-modal? (atom false))

(def card-data-cache (atom nil))

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
                 (reset! show-modal? true)
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
     (for [amount amounts]
       ^{:key (gensym "card-")}
       [card-contents title amount])]))

(defn most-recent-card-btn [most-recent-card]
  (fn []
    (let [most-recent (or @most-recent-card "{:card \"name\" :taken false}")]
      [:button.button.is-primary
       {:on-click #(reset! show-modal? true)}
       (str most-recent)])))

(defn reset-game-state-btn [admin?]
  (fn []
    (when admin?
      [:section
       [:button.button.is-danger
        {:on-click #(re-frame/dispatch [:reset-game-state])}
        "reset board!"]])))

(defn modal-component [most-recent-card]
  (fn []
    (let [name (-> @most-recent-card
                   keys
                   first)
          _ (re-frame/dispatch [:get-canvas-card-data name])
          card-data (re-frame/subscribe [:card-in-view])
          card-locked (re-frame/subscribe [:card-in-view-locked])]
      [v-box
       :justify :center
       :align :center
       :padding "2em"
       :children [[:div
                   [:button.button.is-primary.is-outlined
                    {:on-click #(do
                                 (reset! show-modal? false)
                                 (re-frame/dispatch [:set-card-in-view-locked false])
                                 (re-frame/dispatch [:set-card-in-view nil]))}
                    [:i.fa.fa-times]]
                   " "
                   [:button {:on-click #(re-frame/dispatch [:set-card-in-view-locked (not @card-locked)])
                             :class    (if @card-locked
                                         "button is-danger is-outlined"
                                         "button is-primary")}
                    [:i.fa.fa-lock]]
                   (if @card-locked
                     (do
                       ;; use cache
                       [:div.modal-card-title
                          {"dangerouslySetInnerHTML"
                           #js{:__html (or @card-data-cache "<h1>404</h1>")}}])
                     (do
                       ;; set cache
                       (reset! card-data-cache @card-data)
                       [:div.modal-card-title
                        {"dangerouslySetInnerHTML"
                         #js{:__html (or @card-data "<h1>404</h1>")}}]))]]])))

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
         [:section.hero.is-large
          [:div.hero-head
           [:header.nav
            [:div.container.has-text-centered
             [:p.title.main-title @name]]]]
          [:div.hero-body
           [:div.container
            (when @show-modal?
              [modal-panel
               :backdrop-opacity 0.9
               :class "modal"
               :backdrop-color "rebeccapurple"
               :wrap-nicely? false
               :backdrop-on-click #(do
                                    (reset! show-modal? false)
                                    (re-frame/dispatch [:set-card-in-view-locked false])
                                    (re-frame/dispatch [:set-card-in-view nil]))
               :child [modal-component most-recent-card]])]
           [:div.container
            [:p.title (str "player id: " @player-name)]]
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
               [:a {:href "#"} "CFD"]]
              [:li
               [:a "david viramontes"]]]]]]])})))