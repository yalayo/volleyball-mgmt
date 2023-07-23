(ns app.backend.leagues.interface
  (:require [app.backend.leagues.core :as core]))

(defn get-db-schema []
  (core/get-db-schema))

(defn get-leagues-data []
  (core/get-leagues-data))