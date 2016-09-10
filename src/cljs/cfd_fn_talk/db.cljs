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
                 :messages-better-than-classes}
   :game-state []})
