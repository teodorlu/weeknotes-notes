(ns weeknotes-notes.main)

;; I want this to be the entrypoint.
;;
;; It should be called with
;;
;;     clojure -X weeknotes-notes.main/start!
;;
;; ... or?
(defn start! [opts]
  (cond-> opts
    (not (:port opts))
    (assoc :port 7984)

    (not (:storage-dir opts))
    (assoc :storage-dir (or (System/getenv "GARDEN_STORAGE")
                            ))
    )
  (let [opts (merge {:port 7984
                     :storage-dir})]))

;; 1. I can pass additional arguments to clojure -X
;;
;; 2. I can use environment variables as an override.
;;    I can respect that override locally, and defualt to something that works when that override is not set.

;; Therefore, I can write a system that /returns the config/
;; That function can take options (overrides).
;;
;; 🤔
