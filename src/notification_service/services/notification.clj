(ns notification-service.services.notification
  (:require
   [clojure.set :as set]
   [clojure.string]
   [notification-service.deliveries.protocol :refer [deliver!]]
   [notification-service.deliveries.sms :as sms]
   [notification-service.deliveries.email :as email]
   [notification-service.deliveries.push :as push]
   [notification-service.repos.protocol :refer [IRepository]]))

;; Registry mapping channel keywords to delivery implementation instances
(def default-deliveries
  {:sms   (sms/->SmsDelivery)
   :email (email/->EmailDelivery)
   :push  (push/->PushDelivery)})

(defn validate-message! [{:keys [category body]}]
  (when (or (nil? category) (not (keyword? category)))
    (throw (ex-info "Invalid category" {:status 400})))
  (when (or (nil? body) (clojure.string/blank? body))
    (throw (ex-info "Message body required" {:status 400}))))

(defn send-message!
  "Main orchestration function.
   Ideally, this would be async, but for simplicity, it's sync here.
   Also in prod this must run from a background worker, not the HTTP request thread.
   Parameters:
   repo - implementation of IRepository
   deliveries - map of channel -> IDelivery impl
   message - map json of the message {:category :sports :body \"...\"}
   returns list of log entries of the delivery attempts."
  [repo deliveries message]
  (validate-message! message)
  (let [category (:category message)
        ;; find subscribers from repo
        ;; ensure repo implements IRepository
        users (if (satisfies? IRepository repo)
                (doall (seq (.find-users-by-category repo category)))
                (throw (ex-info "Repo does not implement IRepository" {})))]
    (->> users
         (mapcat (fn [user]
                   (let [user-channels (set/intersection (:channels user) (set (keys deliveries)))]
                     (for [ch user-channels]
                       (let [delivery (get deliveries ch)
                             result (try
                                      (deliver! delivery message user)
                                      (catch Exception e
                                        {:ok? false :detail (str "exception: " (.getMessage e))}))
                             log-entry {:message message
                                        :user user
                                        :channel ch
                                        :result result}]
                         ;; persist log
                         (.save-log! repo log-entry)
                         log-entry)))))
         doall)))
