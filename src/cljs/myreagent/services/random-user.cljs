(ns myreagent.services.random-user
  (:require [ajax.core :refer [GET]]
            [reagent.core :refer [atom]]))

(def api-url "https://randomuser.me/api/")

(def current-user (atom (sorted-map)))

(defn custom-handler [handler]
  (fn [response]
    (do
      (.log js/console (str (first (:results response))))
      (handler (first  (:results response))))

    (defn error-handler [{:keys [status status-text]}]
      (.log js/console "there was an error"))))

(defn random-user
  ([handler]
   (GET api-url
        {:handler (custom-handler handler)
         :error-handler error-handler
         :keywords? true
         :response-format :json}))
  ([] (random-user identity)))

(defn user-handler [user]
  (reset! current-user user))

(defn user-thumbnail [user]
  (:large (:picture user)))

(defn user-full-name [user]
  (let [name (:name user)]
    (str (:title name) " " (:first name) " " (:last name))))

(defn fetch-random-user []
  (random-user user-handler))

(defn user-component ([user]
                      [:div.panel.panel-default
                       [:div.panel-heading
                        [:h3.panel-title "User"]]
                       [:div.panel-body
                        [:img.img-responsive.img.thumbnail {:src (user-thumbnail user)}]
                        [:div.user-name (user-full-name user)]
                        [:div (:email user)]
                        [:div (:phone user)]]
                       [:div.panel-footer
                        [:button.btn.btn-default {:on-click fetch-random-user} "Load other user"]]])
  ([] [user-component @current-user] ))
