(ns notification-service.repos.protocol)

(defprotocol IRepository
  (save-log! [this log-entry])    ;; store each delivery attempt
  (all-logs [this])               ;; return logs newest-first
  (get-users [this])              ;; pre-populated users
  (find-users-by-category [this category]))
