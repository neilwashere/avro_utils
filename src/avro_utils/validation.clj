(ns avro-utils.validation
  (:require [cheshire.core :as json])
  (:import com.github.fge.jsonschema.main.JsonSchemaFactory
           com.github.fge.jackson.JsonNodeReader))

(def ^:private schema-validator
  (.getValidator (JsonSchemaFactory/byDefault)))

(def ^:private node-reader (JsonNodeReader.))

(defn ^:private string->node [s]
  (.. node-reader (fromReader (java.io.StringReader. s))))

(defn validation-result->map [result]
  (let [valid? (.isSuccess result)
        data (.asJson result)]
    {:valid? valid? :result (json/parse-string (str data))}))

(defn validation-result [schema data]
  (let [schema-node (string->node schema)
        data-node (string->node data)]
    (.. schema-validator (validateUnchecked schema-node data-node))))

(def validate-data-with-schema
  (comp validation-result->map
     validation-result))
