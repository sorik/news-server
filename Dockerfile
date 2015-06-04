FROM clojure

RUN mkdir -p /usr/local/apps/news-server
COPY . /usr/local/apps/news-server
WORKDIR /usr/local/apps/news-server

RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" app-standalone.jar

EXPOSE 8005

CMD ["java", "-jar", "app-standalone.jar", "8005"]
