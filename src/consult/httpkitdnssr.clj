(ns consult.httpkitdnssr
  "
  A small wrapper around HTTP-Kit (http://http-kit.org/) constructed
  to use DNS SR on localhost as provided by Consul (http://www.consul.io/docs/agent/http.html).

  This should enable http-kit to talk to consul-cluster services by name.
  "
  (:require  [org.httpkit.client :as    http]
             [clojure.data.json  :as    json]))

(defn sample [xs]
  (nth xs (Math/floor (rand (count xs)))))

(defn dig2 [service]
  (let [ url (str "http://127.0.0.1:8500/v1/catalog/service/" service)
         response @(http/get url)
         result  (json/read-str (:body response))
        ]
    (sample result)))

(defn query [service]
  (let [ service-info (dig2 service)
         address      (service-info "Address")
         port         (service-info "ServicePort") ]
    (str "" address ":" port)))

(defn service-get-index [service]
  (let [ url      (str "http://" (query service) "/index.html")
         response (http/get url) ]
    @response))

(defn service-request
  ([service]      (service-request service "/" {}))
  ([service path] (service-request service path {})) 
  ([service path options]
    (let [ address (query service)
           url     (str "http://" address path)
           response (http/request (assoc options :url url) identity)
          ]
    response)))
