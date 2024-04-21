(ns weeknotes-notes.system
  (:require
   [integrant.core :as ig]
   [clojure.string :as str]))

;; injected app
;;
;; store
;;
;; http-server

(defn config-dev []
  {:storage-path ".local/storage"
   :port 7984})

(defn config-prod []
  (assert (not (str/blank? (System/getenv "GARDEN_STORAGE"))))
  {:storage-path (System/getenv "GARDEN_STORAGE")
   :port 7777})

(defmethod ig/init-key :weeknotes/store
  [_ _])
