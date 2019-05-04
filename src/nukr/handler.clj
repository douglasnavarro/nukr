(ns nukr.handler
  "Controller. Implements web API through request handling."
  (require [nukr.profile-view :as view]
           [nukr.profile-state :as state]
           [nukr.profile-logic :as logic]))

(defn handle-redirect-profiles [req]
  {:status 302
   :headers {"Location" "/profiles"}})

(defn handle-list-profiles [req]
  (let [app-state (:app-state req)
        profiles @app-state]
    {:status 200
     :headers {}
     :body (view/profiles-page profiles)}))

(defn handle-create-profile [req]
  (let [app-state (:app-state req)
        name      (get-in req [:params "name"])
        hidden    (if (get-in req [:params "hidden"]) true false)
        profile   (logic/create name hidden)
        item      (state/add-profile! profile app-state)]
    {:status 302
     :headers {"Location" "/profiles"}}))

(defn handle-connect-profiles [req]
  (let [app-state    (:app-state req)
        origin-name  (:name (:route-params req))
        target-name  (get-in req [:params "target"])
        _ (state/connect-profiles! origin-name target-name app-state)]
   {:status 200
    :headers {"Location" "/profiles"}
    :body ""}))
