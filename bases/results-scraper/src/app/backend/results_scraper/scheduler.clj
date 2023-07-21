(ns app.backend.results-scraper.scheduler
  (:require [com.brunobonacci.mulog :as μ] 
            [net.cgrand.enlive-html :refer [html-resource select]]
            [chime.core :as chime])
  (:import [java.time LocalTime ZonedDateTime ZoneId Period]
           [java.io FileNotFoundException]))

;; Lueagues list page.
(def leagues-url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/maenner/")

(defn league-list [url]
  (try
    (let [response (html-resource (java.net.URL. url))]
      response)
  
  (catch FileNotFoundException e
    (μ/log ::league-list :exception e :status :failed :possible-reason "Wrong url!")
    "error")
  (catch Exception e
    (μ/log ::league-list :exception e :status :failed :possible-reason "Unknown!"))))

(defn schedule []
  (chime/chime-at (-> (chime/periodic-seq (-> (LocalTime/of 13 0 0)
                                              (.adjustInto (ZonedDateTime/now (ZoneId/of "Europe/Berlin")))
                                              .toInstant)
                                          (Period/ofDays 1)))
                  (fn [time]
                    (league-list leagues-url)
                    (μ/log ::task-execution :message (str "Chiming at" time)))))