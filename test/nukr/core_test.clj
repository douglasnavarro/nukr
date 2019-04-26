(ns nukr.core-test
  (:require [clojure.test :refer :all]
            [nukr.core :refer :all]
            [ring.mock.request :as mock]))

(deftest home-handler-test
  (testing "home endpoint"
    (is (= (home (mock/request :get "/"))
           {:status 200
            :body   "Welcome to Nukr!"
            :headers {"content-type" "text/plain"}})
        " returns 200 and greeting message on GET")))
