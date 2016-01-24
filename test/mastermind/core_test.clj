(ns mastermind.core-test
  (:require [clojure.test :refer :all]
            [mastermind.core :refer :all]))

(deftest color-and-position-matches-exact-matches-only
	(is (= 0 (color-and-position-matches [1 1 1 1] [2 3 4 5])) "Different numbers entirely")
  (is (= 0 (color-and-position-matches [1 2 3 4] [3 4 2 1])) "Same numbers but wrong positions")
  (is (= 1 (color-and-position-matches [1 1 1 1] [1 2 3 4])) "One match for simple secret")
  (is (= 1 (color-and-position-matches [1 2 3 4] [1 3 4 2])) "One match for another secret")
  (is (= 2 (color-and-position-matches [1 1 1 1] [1 1 3 4])) "Two matches for simple secret")
  (is (= 2 (color-and-position-matches [1 2 3 4] [4 2 3 1])) "Two matches for another secret")
  (is (= 3 (color-and-position-matches [1 1 1 1] [1 1 1 4])) "Three matches for simple secret")
  (is (= 3 (color-and-position-matches [1 2 3 4] [5 2 3 4])) "Three matches for another secret")
  (is (= 4 (color-and-position-matches [1 1 1 1] [1 1 1 1])) "Four matches for a simple secret - a winning guess")
  (is (= 4 (color-and-position-matches [1 2 3 4] [1 2 3 4])) "Four matches for a another secret - a winning guess")
)

(deftest color-only-matches-same-color-different-positions
  (is (= 0 (color-only-matches [1 2 3 4] [5 6 5 6])) "No matches")
  (is (= 0 (color-only-matches [1 2 3 4] [1 6 5 4])) "Ignores exact matches, no matches")
  (is (= 1 (color-only-matches [1 2 3 4] [5 5 5 1])) "One match")
  (is (= 1 (color-only-matches [1 2 3 4] [5 1 1 1])) "Surplus guessed")
  (is (= 1 (color-only-matches [1 1 2 2] [5 5 3 1])) "More colors in secret than guessed")
  (is (= 1 (color-only-matches [1 2 3 4] [1 6 3 2])) "Ignores exact matches, One match")
  (is (= 2 (color-only-matches [1 2 3 4] [5 1 6 2])) "Two matches")
  (is (= 3 (color-only-matches [1 2 3 4] [5 3 2 1])) "Three matches")
  (is (= 4 (color-only-matches [1 1 2 2] [2 2 1 1])) "Double colors, all matched in wrong position")
  (is (= 0 (color-only-matches [1 2 3 4] [1 2 3 4])) "Winning guess has no matches")
)

(deftest score-guess-reports-guess-and-exact-and-inexact-matches
  (is (= { :guess [1 4 3 8] :exact-match? false :color-and-position-matches 2 :color-only-matches 1 } (score-guess [1 2 3 4] [1 4 3 8])))
  (is (true? (:exact-match? (score-guess [1 2 3 4] [1 2 3 4]))))
)

(deftest correct-guess-is-true-for-exact-match
  (is (true? (correct-guess? [1 2 3 4] [1 2 3 4])))
  (is (false? (correct-guess? [1 2 3 4] [4 3 2 1])))
  (is (false? (correct-guess? [1 2 3 4] [5 6 7 8])))
)

(deftest game-status-determines-win-lose-or-playing
  (is (= :won (game-status true 1 12)) "Win if won? is true")
  (is (= :playing (game-status false 1 12)) "Playing if not won and less than game max turns")
  (is (= :playing (game-status false 11 12)) "Playing if one turn remaining")
  (is (= :lost (game-status false 12 12)) "Lost if # turns taken = game max turns")
)

(deftest init-game-state-structure
  (is (= { :max-turns 12 :secret [1 2 3 4] :turns [] :state :playing } (init-game-state [1 2 3 4])))
)

(deftest play-winning-guess
  (is (= { :max-turns 12 
           :secret [1 2 3 4] 
           :turns [ 
             { :guess [ 1 2 3 4] 
               :exact-match? true 
               :color-and-position-matches 4
               :color-only-matches 0 } ]
           :state :won }
        (guess [ 1 2 3 4 ] 
          { :max-turns 12 
            :secret [ 1 2 3 4 ] 
            :turns [ ]
            :state :playing }
          )))
)

(deftest play-incorrect-guess
  (is (= { :max-turns 12 
           :secret [1 2 3 4] 
           :turns [ 
             { :guess [ 5 5 5 5] 
               :exact-match? false 
               :color-and-position-matches 0 
               :color-only-matches 0 } ]
           :state :playing }
        (guess [ 5 5 5 5 ] 
          { :max-turns 12 
            :secret [ 1 2 3 4 ] 
            :turns [ ]
           :state :playing }
          )))
)

(deftest play-incorrect-guess-with-nonempty-game
  (is (= { :max-turns 12 
           :secret [1 2 3 4] 
           :turns [ 
             { :guess [ 6 6 6 6] 
               :exact-match? false 
               :color-and-position-matches 0 
               :color-only-matches 0 },
             { :guess [ 5 5 5 5] 
               :exact-match? false 
               :color-and-position-matches 0 
               :color-only-matches 0 } ]
           :state :playing }
        (guess [ 5 5 5 5 ] 
          { :max-turns 12 
            :secret [ 1 2 3 4 ] 
            :turns [ 
              { :guess [ 6 6 6 6] 
                :exact-match? false 
                :color-and-position-matches 0 
                :color-only-matches 0 }
            ]
           :state :playing }
          )))
)

(deftest play-losing-game
  (is (= :lost (:state 
    (guess [5 5 5 5] 
      { :max-turns 12 
           :secret [1 2 3 4] 
           :turns [               ; This input is sort of terrifying...
             { :guess [ 6 6 6 6] 
               :exact-match? false 
               :color-and-position-matches 0 
               :color-only-matches 0 },
             { :guess [ 6 6 6 6] 
               :exact-match? false 
               :color-and-position-matches 0 
               :color-only-matches 0 },
             { :guess [ 6 6 6 6] 
               :exact-match? false 
               :color-and-position-matches 0 
               :color-only-matches 0 },
             { :guess [ 6 6 6 6] 
               :exact-match? false 
               :color-and-position-matches 0 
               :color-only-matches 0 },
             { :guess [ 6 6 6 6] 
               :exact-match? false 
               :color-and-position-matches 0 
               :color-only-matches 0 },
             { :guess [ 6 6 6 6] 
               :exact-match? false 
               :color-and-position-matches 0 
               :color-only-matches 0 },
             { :guess [ 6 6 6 6] 
               :exact-match? false 
               :color-and-position-matches 0 
               :color-only-matches 0 },
             { :guess [ 6 6 6 6] 
               :exact-match? false 
               :color-and-position-matches 0 
               :color-only-matches 0 },
             { :guess [ 6 6 6 6] 
               :exact-match? false 
               :color-and-position-matches 0 
               :color-only-matches 0 },
             { :guess [ 6 6 6 6] 
               :exact-match? false 
               :color-and-position-matches 0 
               :color-only-matches 0 },
             { :guess [ 5 5 5 5] 
               :exact-match? false 
               :color-and-position-matches 0 
               :color-only-matches 0 } ]
           :state :playing }      
      )))) 
)