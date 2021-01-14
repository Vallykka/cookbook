(ns cookbook.core-test
  (:require [clojure.test :refer :all]
            [clojure.spec.gen.alpha :as spec-gen]
            [cookbook.logic :refer :all]
            [cookbook.db.core :refer [db] :as db-core]
            [cookbook.config :refer [env]]
            [mount.core :refer [start stop]]))

(defn with-fix-db [t]
  (start [#'env #'db])
  (t)
  )
(use-fixtures :once with-fix-db)

(def test-name (spec-gen/generate (spec-gen/string-alphanumeric)))

(deftest add-dish-test
  (testing "Adds dish record"
    (add-dish test-name "test" [{"product" "294d1955-f95c-4132-90c4-6789abda6fd1", "count" {"1":400}}])
    (let [resp (db-core/dishes-by-name {:name test-name})]
      (is (= 1 (count resp)))
      (is (= test-name (-> resp first :name)))
      )
   ))
