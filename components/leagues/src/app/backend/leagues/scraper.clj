(ns app.backend.leagues.scraper
  (:require [com.brunobonacci.mulog :as μ]
            [net.cgrand.enlive-html :refer [html-resource select]]
            [resilience4clj-timelimiter.core :as tl]
            [resilience4clj-retry.core :as r]
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

(defn- extract [data league-id gender] 
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
                  :standing-team (.toString (UUID/randomUUID))
                  :team-name team
                  :team-gender gender
                  :standing-games games
                  :standing-scores scores
                  :standing-sets sets
                  :standing-points points}]
    standing))

(defn scan-table [data]
  (let [url (:url data)
        league-id (:league-id data)
        gender (:gender data)
        website-content (html-resource (java.net.URL. url))
        content (select website-content [:div.liga-detail :table.table.table-striped.table-hover.title-top :tbody :tr])
        result (atom [])]
    (doseq [item content]
      (swap! result #(conj % (extract (:content item) league-id gender))))
    (store-teams @result)
    (store-standings @result)))

(defn- decorate-scan [data]
  (let [limiter (tl/create {:timeout-duration 10000})
        retry (r/create {:max-attempts 3
                         :wait-duration 5000})
        protected-scan (-> (scan-table data)
                            (tl/decorate limiter)
                            (r/decorate retry))]
    (protected-scan data)))

(defn process-table [data]
  (if (storage/exist-standing? (:league-id data))
    (μ/log :already-scanned :message "League already scanned: " (:league-id data))
    (do
      (μ/log :standing-scanned :message (str "Scan started for league" (:league-id data)))
      (decorate-scan data)
      (μ/log :standing-scanned :message (str "Scan finished for league" (:league-id data))))))