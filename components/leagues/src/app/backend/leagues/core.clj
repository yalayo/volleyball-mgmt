(ns app.backend.leagues.core
  (:require [com.brunobonacci.mulog :as μ]
            [clojure.spec.alpha :as s]
            [app.backend.leagues.spec :as spec]
            [app.backend.leagues.storage :as storage])
  (:import (java.util UUID)))

(def ^:private base-url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/")

(def ^:private men-extra-leagues-urls
  [{:name "Bezirksklasse 20" :category :bezirk :sub-category "bezirksklasse" :area "" :url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/maenner/bezirksklasse-20-maenner/"}
   {:name "Kreisliga UNNA Jungen/Mixed" :category :jungen :sub-category "unna-mixed" :area "" :url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/maenner/kreislauf-unna/"}])

(def ^:private women-extra-leagues-urls
  [{:name "Bezirksklasse 20" :category :bezirk :sub-category "bezirksklasse" :area "" :url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/maenner/bezirksklasse-20-maenner/"}
   {:name "Kreisliga UNNA Jungen/Mixed" :category :jungen :sub-category "unna-mixed" :area "" :url "https://www.volleyball.nrw/spielwesen/ergebnisdienst/maenner/kreislauf-unna/"}])

;; Template to generate all the leagues urls from where to scrap
(def ^:private template [{:name "Oberliga" :category "ober-klasse" :sub-category "oberliga" :area "" :gender "maenner" :amount 3}
               {:name "Verbandsliga" :category "ober-klasse" :sub-category "verbandsliga" :area "" :gender "maenner" :amount 4}
               {:name "Landesliga" :category "lande" :sub-category "landesliga" :area "" :gender "maenner" :amount 8}
               {:name "Bezirksliga" :category "bezirk" :sub-category "bezirksliga" :area "" :gender "maenner" :amount 16}
               {:name "Oberliga" :category "ober-klasse" :sub-category "oberliga" :area "" :gender "frauen" :amount 3}
               {:name "Verbandsliga" :category "ober-klasse" :sub-category "verbandsliga" :area "" :gender "frauen" :amount 4}
               {:name "Landesliga" :category "lande" :sub-category "landesliga" :area "" :gender "frauen" :amount 8}
               {:name "Bezirksliga" :category "bezirk" :sub-category "bezirksliga" :area "" :gender "frauen" :amount 16}
               {:name "Bezirksklasse" :category "bezirk" :sub-category "bezirksklasse" :area "" :gender "frauen" :amount 28}])

(defn- calculate-ligas-urls [base-url liga-name category sub-category area gender pos]
  {:league-id (.toString (UUID/randomUUID)) :name (str liga-name " " pos) :category (keyword category) :sub-category sub-category :area area :url (str base-url gender "/" sub-category "-" pos "-" gender)})

(defn- generate-urls [template]
  (let [result (atom [])]
    (doseq [item template]
      (doseq [pos (range (:amount item))]
        (let [data (calculate-ligas-urls base-url (:name item) (:category item) (:sub-category item) (:area item) (:gender item) (inc pos))]
          (when (s/valid? ::spec/league-data data)
            (swap! result #(conj % data))))))
    (concat @result men-extra-leagues-urls women-extra-leagues-urls)))

(defn store-schema []
  (μ/trace ::store-leagues-schema []
            (storage/store-schema)))

(defn store-leagues-data []
  (μ/trace ::store-leagues-data [] 
           (storage/store-leagues-data (generate-urls template))))