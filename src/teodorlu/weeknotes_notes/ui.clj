(ns teodorlu.weeknotes-notes.ui
  (:require
   [hiccup.page :as page]
   [teodorlu.weeknotes-notes.path :as path]))

(defn index [_req]
  (list
   [:p "Write notes:"]
   [:form {:action path/submit-note}
    [:div [:textarea]]
    [:div [:input {:type "submit" :value "Submit note"}]]]))

(defn page-index [req]
  {:status 200
   :headers {"content-type" "text/html"}
   :body
   (page/html5
    [:head
     [:meta {:charset "UTF-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]]
    [:body (index req)])})
