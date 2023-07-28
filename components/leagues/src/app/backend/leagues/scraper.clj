(ns app.backend.leagues.scraper
  (:require [net.cgrand.enlive-html :refer [html-resource select]]
            [app.backend.leagues.spec :as spec]))

;; Organizing the data
(defn- extract-attribute [data]
  (tap> data)
  (let [value (first (:content data))] 
    (if (= (type value) java.lang.String)
      (str/trim value)
      (first (:content value)))))

(defn- extract [data]
  (doseq [item data] 
    (if (= (first item) :content)
      (if (s/valid? ::spec/column (last item))
        (let [content (last item)
              place (extract-attribute (first content))
              team (extract-attribute (second content))
              games (extract-attribute (nth content 2))
              scores (extract-attribute (nth content 3))
              sets (extract-attribute (nth content 4))
              points (extract-attribute (nth content 5))]
          (tap> content)
          {:place place :team team :games games :scores scores :sets sets :points points})
        (s/explain ::spec/column (last item)))
     )))

(defn scan-table [url]
  (let [website-content (html-resource (java.net.URL. url))
        content (select website-content [:div.liga-detail :table.table.table-striped.table-hover.title-top :tbody :tr])]
    (map extract content)))