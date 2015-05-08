(ns main
    (:require [liberator.core :refer [resource defresource]]
              [ring.middleware.params :refer [wrap-params]]
              [compojure.core :refer [defroutes ANY]]))

(defresource news [data]
    :available-media-types ["application/json"]
    :allowed-methods [:get :post]
    :handle-ok {:got true}
    :post! true)

(defroutes app
    (ANY "/news" [data] (news data)))

(def handler
    (-> app
        wrap-params))