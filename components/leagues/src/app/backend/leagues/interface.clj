(ns app.backend.leagues.interface
  (:require [app.backend.leagues.core :as core]))

(defn store-schema []
  (core/store-schema))

(defn store-leagues-data []
  (core/store-leagues-data))

(defn scrap-league-standings []
  (core/scrap-league-standings))