(ns app.backend.leagues.scheduler
  (:require [com.brunobonacci.mulog :as μ]
            [chime.core :as chime]
            [clojure.core.async :refer [<!!]]
            [app.backend.leagues.scraper :as scraper])
  (:import [java.time LocalTime ZonedDateTime ZoneId Period Instant Duration]
           [java.io FileNotFoundException]))

(defn schedule-standings-scans [channel]
  (chime/chime-at (-> (chime/periodic-seq (Instant/now) (Duration/ofMinutes 1))
                      rest)
                  (fn [time]
                    (let [data (<!! channel)]
                      (if-some [data nil]
                        (μ/log :standing-scanned :message "Process finished for " data)
                        (μ/trace :standing-scanned :message (str "Scanned at " time) []
                                 (scraper/scan-table data)))))))