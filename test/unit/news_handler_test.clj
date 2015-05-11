(ns news-handler-test
  (:use clojure.test)
  (:require [news-handler :refer :all :as nh]))

(def mock-success-insert-function
  (fn [_]
    "success"))

(def mock-fail-insert-function
  (fn [_] (throw (Exception. "failed"))))

(def expected-fetch-result [{:any "data"} {:the "other"}])

(def mock-success-fetch-function
  (fn [] expected-fetch-result))

(def mock-fail-fetch-function
  (fn [] (throw (Exception. "failed"))))

(def expected-get-by-id-result {:any "data"})

(def mock-success-get-by-id-function
  (fn [_] expected-get-by-id-result))

(def mock-fail-get-by-id-function
  (fn [_] (throw (Exception. "any error"))))

(deftest insert
  (testing  "when db process is successful without an exception"
    (testing  "should return db result"
      (is (= "success"
             (nh/insert-news mock-success-insert-function {:any "data"})))))

  (testing "when db process throws an exception"
    (testing "should throw an exception"
      (is (thrown-with-msg? Exception #"failed"
                            (nh/insert-news mock-fail-insert-function {:any "data"}))))))

(deftest fetch
  (testing "when db process is successful without an exception"
    (testing "should return db result"
      (is (= expected-fetch-result
             (nh/fetch-news mock-success-fetch-function)))))
  (testing "when db process throws an exception"
    (testing "should throw an exception"
      (is (thrown-with-msg? Exception #"failed"
                            (nh/fetch-news mock-fail-fetch-function))))))

(deftest get-by-id
  (testing "when db process is successful without an exception"
    (testing "should return db result"
      (is (= expected-get-by-id-result
             (nh/get-news-by-id mock-success-get-by-id-function "anyId")))))
  (testing "when the news id is not valid"
    (testing "should throw an exception"
      (is (thrown? Exception
                   (nh/get-news-by-id mock-fail-get-by-id-function "anyId"))))))
