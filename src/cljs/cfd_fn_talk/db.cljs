(ns cfd-fn-talk.db)

(def default-db
  {:name       "(λ jeopardy)"
   :categories #{:immutability
                 :lisp
                 ;:multicore
                 :divergent-thinking
                 ;:people-in-computing
                 :limits
                 :elixir/erlang
                 :messages-over-classes}
   :game-state []
   :player-name nil
   :player-count 0
   :most-recent-card nil
   :card-in-view nil
   :card-pointers {:lisp-200 "https://usecanvas.com/dviramontes/lisp-200/3ZbKcTXhr5s5g42PcbkXor"
                   :lisp-400 "https://usecanvas.com/dviramontes/lisp-400/1rv0mwdFub3b0jsh6My3u6"
                   :lisp-800 "https://usecanvas.com/dviramontes/lisp-400/0WaTxjhHMOVZ9l5Fkc4pqP"
                   :lisp-1000 "https://usecanvas.com/dviramontes/lisp-1000/2xoznBcmhx1Dj3azYohCm8"
                   :immutability-200 "https://usecanvas.com/dviramontes/immutability-200/2UChDbxHHhtSlYBnGI4hEk"
                   :immutability-400 ""
                   :immutability-800 ""
                   :immutability-1000 ""
                   :messages-over-classes-200 "https://usecanvas.com/dviramontes/messages-over-classes-200/6iIRhz4tUielHy3t4EZ9oJ"
                   :messages-over-classes-400 ""
                   :messages-over-classes-800 ""
                   :messages-over-classes-1000 ""
                   :divergent-thinking-200 "https://usecanvas.com/dviramontes/divergent-thinking-200/0nGRwo3awukhotReq549Ig"
                   :divergent-thinking-400 "https://usecanvas.com/dviramontes/divergent-thinking-400/1oAtosbeuTLkDTowWa1uHL"
                   :divergent-thinking-800 ""
                   :divergent-thinking-1000 ""
                   :limits-200 "https://usecanvas.com/dviramontes/limits-200/5VamWjkYGCTZ2xJreU0lxq"
                   :limits-400 "https://usecanvas.com/dviramontes/limits-400/48bq0oeapj7YuAAehFyGBu"
                   :limits-800 "https://usecanvas.com/dviramontes/limits-800/758FHExSqi1J3wLCojKVHK"
                   :limits-1000 "https://usecanvas.com/dviramontes/limits-1000/3YohyPYU6p2TL9BxIB3pXx"
                   :erlang-200 "https://usecanvas.com/dviramontes/erlang-200/3sj1qEJYFzYYBzoPgbRLHb"
                   :erlang-400 ""
                   :erlang-800 ""
                   :erlang-1000 "https://usecanvas.com/dviramontes/fp-is-fun/1sHK8mxzOWH8CM4EAqKFWd"}})