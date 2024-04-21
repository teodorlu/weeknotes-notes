(ns weeknotes-notes.system
  (:require
   [integrant.core :as ig]))

;; injected app
;;
;; store
;;
;; http-server

(defmethod ig/init-key :weeknotes/config
  [_ _]
  {:garden-storage-path (or (System/getenv "GARDEN_STORAGE") ".local/garden-storage")
   :port (if (System/getenv "GARDEN_STORAGE") 7777 7984)})

(defmethod ig/init-key :weeknotes/store
  [_ _])
