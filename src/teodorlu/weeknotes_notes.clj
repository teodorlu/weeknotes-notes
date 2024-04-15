(ns teodorlu.weeknotes-notes
  (:require
   [nextjournal.garden-email :as garden-email]
   [nextjournal.garden-id :as garden-id]
   [org.httpkit.server :as server]
   [ring.middleware.params :as ring.params]
   [ring.middleware.session :as session]
   [ring.middleware.session.cookie :refer [cookie-store]]))

;; Design
;;
;; - a text field for putting weeknotes
;; - a way to extract this week's weeknotes in org-mode or markdown

(defn garden-storage []
  (or (System/getenv "GARDEN_STORAGE") ".local/garden-storage"))

(defn html-response [response body]
  (assoc response
         :status 200
         :headers {"content-type" "text/html"}
         :body body))

(defn app [req]
  (html-response {} "OK!"))

(def wrapped-app
  (-> app
      ;; garden-email
      (ring.params/wrap-params)
      (garden-email/wrap-with-email #_{:on-receive (fn [email] (println "Got mail"))})
      ;; garden-id
      (garden-id/wrap-auth #_{:github [{:team "nextjournal"}]})
      (session/wrap-session {:store (cookie-store)})))

(defn start! [opts]
  (let [server (server/run-server #'wrapped-app
                                  (merge {:legacy-return-value? false
                                          :host "0.0.0.0"
                                          :port 7777}
                                         opts))]
    (println (format "server started: http://localhost:%s"
                     (server/server-port server)))))

(comment
  (def server (start! {:port 7196}))
  )

#_(start! {:port 7108})
