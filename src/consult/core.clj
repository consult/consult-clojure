(ns consult.core)

(def kv)
(def kv-put!)
(def kv-delete!)

(def agent-checks)
(def agent-services)
(def agent-members)
(def agent-join!)
(def agent-force-leave!)
(def agent-check-register-put!)
(def agent-check-deregister!)
(def agent-check-pass!)
(def agent-check-warn!)
(def agent-check-fail!)
(def agent-service-register-put!)
(def agent-service-deregister!)

(def catalog-register!)
(def catalog-deregister!)
(def catalog-datacenters)
(def catalog-nodes)
(def catalog-services)
(def catalog-service)
(def catalog-node)

(def health-node)
(def health-checks)
(def health-service)
(def health-state)

(def status-leader)
(def status-peers)

;;  http://localhost:8500/v1/
;;    kv/
;;            <key>                          ~ GET PUT DELETE
;;    agent/
;;            checks                         ~ GET
;;            services                       ~ GET
;;            members                        ~ GET
;;            join/<address>                 ~ GET (performs action)
;;            force-leave/<node>             ~ GET (performs action)
;;            check/
;;                  register                 ~ PUT (JSON)
;;                  deregister/<checkId>     ~ ??? (method not listed)
;;                  pass/<checkId>           ~ ??? (method not listed)
;;                  warn/<checkId>           ~ ??? (method not listed)
;;                  fail/<checkId>           ~ ??? (method not listed)
;;            service/
;;                    register               ~ PUT
;;                    deregister/<serviceId> ~ ??? (method not listed)
;;    catalog/
;;            register                       ~ PUT
;;            deregister                     ~ PUT
;;            datacenters                    ~ GET
;;            nodes                          ~ GET
;;            services                       ~ GET
;;            service/<service>              ~ GET
;;            node/<node>                    ~ GET
;;    health/
;;            node/<node>                    ~ GET
;;            checks/<service>               ~ GET
;;            service/<service>              ~ GET
;;            state/<state>                  ~ GET
;;    status/
;;            leader                         ~ ??? (method not listed)
;;            peers                          ~ ??? (method not listed)
