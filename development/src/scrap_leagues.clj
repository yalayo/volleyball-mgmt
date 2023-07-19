(ns scrap-leagues
  (:require [net.cgrand.enlive-html :refer [html-resource select]]
            [portal.api :as p]
            [clojure.spec.alpha :as s]))

;; Code to activate portal
(def portal-atom (atom nil))

(defn launch-portal []
  (let [portal (p/open (or @portal-atom {:launcher :vs-code}))]
    (reset! portal-atom portal)
    (add-tap #'p/submit)
    portal))

(comment
  (launch-portal))

;; Urls
(def base-url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/")
(def men-url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/maenner/")
(def women-url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/frauen/")



(def website-content
  "Returns: list of html content as hash-maps"
  (html-resource (java.net.URL. men-url )))

(tap> (select website-content [:div#ergebnisse99 :table :tbody#container-mix :tr]))

(def men-leagues-urls 
  [{:name "Bezirksklasse 20" :category "bezirk" :sub-category "bezirksklasse" :area "" :url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/maenner/bezirksklasse-20-maenner/"}
   {:name "Kreisliga UNNA Jungen/Mixed" :category "jungen" :sub-category "unna-mixed" :area "" :url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/maenner/kreislauf-unna/"}
   ])

(defn calculate-ligas-urls [base-url liga-name category sub-category area gender pos]
  (into {} {:name (str liga-name " " pos) :category category :sub-category sub-category :area area :url (str base-url gender "/" sub-category "-" pos "-" gender)}))

;; To get the urls for the Oberliga
(doseq [pos (range 3)]
  (tap> (calculate-ligas-urls base-url "Oberliga" "ober-klasse" "oberliga" "" "maenner" (inc pos))))

;; To get the urls for the Verbandsliga
(doseq [pos (range 4)]
  (tap> (calculate-ligas-urls base-url "Verbandsliga" "ober-klasse" "verbandsliga" "" "maenner" (inc pos))))

;; To get the urls for the Landesliga
(doseq [pos (range 8)]
  (tap> (calculate-ligas-urls base-url "Landesliga" "lande" "landesliga" "" "maenner" (inc pos))))

;; To get the urls for the Bezirksliga
(doseq [pos (range 16)]
  (tap> (calculate-ligas-urls base-url "Bezirksliga" "bezirk" "bezirksliga" "" "maenner" (inc pos))))


;; Women
;; To get the urls for the Oberliga
(doseq [pos (range 3)]
  (tap> (calculate-ligas-urls base-url "Oberliga" "ober-klasse" "oberliga" "" "frauen" (inc pos))))

;; To get the urls for the Verbandsliga
(doseq [pos (range 4)]
  (tap> (calculate-ligas-urls base-url "Verbandsliga" "ober-klasse" "verbandsliga" "" "frauen" (inc pos))))

;; To get the urls for the Landesliga
(doseq [pos (range 8)]
  (tap> (calculate-ligas-urls base-url "Landesliga" "lande" "landesliga" "" "frauen" (inc pos))))

;; To get the urls for the Bezirksliga
(doseq [pos (range 16)]
  (tap> (calculate-ligas-urls base-url "Bezirksliga" "bezirk" "bezirksliga" "" "frauen" (inc pos))))

;; To get the urls for the Bezirksklasse
(doseq [pos (range 28)]
  (tap> (calculate-ligas-urls base-url "Bezirksklasse" "bezirk" "bezirksklasse" "" "frauen" (inc pos))))

;; Extra leagues
(def women-leagues-urls
  [{:name "Bezirksklasse 20" :category "bezirk" :sub-category "bezirksklasse" :area "" :url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/maenner/bezirksklasse-20-maenner/"}
   {:name "Kreisliga UNNA Jungen/Mixed" :category "jungen" :sub-category "unna-mixed" :area "" :url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/maenner/kreislauf-unna/"}])

;; Method to scrape an url using selectors
(defn scrape [url selector]
  (let [doc (html-resource (java.net.URL. url))
        nodes (select doc selector)]
    (map #(-> % :content first) nodes)))

;; Getting the positions table information
(def example-league-url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/maenner/bezirksklasse-20-maenner/")
(def result (scrape example-league-url [:div.liga-detail :table.table.table-striped.table-hover.title-top :tbody :tr]))
(tap> result)

;; Creating spec to validate that the data comming from the website hasn't change its structure.
(s/def ::content (s/coll-of string?))
(s/def ::tag (s/and keyword? #(= % :td)))

(s/def ::data-title string?)
(s/def ::attrs (s/and map? 
                      #(contains? % :data-title) 
                      (s/keys :req-un [::data-title])))
(s/def ::row (s/and map?
                    (s/keys :req-un [::attrs ::content ::tag])))
(s/def ::table (s/coll-of ::row))

;; To watch the data
(def row (first result))
(tap> row)
(def content (:content row))
(tap> content)
(def tag (:tag row))
(tap> tag)
(def attrs (:attrs row))
(tap> (first (keys attrs)))

;; Testing the specs
(s/valid? ::sequence result)
(s/valid? ::sequence row)
(s/valid? ::content content)
(s/valid? ::tag tag)
(s/valid? ::attrs attrs)
(s/valid? ::row row)
(s/valid? ::table result)