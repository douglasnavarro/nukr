(ns nukr.handlers
 "Request handler functions.")

(defn home [req]
  {:status 200
   :body "Welcome to Nukr!"
   :headers {"content-type" "text/plain"}})
