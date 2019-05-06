(ns nukr.handler
  "Controller. Implements web API through request handling."
  (require [nukr.profile-view :as view]
           [nukr.profile-state :as state]
           [nukr.profile-logic :as logic]
           [clojure.data.json :as json]))

(defn- profile-not-found-response [name]
  {:status 404
   :headers {}
   :body (str "profile " name " not found.")})

(defn handle-list-profiles [req]
  (let [app-state (:app-state req)
        profiles @app-state]
    {:status 200
     :headers {"Content-type" "application/json; charset=utf8"}
     :body (json/write-str profiles)}))

(defn handle-get-profile [req]
  (let [app-state (:app-state req)
        name      (:name (:route-params req))
        profile   (state/get-profile name app-state)]
    (if profile
      {:status 200
       :headers {"Content-type" "application/json; charset=utf8"}
       :body (json/write-str profile)}
      {:status 404
       :headers {"Content-type" "application/json; charset=utf8"}})))

(defn handle-html-view [req]
  (let [app-state (:app-state req)
        profiles @app-state]
    {:status 200
     :headers {"Content-type" "text/html; charset=utf-8"}
     :body (view/profiles-page profiles)}))

(defn handle-create-profile [req]
  (let [app-state (:app-state req)
        name      (get-in req [:params "name"])
        hidden    (if (get-in req [:params "hidden"]) true false)
        profile   (logic/create name hidden)
        item      (state/add-profile! profile app-state)]
    (if item
      {:status 302
       :headers {"Location" (str "/profiles/" name)}}
      {:status 409
       :headers {"Refresh" "1"}
       :body "Profile already exists."})))

(defn handle-connect-profiles [req]
  (let [app-state      (:app-state req)
        origin-name    (:name (:route-params req))
        target-name    (get-in req [:params "target"])
        origin-profile (state/get-profile origin-name app-state)
        target-profile (state/get-profile target-name app-state)]
    (cond
      (nil? origin-profile) (profile-not-found-response origin-name)
      (nil? target-profile) (profile-not-found-response target-name)
      (state/connect-profiles! origin-name target-name app-state)
      {:status 200
       :headers {"Content-type" "text/plain"}
       :body "Profiles successfully connected."}
      :else {:status 409
             :headers {"Content-type" "text/plain"}
             :body "Profiles already connected."})))
