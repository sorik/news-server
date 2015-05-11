(ns main-test
  (:use clojure.test)
  (:require [main :refer :all]))

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

(deftest insert
  (testing  "when db process is successful without exception"
    (testing  "should return db result"
      (is (= "success"
             (main/insert-news mock-success-insert-function {:any "data"})))))

  (testing "when db process throws an exception"
    (testing "should throw an exception"
      (is (thrown-with-msg? Exception #"failed"
                            (main/insert-news mock-fail-insert-function {:any "data"}))))))

(deftest fetch
  (testing "when db process is successful without exception"
    (testing "should return db result"
      (is (= expected-fetch-result
             (main/fetch-news mock-success-fetch-function)))))
  (testing "when db process throws an exception"
    (testing "should throw an exception"
      (is (thrown-with-msg? Exception #"failed"
                            (main/fetch-news mock-fail-fetch-function))))))
