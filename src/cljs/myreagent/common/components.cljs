(ns myreagent.common.components
  (:require [reagent.core :as reagent :refer [atom]]))

(def links {:home {:title "Home"
                   :url "/"}
            :about {:title "About"
                    :url "/about"}
             :joke {:title "Jokes"
                   :url "/chuck"}})


(defn page-header []
  [:nav.navbar.navbar-default
   [:div.container-fluid
    [:div.navbar-header
     [:div.navbar-brand
      [:a {:href "#"} "Playground"]]]
    [:ul.nav.navbar-nav
     (for [page-key (keys links)]
       (let [title (get-in links [page-key :title])
             url (get-in links [page-key :url])]
         [:li {:key page-key}
          [:a {:href url}  title]]))]]])

(def modal-dialog-stack (atom (vector)))
