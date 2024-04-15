(ns teodorlu.weeknotes-notes.ui
  (:require
   [hiccup.page :as page]
   [teodorlu.weeknotes-notes.path :as path]))

(defn write-a-note [_req]
  (list
   [:p "Write notes:"]
   [:form {:action path/submit-note :method "post"}
    [:div [:textarea {:name path/submit-note-note-text-name}]]
    [:div [:input {:type "submit" :value "Submit note"}]]]))

(defn page-index [req]
  {:status 200
   :headers {"content-type" "text/html"}
   :body
   (page/html5
    [:head
     [:meta {:charset "UTF-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]]
     [:body (write-a-note req)])})

(def ^:private -submit-last-req (atom nil))

(defn page-submit-note [req]
  (reset! -submit-last-req req)
  {:status 200
   :headers {"content-type" "text/html"}
   :body
   (page/html5
       [:head
        [:meta {:charset "UTF-8"}]
        [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]]
     [:body "Submitted!"])})

(comment
  (-> @-submit-last-req
      :form-params
      (get path/submit-note-note-text-name))


  )
