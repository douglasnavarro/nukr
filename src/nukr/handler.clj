(ns nukr.handler
  "Controller. Implements web API through request handling."
  (require [nukr.profile-view :as view]
           [nukr.profile-state :as state]
           [nukr.profile-logic :as logic]
           [clojure.data.json :as json]))

(defn- profile-not-found-response [name]
  {:status 404
   :headers {"Connection" "close"}
   :body (str "profile " name " not found.")})

(def map-hidden {"true" true "false" false})

(def json-and-close
  {"Content-type" "application/json; charset=utf8"
   "Connection" "close"})

(defn handle-list-profiles [req]
  (let [app-state (:app-state req)
        profiles @app-state]
    {:status 200
     :headers json-and-close
     :body (json/write-str profiles)}))

(defn handle-get-profile [req]
  (let [app-state (:app-state req)
        name      (:name (:route-params req))
        profile   (state/get-profile name app-state)]
    (if profile
      {:status 200
       :headers json-and-close
       :body (json/write-str profile)}
      {:status 404
       :headers json-and-close})))

(defn handle-create-profile [req]
  (let [app-state  (:app-state req)
        name       (get-in req [:params "name"])
        hidden     (get map-hidden (get-in req [:params "hidden"]))
        profile    (logic/create name hidden)
        item       (state/add-profile! profile app-state)]
    (if item
      {:status 302
       :headers (merge
                 {"Location" (str "/profiles/" name)}
                 json-and-close)}
      {:status 409
       :headers {"Connection" "close"}
       :body "Profile already exists."})))

(defn handle-get-connections [req]
  (let [app-state (:app-state req)
        name (:name (:route-params req))
        profile (state/get-profile name app-state)]
    (if profile
      {:status 200
       :headers json-and-close
       :body (json/write-str (logic/get-connections profile))}
      (profile-not-found-response name))))

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

(defn handle-get-suggestions [req]
  (let [app-state (:app-state req)
        name (:name (:route-params req))
        profile (state/get-profile name app-state)]
    (if profile
      {:status 200
       :headers json-and-close
       :body (json/write-str (state/get-suggestions profile @app-state))}
      (profile-not-found-response name))))

(defn handle-html-view [req]
  (let [app-state (:app-state req)
        profiles @app-state]
    {:status 200
     :headers {"Content-type" "text/html; charset=utf-8"}
     :body (view/profiles-page profiles)}))
