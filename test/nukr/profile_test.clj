(ns nukr.profile-test
  (:require [clojure.test :refer :all]
            [nukr.profile :as profile]))

(deftest create-profile-success-test
  (is (= {:id 1
          :name "Edward"
          :connections []}
         (profile/create 1 "Edward"))
      "create-profile returns map expected"))

(deftest create-profile-validates-input-test
  (is (thrown? AssertionError
               (profile/create "1" "Edward"))
      "create-profile validates id field type")
  (is (thrown? AssertionError
               (profile/create 1 123))
      "create-profile validates name field type"))

(deftest profile-get-fields-test
  (let [a-profile (profile/create 1 "Edward")]
    (is (= 1        (profile/get-id a-profile)))
    (is (= "Edward" (profile/get-name a-profile)))))
