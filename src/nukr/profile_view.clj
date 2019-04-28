(ns nukr.profile-view
  (require [hiccup.page :refer [html5]]))

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
           [:h1 "Nukr"]
           [:p (str profiles)]]
          [:script {:src "/materialize/js/materialize.min.js"}]]))
