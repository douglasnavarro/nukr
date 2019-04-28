(ns nukr.profile-model
  (:require [nukr.profile-logic :refer :all]))

(defn create-profile-storage! [] (ref #{}))

(defn add-profile!
  [name profile-list]
  (let [profile (create name)]
    (dosync
      (alter profile-list conj profile))))
