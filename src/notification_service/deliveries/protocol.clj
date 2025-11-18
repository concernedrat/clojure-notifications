(ns notification-service.deliveries.protocol)

(defprotocol IDelivery
  "Delivery protocol. deliver! should return a map with keys:
    in production I would use an async model here, but for simplicity it's sync.
   :ok? (boolean) :detail (string) :timestamp (java.time.Instant)"
  (deliver! [this message user]))
