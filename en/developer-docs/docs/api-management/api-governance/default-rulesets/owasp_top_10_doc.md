# OWASP Top 10

A set of rules enforcing OWASP security guidelines to prevent common vulnerabilities and ensure secure coding practices.
### ❌ owasp:api1:2023-no-numeric-ids

Use random IDs that cannot be guessed. UUIDs are preferred but any other random string will do.

---

### ❌ owasp:api2:2023-no-http-basic

Basic authentication credentials transported over network are more susceptible to interception than other forms of authentication, and as they are not encrypted it means passwords and tokens are more easily leaked.

---

### ❌ owasp:api2:2023-no-api-keys-in-url

API Keys are passed in headers, cookies, or query parameters to access APIs. Those keys can be eavesdropped, especially when they are passed in the URL, as logging or history tools will keep track of them and potentially expose them.

---

### ❌ owasp:api2:2023-no-credentials-in-url

URL parameters MUST NOT contain credentials such as API key, password, or secret.

---

### ❌ owasp:api2:2023-auth-insecure-schemes

There are many [HTTP authorization schemes](https://www.iana.org/assignments/http-authschemes/) but some of them are now considered insecure, such as negotiating authentication using specifications like NTLM or OAuth v1.

---

### ❌ owasp:api2:2023-jwt-best-practices

JSON Web Tokens RFC7519 is a compact, URL-safe, means of representing claims to be transferred between two parties. JWT can be enclosed in encrypted or signed tokens like JWS and JWE.

The [JOSE IANA registry](https://www.iana.org/assignments/jose/jose.xhtml) provides algorithms information.

RFC8725 describes common pitfalls in the JWx specifications and in
their implementations, such as:
- the ability to ignore algorithms, eg. `{"alg": "none"}`;
- using insecure algorithms like `RSASSA-PKCS1-v1_5` eg. `{"alg": "RS256"}`.
An API using JWT should explicit in the `description`
that the implementation conforms to RFC8725.
```
components:
  securitySchemes:
    JWTBearer:
      type: http
      scheme: bearer
      bearerFormat: JWT
      description: |-
        A bearer token in the format of a JWS and conformato
        to the specifications included in RFC8725.
```

---

### ❌ owasp:api2:2023-short-lived-access-tokens

Using short-lived access tokens is a good practice, and when using OAuth 2 this is done by using refresh tokens. If a malicious actor is able to get hold of an access token then rotation means that token might not work by the time they try to use it, or it could at least reduce how long they are able to perform malicious requests.

---

### ❌ owasp:api4:2023-rate-limit

Define proper rate limiting to avoid attackers overloading the API. There are many ways to implement rate-limiting, but most of them involve using HTTP headers, and there are two popular ways to do that:

IETF Draft HTTP RateLimit Headers:. [https://datatracker.ietf.org/doc/draft-ietf-httpapi-ratelimit-headers/](https://datatracker.ietf.org/doc/draft-ietf-httpapi-ratelimit-headers)/

Customer headers like X-Rate-Limit-Limit (Twitter: [https://developer.twitter.com/en/docs/twitter-api/rate-limits](https://developer.twitter.com/en/docs/twitter-api/rate-limits) or X-RateLimit-Limit (GitHub: [https://docs.github.com/en/rest/overview/resources-in-the-rest-api](https://docs.github.com/en/rest/overview/resources-in-the-rest-api))

---

### ❌ owasp:api4:2023-rate-limit-retry-after

Define proper rate limiting to avoid attackers overloading the API. Part of that involves setting a Retry-After header so well meaning consumers are not polling and potentially exacerbating problems.

---

### ❌ owasp:api4:2023-array-limit

Array size should be limited to mitigate resource exhaustion attacks. This can be done using `maxItems`. You should ensure that the subschema in `items` is constrained too.

---

### ❌ owasp:api4:2023-string-limit

String size should be limited to mitigate resource exhaustion attacks. This can be done using `maxLength`, `enum` or `const`.

---

### ❌ owasp:api4:2023-integer-limit

Integers should be limited to mitigate resource exhaustion attacks. This can be done using `minimum` and `maximum`, which can with e.g.: avoiding negative numbers when positive are expected, or reducing unreasonable iterations like doing something 1000 times when 10 is expected.

---

### ❌ owasp:api4:2023-integer-limit-legacy

Integers should be limited to mitigate resource exhaustion attacks. This can be done using `minimum` and `maximum`, which can with e.g.: avoiding negative numbers when positive are expected, or reducing unreasonable iterations like doing something 1000 times when 10 is expected.

---

### ❌ owasp:api4:2023-integer-format

Integers should be limited to mitigate resource exhaustion attacks. Specifying whether int32 or int64 is expected via `format`.

---

### ❌ owasp:api8:2023-define-cors-origin

Setting up CORS headers will control which websites can make browser-based HTTP requests to your API, using either the wildcard "*" to allow any origin, or "null" to disable any origin. Alternatively you can use "Access-Control-Allow-Origin: https://example.com" to indicate that only requests originating from the specified domain (https://example.com) are allowed to access its resources.

More about CORS here: [https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS).

---

### ❌ owasp:api8:2023-no-scheme-http

Server interactions must use the http protocol as it's inherently insecure and can lead to PII and other sensitive information being leaked through traffic sniffing or man-in-the-middle attacks. Use the https or wss schemes instead.

Learn more about the importance of TLS (over SSL) here: [https://cheatsheetseries.owasp.org/cheatsheets/Transport_Layer_Security_Cheat_Sheet.html](https://cheatsheetseries.owasp.org/cheatsheets/Transport_Layer_Security_Cheat_Sheet.html)

---

### ❌ owasp:api8:2023-no-server-http

Server interactions must not use the http:// as it's inherently insecure and can lead to PII and other sensitive information being leaked through traffic sniffing or man-in-the-middle attacks. Use https:// or wss:// protocols instead.

Learn more about the importance of TLS (over SSL) here: [https://cheatsheetseries.owasp.org/cheatsheets/Transport_Layer_Security_Cheat_Sheet.html](https://cheatsheetseries.owasp.org/cheatsheets/Transport_Layer_Security_Cheat_Sheet.html)

---

### ❌ owasp:api9:2023-inventory-access

Servers are required to use vendor extension x-internal set to true or false to explicitly explain the audience for the API, which will be picked up by most documentation tools.

---

### ❌ owasp:api9:2023-inventory-environment

Make it clear which servers are expected to run as which environment to avoid unexpected problems, exposing test data to the public, or letting bad actors bypass security measures to get to production-like environments.

---

### ⚠️ owasp:api3:2023-no-additionalProperties

By default JSON Schema allows additional properties, which can potentially lead to mass assignment issues, where unspecified fields are passed to the API without validation. Disable them with `additionalProperties: false` or add `maxProperties`.

---

### ⚠️ owasp:api3:2023-constrained-additionalProperties

By default JSON Schema allows additional properties, which can potentially lead to mass assignment issues, where unspecified fields are passed to the API without validation. Disable them with `additionalProperties: false` or add `maxProperties`

---

### ⚠️ owasp:api3:2023-no-unevaluatedProperties

By default JSON Schema allows unevaluated properties, which can potentially lead to mass assignment issues, where unspecified fields are passed to the API without validation. Disable them with `unevaluatedProperties: false` or add `maxProperties`.

---

### ⚠️ owasp:api3:2023-constrained-unevaluatedProperties

By default JSON Schema allows unevaluated properties, which can potentially lead to mass assignment issues, where unspecified fields are passed to the API without validation. Disable them with `unevaluatedProperties: false` or add `maxProperties`

---

### ⚠️ owasp:api4:2023-rate-limit-responses-429

OWASP API Security recommends defining schemas for all responses, even errors. A HTTP 429 response signals the API client is making too many requests, and will supply information about when to retry so that the client can back off calmly without everything breaking. Defining this response is important not just for documentation, but to empower contract testing to make sure the proper JSON structure is being returned instead of leaking implementation details in backtraces. It also ensures your API/framework/gateway actually has rate limiting set up.

---

### ⚠️ owasp:api4:2023-string-restricted

To avoid unexpected values being sent or leaked, strings should have a `format`, RegEx `pattern`, `enum`, or `const`.

---

### ⚠️ owasp:api8:2023-define-error-validation

Carefully define schemas for all the API responses, including either 400, 422 or 4XX responses which describe errors caused by invalid requests.

---

### ⚠️ owasp:api8:2023-define-error-responses-401

OWASP API Security recommends defining schemas for all responses, even errors. The 401 describes what happens when a request is unauthorized, so its important to define this not just for documentation, but to empower contract testing to make sure the proper JSON structure is being returned instead of leaking implementation details in backtraces.

---

### ⚠️ owasp:api8:2023-define-error-responses-500

OWASP API Security recommends defining schemas for all responses, even errors. The 500 describes what happens when a request fails with an internal server error, so its important to define this not just for documentation, but to empower contract testing to make sure the proper JSON structure is being returned instead of leaking implementation details in backtraces.

---

### ℹ️ owasp:api7:2023-concerning-url-parameter

Using external resources based on user input for webhooks, file fetching from URLs, custom SSO, URL previews, or redirects can lead to a wide variety of security issues.

Learn more about Server Side Request Forgery [https://owasp.org/API-Security/editions/2023/en/0xa7-server-side-request-forgery/](https://owasp.org/API-Security/editions/2023/en/0xa7-server-side-request-forgery/).

---
