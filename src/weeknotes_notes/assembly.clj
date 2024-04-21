(ns weeknotes-notes.assembly
  (:require
   [babashka.fs :as fs]
   [babashka.process :refer [shell]]
   [clj-reload.core :as clj-reload]
   [clj-simple-router.core :as clj-simple-router]
   [clojure.string :as str]
   [nextjournal.garden-email :as garden-email]
   [nextjournal.garden-id :as garden-id]
   [org.httpkit.server :as server]
   [ring.middleware.params :as ring.params]
   [ring.middleware.session :as session]
   [ring.middleware.session.cookie :refer [cookie-store]]
   [weeknotes-notes.core :as core]
   [weeknotes-notes.path :as path]
   [weeknotes-notes.store :as store]
   [weeknotes-notes.ui :as ui]))

;; Design
;;
;; - a text field for putting weeknotes
;; - a way to extract this week's weeknotes in org-mode or markdown

(def app
  (clj-simple-router/router
   {(str "HEAD " path/root)
    (constantly {:status 202})

    (str "GET " path/root)
    #'ui/page-index

    (str "POST " path/submit-note)
    #'ui/page-submit-note

    }))

(def wrapped-app
  (-> #'app
      ;; garden-email
      (ring.params/wrap-params)
      (garden-email/wrap-with-email #_{:on-receive (fn [email] (println "Got mail"))})
      ;; garden-id
      (garden-id/wrap-auth #_{:github [{:team "nextjournal"}]})
      (session/wrap-session {:store (cookie-store)})))

(defn log-request [request]
  (prn [(:request-method request) (:uri request)])
  request)

(defn request-enter [request]
  (-> request log-request))

(defn response-exit [response]
  (-> response))

(let [root (fs/file (core/garden-storage) "edn-store")]
  (fs/create-dirs root)
  (def store (store/->FolderBackedEdnStore root)))

(defn wrapped-wrapped-app [req]
  (-> req
      log-request
      (assoc :weeknotes-notes/store store)
      wrapped-app))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn start! [opts]
  (let [server (server/run-server #'wrapped-wrapped-app
                                  (merge {:legacy-return-value? false
                                          :host "0.0.0.0"
                                          :port 7777}
                                         opts))
        local-url (format "http://localhost:%s" (server/server-port server))]
    (println "Server started:" local-url)
    (when (:browse? opts)
      (let [browser (System/getenv "BROWSER")]
        (when (:browse? opts)
          (cond (not (str/blank? browser)) (shell browser local-url)
                (fs/which "open") (shell "open" local-url)
                :else (println "Please open" local-url "in your web browser.")))))
    server))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defonce dev-server (atom nil))

#_
(do (when-let [s @dev-server]
      (print "Stopping server ... ")
      (server/server-stop! s)
      (println "stopped."))
    (reset! dev-server (start! {:port 7984
                                :browse? true})))

#_(start! {:port 7108})

(comment
  (clj-reload/reload)
  :rcf)
