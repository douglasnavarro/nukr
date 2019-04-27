(ns nukr.profile)

(defn create [id name]
  (assert (integer? id)
          "id must be integer!")
  (assert (string? name)
          "name must be string!")
  {:id id :name name :connections []})

(defn get-id [profile] (:id profile))

(defn get-name [profile] (:name profile))
