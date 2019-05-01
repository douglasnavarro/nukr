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
