(ns nukr.profile-state
  "Leverage Clojure's STM to safely implement in-memory state.
   `name` field is used as unique identifier for model."
  (:require [nukr.profile-logic :refer :all]
            [clojure.tools.logging :as log]))

(defn create-profile-storage! [] (ref #{}))

(defn get-profile
  [name profile-storage]
  (first (filter #(= name (get-name %)) @profile-storage)))

(defn add-profile!
  [profile profile-storage]
  (log/info "Adding " profile " to storage.")
  (dosync
    (alter profile-storage conj profile)))

(defn remove-profile!
  [name profile-storage]
  (log/info "Removing " name " from storage.")
  (let [profile (get-profile name profile-storage)]
    (dosync
      (alter profile-storage disj profile))))

(defn connect-profiles!
  [origin-name target-name profile-storage]
  (log/info "Connecting " origin-name " and " target-name)
  (let [origin-profile (get-profile origin-name profile-storage)
        target-profile (get-profile target-name profile-storage)
        connected      (connect origin-profile target-profile)]
    (dosync
      (remove-profile! origin-profile profile-storage)
      (remove-profile! target-profile profile-storage)
      (add-profile! (first connected) profile-storage)
      (add-profile! (second connected) profile-storage))))

; (defn update-profile!
;   "Remove profile identified by `name` from profile-storage
;   and add new-profile. Names must match."
;   [name new-profile profile-storage]
;   (assert (= name (get-name new-profile))
;           "Names must match!")
;   (dosync
;     (let [to-remove-profile (get-profile name profile-storage)]
;       (alter profile-storage disj to-remove-profile)
;       (alter profile-storage conj new-profile))))
