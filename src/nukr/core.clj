(ns nukr.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :refer [not-found]]))

(defn home [req]
  {:status 200
   :body "Welcome to Nukr!"
   :headers {"content-type" "text/plain"}})

(defn create-profile [req])

(defroutes app
  (GET "/" [] home)
  (POST "/profiles/create" [] create-profile)
  (not-found "Page not found."))

(defn -main [port]
  (jetty/run-jetty app                 {:port (Integer. port)}))

(defn -dev-main [port]
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
