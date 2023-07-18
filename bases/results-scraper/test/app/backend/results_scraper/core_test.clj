(ns app.backend.results-scraper.core-test
  (:require [clojure.test :as test :refer :all]
            [app.backend.results-scraper.core :as core]))

(deftest no-conection
  (is (= (core/league-list) "error")))
