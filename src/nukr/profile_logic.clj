(ns nukr.profile-logic
  "Pure functions for dealing with model/profiles logic")

(defn create
  [name]
  (assert (string? name)
          "name must be string!")
  {:name name :connections #{} :hidden false})

(defn get-name [profile] (:name profile))

(defn get-connections [profile] (:connections profile))

(defn hidden? [profile] (:hidden profile))

(defn add-connection
  "Return new `profile` with `name` added to its connections "
  [profile name]
  (let [connections (get-connections profile)]
    (->> name
         (conj connections)
         (assoc profile :connections))))

(defn connect
  "Return vector of 2 profiles with connections updated"
  [profile1 profile2]
  [(add-connection profile1 (get-name profile2))
   (add-connection profile2 (get-name profile1))])
