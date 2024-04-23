(ns weeknotes-notes.assembly
  (:require
   [clj-simple-router.core :as clj-simple-router]
   [nextjournal.garden-email :as garden-email]
   [nextjournal.garden-id :as garden-id]
   [ring.middleware.params :as ring.params]
   [ring.middleware.session :as session]
   [ring.middleware.session.cookie :refer [cookie-store]]
   [weeknotes-notes.path :as path]
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
