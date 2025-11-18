(ns notification-service.deliveries.protocol)

(defprotocol IDelivery
  "Delivery protocol. deliver! should return a map with keys:
   :ok? (boolean) :detail (string) :timestamp (java.time.Instant)"
  (deliver! [this message user]))
