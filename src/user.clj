(ns user
  (:require
   integrant.repl
   [weeknotes-notes.core :as core]
   [clj-reload.core :as clj-reload]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn restart! []
  (require 'weeknotes-notes.system)
  (integrant.repl/set-prep! weeknotes-notes.core/default-config)
  (integrant.repl/halt)
  (clj-reload/reload)
  (integrant.repl/go))

;; run restart! from a REPL to start!
