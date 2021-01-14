(ns cookbook.logic
  (:require [cookbook.db.core :as db-core]
            [clojure.spec.alpha :as s]
            [clj-postgresql.core :as pg]
            [cheshire.core :as json]
            [clojure.tools.logging :as log])
  (:import (java.sql SQLException)
           (org.postgresql.util PGobject)))

(s/def ::non-empty-str (every-pred string? not-empty))
(s/def ::->jsonb
  (s/and (every-pred coll? not-empty)
         (s/conformer (fn [coll]
                        (try (json/encode coll)
                             (catch Exception e ::s/invalid))))))
(s/def ::->jsonb-pg-obj
  (s/conformer (fn [jsonb]
                 (try (pg/object "jsonb" jsonb)
                      (catch SQLException e ::s/invalid)))))

(s/def ::->char-pg-uuid
  (s/and (every-pred not-empty uuid?)
  (s/conformer (fn [id]
                 (try (pg/object "uuid" id)
                      (catch SQLException e ::s/invalid))))))

(s/def ::->pg-obj-to-value
  (s/conformer (fn [coll]
                 (try
                   (let [[field obj] (first coll)]
                     {field (.getValue obj)})
                   (catch SQLException e ::s/invalid)))))

(defn check-request-param
  "Throws exception on first failed validation"
  [param & specs]
  (reduce
    #(if-let [problem (s/explain-data %2 %1)]
       (throw
         (ex-info (str "Bad request: " (:clojure.spec.alpha/value problem)) problem))
       (s/conform %2 %1)
       ) param specs))

(defn ->pg-json [coll]
  (when-some [col (first coll)]
    (when-some [[field obj] col]
      {field (json/decode (.getValue obj))})))

(defn add-dish
  [name algo ingredients]
  (check-request-param name ::non-empty-str)
  (try
    (let [ingredients-formatted (-> ingredients
                                    (check-request-param ::->jsonb ::->jsonb-pg-obj))
          res (db-core/insert-dish {:name name, :algo algo, :ingredients ingredients-formatted})]
      (log/info (format "Inserted: %s, res: %s" name res))
      res)
    (catch SQLException e
      (throw (ex-info (format "Couldn't add dish %s. Reason: %s: " name (.getMessage e)) {} e)))))

(defn update-dish
  [id name algo ingredients]
  (map #(check-request-param % ::non-empty-str) [id name])
  (try
    (let [ingredients-formatted (-> ingredients
                                    (check-request-param ::->jsonb ::->jsonb-pg-obj))
          res (db-core/update-dish {:name name, :algo algo, :ingredients ingredients-formatted})]
      (log/info (format "Updated %s, res: %s" name res))
      res)
    (catch SQLException e
      (throw (ex-info (format "Couldn't update dish %s. Reason: %s: " name (.getMessage e)) {} e)))))

(defn delete-dish
  [id]
  (check-request-param id ::non-empty-str)
  (try (db-core/delete-dish {:id id})
       (catch SQLException e
         (throw (ex-info (format "Couldn't delete dish %s. Reason: %s: " id (.getMessage e)) {} e)))))

(defn get-dish-by-id [id]
  (try
    (let [param (-> id
                    (check-request-param ::non-empty-str ::->char-pg-uuid))
          response-map (db-core/dish-by-id {:id param})
          converted (->pg-json (filter #(instance? PGobject (second %)) (seq response-map)))
          res (merge response-map converted)
          ]
      res)
    (catch SQLException e
        (throw (ex-info (format "Couldn't find dish %s. Reason: %s: " id (.getMessage e)) {} e)))))

(defn get-dish-list [name]
  (let [param (if (some? name) (str name "%") "%")]
    (try (db-core/dishes-by-name {:name param})
         (catch SQLException e
           (throw (ex-info (format "Couldn't find dish %s. Reason: %s: " name (.getMessage e)) {} e))))))

(defn get-catalog [type]
  (check-request-param type ::non-empty-str)
  (case type
    "product" (db-core/product)
    "measure" (db-core/measure)
    "Catalog not found"
    ))
