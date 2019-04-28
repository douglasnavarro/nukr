(ns nukr.profile-model
  (:require [nukr.profile-logic :refer :all]))

(defn create-profile-storage! [] (ref {}))

(defn add-profile
  [profile profile-list]
  (dosync
    (alter profile-list assoc (get-id profile) profile)))
