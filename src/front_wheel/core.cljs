(ns front-wheel.core
  (:require
    [reagent.core :as reagent :refer [atom]]
    [front-wheel.api.core :refer [empty-api-response api-get]]))

(enable-console-print!)

(def width "250px")

(defonce state
  (atom {:bike-stations empty-api-response
         :random-secret empty-api-response}))

(defn data [resource]
  (get-in @state [resource :data]))

(defn header []
  [:h1 {:style {:border-bottom "5px solid #555" :width width}} "Bike" [:br] "Stations"])

(defn loader [what]
  (when (get-in @state [what :loading])
    [:p "Loading..."]))

(defn maybe [resource component]
  (if (get-in @state [resource :loaded])
    component
    (loader resource)))

(defn bike-stations-list [stations]
  (map
    (fn [station]
      (let [[vendor place] (clojure.string/split (:name station) ", " )]
        [:div {:key (:name station) :style {:margin-bottom "10px" :width width}}
         [:div {:style {:background "#cfcfcf" :padding "7px 0px" :width "70px"
                        :text-align "center" :color "#555" :float "right"
                        :position "relative" :top "2px" :font-size "12px"}}
          (str (:bikes-count station) " bikes")]
         [:div {:style {:font-size "14px" :margin-bottom "2px"}} vendor]
         [:div {:style {:font-size "12px" :color "#777"}} place]]))
    stations))

(def bike-stations
  (with-meta
    (fn [] (maybe :bike-stations (bike-stations-list (data :bike-stations))))
    {:component-did-mount (api-get state :bike-stations {})}))

(defn secret-button []
  [:button
   {:on-click
    (fn []
      (api-get state :random-secret {:basic-auth {:username "x" :password "y"}}))}
   "secret secret"])

(defn secret-secret []
  (maybe :random-secret [:p {:style {:margin-left "15px"}} (data :random-secret)]))

(defn secrets []
  [:div {:style {:margin-top "50px"}}
   (secret-button)
   (secret-secret)])

(defn app []
  [:div
   (header)
   (bike-stations)
   (secrets)])

(reagent/render-component [app] (. js/document (getElementById "app")))

(defn on-js-reload [])
