(ns weeknotes-notes.production)

;; I want this to be the entrypoint in production.
;; It should set-prep to production config.
;;
;; It should be called with
;;
;;     clojure -X weeknotes-notes.production/start!
;;
;; ... or?
(defn start! [])

;; 1. I can pass additional arguments to clojure -X
;;
;; 2. I can use environment variables as an override.
;;    I can respect that override locally, and defualt to something that works when that override is not set.

;; Therefore, I can write a system that /returns the config/
;; That function can take options (overrides).
;;
;; 🤔
