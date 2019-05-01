(ns nukr.profile-test
  (:require [clojure.test :refer :all]
            [nukr.profile-logic :as profile]))

(deftest create-profile-success-test
  (is (= {:name "Edward"
          :connections #{}
          :hidden false}
         (profile/create "Edward"))
      "create-profile 1-arity returns expected map")
  (is (= {:name "Edward"
          :connections #{}
          :hidden true}
         (profile/create "Edward" true))
      "create-profile 2-arity returns expected map"))

(deftest create-profile-validates-input-test
  (is (thrown? AssertionError
               (profile/create 123))
      "create-profile validates name field type"))

(deftest profile-get-fields-test
  (let [a-profile   (profile/create "Edward")]
    (is (= "Edward" (profile/get-name a-profile)))
    (is (= #{}      (profile/get-connections a-profile)))
    (is (= false    (profile/hidden? a-profile)))))

(deftest add-connection-test
  (let [a-profile   (profile/create "Edward")
        new-profile (profile/add-connection a-profile "Cris")]
    (is (= #{"Cris"}
           (profile/get-connections new-profile)))))

(deftest connect-test
  (let [profile1   (profile/create "Cris")
        profile2   (profile/create "Ed")
        new-1      {:name "Cris" :connections #{"Ed"}   :hidden false}
        new-2      {:name "Ed"   :connections #{"Cris"} :hidden false}]
    (is (= [new-1 new-2]
           (profile/connect profile1 profile2))
        "connect func returns connected version of input profiles")))
