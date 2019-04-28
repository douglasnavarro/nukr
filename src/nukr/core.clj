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
            [nukr.handler :refer :all]))

(def app-state (model-state/create-profile-storage!))

(defn wrap-state [handler]
  "Middleware to pass state to handlers along with the request."
  (fn [req]
    (handler (assoc req :app-state app-state))))

(defroutes routes
  (ANY "/request" [] handle-dump)
  (GET  "/" [] handle-redirect-profiles)
  (GET  "/profiles" [] handle-list-profiles)
  (POST "/profiles" [] handle-create-profile)
  (not-found "Page not found."))

(def app
  (wrap-state
    (wrap-file-info
       (wrap-resource
         (wrap-params
           routes)
         "static"))))

(defn -main [port]
  (jetty/run-jetty
    app
    {:port (Integer. port)}))

(defn -dev-main [port]
  (jetty/run-jetty
    (wrap-reload #'app)
    {:port (Integer. port)}))
