FROM clojure

RUN mkdir -p /usr/local/apps/news-server

WORKDIR /usr/local/apps/news-server
COPY ./target/news-server-standalone.jar app-standalone.jar

EXPOSE 8005

CMD ["java", "-jar", "app-standalone.jar", "8005"]
