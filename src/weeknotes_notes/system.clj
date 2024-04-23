(ns weeknotes-notes.system
  (:require
   [babashka.fs :as fs]
   [integrant.core :as ig]
   [org.httpkit.server :as httpkit]
   [weeknotes-notes.assembly :as assembly]
   [weeknotes-notes.store :as store]))

;; Our system components are:

:weeknotes-notes/store
;; a way to store EDN.

:weeknotes-notes/injected-app
;; an function from request to response, with required dependencies injected.

:weeknotes-notes/http-server
;; a real, running HTTP-server bound to a port.

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
  [_ {:keys [port app]}]
  (println "Server starting on " (str "http://localhost:" port))
  (httpkit/run-server app
                      {:legacy-return-value? false
                       :host "0.0.0.0"
                       :port port}))

(defmethod ig/halt-key! :weeknotes-notes/http-server
  [_ server]
  (httpkit/server-stop! server))

(defn default-config []
  (let [edn-store-root ".local/storage/edn-store"]
    (fs/create-dirs edn-store-root)
    {:weeknotes-notes/store {:root edn-store-root}
     :weeknotes-notes/injected-app {:store (ig/ref :weeknotes-notes/store)}
     :weeknotes-notes/http-server {:app (ig/ref :weeknotes-notes/injected-app)
                                   :port 7984}}))

(comment
  (def mysys (ig/init (default-config)))
  (ig/halt! mysys)

  (store/list-uuids (:weeknotes-notes/store mysys))

  :rcf)
