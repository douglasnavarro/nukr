(ns nukr.profile-model
  "Leverage Clojure's STM to safely implement in-memory state.
   `name` field is used as unique identifier for model."
  (:require [nukr.profile-logic :refer :all]))

(defn create-profile-storage! [] (ref #{}))

(defn get-profile
  [name profile-storage]
  (first (filter #(= name (get-name %)) @profile-storage)))

(defn add-profile!
  [name hidden-choice profile-storage]
  (let [profile (create name hidden-choice)]
    (dosync
      (alter profile-storage conj profile))))

(defn update-profile!
  "Remove profile identified by `name` from profile-storage
  and add new-profile. Names must match."
  [name new-profile profile-storage]
  (assert (= name (get-name new-profile))
          "Names must match!")
  (dosync
    (let [to-remove-profile (get-profile name profile-storage)]
      (alter profile-storage disj to-remove-profile)
      (alter profile-storage conj new-profile))))
