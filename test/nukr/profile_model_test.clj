(ns nukr.profile-model-test
  (:require [clojure.test :refer :all]
            [nukr.profile-logic :as logic]
            [nukr.profile-state :as state]))

(def cris-mock {:name "Cris" :connections #{} :hidden false})
(def ed-mock {:name "Ed" :connections #{} :hidden false})

(deftest add-profile-test
  (let [storage (ref #{})
        _ (state/add-profile! cris-mock storage)]
   (is (= #{{:name "Cris" :connections #{} :hidden false}}
          @storage))
   (is (= #{{:name "Cris" :connections #{} :hidden false}
            {:name "Ed" :connections #{} :hidden false}}
         (do (state/add-profile! ed-mock storage))))))

(deftest remove-profile-test
  (let [storage (ref #{cris-mock ed-mock})]
   (is (= #{{:name "Cris" :connections #{} :hidden false}}
          (do (state/remove-profile! "Ed" storage))))
   (is (= #{}
          (do (state/remove-profile! "Cris" storage))))))

(deftest connect-profiles-test
  (let [storage (ref #{cris-mock ed-mock})]
   (is (= #{{:name "Cris" :connections #{"Ed"} :hidden false}
            {:name "Ed" :connections #{"Cris"} :hidden false}}
          (do (state/connect-profiles! "Cris" "Ed" storage))))))

(deftest get-suggestions-test
  (let [cris  {:name "Cris" :connections #{"David"} :hidden false}
        ed    {:name "Ed" :connections #{"David"} :hidden false}
        david {:name "David" :connections #{"Cris" "Ed"} :hidden false}
        storage (ref #{cris ed david})]
    (is (= '({:target "Ed" :relevance 1})
           (state/get-suggestions cris @storage))
        "get-suggestion sets target and relevance correctly")
    (is (= '({:target "Cris" :relevance 1})
           (state/get-suggestions ed @storage))
        "get-suggestion sets target and relevance correctly")
    (is (= '()
           (state/get-suggestions david @storage))
        "no suggestion if already connected to everyone")))

(deftest get-suggestions-test-with-hidden
  (let [cris  {:name "Cris" :connections #{"David"} :hidden false}
        ed    {:name "Ed" :connections #{"David"} :hidden true}
        david {:name "David" :connections #{"Cris" "Ed"} :hidden false}
        storage (ref #{cris ed david})]
    (is (= '()
           (state/get-suggestions cris @storage))
        "hidden profile must not be shown in suggestion")
    (is (= '({:target "Cris" :relevance 1})
           (state/get-suggestions ed @storage)))
    (is (= '()
           (state/get-suggestions david @storage)))))
