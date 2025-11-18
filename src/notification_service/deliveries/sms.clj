(ns notification-service.deliveries.sms
  (:require [notification-service.deliveries.protocol :refer [IDelivery]]
            [java-time :as jt]))

(defrecord SmsDelivery []
  IDelivery
  (deliver! [_ message user]
    ;; mocked implementation; replace with Twilio if this were real
    (try
      ;; Simulate sending
      {:ok? true
       :detail (format "SMS to %s: %s" (:phone user) (:body message))
       :timestamp (jt/instant)}
      (catch Exception e
        {:ok? false
         :detail (str "SMS failure: " (.getMessage e))
         :timestamp (jt/instant)}))))
