(ns avro-utils.avro2json
  (:require [clojure.java.io :as io])
  (:import com.github.fge.avro.Avro2JsonSchemaProcessor
           com.github.fge.jackson.JsonLoader
           com.github.fge.jsonschema.core.keyword.syntax.SyntaxProcessor
           com.github.fge.jsonschema.core.messages.JsonSchemaSyntaxMessageBundle
           [com.github.fge.jsonschema.core.processing ProcessingResult ProcessorChain]
           com.github.fge.jsonschema.core.report.ListProcessingReport
           com.github.fge.jsonschema.core.tree.SimpleJsonTree
           com.github.fge.jsonschema.core.util.ValueHolder
           com.github.fge.jsonschema.library.DraftV4Library
           com.github.fge.msgsimple.load.MessageBundles))

(def ^:private bundle
  (MessageBundles/getBundle JsonSchemaSyntaxMessageBundle))

(def ^:private syntax
  (SyntaxProcessor. bundle
                    (.getSyntaxCheckers (DraftV4Library/get))))

(def ^:private avro
  (Avro2JsonSchemaProcessor.))

(def ^:private processor
  (.. (ProcessorChain/startWith avro)
      (chainWith syntax)
      (getProcessor)))

(defn processing-result->string [result]
  (str (.. result
           (getResult)
           (getValue)
           (getBaseNode))))

(defn avro-schema->processing-result [s]
  (let [report (ListProcessingReport.)
        holder (-> s
                   JsonLoader/fromString
                   SimpleJsonTree.
                   ValueHolder/hold)]

    (ProcessingResult/uncheckedResult processor
                                      report
                                      holder)))

(def avro-schema->json-schema
  "Convenience function to turn an avro schema into
  a valid V4 JSON schema."
  (comp processing-result->string
     avro-schema->processing-result))
