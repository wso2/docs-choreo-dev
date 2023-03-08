# Pass End-User Attributes to the Backend
There are scenarios where a backend service needs to apply specific logic or make decisions depending on the user consuming an API. In such scenarios, you must pass end-user attributes to the backend during an API call.

Choreo provides a method to send user information to a backend service through a JSON Web Token (JWT) in an HTTP header of an API request.

## How it works

The backend JWT contains claims transferred between the parties, such as the end-user and the backend. A claim can be metadata of the request or data about the user. A set of claims is called a dialect, for example, `http://wso2.org/claims`.

For each API request, a digitally signed JWT is carried to the backend service in the following format to ensure that the authenticity of the claims is verified:
`{token header}.{claims list}.{signature}`

When the request goes through Choreo, the backend JWT is appended as a transport header to the outgoing message. The backend service fetches the JWT and retrieves the required information about the user, application, or token.

## Claims

Claims are pieces of information included in the JWT. Here's a sample claims set:

``` java
{
  "http://wso2.org/claims/apiname": "DefaultAPId",
  "http://wso2.org/claims/version": "1.0.0",
  "http://wso2.org/claims/keytype": "PRODUCTION",
  "iss": "wso2.org/products/am",
  "http://wso2.org/claims/enduserTenantId": "0",
  "exp": 1673245727,
  "http://wso2.org/claims/usertype": "Application_User",
  "iat": 1673242127,
  "jti": "6e3f4392-8bd9-4900-9d08-eaab7429c510",
  "http://wso2.org/claims/apicontext": "/9e71ab5e-6df5-4727-92d2-80ecf1a6218d/qbky/default/1.0.0"
}
```

The following table describes the information contained in the JWT claims set:

|             **Claim Name**              |          **Description**           |  **Mandatory/Optional**  |
|-----------------------------------------|------------------------------------|--------------------------|
| `iat`                                   |  The time the token was issued.    |   Mandatory              |
| `jti`                                   |  Unique token identifier.          |   Mandatory              |
| `exp`                                   |  The token expiry time.            |   Mandatory              |
| `iss`                                   |  The issuer of the token.          |   Mandatory              |
| `http://wso2.org/claims/apiname`        |  The name of the API in Choreo.    |   Optional               |
| `http://wso2.org/claims/version`        |  The API version.                  |   Optional               |
| `http://wso2.org/claims/keytype`        |  The environment in Choreo that the API is in (`Development` or `production`).|   Optional     |
| `http://wso2.org/claims/apicontext`     |  API context in Choreo.            |   Optional               |


To verify the authenticity of claims in a JWT, the claims must be validated using the public key corresponding to the private key used to sign the JWT.

JSON web key set (JWKS) is a set of keys to validate a JWT. It contains a collection of JSON web keys, which are public keys used to verify the signature of a JWT.

Typically, when a third party (such as an identity provider)issues a JWT and the recipient needs to verify its signature, they can use a JWKS. 
JWKS allows the issuer to rotate keys dynamically rather than hardcoding the public key in the application. The recipient can obtain the public key by accessing the JWKS endpoint.

## JWKS support in Choreo to validate the JWT

Choreo provides an endpoint to specify the public keys for backend JWT validation. Here are the endpoint URLs for the US East and EU regions:

- [https://gateway.e1-us-east-azure.choreoapis.dev/.wellknown/jwks](https://gateway.e1-us-east-azure.choreoapis.dev/.wellknown/jwks)
- [https://gateway.e1-eu-north-azure.choreoapis.dev/.wellknown/jwks](https://gateway.e1-eu-north-azure.choreoapis.dev/.wellknown/jwks)

The endpoint provides one or more signing keys to validate the JWT.
The JSON web keys have a kid identifier that can be matched with the same property on the JWT to decide which key to use when validating.

Here is a sample JWKS response:

``` java
{
   "keys": [
       {
           "kty": "RSA",
           "e": "AQAB",
           "use": "sig",
           "kid": "ZjcwNmI2ZDJmNWQ0M2I5YzZiYzJmZmM4YjMwMDFlOTA4MGE3ZWZjZTMzNjU3YWU1MzViYjZkOTkzZjYzOGYyNg",
           "alg": "RS256",
           "n": "8vjeHzRhvpfMystncPnLBWy_t5F3eCxbcLbdugWnzfnIgaV6TWnqPBUagJBKpzRZs4A9Qja_ZrSVJjYsbARzCS_qiWp0Cdwkqn6ZCXpmbpfjYnKORq8N8M-zWaSZYbNvWJ5oSO4kH-LKWzODaFebwTJBpsR1vChHH95doxFuUjiZaisVaQgUJ6drRdlDtImp9r9EAX36YROuYFPoEJcvsH4_uuAR6ClJ12RE3M-YN4NTi1waVNvGbz43oNrpPy7SXgpizingxSGMqI6WU2ysRmk_f9ALgiPIpFDpufiCTYaIcRT-YcUyp9nMDlTRskMuD-dQ1sdJOa11P_yMs-glfQ"
       }
   ]
}
```

The following table describes the information contained in the JWKS response:

| **Property** |                                 **Description**                                    |  
|--------------|------------------------------------------------------------------------------------|
| `kty`        |  The cryptographic family to which the key belongs. <br> Choreo only supports RSA. |
| `e`          |  The exponent value of the public key.                                             |
| `use`        |  The purpose of the key. For example, whether it is for signing or encryption.     |
| `kid`        |  The identification parameter to match a specific key.                             |
| `alg`        |  The algorithm to use with the key.                                                |
| `n`          |  The modulus value of the public key.                                              |

