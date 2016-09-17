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
   :game-state []})
