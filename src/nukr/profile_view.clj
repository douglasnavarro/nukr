(ns nukr.profile-view
  "Views. Provides dynamic declarative html generation for handlers."
  (require [hiccup.page :refer [html5]]
           [nukr.profile-logic :refer :all]))

(defn- create-profile-form []
  (html5 [:form {:method "POST" :action "/profiles"}
          [:input {:type "text" :name "name"
                   :placeholder "How should we call you? :)"}]
          [:p
           [:label
            [:input.filled-in {:type "checkbox" :name "hidden"}]
            [:span "I don't want suggestions"]]]
          [:button {:type "submit" :class "btn purple"} "Join"]]))

(defn- create-card [profile profiles]
  (let [name (get-name profile)]
    (html5 [:div.card
            [:div.card-image
             [:img {:src "/placeholders/avatar.jpg"}
              (if (hidden? profile)
                [:span.card-title
                 [:i.material-icons.tiny "visibility_off"]])]]
            [:div.card-content
             [:div.row.center-align
              [:span.card-title name]]
             [:div.row.center-align
              [:a.dropdown-trigger.btn-small
               {:href "#"
                :data-target (str "dropdown-" name)}
               "Connect"]
              [:ul.dropdown-content {:id (str "dropdown-" name)}
               (for [other-profile profiles]
                 (if (not (= profile other-profile))
                   [:li
                    [:a {:id "dropdown-option"
                         :onclick (format "makePutRequest(\"%s\", \"%s\")"
                                          name
                                          (get-name other-profile))}
                        (get-name other-profile)]]))]]
             [:div.row.center-align
              [:p (get-connections profile)]]]])))

(defn profiles-page [profiles]
  (html5 {:lang :en}
         [:head
          [:title "Nukr"]
          [:meta {:name :viewport :charset "UTF-8"
                  :content "width=device-width, initial-scale=1.0"}]
          [:link {:href "/materialize/css/materialize.min.css"
                  :rel :stylesheet}]
          [:link {:href "style.css"
                  :rel :stylesheet}]
          [:link {:href "https://fonts.googleapis.com/icon?family=Material+Icons"
                  :rel :stylesheet}]]
         [:body
          [:div.container
           [:p profiles]
           [:div.row {:id "titles"}
             [:div {:class "col s12"}
              [:h1 "Nukr"]
              [:h5 "The future is purple - and connected!"]
              [:hr]]]
           [:div.row {:id "profiles-list"}
            (if (seq profiles)
              (for [profile profiles]
                [:div {:class "col l3 m4 s12"}
                 (create-card profile profiles)])
              [:div.col
               [:p "No profiles. Lead the way!"]])]
           [:div.row {:id "create-profile"}
            [:div {:class "col s4 push-s4 center-align"}
             (create-profile-form)]]]
          [:script {:src "/materialize/js/materialize.min.js"}]
          [:script {:src "https://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"}]
          [:script {:src "script.js"}]]))
