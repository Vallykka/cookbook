(ns cookbook.core
  (:require [mount.core :refer [start stop defstate]]
            [cookbook.config :refer [env]]
            [cookbook.db.core :refer [db]]
            [cookbook.db.migration :as migration]
            [cookbook.router :as router]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defstate ^{:on-reload :noop} webserver
          :start (let [{jetty-config :server} env]
                   (run-jetty router/app jetty-config))
          :stop (.stop webserver))

(defn run []
  (start [#'env #'db #'webserver])
  (migration/migrate))

(defn shutdown []
  (stop [#'db #'webserver]))

(defn -main
  [& args]
  (run))
