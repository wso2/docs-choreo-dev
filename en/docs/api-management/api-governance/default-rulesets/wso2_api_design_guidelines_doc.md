# WSO2 API Design Guidelines

A guide detailing best practices for creating robust, scalable, and secure APIs, ensuring alignment with industry standards for optimal design.
### ❌ path-casing

Paths must be `kebab-case`, with hyphens separating words.

**Invalid Example**

`userInfo` must be separated with a hyphen.

```json
{
    "/userInfo": {
        "post: }
       ....
}
``` 

**Valid Example**

```json
{
    "/user-info": {
       "post: }
       ....
}
```

---

### ❌ paths-no-file-extensions

Paths must not include `json` or `xml` file extensions.

**Invalid Example**

The path contains a `.json` extension. 

```json
{
    "/user.json": {
       "post: }
       ....
}
``` 

**Valid Example**

```json
{
    "/user": {
       "post: }
       ....
}
```

---

### ❌ paths-no-http-verbs

Verbs such as `get`, `delete`, and `put` must not be included in paths because this information is conveyed by the HTTP method.

**Invalid Example**

The path contains the verb `get`. 

```json
{
    "/getUsers": {
       "post: }
       ....
}
``` 

**Valid Example**

```json
{
    "/user": {
       "post: }
       ....
}
```

---

### ❌ path-parameters-snake-case

Path parameters must be `snake_case`, with each word separated by an underscore character and the first letter of each word lowercase. Also, the path parameter must not contain digits.

**Invalid Example**

The `name` property on line 9 (`userId`) must be separated by an underscore character and the `I` must be lowercase.

```json
{
    "paths": {
      "/users/{userId}": {
        "parameters": [
          {
            "schema": {
              "type": "integer"
            },
            "name": "userId",
            "in": "path"
          }
        ]
      }
    }
  }
```

**Valid Example**

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

### ❌ query-parameters-snake-case

Query parameters must be `snake_case`, with each word separated by an underscore character and the first letter of each word lowercase. Also, the query parameter must not contain digits.

**Invalid Example**

The `name` property on line 8 (`user-Id`) must be separated by an underscore character and the `I` must be lowercase.

```json
{
   "parameters": [
     {
       "schema": {
         "type": "string"
       },
       "in": "query",
       "name": "user-Id"
     }
   ]
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
        "in": "query",
        "name": "user_id"
      }
    ]
 }
```

---

### ⚠️ resource-names-plural

Resource names should generally be plural. 

**Invalid Example**

```json
{
    "paths": {
      "/user": 
    }
  }
```

**Valid Example**

```json
{
    "paths": {
      "/users": 
    }
}
```

---

### ⚠️ paths-avoid-special-characters

Paths should not contain special characters, such as `$` `&` `+` `,` `;` `=` `?` and `@%`.

**Invalid Example**

The path contains an ampersand. 

```json
{
    "/user&info": {
       "post: }
       ....
}
``` 

**Valid Example**

```json
{
    "/user": {
       "post: }
       ....
}
```

---

### ℹ️ server-has-api

Server must have /api

---
