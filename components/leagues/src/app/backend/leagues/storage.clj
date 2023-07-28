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
   {:db/ident :standing-id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-season
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-league
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-item-id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-item-parent
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-item-team
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-item-place
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-item-games
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-item-scores
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-item-sets
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :standing-item-points
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

(defn store-league-standings [data]
  ;; A bit more of processing is needed here.
  (database/transact data))

(defn leagues-to-scan []
  (let [data (database/query "[:find ?n ?a :where [?e :league-id ?n][?e :url ?a]]")
        keys [:league-id :url]
        result (atom [])]
    (doseq [item data]
      (swap! result #(conj % (zipmap keys item))))
    @result))