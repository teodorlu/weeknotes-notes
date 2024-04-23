(ns weeknotes-notes.core
  (:require
   [babashka.fs :as fs]
   [integrant.core :as ig]))

(defn default-config []
  (let [edn-store-root ".local/storage/edn-store"]
    (fs/create-dirs edn-store-root)
    {:weeknotes-notes/store {:root edn-store-root}
     :weeknotes-notes/injected-app {:store (ig/ref :weeknotes-notes/store)}
     :weeknotes-notes/http-server {:app (ig/ref :weeknotes-notes/injected-app)
                                   :port 7984}}))
