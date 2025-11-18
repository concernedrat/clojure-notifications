(ns notification-service.routes
  (:require [ring.util.response :refer [response status content-type]]
            [cheshire.core :as json]
            [cheshire.generate :as json-gen]
            [notification-service.repos.inmemory-repo :as mem]
            [notification-service.services.notification :as svc]))

;; Add custom JSON encoder for java.time.Instant, for some reason
;; it doesn't work out of the box with Cheshire. ¯\_(ツ)_/¯
(json-gen/add-encoder java.time.Instant
                      (fn [instant jsonGenerator]
                        (.writeString jsonGenerator (str instant))))

;; Initialize in-memory repo and default deliveries
(def repo (mem/make-repo))
(def deliveries svc/default-deliveries)

(defn parse-json-body [req]
  (when-let [body (slurp (:body req))]
    (json/parse-string body true)))

;; Route Handlers
(defn post-message [req]
  (try
    (let [{:keys [category body]} (parse-json-body req)
          message {:category (keyword category) :body body}
          logs (svc/send-message! repo deliveries message)]
      (-> (response (json/generate-string {:status "ok" :sent (count logs)}))
          (content-type "application/json")))
    (catch clojure.lang.ExceptionInfo e
      (let [data (ex-data e)]
        (-> (response (json/generate-string {:error (.getMessage e)}))
            (status (or (:status data) 400))
            (content-type "application/json"))))
    (catch Exception e
      (-> (response (json/generate-string {:error "internal error" :detail (.getMessage e)}))
          (status 500)
          (content-type "application/json")))))

(defn get-logs [req]
  (let [params (:query-params req)
        user-id (get params "user")
        category (when-let [c (get params "category")] (keyword c))
        logs (cond
               (and user-id category)
               ;; Filter by both user and category
               (let [user-logs (.find-logs-by-user repo user-id)]
                 (filter #(= category (get-in % [:message :category])) user-logs))
               
               user-id
               ;; Filter by user only
               (.find-logs-by-user repo user-id)
               
               category
               ;; Filter by category only
               (.find-logs-by-category repo category)
               
               :else
               ;; No filters, return all logs
               (.all-logs repo))]
    (-> (response (json/generate-string {:logs logs}))
        (content-type "application/json"))))

(defn get-users [_req]
  (-> (response (json/generate-string {:users (.get-users repo)}))
      (content-type "application/json")))
