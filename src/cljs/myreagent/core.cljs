(ns myreagent.core
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [myreagent.components :as components :refer [selection-list get-value set-value! text-input state]]
            [ajax.core :refer [POST]]))

;; -------------------------
;; Views

;; (defn save-doc []
;;   (POST (str js/context "/save")
;;         {:params (:doc @state)
;;          :handler (fn [_] (swap! state assoc :saved? true))}))

(defn save-doc []
  (.log js/console (clj->js @state)))

(defn home-page []
  [:div.container
   [:h2 "Generar manos de poker"]
   [:div [components/random-hand-component]]
   [components/poker-score]
   [:div [components/new-hand-button] [components/reset-history-button] [components/generate-hands-button 10000]]
   [:div [:a {:href "/about"} "go to about page"]]
   [:div [:a {:href "/another"} "go to another page"]]])

(def unvalor (atom "texto"))
(def user-info (atom {:nombre "Victor"
                      :user "baskeboler"
                      :email "baskeboler@gmail.com"}))

(defn uninput [miatomo]
  [:div
   [:input
    {:type "text" :defaultValue @miatomo :on-change #(reset! miatomo (-> % .-target .-value))}]])
(defn displayvalor [valor]
  [:div valor])

(defn about-page []
  [:div [:h2 "About myreagent"]
   [:div [:a {:href "/"} "go to the home page"]]
   [:div "Aca hay mas texto"]
   [:div "Y acá de nuevo"]
   [uninput unvalor]
   [displayvalor @unvalor]
   [uninput (atom (:nombre user-info))]
   [displayvalor (:nombre @user-info)]])
(defn another-page []
  [:div
   [:div.page-header [:h1 "Reagent Form"]]
   [text-input :first-name "First name"]
   [text-input :last-name "Last name"]
   [selection-list :favorite-drinks "Favorite drinks"
    [:coffee "Coffee"]
    [:beer "Beer"]
    [:crab-juice "Crab juice"]]
   [:button
    {:type "submit"
     :class "btn btn-default"
     :on-click save-doc} "Save"]])

;; -------------------------
;; Routes

(def page (atom #'home-page))

(defn current-page []
  [:div [@page]])

(secretary/defroute "/" []
  (reset! page #'home-page))

(secretary/defroute "/about" []
  (reset! page #'about-page))
(secretary/defroute "/another" []
  (reset! page #'another-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (secretary/dispatch! path))
    :path-exists?
    (fn [path]
      (secretary/locate-route path))})
  (components/init-poker!)
  (accountant/dispatch-current!)
  (mount-root))
