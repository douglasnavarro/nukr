(ns nukr.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :refer [not-found]]
            [nukr.handlers :refer :all]))

(defroutes app
  (GET "/" [] home)
  (not-found "Page not found."))

(defn -main [port]
  (jetty/run-jetty app                 {:port (Integer. port)}))

(defn -dev-main [port]
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
