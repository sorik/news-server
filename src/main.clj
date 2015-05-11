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
   :fetch news/fetch})

(defn insert-news [f news]
  (f news))

(defn fetch-news [f]
  (f))

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
                       :exists? (if-let [d (news/get-news-by-id id)] {::data d})
                       :handle-ok ::data))
  (ANY "/news" [data] (news data)))

(def handler
    (-> app
        wrap-params))
