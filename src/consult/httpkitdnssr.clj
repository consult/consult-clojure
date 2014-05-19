(ns consult.httpkitdnssr
  "
  A small wrapper around HTTP-Kit (http://http-kit.org/) constructed
  to use DNS SR on localhost as provided by Consul (http://www.consul.io/docs/agent/http.html).

  This should enable http-kit to talk to consul-cluster services by name.
  "
  (:require  [org.httpkit.client :as    http]
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

(defn sample [xs]
  (nth xs (Math/floor (rand (count xs)))))

(defn dig2 [service]
  (let [ url (str "http://127.0.0.1:8500/v1/catalog/service/" service)
         response @(http/get url)
         result  (json/read-str (:body response))
        ]
    (sample result)))

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
  (let [ service-info (dig2 service)
         address      (service-info "Address")
         port         (service-info "ServicePort") ]
    (str "" address ":" port)))

(defn service-get-index [service]
  (let [ url      (str "http://" (query service) "/index.html")
         response (http/get url) ]
    @response))

; (service-get-index "foobar")
