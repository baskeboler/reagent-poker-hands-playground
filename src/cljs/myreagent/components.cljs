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

(defn reset-history! []
  (reset! scores (sorted-map))
  (reset! scores-count 0))
(defn reset-history-button []
  [:button.btn.btn-danger
   {:on-click #(reset-history!)} "Limpiar historial"])


(defn generate-hands! [times]
    (reset-history!)
    (dotimes [_ times]
      (add-new-hand! (poker/random-hand))))

(defn init-poker! []
  (generate-hands! 100000))

(defn generate-hands-button [times]
  [:button.btn.btn-default {:on-click #(generate-hands! times)} (str "Generate " times " hands")])
(defn new-hand-button []
  [:button.btn.btn-default {:on-click #(add-new-hand! (poker/random-hand))} "Otra mano"])

(defn distribution-table [freqs]
  [:table.table
   [:thead
    [:caption "Distribución"]
    [:tr
    [:th "Score"] [:th "Times"] [:th "%"]]]
   [:tbody
     (for
     [[freq, times] freqs]
     [:tr {:key freq}
      [:td (poker/poker-score-mapping freq)]
      [:td (str times)]
      [:td (str (.toPrecision
                  (*
                    (/
                      times
                      @scores-count)
                    100)
                  4))]])]])


(defn poker-score []
  (let [print-freq (fn [arg]
                     (let [[freq, times] arg]
                       (str "Score "
                            (str freq)
                            ", "
                            (str times)
                            " times ("
                            (str
                              (.toPrecision (*
                                (/
                                  times
                                  @scores-count)
                                100.0) 5))
                            (str \%)
                            ")")))]
  [:div
   [:div (str "Score: " (poker/hand-score-description
                          (get-in @state [:hand])))]
   [:div (str "Jugadas: " @scores-count)]
   [distribution-table (frequencies
                                 (vals @scores))]]))

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
