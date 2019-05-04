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

; (deftest handle-connect-profiles-test
;   (testing "/profile/:name/connections endpoint"
;     (is (= {:status 200
;             :body "Connected!"
;             :headers {}}
;            (app (mock/request :put "profile/Cris/connections"))))))

; (deftest profiles-endpoint-test
;   (let [response (app (mock/request :get "/profiles"))]
;     (is (= 200 (:status response)))
;     (is (str/includes? (:body response) "Cris"))))
