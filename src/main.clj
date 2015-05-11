(ns main
    (:require [liberator.core :refer [resource defresource]]
              [ring.middleware.params :refer [wrap-params]]
              [compojure.core :refer [defroutes ANY]]
              [cheshire.core :refer [generate-string parse-string]]
              [news :refer :all]))


(def init
  (fn []
    (news/connect-to-db)))

(def destroy
  (fn []
    (news/disconnect)))

(def db-interfaces
  {:insert news/insert
   :fetch news/fetch
   :get-by-id news/get-by-id})

(defn insert-news [f news]
  (f news))

(defn fetch-news [f]
  (f))

(defn get-news-by-id [f id]
  (f id))

(defresource news [data]
    :available-media-types ["application/json"]
    :allowed-methods [:get :post]
    :handle-exception (fn [e] (str (:exception e)))
    :handle-ok (fn [_]
                   (let [news-list (fetch-news (:fetch db-interfaces))]
                     (generate-string news-list)))

    :post! (fn [ctx]
             (dosync
              (let [body-str (slurp (get-in ctx [:request :body]))
                    body (parse-string body-str true)]
                (insert-news (:insert db-interfaces) body)))))

(defroutes app
  (ANY "/news/:id" [id] (resource
                       :allowed-methods [:get]
                       :available-media-types ["application/json"]
                       :handle-exception (fn [e] (str (:exception e)))
                       :exists? (fn [_] (if-let [d (get-news-by-id (:get-by-id db-interfaces) id)]
                                  {::data d}))
                       :handle-ok ::data))
  (ANY "/news" [data] (news data)))

(def handler
    (-> app
        wrap-params))
