(defproject nukr "0.1.0-SNAPSHOT"
  :description "Nukr project"
  :url "http://nubank.com.br"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [ring "1.6.3"]
                 [ring/ring-mock "0.3.2"]
                 [compojure "1.6.1" :exclusions [ring/ring-codec]]
                 [hiccup "1.0.5"]]

  :main nukr.core
  :profiles {:dev
             {:main nukr.core/-dev-main}}
  :plugins [[lein-eftest "0.5.7"]]
  :repl-options {:init-ns nukr.profile-logic})
