(ns news-handler
    (:gen-class))

(defn insert-news [f news]
    (f news))

(defn fetch-news [f]
    (f))

(defn get-news-by-id [f id]
    (f id))
