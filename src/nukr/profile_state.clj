(ns nukr.profile-state
  "Leverage Clojure's STM to safely implement in-memory state.
   `name` field is used as unique identifier for profile model."
  (:require [nukr.profile-logic :refer :all]
            [clojure.tools.logging :as log]))

(defn create-profile-storage! [] (ref #{}))

(defn get-profile
  [name profile-storage]
  (first (filter #(= name (get-name %)) @profile-storage)))

(defn get-not-connected
  [profile profile-storage]
  (filter #(not (connected? profile %)) (disj profile-storage profile)))

(defn- get-suggestion-eligible
  "Get profiles eligible for suggestion to `profile`
  i.e not hidden and not connected to `profile`."
  [profile profile-storage]
  (filter #(not (hidden? %)) (get-not-connected profile profile-storage)))

(defn get-suggestions
  "Return list of suggestions for `profile`."
  [profile profile-storage]
  (log/info "Getting suggestions for" profile)
  (let [suggestions (get-suggestion-eligible profile profile-storage)]
    (for [sugg suggestions]
      (hash-map :target    (get-name sugg)
                :relevance (count (intersect-connections profile sugg))))))

(defn add-profile!
  "Add `profile` to `profile-storage` if not already present."
  [profile profile-storage]
  (log/info "Adding" profile "to storage.")
  (if (get-profile (get-name profile) profile-storage)
    (do (log/info profile "already stored. Ignoring.")
        false)
    (dosync
      (alter profile-storage conj profile))))

(defn remove-profile!
  "Remove `profile` from `profile-storage`
  if the first exists in the second."
  [name profile-storage]
  (log/info "Removing" name "from storage.")
  (let [profile (get-profile name profile-storage)]
    (if profile
      (dosync
        (alter profile-storage disj profile))
      false)))

(defn connect-profiles!
  "Updates `profile-storage` with connected version of profiles
  identified by `origin-name` and `target-name`."
  [origin-name target-name profile-storage]
  (log/info "Connecting" origin-name "and" target-name)
  (let [origin-profile (get-profile origin-name profile-storage)
        target-profile (get-profile target-name profile-storage)
        connected      (connect origin-profile target-profile)]
    (dosync
      (remove-profile! origin-name profile-storage)
      (remove-profile! target-name profile-storage)
      (add-profile! (first connected) profile-storage)
      (add-profile! (second connected) profile-storage))))
