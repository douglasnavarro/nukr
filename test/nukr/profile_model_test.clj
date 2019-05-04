(ns nukr.profile-model-test
  (:require [clojure.test :refer :all]
            [nukr.profile-logic :as logic]
            [nukr.profile-state :as state]))

(deftest add-profile-test
  (let [storage (state/create-profile-storage!)
        cris (logic/create "Cris")
        ed (logic/create "Ed")
        _ (state/add-profile! cris storage)]
   (is (= #{{:name "Cris" :connections #{} :hidden false}}
          @storage))
   (is (= #{{:name "Cris" :connections #{} :hidden false}
            {:name "Ed" :connections #{} :hidden false}}
         (do (state/add-profile! ed storage))))))

(deftest remove-profile-test
  (let [storage (state/create-profile-storage!)
        cris (logic/create "Cris")
        ed (logic/create "Ed")
        _ (state/add-profile! cris storage)
        _ (state/add-profile! ed storage)]
   (is (= #{{:name "Cris" :connections #{} :hidden false}}
          (do (state/remove-profile! "Ed" storage))))
   (is (= #{}
          (do (state/remove-profile! "Cris" storage))))))

(deftest connect-profiles-test
  (let [storage (state/create-profile-storage!)
        cris (logic/create "Cris")
        ed (logic/create "Ed")
        _ (state/add-profile! cris storage)
        _ (state/add-profile! ed storage)]
   (is (= #{{:name "Cris" :connections #{"Ed"} :hidden false}
            {:name "Ed" :connections #{"Cris"} :hidden false}}
          (do (state/connect-profiles! "Cris" "Ed" storage))))))

(deftest get-suggestions-test
  (let [cris  {:name "Cris" :connections #{"David"} :hidden false}
        ed    {:name "Ed" :connections #{"David"} :hidden false}
        david {:name "David" :connections #{"Cris" "Ed"} :hidden false}
        profiles (ref #{cris ed david})]
    (is (= '({:target "Ed" :relevance 1})
           (state/get-suggestions cris @profiles))
        "get-suggestion sets target and relevance correctly")
    (is (= '({:target "Cris" :relevance 1})
           (state/get-suggestions ed @profiles))
        "get-suggestion sets target and relevance correctly")
    (is (= '()
           (state/get-suggestions david @profiles))
        "no suggestion if already connected to everyone")))

(deftest get-suggestions-test-with-hidden
  (let [cris  {:name "Cris" :connections #{"David"} :hidden false}
        ed    {:name "Ed" :connections #{"David"} :hidden true}
        david {:name "David" :connections #{"Cris" "Ed"} :hidden false}
        profiles (ref #{cris ed david})]
    (is (= '()
           (state/get-suggestions cris @profiles))
        "hidden profile must not be shown in suggestion")
    (is (= '({:target "Cris" :relevance 1})
           (state/get-suggestions ed @profiles)))
    (is (= '()
           (state/get-suggestions david @profiles)))))
