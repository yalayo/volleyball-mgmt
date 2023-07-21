(ns app.backend.database.interface
  (:require [app.backend.database.core :as core]))

(defn init []
  (core/init))