(ns ^:figwheel-no-load myreagent.dev
  (:require
    [myreagent.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
