(ns weeknotes-notes.system
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]
   [integrant.core :as ig]
   [weeknotes-notes.assembly :as assembly]
   [weeknotes-notes.store :as store]))









;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; THIS NAMESPACE IS NOT YET WORKING AND IS NOT YET IN USE
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; THERE's A BUG!
;;
;; SEE THE (comment ,,,) FROM AT THE END OF THE FILE















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

(defn hello [x msg]
  (prn msg)
  x)

(defmethod ig/init-key :weeknotes-notes/injected-app
  [_ {:keys [store]}]
  (fn [req]
    (-> req
        (hello 1)
        (assoc :weeknotes-notes/store store)
        (hello 2)
        assembly/request-enter
        (hello 3)
        assembly/wrapped-app ;; it crashes here
        (hello 4)
        assembly/response-exit)))

(defmethod ig/init-key :weeknotes-notes/http-server
  [_ _]
  "http server")

(comment
  (def mysys (ig/init (config-dev)))
  (ig/halt! mysys)

  (store/list-uuids (:weeknotes-notes/store mysys))

  ((:weeknotes-notes/injected-app mysys)
   {:request-method :get
    :uri "/"})

  (defn xxx [req]
    (def yyy req)

    req)

  yyy


  (let [req {:request-method :get
             :uri "/"}
        store (:weeknotes-notes/store mysys)]
    (-> req
        (assoc :weeknotes-notes/store store)
        xxx
        assembly/wrapped-app)


    )

  (assembly/wrapped-wrapped-app {:request-method :get
                                 :uri "/"})



  :rcf)
