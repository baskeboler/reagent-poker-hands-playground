(ns myreagent.core
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [myreagent.components :as components :refer [selection-list get-value set-value! text-input state]]
            [myreagent.poker.components :as poker]
            [myreagent.common.components :as common]
            [myreagent.services.chuck-norris :refer [random-joke]]
            [myreagent.services.random-user :refer [random-user user-component]]))

;; -------------------------
;; Views
(def current-joke (atom ""))

(defn joke-handler [joke]
  (do
    (reset! current-joke joke)))

(defn get-joke []
  (random-joke joke-handler))

(defn save-doc []
  (.log js/console (clj->js @state)))

(defn home-page []
  [:div.container
   [:h2 "Generar manos de poker"]
   [:div [poker/random-hand-component]]
   [poker/poker-score]
   [:div
    [poker/new-hand-button]
    [poker/reset-history-button]
    [poker/generate-hands-button 10000]]])

(def unvalor (atom "texto"))
(def user-info (atom {:nombre "Victor"
                      :user "baskeboler"
                      :email "baskeboler@gmail.com"}))

(defn uninput [miatomo label]
  [:div.form-group
   [:label label]
   [:input
    {:type "text"
     :class "form-control"
     :defaultValue @miatomo
     :on-change #(reset!
                  miatomo (-> % .-target .-value))}]])

(defn displayvalor [valor]
  [:div.form-control-static valor])

(defn about-page []
  [:div.container
   [:h2 "About myreagent"]
      [:div "Aca hay mas texto"]
   [:div "Y acá de nuevo"]
   [uninput unvalor "un valor"]
   [displayvalor @unvalor]
   [uninput (atom (:nombre user-info)) "Otro valor"]
   [displayvalor (:nombre @user-info)]])

(defn another-page []
  [:div.container
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

(defn joke-page []
  [:div.container
   [:h2 "Chuck Norris n' shit"]
   [:hr]
   [:blockquote
    [:p
     @current-joke]]
   [:button.btn.btn-default {:on-click get-joke} "Get joke!"]])

(defn user-page []
  [:div.container
   [:h2 "User details"]
   [user-component]])
;; -------------------------
;; Routes

(def page (atom #'home-page))

(defn current-page []
  [:div
   [common/page-header]
   [@page]])

(secretary/defroute "/" []
  (reset! page #'home-page))

(secretary/defroute "/about" []
  (reset! page #'about-page))
(secretary/defroute "/another" []
  (reset! page #'another-page))
(secretary/defroute "/chuck" []
  (reset! page #'joke-page))
(secretary/defroute "/user" []
  (reset! page #'user-page))
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
  (poker/init-poker!)
  (get-joke)
  (accountant/dispatch-current!)
  (mount-root))
