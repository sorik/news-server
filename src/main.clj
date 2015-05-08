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


(defresource news [data]
    :available-media-types ["application/json"]
    :allowed-methods [:get :post]
    :handle-ok (fn [_]
                   (let [news-list (news/fetch)]
                     (generate-string news-list)))

    :post! (fn [ctx]
             (dosync
              (let [body-str (slurp (get-in ctx [:request :body]))
                    body (parse-string body-str true)]
                (news/insert body)))))

(defroutes app
    (ANY "/news" [data] (news data)))

(def handler
    (-> app
        wrap-params))
