(ns consult.httpkitdnssr
  "
  A small wrapper around HTTP-Kit (http://http-kit.org/) constructed
  to use DNS SR on localhost as provided by Consul (http://www.consul.io/docs/agent/http.html).

  This should enable http-kit to talk to consul-cluster services by name.
  "
  (:require  [org.httpkit.client :as    http]
             [clojure.data.json  :as    json]))

(defn dig2 [service]
  (let [ url (str "http://127.0.0.1:8500/v1/catalog/service/" service)
         response @(http/get url)
         body     (or (:body response) (throw (ex-info "No body returned in response to consul service query" response)))
         result   (json/read-str body)
        ]
    (rand-nth result)))

(defn query [service]
  "Get a '<HOST>:PORT' pair for a service.
   Randomly picks a service node from the list of nodes returned"
  (let [ service-info (dig2 service)
         address      (service-info "Address")
         port         (service-info "ServicePort") ]
    (str "" address ":" port)))

(defn service-http-request
  "
   Request an http-resource through the httpkit 'client/request' function.
   Uses the 'query' function to find a node matching the requested service.

   Usage: (service-http-request :foobar                            ) or
          (service-http-request :foobar '/path'                    ) or
          (service-http-request :foobar '/path' {:options :options})
  "
  ([service]      (service-http-request service "/" {}))
  ([service path] (service-http-request service path {}))
  ([service path options]
    (let [ address (query service)
           url     (str "http://" address path)
           response (http/request (assoc options :url url) identity)
          ]
    response)))
