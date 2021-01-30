(ns cookbook.db.core
  (:require
    [mount.core :refer [defstate]]
    [cookbook.config :refer [env]]
    [clj-postgresql.core :as pg]
    [conman.core :as conman]))

(defstate ^{:on-reload :noop} db
          :start (let [db (env :db)]
                  (pg/spec :dbname (db :dbname)
                           :host (db :host)
                           :user (db :user)
                           :password (db :password) ))
          :stop (pg/close! db))

(conman/bind-connection db "sql/query.sql")
