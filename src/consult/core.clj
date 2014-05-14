(ns consult.core
  "A clojure wrapper for http://www.consul.io/docs/agent/http.html

  Individual methods are provided of the form (method base data)
  where base is the base url for the service - by default Consul
  uses: http://localhost:8500

  There is also the (api base) function that returns a map
  of the existing methods with the base partially applied.

  Current functions include:

    * kv                        ~ Get a key-value pair
    * kv-put!                   ~ Put a new value against a key
    * kv-delete!                ~ Delete a key
    * agent-checks              ~ List checks registered against an agent
    * agent-services            ~ List services registered against an agent
    * agent-members             ~ List members registered against an agent
    * agent-join!               ~ Make agent join a cluster
    * agent-force-leave!        ~ Make agent leave a cluster
    * agent-check-register!     ~ Add a registration check ??
    * agent-check-deregister!   ~ Add a deregistration check ??
    * agent-check-pass!         ~ Add a pass check ??
    * agent-check-warn!         ~ Add a warn check ??
    * agent-check-fail!         ~ Add a fail check ??
    * agent-service-register!   ~ Register a new service with an agent
    * agent-service-deregister! ~ Deregister a service with an agent
    * catalog-register!         ~ Low level service registration
    * catalog-deregister!       ~ Low level service un-registration
    * catalog-datacenters       ~ Low level data-center listing
    * catalog-nodes             ~ Low level node listing
    * catalog-services          ~ Low level services listing
    * catalog-service           ~ Low level individual service information
    * catalog-node              ~ Low level individual node information
    * health-node               ~ Node health
    * health-checks             ~ Node health checks info
    * health-service            ~ Service health
    * health-state              ~ Overall health state ??
    * status-leader             ~ Leader node information
    * status-peers              ~ Peer information
  "
  (:require  [org.httpkit.client :as http]
             [clojure.data.json :as json]))


;; Helpers to serialize/deserialize the reponse
(defn- json?    [data     ] (try (json/read-str data) (catch Throwable e nil)))
(defn- response [prom     ] (let [_        (or prom                (throw (ex-info "Expected a promise to be returned" {})))
                                  data     (or @prom               (throw (ex-info "Expected an HTTP response" {})))
                                  status   (:status data)
                                  _        (or (<= 200 status 299) (throw (ex-info "Non-200 response from server" data)))
                                  body     (or (:body data)        (throw (ex-info "Expected an HTTP response body" {})))
                                  ]
                                (json? body)))
(defn- hget     [path     ] (response (http/get path)))
(defn- hput     [path body] (response (http/put path {:body (json/write-str body)})))
(defn- hdelete  [path     ] (response (http/delete path)))

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
  " This endpoint is used to return the all the services that are registered with
    the local agent. These services were either provided through configuration
    files, or added dynamically using the HTTP API. It is important to note that
    the services known by the agent may be different than those reported by the
    Catalog. This is usually due to changes being made while there is no leader
    elected. The agent performs active anti-entropy, so in most situations
    everything will be in sync within a few seconds.
  "
  [base]
  (hget (str base "/v1/agent/services")))

(defn agent-members
  "This endpoint is hit with a GET and returns the members the agent sees in
  the cluster gossip pool. Due to the nature of gossip, this is eventually
  consistent and the results may differ by agent. The strongly consistent view
  of nodes is instead provided by /v1/catalog/nodes."
  [base]
  (hget (str base "/v1/agent/members")))

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
  " This endpoint is hit with a GET and returns the nodes providing a service in
    a given DC. By default the datacenter of the agent is queried, however the dc
    can be provided using the ?dc= query parameter.  The service being queried
    must be provided after the slash. By default all nodes in that service are
    returned. However, the list can be filtered by tag using the ?tag= query
    parameter."
  [base id]
  (hget (str base (name id))))

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
  { :kv                        (partial kv                        base)
    :kv-put!                   (partial kv-put!                   base)
    :kv-delete!                (partial kv-delete!                base)
    :agent-checks              (partial agent-checks              base)
    :agent-services            (partial agent-services            base)
    :agent-members             (partial agent-members             base)
    :agent-join!               (partial agent-join!               base)
    :agent-force-leave!        (partial agent-force-leave!        base)
    :agent-check-register!     (partial agent-check-register!     base)
    :agent-check-deregister!   (partial agent-check-deregister!   base)
    :agent-check-pass!         (partial agent-check-pass!         base)
    :agent-check-warn!         (partial agent-check-warn!         base)
    :agent-check-fail!         (partial agent-check-fail!         base)
    :agent-service-register!   (partial agent-service-register!   base)
    :agent-service-deregister! (partial agent-service-deregister! base)
    :catalog-register!         (partial catalog-register!         base)
    :catalog-deregister!       (partial catalog-deregister!       base)
    :catalog-datacenters       (partial catalog-datacenters       base)
    :catalog-nodes             (partial catalog-nodes             base)
    :catalog-services          (partial catalog-services          base)
    :catalog-service           (partial catalog-service           base)
    :catalog-node              (partial catalog-node              base)
    :health-node               (partial health-node               base)
    :health-checks             (partial health-checks             base)
    :health-service            (partial health-service            base)
    :health-state              (partial health-state              base)
    :status-leader             (partial status-leader             base)
    :status-peers              (partial status-peers              base)
   })
