(ns main
  (:gen-class)
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY]]
            [cheshire.core :refer [generate-string parse-string]]
            [news-handler :refer :all]
            [news :refer :all]
            [ring.adapter.jetty :as jetty]))

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

(defresource news [data]
    :available-media-types ["application/json"]
    :allowed-methods [:get :post]
    :handle-exception (fn [e] (str (:exception e)))
    :malformed? (fn [ctx]
                  (when (= :post (get-in ctx [:request :request-method]))
                    (let [body-str (slurp (get-in ctx [:request :body]))]
                      (try
                        (do
                          {::body (parse-string body-str true)}
                          false)
                        (catch Exception e
                          true)))))
    :handle-ok (fn [_]
                   (let [news-list (news-handler/fetch-news (:fetch db-interfaces))]
                     (generate-string news-list)))

    :post! (fn [ctx]
             (news-handler/insert-news (:insert db-interfaces) ::body)))

(defroutes app
  (ANY "/news/:id" [id] (resource
                       :allowed-methods [:get]
                       :available-media-types ["application/json"]
                       :handle-exception (fn [e] (str (:exception e)))
                       :exists? (fn [_] (if-let [d (news-handler/get-news-by-id (:get-by-id db-interfaces) id)]
                                  {::data d}))
                       :handle-ok ::data))
  (ANY "/news" [data] (news data)))

(def handler
    (-> app
        wrap-params))

(defn -main []
  (init)
  (jetty/run-jetty handler {:port 3000})
  (destroy))
