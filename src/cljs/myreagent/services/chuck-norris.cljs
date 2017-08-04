(ns myreagent.services.chuck-norris
  (:require [ajax.core
             :as ajax
             :refer [GET POST]]
            [reagent.core :as reagent :refer [atom]]))

(def api-url "https://api.chucknorris.io/jokes/random")


(defn random-joke-handler
  ([custom-handler]
   (fn [resp]
     (do
       (println (str resp))
       (custom-handler (:value resp)))))
  ([]
   (fn []
     (random-joke-handler [identity]))))

(defn default-error-handler [{:keys [status status-text]}]
  (.log js/console (str
                    "Error code: "
                    status
                    " - "
                    status-text)))

(defn random-joke
  ([handler] (GET api-url
        {:handler (random-joke-handler handler)
         :error-handler default-error-handler
         :response-format :json
         :keywords? true
         }))
  ([] (random-joke identity)))
