(ns nukr.profile-logic-test
  (:require [clojure.test :refer :all]
            [nukr.profile-logic :refer :all]))

(def ed-mock {:name "Ed" :connections #{} :hidden false})
(def cris-mock {:name "Cris" :connections #{} :hidden false})

(deftest create-profile-success-test
  (is (= ed-mock (create "Ed"))
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
  (let [a-profile ed-mock]
    (is (= "Ed"  (get-name a-profile)))
    (is (= #{}   (get-connections a-profile)))
    (is (= false (hidden? a-profile)))))

(deftest add-connection-test
  (let [a-profile   ed-mock
        new-profile (add-connection "Cris" a-profile)]
    (is (= {:name "Ed" :connections #{"Cris"} :hidden false}))))

(deftest connect-test
  (let [profile1   cris-mock
        profile2   ed-mock
        new-1      {:name "Cris" :connections #{"Ed"}   :hidden false}
        new-2      {:name "Ed"   :connections #{"Cris"} :hidden false}]
    (is (= [new-1 new-2]
           (connect profile1 profile2))
        "connect returns connected version of input profiles")))

(deftest connected?-test
  (let [cris  {:name "Cris" :connections #{"David"} :hidden false}
        david {:name "David" :connections #{"Cris" "Ed"} :hidden false}
        ed    {:name "Ed" :connections #{"David"} :hidden false}]
    (is (connected? cris david))
    (is (connected? ed david))
    (is (not (connected? cris ed)))))

(deftest intersect-connections-test
  (let [cris {:name "Cris" :connections #{"David" "X"} :hidden false}
        ed   {:name "Ed" :connections #{"David" "Z"} :hidden false}
        z    {:name "Z" :connections #{"Ed"} :hidden false}]
    (is (= #{"David"} (intersect-connections cris ed)))
    (is (= #{}        (intersect-connections cris z)))))
