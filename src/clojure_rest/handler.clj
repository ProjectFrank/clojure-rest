(ns clojure-rest.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clj-http.client :as http]
            [clj-time.core :as time]
            [pandect.algo.sha256 :refer [sha256-hmac sha256-hmac* sha256-hmac-bytes]]
            [clojure.data.codec.base64 :as b64]))

(defn uri-encode [s]
  (java.net.URLEncoder/encode s))

(def amaz-creds {"AWSAccessKeyId" "AKIAJOV5RN7PJ4EB2NHQ"
                   "AssociateTag" "anguwongdood-20"})

(def sa-key "F4NP/ayIOAwhhW3iYckiHN9T1rgs/loE3z/yQ6jw")

(defn get-signature [{secret-key "SAKey", :or {secret-key sa-key}, :as params}]
  (->> (dissoc params "SAKey")
       (merge (assoc amaz-creds "Timestamp" (str (time/now))))
       ;; uri-encode the vals
       (map #(update-in % [1] uri-encode))
       ;; sort by byte value
       (into (sorted-map))
       (map (partial clojure.string/join "="))
       (clojure.string/join "&")
       (str "GET\nwebservices.amazon.com\n/onca/xml\n")
       (#(sha256-hmac* % secret-key))
       (b64/encode)
       (map char)
       (apply str)
       (uri-encode)))

(defroutes app-routes
  (GET "/amazon.json" {params :query-params}
       (get-signature params))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (compojure.handler/api)
      (wrap-defaults site-defaults)))
