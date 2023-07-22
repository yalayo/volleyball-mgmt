(ns app.backend.results-scraper.core-test
  (:require [clojure.test :as test :refer :all]
            [app.backend.results-scraper.core :as core]))

#_(deftest no-conection
  (is (= (core/league-list wrong-leagues-url) "error")))

#_(deftest correct-response
  (is (= (core/league-list correct-leagues-url) (not nil))))
