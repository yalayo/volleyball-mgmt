(ns app.backend.database.core
  (:require [com.brunobonacci.mulog :as μ]
            [datahike.api :as d]
            [datahike-firebase.core])
  (:import (clojure.lang ExceptionInfo)))

(def ^:private config {:store {:backend :firebase
                               :db (or (System/getenv "DB_HOST") "http://127.0.0.1:4000")
                               :root (or (System/getenv "DB_ROOT") "volleyball-3-0")
                               :env (if (= (System/getenv "ENVIRONMENT") "prod")
                                      "GOOGLE_APPLICATION_CREDENTIALS")}
                       :schema-flexibility :read
                       :keep-history? false})

(defn init []
  (try
    (if-not (d/database-exists? config)
      (do
        (μ/log :create-database :state :in-progres)
        (d/create-database config)
        (μ/log :create-database :state :done))
      (μ/log :create-database :state :already-exists))
    (catch ExceptionInfo e
      (μ/log :log-exception :exception e))))

(def ^:private conn (d/connect config))

(defn transact [data]
  (let [conn (d/connect config)]
    (d/transact conn data)))

(defn query [query]
  (d/q {:query query}
       @conn))