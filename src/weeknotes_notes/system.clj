(ns weeknotes-notes.system
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]
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

(defn config-dev []
  (let [edn-store-root ".local/storage/edn-store"]
    (fs/create-dirs edn-store-root)
    {:weeknotes-notes/store {:root edn-store-root}
     :weeknotes-notes/injected-app {:store (ig/ref :weeknotes-notes/store)}
     :weeknotes-notes/http-server {:app (ig/ref :weeknotes-notes/injected-app)
                                   :port 7984}}))

(defn config-prod []
  (assert (not (str/blank? (System/getenv "GARDEN_STORAGE"))))
  {:weeknotes-notes/store {:root (str (System/getenv "GARDEN_STORAGE")
                                      "/edn-store")}
   :weeknotes-notes/injected-app {:store (ig/ref :weeknotes-notes/store)}
   :weeknotes-notes/http-server {:app (ig/ref :weeknotes-notes/injected-app)
                                 :port 7777}})

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

(comment
  (def mysys (ig/init (config-dev)))
  (ig/halt! mysys)

  (store/list-uuids (:weeknotes-notes/store mysys))

  ((:weeknotes-notes/injected-app mysys)
   {:request-method :get
    :uri "/"})

  :rcf)
