(ns notification-service.deliveries-test
  (:require
   [clojure.test :refer :all]
   [notification-service.deliveries.protocol :refer [deliver!]]
   [notification-service.deliveries.sms :as sms]
   [notification-service.deliveries.email :as email]
   [notification-service.deliveries.push :as push]))

;; Tests for delivery implementations, not exhaustive but enough for demo purposes
;; In real life, I would mock external services and test failure modes too
(deftest sms-delivery
  (let [d (sms/->SmsDelivery)
        res (deliver! d {:category :sports :body "hello"} {:id "u" :phone "p"})]
    (is (map? res))
    (is (contains? res :ok?))
    (is (contains? res :detail))
    (is (contains? res :timestamp))))

(deftest email-delivery
  (let [d (email/->EmailDelivery)
        res (deliver! d {:category :news :body "test email"} {:id "u" :email "test@example.com"})]
    (is (map? res))
    (is (contains? res :ok?))
    (is (contains? res :detail))
    (is (contains? res :timestamp))
    (is (true? (:ok? res)))))

(deftest push-delivery
  (let [d (push/->PushDelivery)
        res (deliver! d {:category :alerts :body "test push"} {:id "u" :device-id "device123"})]
    (is (map? res))
    (is (contains? res :ok?))
    (is (contains? res :detail))
    (is (contains? res :timestamp))
    (is (true? (:ok? res)))))
