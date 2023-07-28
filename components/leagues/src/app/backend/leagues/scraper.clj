(ns app.backend.leagues.scraper
  (:require [net.cgrand.enlive-html :refer [html-resource select]]
            [clojure.string :as str]
            [app.backend.leagues.storage :as storage])
  (:import (java.util UUID)))

(defn- store-teams [data]
  (let [teams (atom [])]
    (doseq [item data]
      (swap! teams #(conj % {:team-id (:standing-team item) :team-name (:team-name item)})))
    (storage/store-data @teams)))

(defn- store-standings [data]
  (storage/store-data data))

;; Organizing the data
(defn- extract-attribute [data]
  (let [value (first (:content data))] 
    (if (= (type value) java.lang.String)
      (str/trim value)
      (first (:content value)))))

(defn- extract [data league-id] 
  (let [place (extract-attribute (first data))
        team (extract-attribute (second data))
        games (extract-attribute (nth data 2))
        scores (extract-attribute (nth data 3))
        sets (extract-attribute (nth data 4))
        points (extract-attribute (nth data 5))
        standing {:standing-id (.toString (UUID/randomUUID))
                  :standing-season "2022-2023"
                  :standing-league league-id
                  :standing-place place
                  :team-name team
                  :standing-team (.toString (UUID/randomUUID))
                  :standing-games games
                  :standing-scores scores
                  :standing-sets sets
                  :standing-points points}]
    standing))

(defn scan-table [data]
  (let [url (:url data)
        league-id (:league-id data)
        website-content (html-resource (java.net.URL. url))
        content (select website-content [:div.liga-detail :table.table.table-striped.table-hover.title-top :tbody :tr])
        result (atom [])]
    (doseq [item content]
      (swap! result #(conj % (extract (:content item) league-id))))
    (store-teams @result)
    (store-standings @result)))