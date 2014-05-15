(ns consult.httpkitdnssr
  "
  A small wrapper around HTTP-Kit (http://http-kit.org/) constructed
  to use DNS SR on localhost as provided by Consul (http://www.consul.io/docs/agent/http.html).

  This should enable http-kit to talk to consul-cluster services by name.
  "
  (:require  [org.httpkit.client :as    http]
             [clojure.java.shell :refer [sh]]
             [clojure.data.json  :as    json]))

(defn words [text] (clojure.string/split text #"\s"))

(defn fields [item] (clojure.string/split item #"\t"))

(defn clean-header [string] (clojure.string/replace string #";; |:" ""))

(defn parse-lines [[header & items]]
  (if (empty? items)
    {}
    (let [ initial (take-while #(re-find #"^[a-z]" %) items)
           final   (drop-while #(re-find #"^[a-z]" %) items)
          ]
      (merge {(clean-header header) (map fields initial)} (parse-lines final)))))

(defn dig [service]
  (let [ response (sh "dig" "@127.0.0.1" "-p" "8600" (str service ".service.consul") "SRV")
         output   (:out response)
         lines    (clojure.string/split output #"\n")
         clean    (filter #(re-find #"^;; ADDITION|^;; ANSWER|^[a-z]" %) lines)
         groups   (parse-lines clean)
        ] groups))

(defn find-name [answer] (last answer))

(defn find-port [answer] (let [field  (nth (last answer) 4)
                               tokens (words field)
                               item   (nth tokens 2)
                               ] item))

(defn find-ip   [service-name additional]
  ;(filter #(= service-name (first %)) additional)
  (map last additional) ; TODO
  )

(defn query [service]
  (let [service-info (dig service)
        answer       (service-info "ANSWER SECTION")
        service-name (find-name answer)
        _            (or service-name (throw (ex-info "Could not find service name" {:service service})))
        service-port (find-port answer)
        additional   (service-info "ADDITIONAL SECTION")
        service-ips  (find-ip service-name additional)]
        _            (or service-ips (throw (ex-info "Could not find service IP" {:service service})))
    (map #(str "" % ":" service-port) service-ips)))

(defn service-get-index [service]
  (let [ url      (str "http://" (first (query service)) "/index.html")
         response (http/get url) ]
    @response))

; (service-get-index "foobar")
