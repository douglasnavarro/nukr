(ns nukr.profile-view
  "Views. Provides declarative html generation for handlers."
  (require [hiccup.page :refer [html5]]))

(defn- create-profile-form []
  (html5 [:form {:method "POST" :action "/request"}
          [:input {:type "text" :name "name"
                   :placeholder "How should we call you? :)"}]
          [:p
           [:label
            [:input.filled-in {:type "checkbox" :name "hidden"}]
            [:span "I don't want suggestions"]]]
          [:button {:type "submit" :class "btn purple"} "Join"]]))

(defn profiles-page [profiles]
  (html5 {:lang :en}
         [:head
          [:title "Nukr"]
          [:meta {:name :viewport
                  :content "width=device-width, initial-scale=1.0"}]
          [:link {:href "/materialize/css/materialize.min.css"
                  :rel :stylesheet}]]
         [:body
          [:div.container
           [:div.row {:id "titles"}
             [:div {:class "col s12"}
              [:h1 "Nukr"]
              [:h5 "The future is purple - and connected!"]
              [:hr]]]
           [:div.row {:id "profiles-list"}
            [:p (str profiles)]]
           [:div.row {:id "create-profile"}
            [:div {:class "col s6 push-s4 center-align"}
             (create-profile-form)]]]
          [:script {:src "/materialize/js/materialize.min.js"}]]))
