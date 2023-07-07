(ns app.backend.results-scraper.core
  (:require [com.brunobonacci.mulog :as μ])
  (:gen-class))

(μ/set-global-context!
 {:app-name "results-scraper", :version "0.0.1", :env "prod"})

(μ/start-publisher! {:type :console})

;; Check how to use credentials (μ/start-publisher! {:type :cloudwatch :group-name "volleyball-3.0"})

(defn -main [& args]
  (println args)
  (μ/log ::lambda-execution, :args args))