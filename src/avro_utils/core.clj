(ns avro-utils.core
  (:require [avro-utils.avro2json :as a2j]))

(def avro-schema->json-schema
  "Convenience function to turn an avro schema into
  a valid V4 JSON schema."
  (comp a2j/result->node-string
     a2j/avro-schema->processing-result))
