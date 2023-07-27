(ns app.backend.results-scraper.core
  (:require [com.brunobonacci.mulog :as μ]
            [cognitect.aws.credentials :as credentials]
            [app.backend.database.interface :as database]
            [app.backend.results-scraper.scheduler :as scheduler]
            [app.backend.results-scraper.repl :as repl]
            [app.backend.leagues.interface :as leagues]) 
  (:gen-class))

(μ/set-global-context!
 {:app-name "results-scraper", :version "0.0.1", :env "prod"})

(defn- init-logging []
  (let [publisher {:type :multi}
        publishers [{:type :console}
                    {:type        :slack
                     :webhook-url "https://hooks.slack.com/services/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"
                     :transform (partial filter #(#{:init-repl :stop-repl} (:mulog/event-name %)))}]
        cloudwatch {:type :cloudwatch 
                    :group-name "volleyball" 
                    :cloudwatch-client-config {:api :logs
                                               :credentials-provider (credentials/basic-credentials-provider
                                                                      {:access-key-id (System/getenv "AWS_ACCESS_KEY_ID")
                                                                       :secret-access-key (System/getenv "AWS_SECRET_ACCESS_KEY")})}}]
    (when (not (nil? (System/getenv "PLACE_HOLDER")))
      (assoc publisher :publishers (conj publishers cloudwatch)))
    (assoc publisher :publishers publishers)))

(defn- init []
  (println (init-logging))
  (μ/start-publisher! (init-logging)))


(defn -main [& args]
  (init)
  (repl/init)
  (database/init)
  #_(leagues/store-schema)
  #_(leagues/store-leagues-data)
  (scheduler/schedule))