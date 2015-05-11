(ns main-test
  (:use clojure.test
        ring.mock.request)
  (:require [main :refer :all]
            [cheshire.core :refer [generate-string parse-string]]))

(deftest endpoint
  (with-redefs [main/init (fn [] "anything")
                main/destroy (fn [] "anything")
                main/db-interfaces {:insert (fn [_] "success")
                                    :fetch (fn [] [{:some "thing"}])
                                    :get-by-id (fn [_] {:some "id"})}]
    (testing "should accept request"
      (testing "fetch"
        (let [response (app (request :get "/news"))]
          (is (= 200 (:status response)))
          (is (re-find #"application/json" (get-in response [:headers "Content-Type"])))))

      (testing "insert"
        (let [response (app (request :post "/news" (generate-string {"some" "data"})))]
          (is (= 201 (:status response)))))

      (testing "get-by-id"
        (let [response (app (request :get "/news/someId"))]
          (is (= 200 (:status response)))
          (is (re-find #"application/json" (get-in response [:headers "Content-Type"]))))))

    (testing "should reject request"
      (testing "when sent post to /news/:id"
        (let [response (app (request :post "/news/someId"))]
          (is (= 405 (:status response)))))
      (testing "when request body is not json format"
        (let [response (app (request :post "/news" "malformed json"))]
          (is (= 400 (:status response))))))))

(deftest fetch-request
  (testing "should return news data"
    (with-redefs [main/init (fn [] "anything")
                  main/destroy (fn [] "anything")
                  main/db-interfaces {:fetch (fn [] [{:some "thing"} {:other "things"}])}]
      (let [response (app (request :get "/news"))]
        (is (= [{:some "thing"} {:other "things"}]
               (parse-string (:body response) true))))))

  (testing "should return 500 when db process failed"
    (with-redefs [main/init (fn [] "anything")
                  main/destroy (fn [] "anything")
                  main/db-interfaces {:fetch (fn [] (throw (Exception. "some error")))}]
      (let [response (app (request :get "/news"))]
        (is (= 500 (:status response)))
        (is (re-find #"some error" (:body response)))))))

(deftest insert-request
  (testing "should return 500 when db process failed"
    (with-redefs [main/init (fn [] "anything")
                  main/destroy (fn [] "anything")
                  main/db-interfaces {:insert (fn [_] (throw (Exception. "some error")))}]
      (let [response (app (request :post "/news" (generate-string {"some" "data"})))]
        (is (= 500 (:status response)))
        (is (re-find #"some error" (:body response)))))))

(deftest get-by-id-request
  (testing "should return news data"
    (with-redefs [main/init (fn [] "anything")
                  main/destroy (fn [] "anything")
                  main/db-interfaces {:get-by-id (fn [_] {:some "thing"})}]
      (let [response (app (request :get "/news/someId"))]
        (is (= {:some "thing"}
               (parse-string (:body response) true))))))

  (testing "should return 500 when db process failed"
    (with-redefs [main/init (fn [] "anything")
                  main/destroy (fn [] "anything")
                  main/db-interfaces {:get-by-id (fn [_] (throw (Exception. "some error")))}]
      (let [response (app (request :get "/news/someId"))]
        (is (= 500 (:status response)))
        (is (re-find #"some error" (:body response)))))))
