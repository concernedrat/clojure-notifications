(ns notification-service.deliveries.email
  (:require [notification-service.deliveries.protocol :refer [IDelivery]]
            [java-time :as jt]))

(defrecord EmailDelivery []
  IDelivery
  (deliver! [_ message user]
    ;; mocked implementation; replace with actual email sending logic
    (try
      ;; Simulate sending
      {:ok? true
       :detail (format "Email to %s: %s" (:email user) (:body message))
       :timestamp (jt/instant)}
      (catch Exception e
        {:ok? false
         :detail (str "Email failure: " (.getMessage e))
         :timestamp (jt/instant)}))))
