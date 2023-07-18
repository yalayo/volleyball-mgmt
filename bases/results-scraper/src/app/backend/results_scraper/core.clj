(ns app.backend.results-scraper.core
  (:require [com.brunobonacci.mulog :as μ]
            [net.cgrand.enlive-html :refer [html-resource select]]
            [chime.core :as chime])
  (:import [java.time Instant Duration]
           [java.io FileNotFoundException])
  (:gen-class))

(μ/set-global-context!
 {:app-name "results-scraper", :version "0.0.1", :env "prod"})

(μ/start-publisher! {:type :console})

;; Check how to use credentials (μ/start-publisher! {:type :cloudwatch :group-name "volleyball-3.0"})

;; Lueagues list page.
(def url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/maenn/")

(defn league-list []
  (try
    (let [response (html-resource (java.net.URL. url))]
      response)
  
  (catch FileNotFoundException e
    (μ/log ::league-list :exception e :status :failed :possible-reason "Wrong url!")
    "error")
  (catch Exception e
    (μ/log ::league-list :exception e :status :failed :possible-reason "Unknown!"))))

(defn -main [& args]
  (chime/chime-at (-> (chime/periodic-seq (Instant/now) (Duration/ofSeconds 3))
                      rest)
                  (fn [time]
                    (μ/log ::task-execution :args args :message (str "Chiming at" time))
                    (println "Chiming at" time))))