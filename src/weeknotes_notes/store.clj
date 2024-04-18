(ns weeknotes-notes.store
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [clojure.string :as str]))

;; a storage protocol for maps with a :uuid key

(defprotocol Store
  (save-one [store data])
  (list-uuids [store])
  (load-one [store uuid]))

(defrecord FolderBackedEdnStore [root]
  Store
  (save-one [_store data]
    (let [path (fs/file root (str (:uuid data) ".edn"))]
      (spit path (pr-str data))))

  (list-uuids [_store]
    (->> (fs/glob root "*.edn")
         (map fs/file-name)
         (map #(str/replace % #"\.edn$" ""))
         (remove str/blank?)
         (map parse-uuid)))

  (load-one [_store uuid]
    (let [path (fs/file root (str uuid ".edn"))]
      (when (fs/exists? path)
        (edn/read-string (slurp path))))))
