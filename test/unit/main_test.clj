(ns main-test
  (:use clojure.test)
  (:require [main :refer :all]))

(def mock-success-insert-function
  (fn [_]
    "success"))

(def mock-fail-insert-function
  (fn [_]
    (throw (Exception. "failed"))))

(deftest insert
  (testing  "when db process is successful without exception"
    (testing  "should return db result"
      (is (= "success" (main/insert-news mock-success-insert-function {:any "data"})))))

  (testing "when db process throws exception"
    (testing "should throw an exception"
      (is (thrown-with-msg? Exception #"failed"
                            (main/insert-news mock-fail-insert-function {:any "data"}))))))


