# avro-utils

A Clojure library designed to assist in tracking down specific Avro schema validation errors.

This is a very basic wrapper for a couple of utilities found in [json schema avro](https://github.com/fge/json-schema-avro)

## Usage

There is a single convenience method to validate your data against an Avro schema

    (ns test
      (:require [avro-utils.core :refer [validate]))

      (def schema_
        "{
          \"namespace\": \"com.test\",
          \"type\": \"record\",
          \"name\": \"Test\",
          \"fields\": [
                      {\"name\": \"id\", \"type\": \"string\"},
                      {\"name\": \"fact\", \"type\": \"string\"}
                      ]
          }")

    (let [schema schema_
          data {:id "blah" :fact "interesting"}
          validation-result (validate schema data)]
      (assert (= true (:valid? validation-result))))

    (let [schema schema_
          data {:id 1 :fact null}
          validation-result (validate schema data)]
      (assert (= false (:valid? validation-result)))
      (assert (= 2 (count (:result validation-result)))))

      ;; The :result key will contain a sequence of maps that describe the actual errors.

      (assert (= (:result validation-result)
                 ({"level" "error",
                 "schema"
                 {"loadingURI" "#",
                 "pointer" "/definitions/record:com.test.Test/properties/fact"},
                 "instance" {"pointer" "/fact"},
                 "domain" "validation",
                 "keyword" "type",
                 "message"
                 "instance type (null) does not match any allowed primitive type (allowed: [\"string\"])",
                 "found" "null",
                 "expected" ["string"]}
                 {"level" "error",
                 "schema"
                 {"loadingURI" "#",
                 "pointer" "/definitions/record:com.test.Test/properties/id"},
                 "instance" {"pointer" "/id"},
                 "domain" "validation",
                 "keyword" "type",
                 "message"
                 "instance type (integer) does not match any allowed primitive type (allowed: [\"string\"])",
                 "found" "integer",
                 "expected" ["string"]})))
      

## Gotcha

It's possible that not all errors are captured on the first pass. I haven't delved deeper into the library code
to find out why but I know it can happen, for example, when unexpected attributes are in the data. In this case
you will fix your data and try again only to potentially find more errors.

This is an acceptable tradeoff. This code is really for proof of concept and will undoubtedly be replaced
by a better utility. Also, it's still an improvement over the useless errors returned by the Avro validation lib.

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
