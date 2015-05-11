(defproject news-server "0.0.1-SNAPSHOT"
    :description "a small web server for getting and inserting news articles"


    :plugins [[lein-ring "0.9.3"]]

    :dependencies [[org.clojure/clojure "1.6.0"]
                   [liberator "0.12.2"]
                   [compojure "1.3.3"]
                   [ring/ring-core "1.3.2"]
                   [com.novemberain/monger "2.0.0"]
                   [cheshire "5.1.1"]]

    :ring {:handler main/handler :init main/init :destroy main/destroy}

    :test-paths ["test/unit"]
  )
