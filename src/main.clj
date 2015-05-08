(ns main
    (:require [liberator.core :refer [resource defresource]]
              [ring.middleware.params :refer [wrap-params]]
              [compojure.core :refer [defroutes ANY]]
              [clojure.data.json :as json]
              [news :refer :all]))

(defresource news [data]
    :available-media-types ["application/json"]
    :allowed-methods [:get :post]
    :handle-ok {:got true}
    :post! (fn [ctx]
             (dosync
              (let [body-str (slurp (get-in ctx [:request :body]))
                    body (json/read-str body-str :key-fn keyword)]
                (news/insert body)))))

(defroutes app
    (ANY "/news" [data] (news data)))

(def handler
    (-> app
        wrap-params))
