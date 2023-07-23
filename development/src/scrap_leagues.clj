(ns scrap-leagues
  (:require [net.cgrand.enlive-html :refer [html-resource select]]
            [portal.api :as p]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]))

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
  [{:name "Bezirksklasse 20" :category :bezirk :sub-category "bezirksklasse" :area "" :url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/maenner/bezirksklasse-20-maenner/"}
   {:name "Kreisliga UNNA Jungen/Mixed" :category :jungen :sub-category "unna-mixed" :area "" :url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/maenner/kreislauf-unna/"}
   ])

(defn calculate-ligas-urls [base-url liga-name category sub-category area gender pos]
  [{:name (str liga-name " " pos) :category (keyword category) :sub-category sub-category :area area :url (str base-url gender "/" sub-category "-" pos "-" gender)}])

;; To get the urls for the Oberliga
(let [data (atom [])]
  (doseq [pos (range 3)]
    (println "Data: " @data)
    (swap! data #(into % (calculate-ligas-urls base-url "Oberliga" "ober-klasse" "oberliga" "" "maenner" (inc pos)))))
  (tap> @data))


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
  [{:name "Bezirksklasse 20" :category :bezirk :sub-category "bezirksklasse" :area "" :url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/maenner/bezirksklasse-20-maenner/"}
   {:name "Kreisliga UNNA Jungen/Mixed" :category :jungen :sub-category "unna-mixed" :area "" :url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/maenner/kreislauf-unna/"}])

;; Template to generate all the leagues urls from where to scrap
(def template [{:name "Oberliga" :category "ober-klasse" :sub-category "oberliga" :area "" :gender "maenner" :amount 3}
               {:name "Verbandsliga" :category "ober-klasse" :sub-category "verbandsliga" :area "" :gender "maenner" :amount 4}
               {:name "Landesliga" :category "lande" :sub-category "landesliga" :area "" :gender "maenner" :amount 8}
               {:name "Bezirksliga" :category "bezirk" :sub-category "bezirksliga" :area "" :gender "maenner" :amount 16}
               {:name "Oberliga" :category "ober-klasse" :sub-category "oberliga" :area "" :gender "frauen" :amount 3}
               {:name "Verbandsliga" :category "ober-klasse" :sub-category "verbandsliga" :area "" :gender "frauen" :amount 4}
               {:name "Landesliga" :category "lande" :sub-category "landesliga" :area "" :gender "frauen" :amount 8}
               {:name "Bezirksliga" :category "bezirk" :sub-category "bezirksliga" :area "" :gender "frauen" :amount 16}
               {:name "Bezirksklasse" :category "bezirk" :sub-category "bezirksklasse" :area "" :gender "frauen" :amount 28}])

;; Method to generate all the urls and some extra data as well
(defn generate-urls [template]
  (let [result (atom [])]
    (doseq [item template]
      (doseq [pos (range (:amount item))]
        (swap! result #(into % (calculate-ligas-urls base-url (:name item) (:category item) (:sub-category item) (:area item) (:gender item) (inc pos))))))
    @result))

(tap> (concat (generate-urls template) women-leagues-urls men-leagues-urls))

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
(s/def ::content (s/or :a (s/and (s/coll-of any?) #(contains? % :content)) :b (s/coll-of string?)))
(s/def ::tag (s/and keyword? #(= % :td)))

(s/def ::data-title string?)
(s/def ::attrs (s/and #(contains? % :data-title) 
                      (s/keys :req-un [::data-title])))
(s/def ::column (s/keys :req-un [::attrs  ::content ::tag]))
(s/def ::row (s/and map?
                    (s/keys :req-un [::content ::tag])))
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
(s/explain ::row row)
(s/valid? ::table result)
(s/explain ::table result)

;; Organizing the data
(defn extract-attribute [data]
  (tap> data)
  (let [value (first (:content data))] 
    (if (= (type value) java.lang.String)
      (str/trim value)
      (first (:content value)))))

(defn extract [data]
  (doseq [item data] 
    (if (= (first item) :content)
      (if (s/valid? ::column (last item))
        (let [content (last item)
              place (extract-attribute (first content))
              team (extract-attribute (second content))
              games (extract-attribute (nth content 2))
              scores (extract-attribute (nth content 3))
              sets (extract-attribute (nth content 4))
              points (extract-attribute (nth content 5))]
          (tap> content)
          {:place place :team team :games games :scores scores :sets sets :points points})
        (s/explain ::column (last item)))
     )))

(defn scan-table [url]
  (let [website-content (html-resource (java.net.URL. url))
        content (select website-content [:div.liga-detail :table.table.table-striped.table-hover.title-top :tbody :tr])]
    (map extract content)))

(scan-table example-league-url)