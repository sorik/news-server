(ns news
    (:require [monger.core :as mg]
              [monger.collection :as mc]))

(def mongodb-url "mongodb://127.0.0.1/")
(def db-name "words")
(def news-collection "news")

(defn insert [news]
  (let [uri (str mongodb-url db-name)
        {:keys [conn db]} (mg/connect-via-uri uri)]
    (mc/insert db news-collection news)
    (mg/disconnect conn)))
