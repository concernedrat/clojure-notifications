(ns notification-service.app
  (:require
   [clojure.java.io :as io]
   [notification-service.routes :as r]
   [ring.adapter.jetty :refer [run-jetty]]
   [ring.util.response :refer [response]]))

;; My all powerful routing handler XD
(defn handler [req]
  (case [(-> req :request-method) (:uri req)]
    [:post "/messages"] (r/post-message req)
    [:get "/logs"] (r/get-logs req)
    [:get "/users"] (r/get-users req)
    ;; serve barebones UI
    [:get "/"] (-> (slurp (io/resource "web/ui.html")) response)
    ;; default when no route matches
    (response "Not found")))

;; By recommendation of a Clojure guru friend, using Ring Jetty adapter directly for simplicity
(defn -main [& _args]
  (println "Starting server on port 3000")
  (run-jetty handler {:port 3000 :join? false}))
