(ns myreagent.components
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [myreagent.poker.logic :as poker]))

(defn row [label & body]
  [:div.row
   [:div.col.col-md-2 [:span label]]
   [:div.col.col-md-3 body]])

(def state (atom {:doc {}
                  :saved? false
                  :hand (poker/random-hand)
                  :scores {}
                  :scores_count 0}))

(defn set-value! [id value]
  (swap! state assoc :saved? false)
  (swap! state assoc-in [:doc id] value))

(defn get-value [id]
  (get-in @state [:doc id]))

(defn text-input [id label]
  [row label
   [:input
    {:type "text"
     :class "form-control"
     :value (get-value id)
     :on-change #(set-value! id (-> % .-target .-value))}]])

(defn list-item [id k v selections]
  (letfn [(handle-click! []
            (swap! selections update-in [k] not)
            (set-value! id (->> @selections
                                (filter second)
                                (map first)))
            (.log js/console "Clicked!"))]
    [:li {:class (str "list-group-item"
                      (if (k @selections) " active"))
          :on-click handle-click!
          :key k}
     v]))

(defn selection-list [id label & items]
  (let [selections (->> items (map (fn [[k]]  [k false])) (into {}) atom)]
    (fn []
      [:div.row
       [:div.col.col-md-2 [:span label]]
       [:div.col.col-md-5
        [:div.row
         [:ul
          (for [[k v] items]
            [list-item id k v selections])]]]])))
