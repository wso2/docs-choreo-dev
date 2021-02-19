import { callExternalEndpointPOST } from "../../../ui/src/utils/choreo-utils";
import { logger } from "../../../ui/src/utils/logger";
import * as config from "../../../ui/testcafe-run-config.json";

declare const test: TestFn;

fixture("Choreo AI Data Mapper")
  .page(config.testURL);

/**
* The following test sends a data mapping request to the data mapper
* service and checks if the service responds with the expected response.
*/
test.meta({'stable': "true"})("Test Data Mapper service call",async (t)=>{

  const requestBody = [
  {
    "schema": "Person",
    "id": "http://wso2jsonschema.org",
    "title": "root",
    "type": "object",
    "properties": {
      "calendarId": {
        "id": "http://wso2jsonschema.org/calendarId",
        "type": "string"
      },
      "fields": {
        "id": "http://wso2jsonschema.org/fields",
        "type": "string",
        "nullable": "true"
      },
      "kind": {
        "id": "http://wso2jsonschema.org/kind",
        "type": "string",
        "nullable": "true"
      },
      "id": {
        "id": "http://wso2jsonschema.org/id",
        "type": "string",
        "nullable": "true"
      },
      "etag": {
        "id": "http://wso2jsonschema.org/etag",
        "type": "string"
      },
      "scope": {
        "id": "http://wso2jsonschema.org/scope",
        "type": "object",
        "properties": {
          "type": {
            "id": "http://wso2jsonschema.org/scope/type",
            "type": "string"
          },
          "value": {
            "id": "http://wso2jsonschema.org/scope/value",
            "type": "string"
          }
        }
      },
      "role": {
        "id": "http://wso2jsonschema.org/role",
        "type": "string"
      }
    }
  },
  {
    "schema": "Person",
    "id": "http://wso2jsonschema.org",
    "title": "notroot",
    "type": "object",
    "properties": {
      "calendarId": {
        "id": "http://wso2jsonschema.org/calendarId",
        "type": "string"
      },
      "fields": {
        "id": "http://wso2jsonschema.org/fields",
        "type": "string",
        "nullable": "true"
      },
      "kind": {
        "id": "http://wso2jsonschema.org/kind",
        "type": "string",
        "nullable": "true"
      },
      "id": {
        "id": "http://wso2jsonschema.org/id",
        "type": "string",
        "nullable": "true"
      },
      "etag": {
        "id": "http://wso2jsonschema.org/etag",
        "type": "string"
      },
      "scope": {
        "id": "http://wso2jsonschema.org/scope",
        "type": "object",
        "properties": {
          "type": {
            "id": "http://wso2jsonschema.org/scope/type",
            "type": "string"
          },
          "value": {
            "id": "http://wso2jsonschema.org/scope/value",
            "type": "string"
          }
        }
      },
      "role": {
        "id": "http://wso2jsonschema.org/role",
        "type": "string"
      }
    }
  }
  ]

  const expectedResponseBody = {
  "answer": "\nfunction mapPersonToPerson (Person person) returns Person {\n// Some record fields might be missing in the AI based mapping.\n\tPerson person = {calendarId: person.calendarId, kind: person.kind, id: person.id, scope: {type: person.scope.type, value: person.scope.value}, role: person.role, fields: person.fields, etag: person.etag};\n\treturn person;\n}"
  } 

  const response = await callExternalEndpointPOST(t,(config.dataMapperTestURL + "/map/1.0.0"),requestBody,3);
  logger.info("Response from Data Mapper service: " + JSON.stringify(response.data));
  await t.expect(response.data).eql(expectedResponseBody);
  logger.info("Expected response received successfully!");
});
