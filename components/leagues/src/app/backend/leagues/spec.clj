(ns app.backend.leagues.spec
  (:require [clojure.spec.alpha :as s]))

;; Specs used to validate the leagues data generation
(s/def ::league-id string?)
(s/def ::name string?)
(s/def ::category (s/and keyword? #{:ober-klasse :lande :bezirk :jungen}))
(s/def ::sub-category string?)
(s/def ::url string?)
(s/def ::area string?)
(s/def ::league-data (s/keys :req-un [::league-id ::name  ::category ::sub-category ::url ::area]))

;; Specs used to validate leagues standings scraped
(s/def ::content (s/or :a (s/and (s/coll-of any?) #(contains? % :content)) :b (s/coll-of string?)))
(s/def ::tag (s/and keyword? #(= % :td)))
(s/def ::data-title string?)
(s/def ::attrs (s/and #(contains? % :data-title)
                      (s/keys :req-un [::data-title])))
(s/def ::column (s/keys :req-un [::attrs  ::content ::tag]))