(ns nukr.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.handler.dump :refer [handle-dump]]
            [compojure.core :refer [defroutes GET POST ANY]]
            [compojure.route :refer [not-found]])

  (:require [nukr.profile-model :as model-state]
            [nukr.profile-logic :as model-logic]
            [nukr.profile-view :as profile-view]))

(def app-state (model-state/create-profile-storage!))

(defn home [req]
  {:status 200
   :body (profile-view/profiles-page @app-state)
   :headers {}})

(defn list-profiles [req]
  (let [profiles @app-state]
    {:status 200
     :headers {}
     :body (str "<html><head></head><body><div>"
                profiles
                "</div><form method=\"POST\" action=\"/profiles\">"
                "<input type=\"text\" name=\"id\" placeholder=\"id\">"
                "<input type=\"text\" name=\"name\" placeholder=\"name\">"
                "<input type=\"submit\">"
                "</body></html>")}))

(defn create-profile [req]
  (let [id      (Integer. (get-in req [:params "id"]))
        name    (get-in req [:params "name"])
        profile (model-logic/create id name)
        item    (model-state/add-profile profile app-state)])
  {:status 302
   :headers {"Location" "/profiles"}
   :body ""})

(defroutes routes
  (ANY "/request" [] handle-dump)
  (GET  "/" [] home)
  (GET  "/profiles" [] list-profiles)
  (POST "/profiles" [] create-profile)
  (not-found "Page not found."))

(def app
  (wrap-file-info
     (wrap-resource
       (wrap-params
         routes)
       "static")))

(defn -main [port]
  (jetty/run-jetty
    app
    {:port (Integer. port)}))

(defn -dev-main [port]
  (jetty/run-jetty
    (wrap-reload #'app)
    {:port (Integer. port)}))
