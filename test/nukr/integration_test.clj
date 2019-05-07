(ns nukr.integration-test
  (:require [clojure.test :refer :all]
            [nukr.core :refer :all]
            [ring.mock.request :as mock]
            [clojure.string :refer [includes?]]))

(def ed-mock {:name "Ed" :connections #{} :hidden false})
(def rafa-mock {:name "Rafa" :connections #{} :hidden false})
(def cris-mock {:name "Cris" :connections #{} :hidden false})
(def connected-1 {:name "Ed" :connections #{"Cris"} :hidden false})
(def connected-2 {:name "Cris" :connections #{"Ed"} :hidden false})

(def cris-mock-serialized
  "{\"name\":\"Cris\",\"connections\":[],\"hidden\":false}")

(def connected-2-serialized
  "{\"name\":\"Cris\",\"connections\":[\"Ed\"],\"hidden\":false}")

(def json-and-close-headers
  {"Content-type" "application/json; charset=utf8"
   "Connection" "close"})

(deftest profiles-endpoint-GET
  (with-redefs [app-state (ref #{})]
    (let [response (app (-> (mock/request :get "/profiles")))]
      (is (= {:status 200
              :headers json-and-close-headers
              :body "[]"}
             response))))

  (with-redefs [app-state (ref #{cris-mock})]
    (let [response (app (-> (mock/request :get "/profiles")))]
      (is (= {:status 200
              :headers json-and-close-headers
              :body (str "[" cris-mock-serialized "]")}
             response))))

  (with-redefs [app-state (ref #{connected-2})]
    (let [response (app (-> (mock/request :get "/profiles")))]
      (is (= {:status 200
              :headers json-and-close-headers
              :body (str "[" connected-2-serialized "]")}
             response)))))

(deftest profiles-endpoint-POST
  (with-redefs [app-state (ref #{})]
    (let [response (app (-> (mock/request :post "/profiles")
                            (mock/query-string {:name "Cris"
                                                :hidden "false"})))]
      (is (= 302 (:status response)))
      (is (= "/profiles/Cris" (->> "Location" (get (:headers response)))))
      (is (= #{{:name "Cris" :connections #{} :hidden false}}
             @app-state)))))

(deftest profiles-endpoint-POST-already-exists
  (with-redefs [app-state (ref #{cris-mock})]
    (let [response (app (-> (mock/request :post "/profiles")
                            (mock/query-string {:name "Cris"})))]
      (is (= 409 (:status response))))))

(deftest connections-endpoint-PUT-new-connection
  (with-redefs [app-state (ref #{cris-mock ed-mock})]
    (let [response (app (-> (mock/request :put "/profiles/Cris/connections")
                            (mock/query-string {:target "Ed"})))]
      (is (= 200 (:status response)))
      (is (= "Profiles successfully connected." (:body response))))))

(deftest connections-endpoint-PUT-profile-not-found
  (with-redefs [app-state (ref #{cris-mock ed-mock})]
    (let [response (app (-> (mock/request :put "/profiles/David/connections")
                            (mock/query-string {:target "Ed"})))]
      (is (= 404 (:status response)))
      (is (= "profile David not found." (:body response))))))

(deftest connections-endpoint-PUT-already-connected
  (with-redefs [app-state (ref #{connected-1 connected-2})]
    (let [response (app (-> (mock/request :put "/profiles/Ed/connections")
                            (mock/query-string {:target "Cris"})))]
      (is (= 409 (:status response)))
      (is (= "Profiles already connected." (:body response))))))

(deftest connections-endpoint-GET
  (with-redefs [app-state (ref #{connected-1 connected-2})]
    (let [response (app (-> (mock/request :get "/profiles/Ed/connections")))]
      (is (= 200 (:status response)))
      (is (= "[\"Cris\"]" (:body response))))))

(deftest connections-endpoint-GET-not-found
  (with-redefs [app-state (ref #{})]
    (let [response (app (-> (mock/request :get "/profiles/Ed/connections")))]
      (is (= 404 (:status response))))))

(deftest suggestions-endpoint-GET
  (with-redefs [app-state (ref #{ed-mock cris-mock})]
    (let [response (app (-> (mock/request :get "/profiles/Ed/suggestions")))]
      (is (= 200 (:status response)))
      (is (= "[{\"relevance\":0,\"target\":\"Cris\"}]"
             (:body response)))))

  (with-redefs [app-state (ref #{connected-1 connected-2})]
    (let [response (app (-> (mock/request :get "/profiles/Ed/suggestions")))]
      (is (= 200 (:status response)))
      (is (= "[]"
             (:body response))))))

(deftest suggestions-endpoint-GET-not-found
  (with-redefs [app-state (ref #{ed-mock cris-mock})]
    (let [response
          (app (-> (mock/request :get "/profiles/Rafa/suggestions")))]
      (is (= 404 (:status response))))))

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
