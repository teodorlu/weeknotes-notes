(ns weeknotes-notes.system
  (:require
   [integrant.core :as ig]))

;; injected app
;;
;; store
;;
;; http-server

(defmethod ig/init-key :weeknotes/store
  []
  )
