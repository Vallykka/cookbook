(ns cookbook.db.migration
  (:require [migratus.core :as migratus]
            [cookbook.config :refer [env]]))

(defn migrate []
  (migratus/migrate (env :migratus)))
