(defproject news-server "0.0.1-SNAPSHOT"
    :description "a small web server for getting and inserting news articles"


    :plugins [[lein-ring "0.9.3"]]

    :dependencies [[org.clojure/clojure "1.6.0"]
                   [liberator "0.12.2"]
                   [compojure "1.3.3"]
                   [ring/ring-core "1.3.2"]
                   [com.novemberain/monger "2.0.0"]
                   [cheshire "5.1.1"]
                   [ring/ring-mock "0.2.0"]
                   [ring/ring-jetty-adapter "1.2.1"]]

    :test-paths ["test/unit"]

    :target-path "target/%s/"
    :clean-targets [:target-path]

    :uberjar-name "news-server-standalone.jar"

    :main main
    :aot :all
  )
