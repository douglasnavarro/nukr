(ns nukr.profile-logic
  "Pure functions for dealing with model/profiles logic.
   `name` field is used as unique identifier for model.")

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

(defn toggle-hidden [profile] (update profile :hidden not))
