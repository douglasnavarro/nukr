(ns nukr.profile-test
  (:require [clojure.test :refer :all]
            [nukr.profile :as profile]))

(deftest create-profile-success-test
  (is (= {:id 1
          :name "Edward"
          :connections #{}
          :hidden false}
         (profile/create 1 "Edward"))
      "create-profile returns expected map"))

(deftest create-profile-validates-input-test
  (is (thrown? AssertionError
               (profile/create "1" "Edward"))
      "create-profile validates id field type")
  (is (thrown? AssertionError
               (profile/create 1 123))
      "create-profile validates name field type"))

(deftest profile-get-fields-test
  (let [a-profile   (profile/create 1 "Edward")]
    (is (= 1        (profile/get-id a-profile)))
    (is (= "Edward" (profile/get-name a-profile)))
    (is (= #{}      (profile/get-connections a-profile)))
    (is (= false    (profile/hidden? a-profile)))))

(deftest add-connection-test
  (let [a-profile   (profile/create 1 "Edward")
        new-profile (profile/add-connection a-profile 2)]
    (is (= #{2}
           (profile/get-connections new-profile)))))

(deftest connect-test
  (let [profile1   (profile/create 1 "Cris")
        profile2   (profile/create 2 "Ed")
        new-1      {:id 1 :name "Cris" :connections #{2} :hidden false}
        new-2      {:id 2 :name "Ed"   :connections #{1} :hidden false}]
    (is (= [new-1 new-2]
           (profile/connect profile1 profile2))
        "connect func returns connected version of input profiles")))
