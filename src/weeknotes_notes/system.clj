(ns weeknotes-notes.system
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]
   [integrant.core :as ig]
   [weeknotes-notes.assembly :as assembly]
   [weeknotes-notes.store :as store]))

;; store
;;
;; injected app
;;
;; http-server

:weeknotes-notes/store
:weeknotes-notes/injected-app
:weeknotes-notes/http-server

(defn config-dev []
  (let [edn-store-root ".local/storage/edn-store"]
    (fs/create-dirs edn-store-root)
    {:weeknotes-notes/store {:root edn-store-root}
     :weeknotes-notes/injected-app {:store (ig/ref :weeknotes-notes/store)}
     :weeknotes-notes/http-server {:port 7984}}))

(defn config-prod []
  (assert (not (str/blank? (System/getenv "GARDEN_STORAGE"))))
  {:weeknotes-notes/store {:root (str (System/getenv "GARDEN_STORAGE")
                                      "/edn-store")}
   :weeknotes-notes/injected-app {}
   :weeknotes-notes/http-server {:port 7777}})

(defmethod ig/init-key :weeknotes-notes/store
  [_ {:keys [root]}]
  (store/->FolderBackedEdnStore root))

(defmethod ig/init-key :weeknotes-notes/injected-app
  [_ {:keys [store]}]
  (fn [req]
    (-> req
        (assoc :weeknotes-notes/store store)
        assembly/request-enter
        assembly/wrapped-app
        assembly/response-exit)))

(defmethod ig/init-key :weeknotes-notes/http-server
  [_ _]
  "http server")

(comment
  (def mysys (ig/init (config-dev)))
  (ig/halt! mysys)

  (store/list-uuids (:weeknotes-notes/store mysys))

  )
