(ns front-wheel.api.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(def api-base-url "http://localhost:8899/");;https://back-wheel.herokuapp.com/")

(def empty-api-response
  {:loading false :loaded false :data nil})

(defn api-get [state resource options]
  (go
    (prn "api-get" resource options (str api-base-url (name resource)))
    (swap! state assoc-in [resource :loading] true)
    (let [response (<! (http/get (str api-base-url (name resource))
                                 (merge {:with-credentials? false} options)))]
      (swap! state assoc resource {:data (get-in response [:body resource]) :loaded true :loading false})
      (prn @state))))
