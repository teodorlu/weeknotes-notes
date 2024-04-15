(ns teodorlu.weeknotes-notes.ui
  (:require
   [hiccup.page :as page]
   [teodorlu.weeknotes-notes.path :as path]
   [teodorlu.weeknotes-notes.store :as store]))

(defn fragment-write-note [_req]
  (list
   [:p "Write notes:"]
   [:form {:action path/submit-note :method "post"}
    [:div [:textarea {:name path/submit-note-note-text-name}]]
    [:div [:input {:type "submit" :value "Submit note"}]]]))

(defn fragment-list-notes [req]
  (when-let [store (:weeknotes-notes/store req)]
    [:ul
     (for [uuid (store/list-uuids store)]
       [:li uuid])]))

(defn page-index [req]
  {:status 200
   :headers {"content-type" "text/html"}
   :body
   (page/html5
       [:head
        [:meta {:charset "UTF-8"}]
        [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]]
       [:body
        (fragment-write-note req)
        (fragment-list-notes req)
        [:p "Source on Github: " [:a {:href path/github-source-url} path/github-shortname] "."]])})

(def ^:private -submit-last-req (atom nil))

(defn page-submit-note [req]
  (reset! -submit-last-req req)
  (when-let [note-text (some-> req :form-params (get path/submit-note-note-text-name))]
    (when-let [store (get req :weeknotes-notes/store)]
      (store/save-one store
                      {:uuid (random-uuid) :note note-text})))
  {:status 200
   :headers {"content-type" "text/html"}
   :body
   (page/html5
       [:head
        [:meta {:charset "UTF-8"}]
        [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]]
       [:body
        [:p "Submitted!"]
        [:p [:a {:href path/root} "Write another."]]])})

(comment
  (-> @-submit-last-req
      :form-params
      (get path/submit-note-note-text-name))


  )
