(ns nukr.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.handler.dump :refer [handle-dump]]
            [compojure.core :refer [defroutes GET POST PUT ANY]]
            [compojure.route :refer [not-found]])

  (:require [nukr.profile-model :as model-state]
            [nukr.handler :refer :all]))

(def app-state (model-state/create-profile-storage!))

(defn wrap-state [handler]
  "Middleware to pass state to handlers along with the request."
  (fn [req]
    (handler (assoc req :app-state app-state))))

(def sim-methods {"PUT" :put
                  "DELETE" :delete})

(defn wrap-simulated-methods [handler]
  (fn [req]
    (if-let [method (and (= :post (:request-method req))
                         (sim-methods (get-in req [:params "_method"])))]
      (handler (assoc req :request-method method))
      (handler req))))

(defroutes routes
  (ANY "/request" [] handle-dump)
  (GET  "/" [] handle-redirect-profiles)
  (GET  "/profiles" [] handle-list-profiles)
  (POST "/profiles" [] handle-create-profile)
  (PUT "/profiles/:name" [] handle-update-profile)
  (GET "/profiles/:name/suggestions" [] handle-dump)
  (not-found "Page not found."))

(def app
  (wrap-simulated-methods
    (wrap-state
      (wrap-file-info
         (wrap-resource
           (wrap-params
             routes)
           "static")))))

(defn -main [port]
  (jetty/run-jetty
    app
    {:port (Integer. port)}))

(defn -dev-main [port]
  (jetty/run-jetty
    (wrap-reload #'app)
    {:port (Integer. port)}))
