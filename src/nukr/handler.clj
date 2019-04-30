(ns nukr.handler
  "Controller. Implements web API through request handling."
  (require [nukr.profile-view :as profile-view]
           [nukr.profile-model :as model-state]))

(defn handle-redirect-profiles [req]
  {:status 302
   :headers {"Location" "/profiles"}})

(defn handle-list-profiles [req]
  (let [app-state (:app-state req)
        profiles @app-state]
    {:status 200
     :headers {}
     :body (profile-view/profiles-page profiles)}))

(defn handle-create-profile [req]
  (let [app-state (:app-state req)
        name      (get-in req [:params "name"])
        hidden    (and (get-in req [:params "hidden"]) true)
        item      (model-state/add-profile! name hidden app-state)])
  {:status 302
   :headers {"Location" "/profiles"}
   :body ""})

(defn handle-update-profile [req])
