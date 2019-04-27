(ns nukr.profile)

(defn create-profile [id name]
  (assert (integer? id))
  (assert (string? name))
  {:id id :name name})

(defn profile-id [profile] (:id profile))

(defn profile-name [profile] (:name profile))
