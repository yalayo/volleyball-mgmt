(ns experiments)

(require '[chime.core :as chime])
(import '[java.time Instant Duration])

(let [now (Instant/now)]
  (chime/chime-at (-> (chime/periodic-seq (Instant/now) (Duration/ofSeconds 3))
                      rest)
                  (fn [time]
                    (println "Chiming at" time))))