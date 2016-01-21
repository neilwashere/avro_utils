(ns avro-utils.core-test
  (:require [clojure.test :refer :all]
            [avro-utils.core :refer :all]))

(deftest validation-success

  (testing "A valid map will pass schema validation"
    (let [schema "sample.json"
          data {:id "some id" :fact "this is true"}
          result (validate schema data)]
      (is (:valid? result)))))

(deftest validation-failure
  (let [schema "sample.json"
        data {:id 1 :fact 24}
        result (validate schema data)]

    (testing "An invalid map will fail schema validation"
      (is (not (:valid? result))))

    (testing "The result of validation will contain 2 errors"
      (let [error-count (filter
                         #(= "error" (get %1 "level"))
                         (:result result))]
        (is (= 2 (count error-count)))))

    (testing "The errors will show :id requires a string"
      (let [error (first
                   (filter
                    #(= {"pointer" "/id"} (get %1 "instance"))
                    (:result result)))]
        (is (= "instance type (integer) does not match any allowed primitive type (allowed: [\"string\"])"
               (get error "message")))))

    (testing "The errors will show :fact requires a string"
      (let [error (first
                   (filter
                    #(= {"pointer" "/fact"} (get %1 "instance"))
                    (:result result)))]
        (is (= "instance type (integer) does not match any allowed primitive type (allowed: [\"string\"])"
               (get error "message")))))

    ;; There should really be 2 errors, but it seems the validation chokes when
    ;; unexpected attributes are present.
    (testing "Sadly, not all errors are shown at once"
      (let [data {:additional "oops" :id 1 :fact "true"}
            result (:result (validate schema data))]
        (is (= 1 (count result)))
        (is (= "object instance has properties which are not allowed by the schema: [\"additional\"]"
               (get (first result) "message")))))))
