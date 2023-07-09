(require '[cheshire.core :as json])
(println (json/generate-string {:projects *input*}))