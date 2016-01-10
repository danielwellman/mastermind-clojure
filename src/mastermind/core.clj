(ns mastermind.core)

(defn color-and-position-matches [secret guess]
  "Returns the number of elements in both the color and position"
  (let [position-pairs (map vector secret guess)]
    (reduce 
    	(fn [count [secret-value guess-value]] (+ count (if (= secret-value guess-value) 1 0))) 
    	0 position-pairs)
  ))

; Private helper functions

(defn transpose [m]
  (if (seq m)
    (apply mapv vector m)
    (empty m))
  )

(defn remove-matches [a b]
  "Returns a tuple of the inputs after removing exact position and color matches"
  (let [position-pairs (map vector a b)]
    (transpose (filter (fn [[x y]] (not (= x y))) position-pairs))    
    )
)

; ... back to our API

(defn color-only-matches [secret guess]
  "Returns the number of pegs with correct color but wrong position."
  (let [[secret-without-matches guess-without-matches] (remove-matches secret guess)
        secret-frequencies (frequencies secret-without-matches)
        guess-frequencies (frequencies guess-without-matches)]
    (reduce (fn [count [element frequency]] (+ count (min frequency (get guess-frequencies element 0))))
      0 secret-frequencies)))


