(ns notification-service.repos.inmemory-repo
  (:require [notification-service.repos.protocol :refer [IRepository]]
            [java-time :as jt]))

;; In-memory repository implementation for testing purposes, of course I would put this behind
;; a proper database in production, with connection pooling, retries, etc.
;; Or even a STM based solution if the use case required it.
(def default-users
  ;; mock users for testing
  [{:id "u1" :name "Alice" :email "alice@example.com" :phone "+10000001"
    :subscribed #{:sports :movies}
    :channels #{:email :sms}}
   {:id "u2" :name "Bob" :email "bob@example.com" :phone "+10000002"
    :subscribed #{:finance}
    :channels #{:push :email}}
   {:id "u3" :name "Jorge" :email "jorge@example.com" :phone "+10000003"
    :subscribed #{:sports :finance :movies}
    :channels #{:sms :push}}])

(defn make-repo []
  (let [logs (atom [])  
        users (atom default-users)]
    ;; Using reify to create an anonymous implementation of IRepository, cause why not
    (reify IRepository
      (save-log! [_ entry]
        (swap! logs #(cons (assoc entry :logged-at (jt/instant)) %))
        entry)
      (all-logs [_] @logs)
      (get-users [_] @users)
      (find-users-by-category [_ category]
        (filter #(contains? (:subscribed %) category) @users))
      (find-logs-by-user [_ user-id]
        (filter #(= (get-in % [:user :id]) user-id) @logs))
      (find-logs-by-category [_ category]
        (filter #(= (get-in % [:message :category]) category) @logs)))))
