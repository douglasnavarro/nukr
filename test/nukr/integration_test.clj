(ns nukr.integration-test
  (:require [clojure.test :refer :all]
            [nukr.core :refer :all]
            [ring.mock.request :as mock]
            [clojure.string :refer [includes?]]))

(def ed-mock {:name "Ed" :connections #{} :hidden false})
(def cris-mock {:name "Cris" :connections #{} :hidden false})
(def connected-1 {:name "Ed" :connections #{"Cris"} :hidden false})
(def connected-2 {:name "Cris" :connections #{"Ed"} :hidden false})

(deftest profiles-endpoint-GET
  (with-redefs [app-state (ref #{})]
    (let [response (app (-> (mock/request :get "/profiles")))]
      (is (= {:status 200
              :headers {"Content-type" "application/json; charset=utf8"}
              :body "[]"}
             response))))
  (with-redefs [app-state (ref #{cris-mock})]
    (let [response (app (-> (mock/request :get "/profiles")))]
      (is (= {:status 200
              :headers {"Content-type" "application/json; charset=utf8"}
              :body
               "[{\"name\":\"Cris\",\"connections\":[],\"hidden\":false}]"}
             response))))
  (with-redefs [app-state (ref #{connected-2})]
    (let [response (app (-> (mock/request :get "/profiles")))]
      (is (= {:status 200
              :headers {"Content-type" "application/json; charset=utf8"}
              :body
               "[{\"name\":\"Cris\",\"connections\":[\"Ed\"],\"hidden\":false}]"}
             response)))))

(deftest profiles-endpoint-POST-redirects
  (with-redefs [app-state (ref #{})]
    (let [response (app (-> (mock/request :post "/profiles")
                            (mock/query-string {:name "Cris"})))]
      (is (= 302 (:status response)))
      (is (= "/profiles/Cris" (->> "Location" (get (:headers response))))))))

(deftest profiles-endpoint-POST-already-exists
  (with-redefs [app-state (ref #{cris-mock})]
    (let [response (app (-> (mock/request :post "/profiles")
                            (mock/query-string {:name "Cris"})))]
      (is (= 409 (:status response))))))

(deftest profiles-endpoint-PUT-new-connection
  (with-redefs [app-state (ref #{cris-mock ed-mock})]
    (let [response (app (-> (mock/request :put "/profiles/Cris/connections")
                            (mock/query-string {:target "Ed"})))]
      (is (= 200 (:status response)))
      (is (= "Profiles successfully connected." (:body response))))))

(deftest profiles-endpoint-PUT-profile-not-found
  (with-redefs [app-state (ref #{cris-mock ed-mock})]
    (let [response (app (-> (mock/request :put "/profiles/David/connections")
                            (mock/query-string {:target "Ed"})))]
      (is (= 404 (:status response)))
      (is (= "profile David not found." (:body response))))))

(deftest profiles-endpoint-PUT-already-connected
  (with-redefs [app-state (ref #{connected-1 connected-2})]
    (let [response (app (-> (mock/request :put "/profiles/Ed/connections")
                            (mock/query-string {:target "Cris"})))]
      (is (= 409 (:status response)))
      (is (= "Profiles already connected." (:body response))))))

;; single-view web app tests
(deftest root-endpoint-GET-no-profiles-test
  (testing "GET / with no profiles"
    (with-redefs [app-state (ref #{})]
      (let [response (app (mock/request :get "/"))]
        (is (= 200 (:status response)))
        (is (includes? (:body response) "No profiles"))))))

(deftest root-endpoint-GET-unconnected-profile-test
  (testing "GET / with one unconnected profile"
    (with-redefs [app-state (ref #{ed-mock})]
      (let [response (app (mock/request :get "/"))]
        (is (= 200 (:status response)))
        (is (includes? (:body response)
                       "<span class=\"card-title\">Ed</span>"))
        (is (includes? (:body response)
                       "No connections"))
        (is (includes? (:body response)
                       "No suggestions"))))))

(deftest root-endpoint-GET-connected-profiles-test
  (testing "GET / with connected profiles"
    (with-redefs [app-state (ref #{connected-1 connected-2})]
      (let [response (app (mock/request :get "/"))]
        (is (= 200 (:status response)))
        (is (includes? (:body response)
                       "<span class=\"card-title\">Ed</span>"))
        (is (includes? (:body response)
                       "<p class=\"connection\">Cris</p>"))
        (is (includes? (:body response)
                       "<span class=\"card-title\">Cris</span>"))
        (is (includes? (:body response)
                       "<p class=\"connection\">Ed</p>"))
        (is (includes? (:body response) "No suggestions"))))))

(deftest root-endpoint-GET-shows-suggestions
  (testing "GET / with connected profiles"
    (with-redefs [app-state (ref #{ed-mock cris-mock})]
      (let [response (app (mock/request :get "/"))]
        (is (= 200 (:status response)))
        (is (includes? (:body response)
                       "<p class=\"suggestion\">Cris: 0"))
        (is (includes? (:body response)
                       "<p class=\"suggestion\">Ed: 0"))))))

(deftest root-endpoint-GET-shows-suggestions
  (testing "GET / with connected profiles"
    (with-redefs [app-state (ref #{ed-mock cris-mock})]
      (let [response (app (mock/request :get "/"))]
        (is (= 200 (:status response)))
        (is (includes? (:body response)
                       "<p class=\"suggestion\">Cris: 0"))
        (is (includes? (:body response)
                       "<p class=\"suggestion\">Ed: 0"))))))
