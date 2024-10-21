# WSO2 Style Guidelines

A set of guidelines focused on enforcing uniformity in API style, including naming conventions, formatting, and documentation to ensure clarity and maintainability across all APIs.
### ❌ operation-operationId-valid-in-url

Operation IDs must not contain characters that are invalid for URLs.

**Invalid Example**

The `operationId` in this example includes a pipe and space, which are invalid for URLs.

```json
{
  "/users": {
    "get": {
      "operationId": "invalid|operationID"
    }
  }
}
```

**Valid Example**

This `operationId` is valid for URLs.

```json
{
  "/users": {
    "get": {
      "operationId": "this-must-be-unique"
    }
  }
}
```

---

### ❌ path-declarations-must-exist

Path parameter declarations must not be empty.

**Invalid Example**

`/users/{}`

**Valid Example**

`/users/{userId}`

---

### ❌ paths-no-trailing-slash

Paths must not end with a trailing slash.

`/users` and `/users/` are separate paths. It's considered bad practice for them to differ based only on a trailing slash. It's usually preferred to not have a trailing slash.

**Invalid Example**

The `users` path ends with a slash.

```json
{
  "/users/": {
    "post": {}
  }
}
```

**Valid Example**

```json
{
  "/user": {
    "post": {}
  }
}
```

---

### ❌ path-params

Path parameters must be defined and valid in either the `path-parameters` or the `operation-parameters` object. Likewise, defined `path-parameters` or `operation-parameters` must be used in the `paths` string.

**Valid Example**

For this path:

`/users/{id}/{location}`

The following path parameters must be defined.

```json
{
  "parameters": [
    {
      "schema": {
        "type": "string"
      },
      "name": "id",
      "in": "path",
      "required": true,
      "description": "This is the user's ID"
    },
    {
      "schema": {
        "type": "string"
      },
      "name": "location",
      "in": "path",
      "required": true,
      "description": "This is the user's location"
    }
  ]
}
```

---

### ❌ server-lowercase

Server URLs must be lowercase. This standard helps meet industry best practices.

**Invalid Example**

The `url` property uses uppercase letters.

```json
{
  "servers": [
    {
      "url": "https://ACME.com/api"
    }
  ]
}
```

**Valid Example**

The `url` property is fully lowercase.

```json
{
  "servers": [
    {
      "url": "https://acme.com/api"
    }
  ]
}
```

---

### ❌ operation-operationId-unique

Every operation in a single document must have a unique `operationID`.

**Valid Example**

In this example, the `operationId` is `get-users`. This `operationId` must be unique in an API document.

```json
{
  "get": {
    "summary": "Get users",
    ... ,
    "operationId": "get-users"
  }
} 
```

---

### ❌ oas2-operation-formData-consume-check

Operations with an `in: formData` parameter must include a `consumes` property with one of these values:

`application/x-www-form-urlencoded`

`multipart/form-data`

**Valid Example**

In this example, the `consumes` property correctly includes the `multipart/form-data` value.

```json
{
  "post": {
    "summary": "Uploads a file",
    "consumes": [
      "multipart/form-data"
    ],
    "parameters": [
      {
        "name": "name",
        "in": "formData",
        "description": "Upload a file",
        "required": false,
        "type": "string"
      }
    ]
  }
}
```

---

### ❌ no-$ref-siblings

Property must not be placed among $ref.

---

### ❌ oas2-api-schemes

OpenAPI 2 host `schemes` reflect the transfer protocol of the API. 
Host schemes must be present and an array with one or more of these values: 
`http`, `https`, `ws`, or `wss`.

**Valid Example**

This example shows that host schemes are `http` and `https`.

```json
{
  "schemes": [
    "http",
    "https"
  ]
}
```


---

### ❌ oas2-discriminator

Discriminator property must be defined and required

---

### ❌ oas2-valid-schema-example

Examples must be valid against their defined schema.

**Valid Example**

The following schema includes the `name` and `petType` properties.

```json
{
  "Pet": {
    "type": "object",
    "properties": {
      "name": {
        "type": "string"
      },
      "petType": {
        "type": "string"
      }
    }
  }
}
```

When referenced in a response example, the property names on line 6 and 7 must match those in the schema (`petName` and `petType`).

```json
{
  "responses": {
    "200": {
      "content": {
        "application/json": {
          "examples": {
            "Pet Example": {
              "petName": "Bubbles",
              "petType": "Dog"
            }
          },
          "schema": {
            "$ref": "#/definitions/Pet"
          }
        }
      }
    }
  }
}
```


---

### ❌ oas3-valid-schema-example

Examples must be valid against their defined schema.

**Valid Example**

The following schema includes the `name` and `petType` properties.

```json
{
  "Pet": {
    "type": "object",
    "properties": {
      "name": {
        "type": "string"
      },
      "petType": {
        "type": "string"
      }
    }
  }
}
```

When referenced in a response example, the property names on line 6 and 7 must match those in the schema (`petName` and `petType`).

```json
{
  "responses": {
    "200": {
      "content": {
        "application/json": {
          "examples": {
            "Pet Example": {
              "petName": "Bubbles",
              "petType": "Dog"
            }
          },
          "schema": {
            "$ref": "#/definitions/Pet"
          }
        }
      }
    }
  }
}
```


---

### ❌ oas2-valid-media-example

Examples must be valid against their defined schema. Common reasons you may see errors if:

* The value used for property examples is not the same type indicated in the schema (string vs. integer, for example).
* Examples contain properties not included in the schema.

**Valid Example**

The following schema indicates that the `id` property is a `string` type.  

```json
"User": {
  "title": "User",
  "type": "object",
  "properties": {
    "id": {
      "type": "string"
    }
  }
}
```

When the example is referenced in a response, the `id` property must be `string`.

```json
{
  "responses": {
    "200": {
      "description": "User Found",
      "schema": {
        "$ref": "#/definitions/User"
      },
      "examples": {
        "Get User Alice Smith": {
          "id": "smith, alice"
        }
      }
    }
  }
}
```


---

### ❌ oas3-valid-media-example

The following schema includes the `name` and `petType` properties.

**Valid Example**

```json
{
  "Pet": {
    "type": "object",
    "properties": {
      "name": {
        "type": "string"
      },
      "petType": {
        "type": "string"
      }
    }
  }
}
```

When referenced in a response example, the property names on line 6 and 7 must match those in the schema (`petName` and `petType`).

```json
{
  "responses": {
    "200": {
      "content": {
        "application/json": {
          "examples": {
            "Pet Example": {
              "petName": "Bubbles",
              "petType": "Dog"
            }
          },
          "schema": {
            "$ref": "#/definitions/Pet"
          }
        }
      }
    }
  }
}
```


---

### ❌ oas3-server-variables

This rule ensures that server variables defined in OpenAPI Specification 3 (OAS3) and 3.1 are valid, not unused, and result in a valid URL. Properly defining and using server variables is crucial for the accurate representation of API endpoints and preventing potential misconfigurations or security issues.

**Recommended**: Yes

**Bad Examples**

1. **Missing definition for a URL variable**:

```yaml
servers:
  - url: "https://api.{region}.example.com/v1"
    variables:
      version:
        default: "v1"
```

In this example, the variable **`{region}`** in the URL is not defined within the **`variables`** object.

2. **Unused URL variable:**

```yaml
servers:
  - url: "https://api.example.com/v1"
    variables:
      region:
        default: "us-west"
```

Here, the variable **`region`** is defined but not used in the server URL.

3. **Invalid default value for an allowed value variable**:

```yaml
servers:
  - url: "https://api.{region}.example.com/v1"
    variables:
      region:
        default: "us-south"
        enum:
          - "us-west"
          - "us-east"
```

The default value 'us-south' isn't one of the allowed values in the **`enum`**.

4. **Invalid resultant URL**:

```yaml
servers:
  - url: "https://api.example.com:{port}/v1"
    variables:
      port:
        default: "8o80"
```

Substituting the default value of **`{port}`** results in an invalid URL.

**Good Example**

```yaml
servers:
  - url: "https://api.{region}.example.com/{version}"
    variables:
      region:
        default: "us-west"
        enum:
          - "us-west"
          - "us-east"
      version:
        default: "v1"
```

In this example, both **`{region}`** and **`{version}`** variables are properly defined and used in the server URL. Also, the default value for **`region`** is within the allowed values.

---

### ❌ array-items

Schemas with `type: array`, require a sibling `items` field.

**Recommended:** Yes

**Good Example**

```yaml
TheGoodModel:
  type: object
  properties:
    favoriteColorSets:
      type: array
      items:
        type: array
        items: {}
```

**Bad Example**

```yaml
TheBadModel:
  type: object
  properties:
    favoriteColorSets:
      type: array
      items:
        type: array
```

---

### ⚠️ contact-url

The `contact` object should have a valid organization URL. 

**Valid Example**

```json
{
  "contact": {
     ... ,
     "url": "https://acme.com",
     ... 
  },
```

---

### ⚠️ contact-email

The `contact` object should have a valid email. 

**Valid Example**

```json
{
  "contact": {
     ... ,
     "email": "support.contact@acme.com"
  },
```

---

### ⚠️ info-contact

The `info` object should include a `contact` object.

**Valid Example**

```json
{
  "info": {
    ... ,
    "contact": {
      "name": "ACME Corporation",
      "url": "https://acme.com",
      "email": "support.contact@acme.com"
    }
  }
}
```

---

### ⚠️ info-description

The `info` object should have a `description` object.

**Valid Example**

```json
{
  "info": {
     ... ,
     "description": "This describes my API."
  }
}
```

---

### ⚠️ info-license

The `info` object should have a `license` object.

**Valid Example**

```json
{
  "info": {
    ... ,
    "license": {
      "name": "Attribution-ShareAlike 4.0 International (CC BY-SA 4.0)",
      "url": "https://creativecommons.org/licenses/by-sa/4.0/"
    }
  }
}
```

---

### ⚠️ license-url

The `license` object should include a valid url.

**Valid Example**

```json
{
  "license": {
    "name": "Attribution-ShareAlike 4.0 International (CC BY-SA 4.0)",
    "url": "https://creativecommons.org/licenses/by-sa/4.0/"
  }
}
```

---

### ⚠️ no-eval-in-markdown

Markdown descriptions should not contain [`eval()` functions](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/eval),
which pose a security risk.

**Invalid Example**

```json
{
  "info": {
    ... ,
    "description": "API for users. eval()"
  }
}
```

---

### ⚠️ no-script-tags-in-markdown

Markdown descriptions should not contain `script` tags, which pose a security risk.

**Invalid Example**

```json
{
  "info": {
    ... ,
    "description": "API for users. <script>alert(\"You are Hacked\");</script>"
  }
}
```

---

### ⚠️ openapi-tags-alphabetical

Global tags specified at the root OpenAPI Document level should be in alphabetical order based on the `name` property.

**Invalid Example**

```json
{
  "tags":[
    {
      "name":"Z Global Tag"
    },
    {
      "name":"A Global Tag"
    }
  ]
}
```

**Valid Example**

```json
{
  "tags":[
    {
      "name":"A Global Tag"
    },
    {
      "name":"Z Global Tag"
    }
  ]
}
```

---

### ⚠️ openapi-tags

At least one global tag should be specified at the root OpenAPI Document level.

**Valid Example**

```json
{
  "tags":[
    {
      "name":"Global Tag #1"
    },
    {
      "name":"Global Tag #2"
    }
  ]
}
```

---

### ⚠️ operation-description

Each operation should have a description.

**Valid Example**

```json
{
  "get": {
    "description": "Get a list of users."
  }
}
```

---

### ⚠️ operation-operationId

All operations should have an `operationId`.

**Valid Example**

```json
{
  "get": {
    "summary": "Get users",
    "operationId": "get-users"
  }
}
```

---

### ⚠️ operation-tags

At least one tag should be defined for each operation.

**Valid Example**

```json
{
  "get": {
    "tags": ["Users"]
  }
}
```

---

### ⚠️ contact-name

The `contact` object should have an organization name.

**Valid Example**

```json
{
  "contact": {
    "name": "ACME Corporation"
  }
}
```

---

### ⚠️ path-keys-no-trailing-slash

Path keys should not end in forward slashes. This is a best practice for working with web tooling, such as mock servers, code generators, application frameworks, and more.

**Invalid Example**

```json
{
  "/users/": {
  }
}
```

**Valid Example**

```json
{
  "/users": {
  }
}
```

---

### ⚠️ path-not-include-query

Paths should not include `query` string items. Instead, add them as parameters with `in: query`.

**Invalid Example**

```json
{
  "/users/{?id}": {
  }
}
```

**Valid Example**

```json
{
  "parameters": [
    {
      "schema": {
        "type": "string"
      },
      "name": "id",
      "in": "path",
      "required": true,
      "description": "User's ID"
    }
  ]
}
```

---

### ⚠️ tag-description

Tags defined at the global level should have a description.

**Valid Example**

```json
{
  "tags": [
    {
      "name": "Users",
      "description": "End-user information"
    }
  ]
}
```

---

### ⚠️ api-servers

A server should be defined at the root document level. This can be localhost, a development server, or a production server.

**Valid OpenAPI V3 Example**

```json
{
  "servers": [
    {
      "url": "https://staging.myprodserver.com/v1",
      "description": "Staging server"
    },
    {
      "url": "https://myprodserver.com/v1",
      "description": "Production server"
    }
  ]
}
```

**Valid OpenAPI V2 Example**

```json
{
  "host": "myprodserver.com",
  "basePath": "/v2",
  "schemes": [
    "https"
  ]
}
```

---

### ⚠️ server-trailing-slash

Server URLs should not end in forward slashes. This is a best practice for working with web tooling, such as mock servers, code generators, application frameworks, and more.

**Invalid Example**

```json
{
  "servers": [
    {
      "url": "https://api.openweathermap.org/data/2.5/"
    }
  ]
}
```

**Valid Example**

```json
{
  "servers": [
    {
      "url": "https://api.openweathermap.org/data/2.5"
    }
  ]
}
```

---

### ⚠️ operation-success-response

Operations should have at least one "2xx" or "3xx" response defined.

**Invalid Example**

```json
{
  "get": {
    "responses": {}
  }
}
```

**Valid Example**

```json
{
  "get": {
    "responses": {
      "200": {
        "description": "OK"
      }
    }
  }
}
```

---

### ⚠️ operation-parameters

Operation parameters should be unique and non-repeating:

* `name` and `in` must be unique

For OAS2:

* Operations should not have `in: body` and `in: formData` parameters.
* Operations should have only one `in: body` parameter.

**Invalid Example**

In this example, the query parameter `"name": "last name"` is repeated.

```json
{
  "parameters": [
    {
      "schema": {
        "type": "string"
      },
      "in": "query",
      "name": "last name",
      "description": "User's last name"
    },
    {
      "schema": {
        "type": "string"
      },
      "in": "query",
      "name": "last name",
      "description": "User's last name"
    }
  ]
}
```

**Valid Example**

In this example, query parameters are unique.

```json
{
  "parameters": [
    {
      "schema": {
        "type": "string"
      },
      "in": "query",
      "name": "first name",
      "description": "User's first name"
    },
    {
      "schema": {
        "type": "string"
      },
      "in": "query",
      "name": "last name",
      "description": "User's last name"
    }
  ]
}
```

---

### ⚠️ typed-enum

All `enum` values should respect the specified type.

**Invalid Example**

In this example, the `enum` type is `integer`, but the values are strings.

```json
{
  "schema": {
    "type": "integer",
    "enum": [
      "standard",
      "metric",
      "imperial"
    ]
  }
}
```

**Valid Example**

In this example, the `enum` type is `string` and the values are strings.

```json
{
  "schema": {
    "type": "string",
    "enum": [
      "standard",
      "metric",
      "imperial"
    ]
  }
}
```

---

### ⚠️ oas3-unused-component

A potentially shareable component is not being used. This may be expected, but you should review sharable components to avoid duplicate entry.

---

### ⚠️ operation-tag-defined

Tags defined at the operation level should also be defined at the global level.

**Operation-level Example**

```json
{
  "get": {
    ... ,
    "tags": [
      "Users"
    ]
  }
}
```

**Global-level Example**

```json
{
  "tags": [
    {
      "name": "Users",
      ... ,
    }
  ]
}
```

---

### ⚠️ oas2-operation-security-defined

Operation `security` values must match a scheme defined in the global `securityDefinitions` object. Empty `security` values for operations are ignored if authentication is not explicitly required or is optional.

**Valid Example**

For this global security scheme:

```json
{
  "securityDefinitions": {
    "API Key": {
      "name": "API Key",
      "type": "apiKey",
      "in": "query"
    }
  }
}
```

This is a valid operation security value:

```json
{
  "operationId": "get-users-userId",
  "security": [
    {
      "API Key": []
    }
  ]
}
```

**Invalid Example**

For the same global security scheme, this is an invalid operation security value:

```json
{
  "operationId": "get-users-userId",
  "security": [
    {
      "oath2": []
    }
  ]
}
```

---

### ⚠️ oas3-operation-security-defined

Operation `security` values must match a scheme defined in the global `components.security.Schemes` object.

**Valid Example**

For this global security scheme:

```json
{
  "components": {
    "security": [
      {
        "app-id": []
      }
    ]
  }
}
```

`app-id` is a valid operation `security` value:

```json
{
  "get": {
    "security": [
      {
        "app-id": []
      }
    ]
  }
}
```

**Invalid Example**

For the same global security scheme, `oath2` is an invalid operation `security` value:

```json
{
  "get": {
    "security": [
      {
        "oath2": []
      }
    ]
  }
}
```

---

### ⚠️ duplicated-entry-in-enum

All enum values should be unique.

**Invalid Example**

There are two `json` enum values.

```json
{
  "schema": {
    "type": "string",
    "enum": [
      "json",
      "json",
      "html"
    ]
  }
}
```

**Valid Example**

All enum values are unique.

```json
{
  "schema": {
    "type": "string",
    "enum": [
      "json",
      "xml",
      "html"
    ]
  }
}
```


---

### ⚠️ server-not-example

Server URLs must not direct to example.com. This helps ensure URLs 
are valid before you distribute your API document.

**Invalid Example**

```json
{
  "servers": [
    {
      ... ,
      "url": "https://example.com"
    }
  ]
}
```

**Valid Example**

```json
{
  "servers": [
    {
      ... ,
      "url": "https://api.openweathermap.org/data/2.5"
    }
  ]
}
```


---

### ⚠️ parameter-description

All `parameter` objects should have a description.

**Valid Example**

```json
{
  "parameters": [
    {
      "schema": {
        "type": "integer"
      },
      ... ,
      ... ,
      "description": "The number of days to include in the response."
    }
  ]
}
```


---

### ⚠️ oas2-anyOf

The `anyOf` keyword is not supported in OAS2. Only `allOf` is supported.

**Invalid Example**

```json
{
  "schema": {
    "anyOf": [
      {
        "properties": {
          "firstName": {
            "type": "string"
          },
          "lastName": {
            "type": "string"
          }
        }
      },
      {}
    ]
  }
}
```

**Valid Example**

```json
{
  "schema": {
    "type": "object",
    "properties": {
      "firstName": {
        "type": "string"
      },
      "lastName": {
        "type": "string"
      }
    }
  }
}
```


---

### ⚠️ oas2-oneOf

The `oneOf` keyword is not supported in OAS2. Only `allOf` is supported.

**Invalid Example**

```json
{
  "schema": {
    "oneOf": [
      {
        "properties": {
          "firstName": {
            "type": "string"
          },
          "lastName": {
            "type": "string"
          }
        }
      },
      {}
    ]
  }
}
```

**Valid Example**

```json
{
  "schema": {
    "type": "object",
    "properties": {
      "firstName": {
        "type": "string"
      },
      "lastName": {
        "type": "string"
      }
    }
  }
}
```


---

### ⚠️ oas3-examples-value-or-externalValue

The `examples` object should include a `value` or `externalValue` field, but cannot include both.

**Invalid Example**

This example includes both a `value` field and an `externalValue` field.

```json
{
  "examples": {
    "example-1": {
      "value": {
        "id": "string",
        "name": "string"
      },
      "externalValue": {
        "id": "string",
        "name": "string"
      }
    }
  }
}
```

**Valid Example**

This example includes only a `value` field.

```json
{
  "examples": {
    "example-1": {
      "value": {
        "id": "string",
        "name": "string"
      }
    }
  }
}
```


---

### ⚠️ path-parameters-on-path-only

Path parameters should be defined on the path level instead of the operation level.

**Invalid Example**

The `user_id` path parameter on line 8 should not be included with the `patch` operation.

```json
{      
  "patch": {
    "parameters": [
      {
        "schema": {
          "type": "integer"
        },
        "name": "user_id",
        "in": "path"
      }
    ]
  }
}
```

**Valid Example**

The `user-id` path parameter is correctly located at the path level.

```json
{
  "paths": {
    "/users/{userId}": {
      "parameters": [
        {
          "schema": {
            "type": "integer"
          },
          "name": "user_id",
          "in": "path"
        }
      ]
    }
  }
}
```

---

### ⚠️ paths-no-query-params

Paths should not have query parameters in them. They should be defined separately in the OpenAPI.

**Invalid Example**

```json
{
  "/users/{?id}": {
```

**Valid Example**

```json
{
  "parameters": [
    {
      "schema": {
        "type": "string"
      },
      "name": "id",
      "in": "path",
      "required": true,
      "description": "User's ID"
    }
  ]
}
```

---

### ℹ️ operation-singular-tag

Operation should not have more than a single tag.

---
