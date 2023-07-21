(ns app.backend.results-scraper.core
  (:require [com.brunobonacci.mulog :as μ] 
            [app.backend.database.interface :as database]
            [app.backend.results-scraper.scheduler :as scheduler]) 
  (:gen-class))

(μ/set-global-context!
 {:app-name "results-scraper", :version "0.0.1", :env "prod"})

(μ/start-publisher! {:type :console})

;; Check how to use credentials (μ/start-publisher! {:type :cloudwatch :group-name "volleyball-3.0"})

(defn -main [& args]
  (database/init)
  (scheduler/schedule))