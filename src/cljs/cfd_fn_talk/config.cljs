(ns cfd-fn-talk.config
  (:require [cljsjs.firebase]))

(def debug?
  ^boolean js/goog.DEBUG)

(defonce firebase-app-init
         {:apiKey        "I9YgeqfDYhR8N8AuhFhNBzVF4ZNrngVjcHZVthag"
          :authDomain    "cdf-fn-talk.firebaseapp.com"
          :databaseURL   "https://cdf-fn-talk.firebaseio.com"
          :storageBucket "cdf-fn-talk.appspot.com"})

(defonce firebase-app
         (.initializeApp js/firebase (clj->js firebase-app-init)))

(defonce firebase-db-ref
         (-> firebase-app .database .ref))
