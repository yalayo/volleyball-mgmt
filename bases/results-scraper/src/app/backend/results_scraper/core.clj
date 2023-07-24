(ns app.backend.results-scraper.core
  (:require [com.brunobonacci.mulog :as μ] 
            [app.backend.database.interface :as database]
            [app.backend.results-scraper.scheduler :as scheduler]
            [app.backend.results-scraper.repl :as repl]
            [app.backend.leagues.interface :as leagues]) 
  (:gen-class))

(μ/set-global-context!
 {:app-name "results-scraper", :version "0.0.1", :env "prod"})

(μ/start-publisher!
  {:type :multi
   :publishers
   [{:type :console}
    #_{:type :cloudwatch :group-name "volleyball"}]})

;; Check how to use credentials (μ/start-publisher! {:type :cloudwatch :group-name "volleyball-3.0"})

(defn -main [& args]
  (database/init)
  #_(leagues/store-schema)
  #_(leagues/store-leagues-data)
  (scheduler/schedule)
  (repl/init))