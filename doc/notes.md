# Implementation Notes and Journal

I started by following the idea discussed at a talk at Clojure/Conj in Philadelphia this year -- start by figuring out what you want the data to look like and build functions up around them.

I'm starting with the functions to store a guess.  I notice that several functions take the same arguments (the secret and the guess).

When I write the tests for `color-and-position-matches` I start by generating examples.  I notice that I have to pick arbitrary values to prove various scenarios (one matches, two match, etc.)  This feels slightly odd to me, and I note that generative testing might be a more natural fit.

Though I wonder at first how I might write a property to hold true for `color-and-position-matches` without actually implementing the algorithm in the test!  I've been assuming my generator would produce both a random secret and a random guess, then the function return value should equal the count of positions that have identical values in the same position.  As I think about this some more, another idea comes to me -- what if I wrote specifications for zero to six positions matching?  e.g. write a generator that emits a secret and a guess that has zero exact matches, then one, then two, etc.  I'll have to come back to this idea later once I get more comfortable with Clojure -- one new thing at a time.

