(ns nukr.profile-logic
  "Pure functions for dealing with model/profiles logic.
   `name` field is used as unique identifier for model."
  (:require [clojure.set :as set]))

(defn create
  ([name]
   (create name false))
  ([name hidden-choice]
   (assert (string? name)
           "name must be string!")
   {:name name :connections #{} :hidden hidden-choice}))

(defn get-name [profile] (:name profile))

(defn get-connections [profile] (:connections profile))

(defn hidden? [profile] (:hidden profile))

(defn connected?
 [profile1 profile2]
 (let [connections1 (get-connections profile1)
       connections2 (get-connections profile2)
       name1 (get-name profile1)
       name2 (get-name profile2)]
   (and (contains? connections2 name1)
        (contains? connections1 name2))))

(defn add-connection
  "Return new `profile` with `name` added to its connections"
  [name profile]
  (let [connections (get-connections profile)]
    (->> name
         (conj connections)
         (assoc profile :connections))))

(defn connect
  "Return vector of 2 profiles with connections updated"
  [profile1 profile2]
  [(add-connection (get-name profile2) profile1)
   (add-connection (get-name profile1) profile2)])

(defn toggle-hidden [profile] (update profile :hidden not))

(defn intersect-connections
 [profile1 profile2]
 (set/intersection
  (get-connections profile1)
  (get-connections profile2)))
