/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

export const datamapperRequestBody = [
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

export const datamapperExpectedResponseBody = {
    "answer": "\nfunction mapPersonToPerson (Person person) returns Person {\n// Some record fields might be missing in the AI based mapping.\n\tPerson person = {calendarId: person.calendarId, kind: person.kind, id: person.id, scope: {type: person.scope.type, value: person.scope.value}, role: person.role, fields: person.fields, etag: person.etag};\n\treturn person;\n}"
    } 