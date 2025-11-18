(ns notification-service.deliveries.push
  (:require [notification-service.deliveries.protocol :refer [IDelivery]]
            [java-time :as jt]))

(defrecord PushDelivery []
  IDelivery
  (deliver! [_ message user]
    ;; mocked implementation; Firebase notifications (wink)
    (try
      ;; Simulate sending
      {:ok? true
       :detail (format "Push to %s: %s" (:device-id user) (:body message))
       :timestamp (jt/instant)}
    (catch Exception e
      {:ok? false
       :detail (str "Push failure: " (.getMessage e))
       :timestamp (jt/instant)}))))
