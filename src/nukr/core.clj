(ns nukr.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.core :refer [defroutes GET POST PUT ANY]]
            [compojure.route :refer [not-found]])

  (:require [nukr.profile-state :as model-state]
            [nukr.handler :refer :all]))

(def app-state (model-state/create-profile-storage!))

(defn wrap-state [handler]
  "Middleware to pass state to handlers along with the request."
  (fn [req]
    (handler (assoc req :app-state app-state))))

(defroutes routes
  (GET  "/" [] handle-html-view)
  (GET  "/profiles" [] handle-list-profiles)
  (POST "/profiles" [] handle-create-profile)
  (GET "/profiles/:name" [] handle-get-profile)
  (GET "/profiles/:name/connections" [] handle-get-connections)
  (PUT "/profiles/:name/connections" [] handle-connect-profiles)
  (GET "/profiles/:name/suggestions" [] handle-get-suggestions)
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
