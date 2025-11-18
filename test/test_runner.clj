(ns test-runner
  (:gen-class)
  (:require [clojure.test :as t]
            ;; require all your test namespaces here:
            notification-service.deliveries-test))

(defn -main [& _]
  ;; list the test namespaces you required above
  (let [namespaces ['notification-service.deliveries-test]]
    (apply t/run-tests namespaces)))
