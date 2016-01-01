(ns mastermind.core-test
  (:require [clojure.test :refer :all]
            [mastermind.core :refer :all]))

(deftest color-and-position-matches-exact-matches-only
	(is (= 0 (color-and-position-matches [1 1 1 1] [2 3 4 5]))))
