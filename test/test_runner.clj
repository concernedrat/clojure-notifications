(ns test-runner
  (:gen-class)
  (:require [clojure.test :as t]
            notification-service.deliveries-test))

(defn -main [& _]
  ;; Run all tests in the notification-service.deliveries-test namespace
  (let [namespaces ['notification-service.deliveries-test]]
    (apply t/run-tests namespaces)))
