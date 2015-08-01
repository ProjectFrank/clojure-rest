(ns clojure-rest.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clj-http.client :as http]
            [clj-time.core :as time]
            [pandect.algo.sha256 :refer [sha256-hmac]]
            [clojure.data.codec.base64 :as b64]))

(defn uri-encode [s]
  (java.net.URLEncoder/encode s))

#_(def amaz-creds {"AWSAccessKeyId" "AKIAJOV5RN7PJ4EB2NHQ"
                 "Timestamp" "blah"
                 "AssociateTag" "anguwongdood-20"})

(def amaz-creds {"AWSAccessKeyId" "AKIAIOSFODNN7EXAMPLE"
                 "AssociateTag" "mytag-20"})

#_(def sa-key "F4NP/ayIOAwhhW3iYckiHN9T1rgs/loE3z/yQ6jw")

(def sa-key "1234567890")

(defn get-signature [params]
  (let [whole-map (assoc (merge params amaz-creds)
                         "Timestamp" "2014-08-18T12:00:00Z"
                         #_(str (time/now)))]
    (->> whole-map
         ;; uri-encode the vals
         (map #(update-in % [1] uri-encode))
         ;; sort by byte value
         (into (sorted-map))
         (map (partial clojure.string/join "="))
         (clojure.string/join "&")
         (str "GET\nwebservices.amazon.com\n/onca/xml\n")
         (#(sha256-hmac % sa-key))
         (.getBytes)
         (b64/encode)
         (map char)
         (apply str))))

(defroutes app-routes
  (GET "/amazon.json" {params :query-params}
       (get-signature params))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (compojure.handler/api)
      (wrap-defaults site-defaults)))
