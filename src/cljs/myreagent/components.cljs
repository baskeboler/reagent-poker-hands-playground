(ns myreagent.components
    (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
              [myreagent.poker :as poker]))
(defn row [label & body]
  [:div.row
   [:div.col.col-md-2 [:span label]]
   [:div.col.col-md-3 body]])

(def state (atom {
                   :doc {}
                   :saved? false
                   :hand (poker/random-hand)
                   :scores {}
                   :scores_count 0
                   }))

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

(defn card-list []
  [:div.row
   (for [card poker/all-cards]
     [:div.col.col-md-2.col-xs-3.card {:key card}
        (str (poker/rank card) " - " (poker/suit-name card)) ])])

(defn hand-component [hand]
    [:div.row
     (for [card hand]
       [:div.col.col-sm-2.card {:key card}
        (str (poker/rank card) " - " (poker/suit-name card))])
   ])
(defonce scores-count (atom 0))
(defonce scores (atom (sorted-map)))

(defn add-new-hand! [hand]
  (let [ id (swap! scores-count inc)]

  (swap! state assoc :hand hand)
  (swap! scores assoc-in [id] (poker/value hand))))
(defn init-poker! []
  (apply add-new-hand!
         (apply ()
           (repeat 1000 (poker/random-hand)))))

(defn new-hand-button []
  [:button.btn.btn-default {:on-click #(add-new-hand! (poker/random-hand))} "Otra mano"])

(defn distribution-table [freqs]
  [:table.table
   [:thead
    [:tr
    [:th "Score"] [:th "Times"] [:th "%"]]]
   [:tbody
   (do
     (for
     [[freq, times] freqs]
     [:tr {:key freq}
      [:td (str freq)]
      [:td (str times)]
      [:td (str (*
                  (/
                    times
                    @scores-count)
                  100))]]))]])


(defn poker-score []
  (let [print-freq (fn [arg]
                     (let [[freq, times] arg]
                       (str "Score "
                            (str freq)
                            ", "
                            (str times)
                            " times ("
                            (str
                              (*
                                (/
                                  times
                                  @scores-count)
                                100.0))
                            (str \%)
                            ")")))]
  [:div
   [:div (str "Score: " (poker/value (get-in @state [:hand])))]
   [:div (str "Jugadas: " @scores-count)]
   [:div (str "Distribucion: ")
    [distribution-table (frequencies
                                 (vals @scores))]]]))

(defn random-hand-component []
  [hand-component (get-in @state [:hand])])

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
