(ns clojure-rest.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [clojure-rest.handler :refer :all]))

(deftest test-app
  (testing "/amazon.json route"
    (let [response (app (mock/request :get "/amazon.json" {"Version" "2013-08-01", "Timestamp" "2014-08-18T12:00:00Z", "Service" "AWSECommerceService", "ResponseGroup" "Images,ItemAttributes,Offers,Reviews", "Operation" "ItemLookup", "ItemId" "0679722769", "AssociateTag" "mytag-20", "AWSAccessKeyId" "AKIAIOSFODNN7EXAMPLE", "SAKey" "1234567890"}))]
      (is (= (:status response) 200))
      (is (= (:body response) "j7bZM0LXZ9eXeZruTqWm2DIvDYVUU3wxPPpp%2BiXxzQc%3D"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
