(ns app.backend.leagues.scheduler
  (:require [com.brunobonacci.mulog :as μ]
            [chime.core :as chime]
            [clojure.core.async :refer [<!!]]
            [app.backend.leagues.scraper :as scraper])
  (:import [java.time Instant Duration]))

(defn schedule-standings-scans [channel]
  (chime/chime-at (-> (chime/periodic-seq (Instant/now) (Duration/ofMinutes 1))
                      rest)
                  (fn [time]
                    (let [data (<!! channel)]
                      (if-some [data nil]
                        (μ/log :standing-scanned :message (str "Process finished for league: " (:league-id data) " at " time))
                        (scraper/process-table data))))))