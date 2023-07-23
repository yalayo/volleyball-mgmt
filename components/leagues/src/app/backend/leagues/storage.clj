(ns app.backend.leagues.storage
  (:require [app.backend.database.interface :as database]))

(def ^:private db-schema
  [{:db/ident :league/name
    :db/valueType :db.type/string
    :db/unique :db.unique/identity
    :db/cardinality :db.cardinality/one}
   {:db/ident :league/category
    :db/valueType :db.type/symbol
    :db/cardinality :db.cardinality/one}
   {:db/ident :league/sub-category
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :league/area
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :league/url
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}])

(defn- store-schema []
  (database/transact db-schema))

(defn store-leagues-data [data]
  (database/transact data))