(ns myreagent.poker)

(def rank-replacements
  {\T 10
   \J 11
   \Q 12
   \K 13
   \A 14})

(defn rank [card]
  (let [[fst _] card]
    (if
     (not
      (js/isNaN
       (js/parseInt (str fst))))
      (js/parseInt (str fst))
      (get rank-replacements fst))))

(defn max-rank [hand]
  (let [ranks (map rank hand)]
    (apply max ranks)))

(defn card [c]
  c)

(def all-ranks (concat
                (range 2 10)
                [\T \J \Q \K \A]))
all-ranks
(def all-suits [\C \D \H \S])
(def suit-names
  {"C" "Clubs"
   "D" "Diamonds"
   "H" "Hearts"
   "S" "Spades"})

all-suits
(def all-cards
  (for [rank all-ranks
        suit all-suits]
    (card (str rank suit))))
all-cards

(defn random-hand []
  (take 5
        (shuffle all-cards)))
(random-hand)

(rank "AH")
(rank "2H") ;=> 2
(rank "4S") ;=> 4
(rank "TS") ;=> 10
(rank "JS") ;=> 11
(rank "QS") ;=> 12
(rank "KS") ;=> 13
(rank "AS") ;=> 14

(defn suit [card]
  (let [[_ snd] card]
    (str snd)))

(defn suit-name [card]
  (get suit-names
       (suit card)))
(suit "4K")
(frequencies [1 2 3 1 1 2])

; hands
;; (def high-seven                   ["2H" "3S" "4C" "5C" "7D"])
;; (def pair-hand                    ["2H" "2S" "4C" "5C" "7D"])
;; (def two-pairs-hand               ["2H" "2S" "4C" "4D" "7D"])
;; (def three-of-a-kind-hand         ["2H" "2S" "2C" "4D" "7D"])
;; (def four-of-a-kind-hand          ["2H" "2S" "2C" "2D" "7D"])
;; (def straight-hand                ["2H" "3S" "6C" "5D" "4D"])
;; (def low-ace-straight-hand        ["2H" "3S" "4C" "5D" "AD"])
;; (def high-ace-straight-hand       ["TH" "AS" "QC" "KD" "JD"])
;; (def flush-hand                   ["2H" "4H" "5H" "9H" "7H"])
;; (def full-house-hand              ["2H" "5D" "2D" "2C" "5S"])
;; (def straight-flush-hand          ["2H" "3H" "6H" "5H" "4H"])
;; (def low-ace-straight-flush-hand  ["2D" "3D" "4D" "5D" "AD"])
;; (def high-ace-straight-flush-hand ["TS" "AS" "QS" "KS" "JS"])


(defn max-repetitions [hand]
  (let [reps (vals
              (frequencies
               (map rank hand)))]
    (apply max reps)))

(defn pair? [hand]
  (==
   (max-repetitions hand)
   2))
;; (apply max
;;   (vals
;;     (frequencies
;;       (map rank high-seven))))
;; (pair? high-seven)
;; (pair? pair-hand)
;; (pair? two-pairs-hand)
;; (pair? three-of-a-kind-hand)

(defn three-of-a-kind? [hand]
  (==
   (max-repetitions hand)
   3))
;; (three-of-a-kind? two-pairs-hand)
;; (three-of-a-kind? three-of-a-kind-hand)


(defn four-of-a-kind? [hand]
  (==
   (max-repetitions hand)
   4))

(defn flush? [hand]
  (let [suits (map suit hand)]
    (==
     (apply max
            (vals
             (frequencies
              suits)))
     5)))
;; (flush? flush-hand)
;; (flush? high-seven)
;; (flush? four-of-a-kind-hand)


(defn full-house? [hand]
  (=
   [2 3]
   (sort
    (vals
     (frequencies
      (map rank hand))))))
;; (full-house? full-house-hand)
;; (full-house? low-ace-straight-hand)

(defn two-pairs? [hand]
  (=
   [1 2 2]
   (sort
    (vals
     (frequencies
      (map rank hand))))))
;; (two-pairs? two-pairs-hand)
;; (two-pairs? three-of-a-kind-hand)
;; (two-pairs? pair-hand)

(defn straight? [hand]
  (let [ranks (sort (map rank hand))
        max-rank (apply max ranks)
        has-ace (== max-rank 14)
        min-rank  (apply min ranks)
        low-straight-seq (if
                          has-ace
                           (range 1 6)
                           (range min-rank (+ min-rank 5)))
        straight-seqs (if
                       has-ace
                        [(range 10 15) (sort
                                        (cons
                                         14
                                         (range 2 6)))]
                        [low-straight-seq])]
    (reduce (fn [a b] (if a a b))
            (map (fn [ss] (= ranks ss)) straight-seqs))))

;; (straight? straight-hand)
(defn straight-flush? [hand]
  (if
   (straight? hand)
    (flush? hand)
    false))
;; (straight-flush? straight-hand)
;; (straight-flush? straight-flush-hand)

(defn value [hand]
  (cond
    (straight-flush? hand) 8
    (four-of-a-kind? hand) 7
    (full-house? hand) 6
    (flush? hand) 5
    (straight? hand) 4
    (three-of-a-kind? hand) 3
    (two-pairs? hand) 2
    (pair? hand) 1
    :else 0))
(let [h (random-hand)] [(value h) h])

(defn hand-score-description [hand]
  (let [score (value hand)]
    (cond
      (== score 0) (str "Un " (max-rank hand))
      (== score 1) "Un par"
      (== score 2) "Doble par"
      (== score 3) "Pierna"
      (== score 4) "Escalera"
      (== score 5) "Color"
      (== score 6) "Full house"
      (== score 7) "Poker"
      (== score 8) "Escalera Real")))
(def poker-score-mapping {0 "Carta más alta"
                          1 "Par"
                          2 "Doble par"
                          3 "Pierna"
                          4 "Escalera"
                          5 "Flush/Color"
                          6 "Full"
                          7 "Poker"
                          8 "Escalera Real"})
