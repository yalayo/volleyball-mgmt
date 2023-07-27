(ns app.backend.database.interface
  (:require [app.backend.database.core :as core]))

(defn init []
  (core/init))

(defn transact [data]
  (core/transact data))

(defn query [query]
  (core/query query))