(ns nukr.profile-test
  (:require [clojure.test :refer :all]
            [nukr.profile-logic :refer :all]))

(deftest create-profile-success-test
  (is (= {:name "Edward"
          :connections #{}
          :hidden false}
         (create "Edward"))
      "create-profile 1-arity returns expected map")
  (is (= {:name "Edward"
          :connections #{}
          :hidden true}
         (create "Edward" true))
      "create-profile 2-arity returns expected map"))

(deftest create-profile-validates-input-test
  (is (thrown? AssertionError
               (create 123))
      "create-profile validates name field type"))

(deftest profile-get-fields-test
  (let [a-profile   (create "Edward")]
    (is (= "Edward" (get-name a-profile)))
    (is (= #{}      (get-connections a-profile)))
    (is (= false    (hidden? a-profile)))))

(deftest add-connection-test
  (let [a-profile   (create "Edward")
        new-profile (add-connection "Cris" a-profile)]
    (is (= #{"Cris"}
           (get-connections new-profile)))))

(deftest connect-test
  (let [profile1   (create "Cris")
        profile2   (create "Ed")
        new-1      {:name "Cris" :connections #{"Ed"}   :hidden false}
        new-2      {:name "Ed"   :connections #{"Cris"} :hidden false}]
    (is (= [new-1 new-2]
           (connect profile1 profile2))
        "connect returns connected version of input profiles")))

(deftest connected?-test
  (let [cris  (->> "Cris" create (add-connection "David"))
        david (->> "David" create  (add-connection "Cris"))
        ed    (->> "Ed" create (add-connection "David"))]
    (is (connected? cris david))
    (is (not (connected? cris ed)))))

(deftest intersect-connections-test
  (let [cris (->> "Cris" create
                  (add-connection "David")
                  (add-connection "X"))
        ed (->> "Ed" create
                (add-connection "David")
                (add-connection "Y"))]
   (is (= #{"David"} (intersect-connections cris ed)))
   (is (= #{}        (intersect-connections cris (create "Z"))))))
