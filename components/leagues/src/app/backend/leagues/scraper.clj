(ns app.backend.leagues.scraper
  (:require [com.brunobonacci.mulog :as μ]
            [net.cgrand.enlive-html :refer [html-resource select]]
            [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [app.backend.leagues.spec :as spec]
            [app.backend.leagues.storage :as storage]))

;; Organizing the data
(defn- extract-attribute [data]
  (tap> data)
  (let [value (first (:content data))] 
    (if (= (type value) java.lang.String)
      (str/trim value)
      (first (:content value)))))

(defn- extract [data league-id]
  (let [result (atom [])]
    (doseq [item data]
      (if (= (first item) :content)
        (if (s/valid? ::spec/column (last item))
          (let [content (last item)
                place (extract-attribute (first content))
                team (extract-attribute (second content))
                games (extract-attribute (nth content 2))
                scores (extract-attribute (nth content 3))
                sets (extract-attribute (nth content 4))
                points (extract-attribute (nth content 5))
                standing {:standing-league league-id
                          :standing-item-place place
                          :name team
                          :standing-item-games games
                          :standing-item-scores scores
                          :standing-item-sets sets
                          :standing-item-points points}]
            (swap! result #(conj % standing)))
          (μ/log :invalid-data :explanation (s/explain-data ::spec/column (last item)))))))
  )

(defn scan-table [data]
  (let [website-content (html-resource (java.net.URL. (:url data)))
        content (select website-content [:div.liga-detail :table.table.table-striped.table-hover.title-top :tbody :tr])
        result (map extract content (:league-id data))]
    (μ/log :standing-scanned :message "Scan result: " result)
    (storage/store-league-standings result)))