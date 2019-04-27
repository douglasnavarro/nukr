(ns nukr.handlers)

(defn home [req]
  {:status 200
   :body "Welcome to Nukr!"
   :headers {"content-type" "text/plain"}})
