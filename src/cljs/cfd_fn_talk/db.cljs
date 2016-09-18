(ns cfd-fn-talk.db)

(def default-db
  {:name       "(λ jeopardy)"
   :categories #{:lazy/immutable
                 :lisp
                 :multicore
                 :divergent-thinking
                 :people-in-computing
                 :limits
                 :elixir/erlang
                 :messages-over-classes}
   :game-state []
   :player-name nil
   :player-count 0
   :most-recent-card nil
   :card-in-view nil
   :card-pointers {:lisp-200 "https://usecanvas.com/dviramontes/lisp-200/3ZbKcTXhr5s5g42PcbkXor"}})
