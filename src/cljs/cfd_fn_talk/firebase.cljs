(ns cfd-fn-talk.firebase
  (:require cljsjs.firebase))

(defonce firebase-app-init
         {:apiKey        "I9YgeqfDYhR8N8AuhFhNBzVF4ZNrngVjcHZVthag"
          :authDomain    "cdf-fn-talk.firebaseapp.com"
          :databaseURL   "https://cdf-fn-talk.firebaseio.com"
          :storageBucket "cdf-fn-talk.appspot.com"})

(defonce firebase-app
         (.initializeApp js/firebase (clj->js firebase-app-init)))

(defonce firebase-db-ref
         (-> firebase-app .database .ref))

(defn ref-for-path
  "Returns a Firebase ref for the node at the given
  path string relative to firebase-db-ref.
  -- Tyler Perkins"
  [rel-path]
  (.child firebase-db-ref rel-path))

(defonce am-i-online?
         (-> firebase-app
             .database
             .ref
             (.child ".info/connected")))
