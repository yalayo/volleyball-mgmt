(ns database
  (:require [datahike.api :as d]
            [datahike-firebase.core]
            [portal.api :as p]))

;; Code to activate portal
(def portal-atom (atom nil))

(defn launch-portal []
  (let [portal (p/open (or @portal-atom {:launcher :vs-code}))]
    (reset! portal-atom portal)
    (add-tap #'p/submit)
    portal))

(comment
  (launch-portal)
  )

;; Create a config map with firebase as storage medium
#_(def config {:store {:backend :firebase
                     :env "GOOGLE_APPLICATION_CREDENTIALS" 
                     :db "https://volleyball-3-0-default-rtdb.europe-west1.firebasedatabase.app" ; 
                     :root "datahike"}
             :schema-flexibility :read
             :keep-history? false})

(def config (merge {:store {:backend :firebase 
                      :db (or (System/getenv "DB_HOST") "http://127.0.0.1:9001")
                      :root (or (System/getenv "DB_ROOT") "volleyball-3-0")}
              :schema-flexibility :read
              :keep-history? false} 
                   (if (= (System/getenv "ENVIRONMENT") "prod") 
                     {:env "GOOGLE_APPLICATION_CREDENTIALS"})))
(tap> config)
;; Create a database at this place, by default configuration we have a strict
;; schema and temporal index
(d/create-database config)

;; To check if the database does not exist and create it.
(d/database-exists? config)

(def conn (d/connect config))

;; The first transaction will be the schema we are using:
(d/transact conn [{:db/ident :name
                   :db/valueType :db.type/string
                   :db/cardinality :db.cardinality/one}])

;; Result 1
{:db-before #datahike/DB {:max-tx 536870912 :max-eid 0},
 :db-after #datahike/DB {:max-tx 536870913 :max-eid 1},
 :tx-data
 [#datahike/Datom [1 :db/ident :name 536870913]
  #datahike/Datom [1 :db/valueType :db.type/string 536870913]
  #datahike/Datom [1 :db/cardinality :db.cardinality/one 536870913]],
 :tempids #:db{:current-tx 536870913},
 :tx-meta nil}
;; Result 2, only changes the transaction ids
{:db-before #datahike/DB {:max-tx 536870913 :max-eid 1},
 :db-after #datahike/DB {:max-tx 536870914 :max-eid 1},
 :tx-data
 [#datahike/Datom [1 :db/ident :name 536870914]
  #datahike/Datom [1 :db/valueType :db.type/string 536870914]
  #datahike/Datom [1 :db/cardinality :db.cardinality/one 536870914]],
 :tempids #:db{:current-tx 536870914},
 :tx-meta nil}

{:db-before #datahike/DB {:max-tx 536870914 :max-eid 1},
 :db-after #datahike/DB {:max-tx 536870915 :max-eid 2},
 :tx-data
 [#datahike/Datom [1 :db/ident :name 536870915]
  #datahike/Datom [1 :db/valueType :db.type/string 536870915]
  #datahike/Datom [1 :db/cardinality :db.cardinality/one 536870915]
  #datahike/Datom [2 :db/ident :age 536870915]
  #datahike/Datom [2 :db/valueType :db.type/long 536870915]
  #datahike/Datom [2 :db/cardinality :db.cardinality/one 536870915]],
 :tempids #:db{:current-tx 536870915},
 :tx-meta nil}


(d/transact conn [{:db/ident :league/name
                   :db/valueType :db.type/string
                   :db/unique :db.unique/identity
                   :db/cardinality :db.cardinality/one }
                  {:db/ident :league/category
                   :db/valueType :db.type/symbol
                   :db/cardinality :db.cardinality/one }
                  {:db/ident :league/sub-category
                   :db/valueType :db.type/string
                   :db/cardinality :db.cardinality/one}
                  {:db/ident :league/area
                   :db/valueType :db.type/string
                   :db/cardinality :db.cardinality/one}
                  {:db/ident :league/url
                   :db/valueType :db.type/string
                   :db/cardinality :db.cardinality/one}])

;; Let's get the current schema
(def schema (d/pull conn [:db/ident]))
;; Print the attributes
(doseq [attr schema]
  (println (get-in attr [:db/ident])))

;; Let's add some data and wait for the transaction
(d/transact conn [{:name  "Alice", :age   20 }
                  {:name  "Bob", :age   30 }
                  {:name  "Charlie", :age   40 }
                  {:age 15 }])

;; Search the data
(d/q '[:find ?e ?n ?a
       :where
       [?e :name ?n]
       [?e :age ?a]]
  @conn)
;; #{[4 "Bob" 30] [5 "Charlie" 40] [3 "Alice" 20]}

;; Clean up the database if it is not needed any more
(d/delete-database config)

(def schm [{:db/ident :league/name-a
            :db/valueType :db.type/string
            :db/unique :db.unique/identity
            :db/cardinality :db.cardinality/one}])

(defn no-schema? []
  (let [result (d/q '[:find ?a :where [_ :db/ident ?a]] @conn)
        new-schema (atom [])]
    (doseq [item schm]
      (println item)
      (when-not (contains? result [(:db/ident item)])
        (swap! new-schema #(into % item))))
    (println @new-schema)) 
  )
;; Example usage
(println (no-schema?))