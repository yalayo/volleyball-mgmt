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
    :db/cardinality :db.cardinality/one}])

(defn store-schema []
  (database/transact db-schema))

(defn store-leagues-data [data]
  (database/transact data))