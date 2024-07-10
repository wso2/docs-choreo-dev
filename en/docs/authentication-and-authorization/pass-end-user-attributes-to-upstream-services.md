# Pass End-User Attributes to Upstream Services

There are scenarios where a backend service needs to apply specific logic or make decisions depending on the user consuming an API. In such scenarios, you must pass end-user attributes to the backend during an API call.

Choreo provides a method to send user information to a backend service through a JSON Web Token (JWT) in an HTTP header of an API request.

## How it works

The backend JWT contains claims transferred between the parties, such as the user and the backend. A claim can be metadata of the request or data about the user. A set of claims is called a dialect, for example, `http://wso2.org/claims`.

For each API request, a digitally signed JWT is carried to the backend service in the following format to ensure that the authenticity of the claims list is verified:

`{token header}.{claims list}.{signature}`

When a request goes through Choreo, the backend JWT is appended as the `X-JWT-Assertion` header in the outgoing message. The backend service fetches the JWT and retrieves the required information about the user, application, or token.

## Claims

Claims are fragments of information included in the JWT. 

The following is a sample claim set added to the end-user token for an access token generated via the authorization code:

!!! tip
    This access token is generated via Asgardeo using the authorization code grant type. Here, the Asgardeo application is configured to include the email claim in the token. 

``` java
{
  "sub": "11f53c32-f8ac-4810-bb79-615b2184baf5",
  "http://wso2.org/claims/apiname": "JWT Test - Endpoint 9090 803",
  "http://wso2.org/claims/applicationtier": "Unlimited",
  "http://wso2.org/claims/version": "1.0.0",
  "http://wso2.org/claims/keytype": "PRODUCTION",
  "iss": "wso2.org/products/am",
  "http://wso2.org/claims/applicationname": "jwtTest2",
  "http://wso2.org/claims/enduserTenantId": "0",
  "http://wso2.org/claims/applicationUUId": "45101ccb-865f-4f48-b7ac-18e43b07edd3",
  "client_id": "IMJB5ZiR1dHQYBdiMIRAGis1WToa",
  "http://wso2.org/claims/subscriber": "5f4a7105-a889-4f92-9612-eef5bafe4eec",
  "azp": "IMJB5ZiR1dHQYBdiMIRAGis1WToa",
  "org_id": "b554e001-761c-4d3a-a7a6-a61d73d34221",
  "http://wso2.org/claims/tier": "Unlimited",
  "scope": "email openid profile",
  "exp": 1690537362,
  "http://wso2.org/claims/applicationid": "45101ccb-865f-4f48-b7ac-18e43b07edd3",
  "http://wso2.org/claims/usertype": "Application_User",
  "org_name": "test",
  "iat": 1690533762,
  "email": "testmail@gmail.com",
  "jti": "69558555-d386-4a81-9ca0-0a23f809cd3c",
  "http://wso2.org/claims/apicontext": "/b554e001-761c-4d3a-a7a6-a61d73d34221/swog/jwt-test/endpoint-9090-803/1.0.0"
}
```

The following table describes the information contained in the sample JWT claims set given above:

|             **Claim Name**              |          **Description**           |  **Mandatory/Optional**  |
|-----------------------------------------|------------------------------------|--------------------------|
| `iat`                                   |  The time the token was issued.    |   Mandatory              |
| `jti`                                   |  The unique token identifier.      |   Mandatory              |
| `exp`                                   |  The token expiry time.            |   Mandatory              |
| `iss`                                   |  The issuer of the token.          |   Mandatory              |
| `http://wso2.org/claims/apiname`        |  The name of the API in Choreo.    |   Optional               |
| `http://wso2.org/claims/version`        |  The API version.                  |   Optional               |
| `http://wso2.org/claims/keytype`        |  The environment in Choreo that the API is in (`Development` or `production`).|   Optional |
| `http://wso2.org/claims/apicontext`     |  The API context in Choreo.        |   Optional               |
| `http://wso2.org/claims/subscriber`     |  The subscriber to the API, usually the app developer. |   Optional |
| `http://wso2.org/claims/applicationname`|  The application through which the API invocation is done. |   Optional |
| `http://wso2.org/claims/applicationid`  |  The ID of the application through which the API invocation is done. |   Optional |
| `http://wso2.org/claims/applicationUUId`|  The UUID of the application.      |   Optional               | 
| `client_id`                             |  The client identifier. This is copied from the original token.             |   Optional |
| `azp`                                   |  The authorized party (the party to which the ID token was issued). This is copied from the original token. |   Optional |
| `org_id`                                |  The organization ID. This is copied from the original token. |   Optional |
| `org_name`                              |  The organization name. This is copied from the original token. |   Optional |
| `http://wso2.org/claims/tier`           |  The tier/price band for the subscription. |   Optional       |
| `scope`                                 |  The scope of the token. This is copied from the original token. |   Optional |              
| `http://wso2.org/claims/usertype`       |  The type of application user whose action invoked the API. |   Optional |
| `email`                                 |  The email address of the user. This is copied from the original token. |   Optional |


!!! note

    The claims that get added to the end-user token can vary depending on the grant type used when generating the access token. For example, if you use the client-credentials grant type to generate the access token, the generated backend JWT would contain the following information:

    ``` java
    { 
      "http://wso2.org/claims/apiname": "DefaultAPI", 
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

To verify the authenticity of claims in a JWT, the claims must be validated using the public key corresponding to the private key used to sign the JWT.

JSON web key set (JWKS) is a set of keys to validate a JWT. It contains a collection of JSON web keys, which are public keys used to verify the signature of a JWT.

Typically, when a third party (such as an identity provider)issues a JWT and the recipient needs to verify its signature, they can use a JWKS. 
JWKS allows the issuer to rotate keys dynamically rather than hard-coding the public key in the application. The recipient can obtain the public key by accessing the JWKS endpoint.

## JWKS support in Choreo to validate the JWT

Choreo provides an endpoint to specify the public keys for backend JWT validation. Here are the endpoint URLs for the US East and EU regions:

- [https://gateway.e1-us-east-azure.choreoapis.dev/.wellknown/jwks](https://gateway.e1-us-east-azure.choreoapis.dev/.wellknown/jwks)
- [https://gateway.e1-eu-north-azure.choreoapis.dev/.wellknown/jwks](https://gateway.e1-eu-north-azure.choreoapis.dev/.wellknown/jwks)

!!! note
    For private data planes (PDPs), use the following JWKS endpoint URL template:
    
    `https://<PDP_GATEWAY_DOMAIN>/.wellknown/jwks`

    Be sure to replace `<PDP-GATEWAY-DOMAIN>` with the default domain configured to access the PDP APIs.

The endpoint provides one or more signing keys to validate the JWT.
The JSON web keys have a kid identifier that can be matched with the same property on the JWT to decide which key to use when validating.

The following is a sample JWKS response:

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

## Enable passing end-user attributes to the backend

To enable passing end-user attributes to the backend through API calls via Choreo, follow the steps given below:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the **Components Listing** pane, click on the component for which you want to pass end-user attributes to the backend.
3. In the left navigation menu, click **Deploy**.
4. Go to the **Set Up** card and click **Endpoint Configurations**. This opens the **Endpoint Configurations** pane.

    !!! note
         If the component is an API Proxy, go to the **Build Area** card and click **Security Settings**. This opens the **Security Settings** pane.
   
5. Select the **Pass Security Context To Backend** checkbox.
6. Optionally, specify appropriate audience values in the **End User Token Audiences** field. Specifying values restricts the JWT to the respective audiences, enabling the backend service to validate and confirm the intended recipients, including itself.

    !!! note
        The backend JWT does not include the audience field (aud) by default.

7. Click **Apply**.
8. To redeploy the component with the applied setting, go to the **Set Up** card and click **Deploy**.
