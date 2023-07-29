(ns app.backend.results-scraper.core
  (:require [com.brunobonacci.mulog :as μ]
            [cognitect.aws.credentials :as credentials]
            [app.backend.database.interface :as database]
            [app.backend.results-scraper.scheduler :as scheduler]
            [app.backend.results-scraper.repl :as repl]
            [app.backend.leagues.interface :as leagues]) 
  (:gen-class))

(defn- init-logging []
  (μ/set-global-context!
   {:app-name "results-scraper", :version "0.0.1", :env "prod"})

  (let [publisher {:type :multi}
        publishers [{:type :console}
                    {:type :slack
                     :webhook-url (System/getenv "WEBHOOK_URL")
                     :transform (partial filter #(#{:init-repl :stop-repl :create-database :log-exception :store-leagues-data :store-leagues-schema :standing-scanned} (:mulog/event-name %)))}]
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
  (μ/start-publisher! (init-logging)))


(defn -main [& args]
  (init)
  (repl/init)
  (scheduler/schedule))

(comment
  #_(database/init)
  #_(leagues/store-schema)
  #_(leagues/store-leagues-data)
  #_(leagues/scrap-league-standings)
  #_(scheduler/schedule)
  )