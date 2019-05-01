(ns nukr.handler
  "Controller. Implements web API through request handling."
  (require [nukr.profile-view :as profile-view]
           [nukr.profile-state :as model-state]
           [nukr.profile-logic :as profile-logic]))

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
        profile   (profile-logic/create name hidden)
        item      (model-state/add-profile! profile app-state)]
    {:status 302
     :headers {"Location" "/profiles"}
     :body ""}))

(defn handle-connect-profiles [req]
  (let [app-state    (:app-state req)
        origin-name  (:name (:route-params req))
        target-name  (get-in req [:params "target"])
        _ (model-state/connect-profiles! origin-name target-name app-state)]
   {:status 200
    :headers {}
    :body "Connected!"}))
