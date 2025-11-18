(ns notification-service.app
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :refer [response]]
            [notification-service.routes :as r]))

(defn handler [req]
  (case [(-> req :request-method) (:uri req)]
    [:post "/messages"] (r/post-message req)
    [:get "/logs"] (r/get-logs req)
    ;; serve UI
    [:get "/"] (-> (slurp (clojure.java.io/resource "web/ui.html")) response)
    ;; default
    (response "Not found")))

(defn -main [& _args]
  (println "Starting server on port 3000")
  (run-jetty handler {:port 3000 :join? false}))
