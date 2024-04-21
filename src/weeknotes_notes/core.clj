(ns weeknotes-notes.core)

(defn garden-storage []
  (or (System/getenv "GARDEN_STORAGE") ".local/storage"))
