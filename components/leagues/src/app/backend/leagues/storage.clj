(ns app.backend.leagues.storage
  (:require [app.backend.database.interface :as database]))

(def ^:private db-schema
  [{:db/ident :league-id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :category
    :db/valueType :db.type/symbol
    :db/cardinality :db.cardinality/one}
   {:db/ident :sub-category
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :gender
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :area
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :url
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :team-id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :team-name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-season
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-league
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one} 
   {:db/ident :standing-team
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-place
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-games
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-scores
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-sets
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-points
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}])

(defn store-schema []
  (let [result (database/query "[:find ?a :where [_ :db/ident ?a]]")
        new-schema (atom [])]
    (doseq [item db-schema]
      (when-not (contains? result [(:db/ident item)])
        (swap! new-schema #(conj % item))))
    (database/transact @new-schema)))

(defn store-leagues-data [data]
  (database/transact data))

(defn store-data [data] 
  (database/transact data))

(defn leagues-to-scan []
  (let [data (database/query "[:find ?n ?a :where [?e :league-id ?n][?e :url ?a]]")
        keys [:league-id :url]
        result (atom [])]
    (doseq [item data]
      (swap! result #(conj % (zipmap keys item))))
    @result))