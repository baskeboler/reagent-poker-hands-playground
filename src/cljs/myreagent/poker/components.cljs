(ns myreagent.poker.components
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [myreagent.poker.logic :as poker]))

(defonce scores-count (atom 0))

(defonce scores (atom (sorted-map)))

(defonce hands (atom (vector)))

(defn card-list []
  "Card list component"
  [:div.row
   (for [card poker/all-cards]
     [:div.col.col-md-2.col-xs-3.card {:key card}
      (str (poker/rank card) " - " (poker/suit-name card))])])

(defn hand-component [hand]
  "Hand display component"
  [:div.row
   (for [card hand]
     [:div.col.col-sm-2.card {:key card}
      (str (poker/rank card) " - " (poker/suit-name card))])])


(defn hand-by-id [id]
  (@hands id))

(defn current-hand []
  (hand-by-id (- @scores-count 1)))

(defn add-new-hand! [hand]
  (let [id (swap! scores-count inc)]
    (swap! hands assoc id hand)
    (swap! scores assoc id (poker/value hand))))

(defn reset-history! []
  (reset! scores (sorted-map))
  (reset! scores-count 0)
  (reset! hands (sorted-map)))

(defn reset-history-button []
  [:button.btn.btn-danger
   {:on-click #(reset-history!)} "Limpiar historial"])


(defn generate-hands! [times]
  (reset-history!)
  (loop [iteration 0]
   
    (if (>= iteration times)
      (.log js/console "Finished generating hands")
      (do
        (add-new-hand! (poker/random-hand))
        (recur (inc iteration))))))

(defn init-poker! []
  (generate-hands! 20000))

(defn generate-hands-button [times]
  [:button.btn.btn-default {:on-click #(generate-hands! times)} (str "Generate " times " hands")])

(defn new-hand-button []
  [:button.btn.btn-default {:on-click #(add-new-hand! (poker/random-hand))} "Otra mano"])

(defn distribution-table [freqs]
  [:table.table
   [:caption "Distribución"]
   [:thead
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
                           (current-hand)))]
     [:div (str "Jugadas: "  @scores-count)]
     [distribution-table (frequencies
                          (vals @scores))]]))

(defn random-hand-component []
  [hand-component (current-hand)])
