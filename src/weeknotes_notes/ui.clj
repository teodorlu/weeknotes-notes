(ns weeknotes-notes.ui
  (:require
   [hiccup.page :as page]
   [weeknotes-notes.path :as path]
   [weeknotes-notes.store :as store]))

(defn fragment-write-weeknotes-note [_req]
  (list
   [:h2 "Write weeknotes-notes"]
   [:form {:action path/submit-note :method "post"}
    [:div [:textarea {:name path/submit-note-note-text-name}]]
    [:div [:input {:type "submit" :value "Submit note"}]]]
   [:p "For now, all notes are public. Don't write secret notes!"]))

(defn fragemnt-weeknotes-note [note+meta]
  (list
   [:p [:code (:uuid note+meta)]]
   [:p (:note note+meta)]))

(defn fragment-list-notes [req]
  (when-let [store (:weeknotes-notes/store req)]
    (list
     [:h2 "All weeknotes-notes"]
     (interpose [:hr]
                 (for [uuid (store/list-uuids store)]
                   (let [note+meta (store/load-one store uuid)]
                     (fragemnt-weeknotes-note note+meta)))))))

(defn page-index [req]
  {:status 200
   :headers {"content-type" "text/html"}
   :body
   (page/html5
       [:head
        [:meta {:charset "UTF-8"}]
        [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]]
       [:body
        [:h1 "Weeknotes-notes"]
        [:p "Notes for writing your weeknotes could be written here."]
        (fragment-write-weeknotes-note req)
        (fragment-list-notes req)
        [:h2 "References"]
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
