(ns mastermind.core)

(defn color-and-position-matches [secret guess]
  "Returns the number of elements in both the color and position"
  (let [position-pairs (map vector secret guess)]
    (reduce 
    	(fn [count [secret-value guess-value]] (+ count (if (= secret-value guess-value) 1 0))) 
    	0 position-pairs)
  ))
