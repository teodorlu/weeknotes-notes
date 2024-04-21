(ns weeknotes-notes.system
  (:require
   [integrant.core :as ig]
   [clojure.string :as str]))

;; store
;;
;; injected app
;;
;; http-server

:weeknotes-notes/store
:weeknotes-notes/injected-app
:weeknotes.notes/http-server

(defn config-dev []
  {:weeknotes-notes/store {:root ".local/storage"}
   :weeknotes-notes/injected-app {}
   :weeknotes.notes/http-server {:port 7984}})

(defn config-prod []
  (assert (not (str/blank? (System/getenv "GARDEN_STORAGE"))))
  {:weeknotes-notes/store {:root (System/getenv "GARDEN_STORAGE")}
   :weeknotes-notes/injected-app {}
   :weeknotes.notes/http-server {:port 7777}})

(defmethod ig/init-key :weeknotes/store
  [_ _])
