(ns nukr.integration-test
  (:require [clojure.test :refer :all]
            [nukr.core :refer :all]
            [ring.mock.request :as mock]))

(deftest home-handler-test
  (testing "home endpoint"
    (is (= {:status  200
            :body    "Welcome to Nukr!"
            :headers {"content-type" "text/plain"}}
           (app (mock/request :get "/")))
        " returns 200 and greeting message on GET")
    (is (= 404
           (:status (app (mock/request :post "/"))))
        " returns 404 on POST")))
