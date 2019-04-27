(ns nukr.profile-test
  (:require [clojure.test :refer :all]
            [nukr.profile :refer :all]))

(deftest create-profile-test
  (is (= {:id 1
          :name "Edward"}
         (create-profile 1 "Edward"))))
