(ns app.backend.results-scraper.repl
  (:require [com.brunobonacci.mulog :as μ]
            [clojure.tools.nrepl.server :refer (start-server stop-server)]))

(defonce __server (atom nil))

(def set-server! (partial reset! __server))

(def port 9669)

(defn start
  []
  (when-not @__server
    (set-server!
     (start-server :port port :bind "0.0.0.0"))
    (μ/log ::init-repl :state :started)))

(defn stop
  []
  (when-let [server @__server]
    (stop-server server)
    (set-server! nil)
    (μ/log ::stop-repl :state :stopped)))


(defn init
  []
  (start))