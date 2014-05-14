(ns consult.core
  "A clojure wrapper for http://www.consul.io/docs/agent/http.html

  Individual methods are provided of the form (method base data)
  where base is the base url for the service - by default Consul
  uses: http://localhost:8500

  There is also the (api base) function that returns a map
  of the existing methods with the base partially applied.
  "
  (:require  [org.httpkit.client :as http]
             [clojure.data.json :as json]))


;; Helpers to serialize/deserialize the reponse
(defn- hget    [path     ] (json/read-str (:body @(http/get path))))
(defn- hput    [path body] (json/read-str (:body @(http/put path {:body (json/write-str body)}))))
(defn- hdelete [path     ] (json/read-str (:body @(http/delete path))))

(defn kv
 "The KV endpoint is used to expose a simple key/value store. This can be used
 to store service configurations or other meta data in a simple way. It has
 only a single endpoint: /v1/kv/<key>

 This method gets the value of the key."
  [base k]
  (hget (str base "/v1/kv/" (name k))))

(defn kv-put!
 "The KV endpoint is used to expose a simple key/value store. This can be used
 to store service configurations or other meta data in a simple way. It has
 only a single endpoint: /v1/kv/<key>

 This method updates the value of the key."
  [base k body]
  (hput (str base "/v1/kv/" (name k)) body))

(defn kv-delete!
 "The KV endpoint is used to expose a simple key/value store. This can be used
 to store service configurations or other meta data in a simple way. It has
 only a single endpoint: /v1/kv/<key>

 This method deletes the key."
  [base k]
  (hdelete (str base "/v1/kv/" (name k))))

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
(defn agent-check-register!
  [])
(defn agent-check-deregister!
  [])
(defn agent-check-pass!
  [])
(defn agent-check-warn!
  [])
(defn agent-check-fail!
  [])
(defn agent-service-register!
  " The register endpoint is used to add a new service to the local agent.
    There is more documentation on services here. Services may also provide a
    health check. The agent is responsible for managing the status of the check and
    keeping the Catalog in sync.

    The register endpoint expects a JSON request body to be PUT. The request
    body must look like:

    {
        :ID    \"redis1\"      ; Optional
        :Name  \"redis\"       ; Mandatory
        :Tags  [ :master :v1 ] ; Optional
        :Port  8000            ; Optional
        :Check                 ; Optional
            {
              :Script   \"/usr/local/bin/check_redis.py\"
              :Interval \"10s\"
              :TTL      \"15s\"
            }
        }
  "
  [base data]
  (hput (str base "/v1/agent/service/register") data))

(defn agent-service-deregister!
  " The deregister endpoint is used to remove a service from the local agent. The
    ServiceID must be passed after the slash. The agent will take care of
    deregistering the service with the Catalog. If there is an associated check,
    that is also deregistered.
  "
  [base id]
  (hdelete (str base "/v1/agent/service/deregister/" (name id))))

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

(defn api
  "A helper (factory) method used for partially applying the options for the individual methods.

  This could be used in the following way:

  (def system (api \"http://localhost:8500\")
  ((:kv system) :testing)"

  [base]
  {:incomplete true})
