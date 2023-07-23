(ns app.backend.leagues.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::name string?)
(s/def ::category (s/and keyword? #{:ober-klasse :lande :bezirk :jungen}))
(s/def ::sub-category string?)
(s/def ::url string?)
(s/def ::area string?)

(s/def ::league-data (s/keys :req-un [::name  ::category ::sub-category ::url ::area]))