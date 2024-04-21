(ns weeknotes-notes.system
  (:require
   [clojure.string :as str]
   [integrant.core :as ig]
   [weeknotes-notes.store :as store]))

;; store
;;
;; injected app
;;
;; http-server

:weeknotes-notes/store
:weeknotes-notes/injected-app
:weeknotes.notes/http-server

(defn config-dev []
  {:weeknotes-notes/store {:root ".local/storage/edn-store"}
   :weeknotes-notes/injected-app {}
   :weeknotes.notes/http-server {:port 7984}})

(defn config-prod []
  (assert (not (str/blank? (System/getenv "GARDEN_STORAGE"))))
  {:weeknotes-notes/store {:root (str (System/getenv "GARDEN_STORAGE")
                                      "/edn-store")}
   :weeknotes-notes/injected-app {}
   :weeknotes.notes/http-server {:port 7777}})

(defmethod ig/init-key :weeknotes-notes/store
  [_ {:keys [root]}]
  (store/->FolderBackedEdnStore root))

(defmethod ig/init-key :weeknotes-notes/injected-app
  [_ _]
  "injected app")

(defmethod ig/init-key :weeknotes-notes/http-server
  [_ _]
  "http server")

(comment
  (def mysys (ig/init (config-dev)))
  (ig/halt! mysys)

  (store/list-uuids (:weeknotes-notes/store mysys))

  )
