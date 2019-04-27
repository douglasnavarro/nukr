(ns nukr.profile
  "Pure functions for dealing with profiles logic")

(defn create
  [id name]
  (assert (integer? id)
          "id must be integer!")
  (assert (string? name)
          "name must be string!")
  {:id id :name name :connections #{} :hidden false})

(defn get-id [profile] (:id profile))

(defn get-name [profile] (:name profile))

(defn get-connections [profile] (:connections profile))

(defn hidden? [profile] (:hidden profile))

(defn add-connection
  "Return new profile with connection added"
  [profile id]
  (let [connections (get-connections profile)]
    (->> id
         (conj connections)
         (assoc profile :connections))))

(defn connect
  "Return vector of 2 profiles with connections updated"
  [profile1 profile2]
  [(add-connection profile1 (get-id profile2))
   (add-connection profile2 (get-id profile1))])
