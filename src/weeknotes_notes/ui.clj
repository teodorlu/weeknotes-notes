(ns weeknotes-notes.ui
  (:require
   [hiccup.page :as page]
   [weeknotes-notes.path :as path]
   [weeknotes-notes.store :as store]))

(defn fragment-write-note [_req]
  (list
   [:p "Write notes:"]
   [:form {:action path/submit-note :method "post"}
    [:div [:textarea {:name path/submit-note-note-text-name}]]
    [:div [:input {:type "submit" :value "Submit note"}]]]
   [:p "For now, all notes are public. Don't write secrets!"]))

(defn fragment-list-notes [req]
  (when-let [store (:weeknotes-notes/store req)]
    (list
     ;; First, list active notes
     [:p "Notes + content:"]
     [:ul
      (for [uuid (store/list-uuids store)
            ;; found the bug!
            ;; This is wrong 👇
            #_#_
            note+meta (store/load-one store uuid)
            ;; it should be in a let!
            ;;
            ;; I introduced this bug while I was coding
            ;; And I was in a REPL
            ;; But I had not loaded the new code that introduced the bug 🙈
            ;;
            ;; Lessons:
            ;;
            ;; - Tests are useful, I currently hand-roll all the logic assuming
            ;;   I won't write bugs. That's a half-decent assuption /up to some point/.
            ;;   After reaching that point, guardrails / structure is required.
            ;;
            ;; - It's super-useful to be able to run something like clj-reload
            ;;   to sync files and REPL.
            ;;
            ;; Actions:
            ;;
            ;; - Tests -- I don't want to write those for now.
            ;;
            ;; - REPL state / files sync: I want to address that now. First
            ;;   integrant and integrant/repl. Then use integrant/repl and
            ;;   clj-reload together.
            ]
        (let [note+meta (store/load-one store uuid)]
          [:li
           [:p [:strong uuid]]
           [:p (:note note+meta)]
           [:p "hello"]
           [:p [:pre (pr-str (dissoc note+meta :uuid :note))]]]))]
     ;; Then, list archived notes
     [:p "Archived notes:"]
     )))

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
