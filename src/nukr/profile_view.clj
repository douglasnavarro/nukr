(ns nukr.profile-view
  "Views. Provides dynamic declarative html generation for handlers."
  (require [hiccup.page :refer [html5]]
           [hiccup.core :refer [html]]
           [nukr.profile-logic :refer :all]
           [nukr.profile-state :refer :all]))

(defn- render-profile-form []
  (html [:form {:method "POST" :action "/profiles"}
         [:input {:type "text" :name "name"
                  :placeholder "How should we call you? :)"}]
         [:p
          [:label
           [:input.filled-in {:type "checkbox" :name "hidden"}]
           [:span "I don't want to appear in suggestions"]]]
         [:button {:type "submit" :class "btn purple"} "Join"]]))

(defn- render-suggestion [sugg]
 [:p {:class "suggestion"}
  (:target sugg) ": " (:relevance sugg) [:label " in common"]])

(defn- render-suggestions [profile profiles]
  (if-let [suggestions (seq (get-suggestions profile profiles))]
    (html [:label "Suggestions"]
      (for [sugg suggestions] (render-suggestion sugg)))
    (html [:label "No suggestions."])))

(defn- render-connections [profile]
 (if-let [connections (seq (get-connections profile))]
   (html [:label "Connections"]
         [:p
          {:class "connection"}
          (clojure.string/join ", " connections)])
   (html [:label "No connections yet." [:br]])))

(defn- render-connect-links
  [profile profiles]
  (html
    [:a.dropdown-trigger
     {:href "#"
      :data-target (str "dropdown-" (get-name profile))}
     "CONNECT"]
    [:ul.dropdown-content {:id (str "dropdown-" (get-name profile))}
     (for [other-profile profiles]
       (if (and
             (not (= profile other-profile))
             (not (connected? profile other-profile)))
         [:li
          [:a {:id "dropdown-option"
               :onclick (format "makeConnectRequest(\"%s\", \"%s\")"
                                (get-name profile)
                                (get-name other-profile))}
              (get-name other-profile)]]))]))

(defn- render-card [profile profiles]
  (let [name (get-name profile)
        connections (seq (get-connections profile))]
    (html [:div.card.large
           [:div.card-image
            [:img {:src "/placeholders/avatar.jpg"}
             (if (hidden? profile)
               [:span.card-title
                [:i.material-icons.tiny "visibility_off"]])]]
           [:div.card-content
            [:span.card-title name]
            (render-connections profile)
            (render-suggestions profile profiles)]
           [:div.card-action.center-align
            (render-connect-links profile profiles)]])))

(defn- base-page [content]
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
          [:div.container content]
          [:script {:src "/materialize/js/materialize.min.js"}]
          [:script {:src "https://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"}]
          [:script {:src "script.js"}]]))

(defn profiles-page [profiles]
  (base-page
    (html
      [:div.row {:id "titles"}
        [:div {:class "col s12"}
         [:h1 "Nukr"]
         [:h5 "The future is "
          [:span.purple {:style "color: white;padding: .4%"} "purple"]
          " - and connected!"]
         [:hr]]]
      [:div.row {:id "profiles-list"}
       (if (seq profiles)
         (for [profile (sort-by :name profiles)]
           [:div {:class "col l2 m4 s12"}
            (render-card profile profiles)])
         [:div.col
          [:p "No profiles. Lead the way!"]])]
      [:div.row {:id "create-profile"}
       [:div {:class "col s4 push-s4 center-align"}
        (render-profile-form)]])))
