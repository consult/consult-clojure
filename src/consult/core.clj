(ns consult.core
  "A clojure wrapper for http://www.consul.io/docs/agent/http.html

    http://<host>:<port>/v1/
      kv/
              <key>                          ~ GET PUT DELETE
      agent/
              checks                         ~ GET
              services                       ~ GET
              members                        ~ GET
              join/<address>                 ~ GET (performs action)
              force-leave/<node>             ~ GET (performs action)
              check/
                    register                 ~ PUT (JSON)
                    deregister/<checkId>     ~ ??? (method not listed)
                    pass/<checkId>           ~ ??? (method not listed)
                    warn/<checkId>           ~ ??? (method not listed)
                    fail/<checkId>           ~ ??? (method not listed)
              service/
                      register               ~ PUT
                      deregister/<serviceId> ~ ??? (method not listed)
      catalog/
              register                       ~ PUT
              deregister                     ~ PUT
              datacenters                    ~ GET
              nodes                          ~ GET
              services                       ~ GET
              service/<service>              ~ GET
              node/<node>                    ~ GET
      health/
              node/<node>                    ~ GET
              checks/<service>               ~ GET
              service/<service>              ~ GET
              state/<state>                  ~ GET
      status/
              leader                         ~ ??? (method not listed)
              peers                          ~ ??? (method not listed)
  "
  (:require  [org.httpkit.client :as http]
             [clojure.data.json :as json]))


(defn kv
 "The KV endpoint is used to expose a simple key/value store. This can be used
 to store service configurations or other meta data in a simple way. It has
 only a single endpoint: /v1/kv/<key>

 This method gets the value of the key."
  [options k]
  @(http/get (str (:base options) "/v1/kv/" k)))

(defn kv-put!
 "The KV endpoint is used to expose a simple key/value store. This can be used
 to store service configurations or other meta data in a simple way. It has
 only a single endpoint: /v1/kv/<key>

 This method updates the value of the key."
  [options k body]
  @(http/put (str (:base options) "/v1/kv/" k) {:body (json/write-str body)}))

(defn kv-delete! [options k]
 "The KV endpoint is used to expose a simple key/value store. This can be used
 to store service configurations or other meta data in a simple way. It has
 only a single endpoint: /v1/kv/<key>

 This method deletes the key."
  [options k]
  @(http/delete (str (:base options) "/v1/kv/" k)))

(defn agent-checks
  [])
(defn agent-services
  [])
(defn agent-members
  [])
(defn agent-join!
  [])
(defn agent-force-leave!
  [])
(defn agent-check-register-put!
  [])
(defn agent-check-deregister!
  [])
(defn agent-check-pass!
  [])
(defn agent-check-warn!
  [])
(defn agent-check-fail!
  [])
(defn agent-service-register-put!
  [])
(defn agent-service-deregister!
  [])

(defn catalog-register!
  [])
(defn catalog-deregister!
  [])
(defn catalog-datacenters
  [])
(defn catalog-nodes
  [])
(defn catalog-services
  [])
(defn catalog-service
  [])
(defn catalog-node
  [])

(defn health-node
  [])
(defn health-checks
  [])
(defn health-service
  [])
(defn health-state
  [])

(defn status-leader
  [])
(defn status-peers
  [])
