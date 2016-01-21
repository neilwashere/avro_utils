(ns avro-utils.core
  (:require [avro-utils
             [avro2json :refer [avro-schema->json-schema]]
             [validation :refer [validate-data-with-schema]]]
            [cheshire.core :as json]))

(defn validate [schema-name data]
  (let [schema (avro-schema->json-schema schema-name)
        data (if (map? data)
               (json/generate-string data)
               data)]
    (validate-data-with-schema schema data)))
