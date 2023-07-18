(ns app.backend.results-scraper.core
  (:require [com.brunobonacci.mulog :as μ]
            [chime.core :as chime])
  (:import [java.time Instant Duration])
  (:gen-class))

(μ/set-global-context!
 {:app-name "results-scraper", :version "0.0.1", :env "prod"})

(μ/start-publisher! {:type :console})

;; Check how to use credentials (μ/start-publisher! {:type :cloudwatch :group-name "volleyball-3.0"})

(defn -main [& args]
  (chime/chime-at (-> (chime/periodic-seq (Instant/now) (Duration/ofSeconds 3))
                      rest)
                  (fn [time]
                    (μ/log ::task-execution :args args :message (str "Chiming at" time))
                    (println "Chiming at" time))))