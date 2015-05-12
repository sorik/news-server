(ns news
  (:gen-class)
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.json :as mj])
  (:import org.bson.types.ObjectId))

(def mongodb-url "mongodb://127.0.0.1/")
(def db-name "words")
(def news-collection "news")

(def db (atom nil))
(def conn (atom nil))

(defn connect-to-db []
  (let [uri (str mongodb-url db-name)
        db-conn (mg/connect-via-uri uri)]
    (reset! db (:db db-conn))
    (reset! conn (:conn db-conn))))

(defn disconnect []
  (mg/disconnect @conn))

(def insert
  (fn [news]
    (mc/insert @db news-collection news)))


(defn fetch []
  (let [news-list (map (fn [news]
                         (update-in news [:_id] str))
                       (mc/find-maps @db news-collection))]
    news-list))

(defn get-by-id [id]
  (mc/find-one-as-map @db news-collection {:_id (ObjectId. id)}))
