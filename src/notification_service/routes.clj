(ns notification-service.routes
  (:require [ring.util.response :refer [response status content-type]]
            [cheshire.core :as json]
            [cheshire.generate :as json-gen]
            [notification-service.repos.inmemory-repo :as mem]
            [notification-service.services.notification :as svc]))

;; Add custom JSON encoder for java.time.Instant
(json-gen/add-encoder java.time.Instant
                      (fn [instant jsonGenerator]
                        (.writeString jsonGenerator (str instant))))

(def repo (mem/make-repo))
(def deliveries svc/default-deliveries)

(defn parse-json-body [req]
  (when-let [body (slurp (:body req))]
    (json/parse-string body true)))

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

(defn get-logs [_req]
  (-> (response (json/generate-string {:logs (.all-logs repo)}))
      (content-type "application/json")))
