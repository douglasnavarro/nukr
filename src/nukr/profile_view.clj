(ns nukr.profile-view
  "Views. Provides dynamic declarative html generation for handlers."
  (require [hiccup.page :refer [html5]]
           [nukr.profile-logic :as profile-logic]))

(defn- create-profile-form []
  (html5 [:form {:method "POST" :action "/profiles"}
          [:input {:type "text" :name "name"
                   :placeholder "How should we call you? :)"}]
          [:p
           [:label
            [:input.filled-in {:type "checkbox" :name "hidden"}]
            [:span "I don't want suggestions"]]]
          [:button {:type "submit" :class "btn purple"} "Join"]]))

(defn- create-card [profile]
  (html5 [:div.card
          [:div.card-image
           [:img {:src "/placeholders/avatar.jpg"}]]
          [:div.card-content
           [:span.card-title (profile-logic/get-name profile)]
           (if (profile-logic/hidden? profile)
             [:i.material-icons "notifications_off"])]]))

(defn- update-profile-form [name])

(defn profiles-page [profiles]
  (html5 {:lang :en}
         [:head
          [:title "Nukr"]
          [:meta {:name :viewport
                  :content "width=device-width, initial-scale=1.0"}]
          [:link {:href "/materialize/css/materialize.min.css"
                  :rel :stylesheet}]
          [:link {:href "https://fonts.googleapis.com/icon?family=Material+Icons"
                  :rel :stylesheet}]]
         [:body
          [:div.container
           [:div.row {:id "titles"}
             [:div {:class "col s12"}
              [:h1 "Nukr"]
              [:h5 "The future is purple - and connected!"]
              [:hr]]]
           [:div.row {:id "profiles-list"}
            (if (seq profiles)
              (for [profile profiles]
                [:div {:class "col l3 m4 s12"}
                 (create-card profile)])
              [:div.col
               [:p "No profiles. Lead the way!"]])]
           [:div.row {:id "create-profile"}
            [:div {:class "col s4 push-s4 center-align"}
             (create-profile-form)]]]
          [:script {:src "/materialize/js/materialize.min.js"}]]))
