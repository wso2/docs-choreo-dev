# Manage Applications

An application in Choreo is a logical representation of a physical application such as a mobile app, web app, device, etc. To consume an API in Choreo, you need to create an application that maps to your physical application and then subscribes to the required API over a usage policy plan that gives you a usage quota. A single application can have multiple subscriptions to APIs. Using the consumer key and consumer secret, you can generate an access token that you can use to invoke all the APIs subscribed to the same application.

This section will walk you through the steps to create an application in Choreo.

Let's get started!

## Step 1: Create an application

Follow the steps below to create an application on Choreo:

1. Sign in to Choreo Developer Portal at [https://devportal.choreo.dev](https://devportal.choreo.dev). Alternatively, click the Developer Portal link on the Choreo Console header. 

    ![Developer Portal](../assets/img/developer-portal/manage-applications/developer-portal.png){.cInlineImage-half}
    
2. Click **Applications** and then, click **+Create**.
3. Enter the application name and select the usage policy. Optionally, add the application description. 
4. Click **Create**.

You are redirected to the application overview page. You can view the throttling tier, the token type, workflow status, and the application owner of the API. 

## Step 2: Generate keys

Choreo provides an OAuth 2.0 bearer token-based authentication for API access. An API access token/key is a string that is passed as an HTTP header of an API request to authenticate the API access.

Once you create an application in Choreo, you can then generate the credentials for it. When you generate the credentials for the first time, Choreo gives you a consumer key and the consumer secret for the application. The consumer key becomes the unique identifier of the application, similar to a user's user name, and is used to authenticate the application or user. You can use this consumer key and consumer secret to generate an API access token by invoking the token endpoint. You can also revoke the access token by invoking the revoke endpoint. For testing purposes, you can generate a test token via the UI. However, we strongly recommend that you do not use the test token in your production environment.

This section will walk you through the steps to generate an API access token in Choreo. 

Let's get started!

### Access token for production 

Depending on your use case, you can generate an access token using one of the two grant types available in Choreo: Client Credentials and Token Exchange. 

!!! note
    We recommend you use the **Token Exchange** grant type to obtain an access token **for production use** and use the **Client Credentials** grant type **for testing purposes**. 

Token Exchange grant type requires you to pass a subject_token as a parameter in the token invocation call. You can obtain a subject token (id token) by using the `open_id` scope. Follow the steps below to generate an access token from the IdP along with the subject token:

1. Register the IdP in Choreo by following the steps in the section [add an external IdP](../administration/connect-to-an-external-identity-provider.md#step-1-add-an-external-idp).

2. Generate a token from the registered IdP with the `openId` scope and retrieve the `id_token` from the token response.  
                
    ``` json tab="Format"
        {"access_token":"<access__token>","refresh_token":"<refresh_token>","scope":"openid","id_token":"<id_token>","token_type":"Bearer","expires_in":3600}
    ```

3. Sign in to Choreo Developer Portal at [https://devportal.choreo.dev](https://devportal.choreo.dev). Alternatively, click the Developer Portal link on the Choreo Console header. 
4. Click **Applications**.
5. In the left pane, click **OAuth 2.0 Tokens**.
6. Expand the **Advanced Configurations** section and review the options. 

    ![Advanced Configurations](../assets/img/developer-portal/manage-applications/advanced-configurations.png){.cInlineImage-half}

    - **Grant Types:** The grant types used to generate the access token. Keep Token Exchange and Refresh Token grant type selected.
    - **Public Client:** Identify the application as a public client to allow authentication without a client secret. You can use this for applications running on a browser or mobile device. 
    - **Application acces   s token expiry time:** The access token expiry time (seconds).
    - **Refresh token expiry time:** The refresh token expiry time (seconds).
    - **ID token expiry time:** ID token expiry time (seconds).

7. Click **Generate Credentials** to generate the credentials for the application for the first time. Copy the **Consumer Key** value.
8. Generate an access token by invoking the token endpoint using the Token Exchange grant type as follows: 
replace the `<application_consumer_key>` with the **Consumer Key** you copied at step 7 and replace `<idp_id_token>` with the **id_token** value you obtained at step 2: 

    ``` java tab="Format"
    curl -k -X POST https://sts.choreo.dev/oauth2/token -d "client_id=<application_consumer_key>" -d "subject_token_type=urn:ietf:params:oauth:token-type:jwt" -d "grant_type=urn:ietf:params:oauth:grant-type:token-exchange" -d "subject_token=<idp_id_token>"
    ```

    ``` java tab="Example"
    curl -k -X POST https://sts.choreo.dev/oauth2/token -d "client_id=ciwnWuwZfbcdzBUcnkhKvi_mcBUa" -d "subject_token_type=urn:ietf:params:oauth:token-type:jwt" -d "grant_type=urn:ietf:params:oauth:grant-type:token-exchange" -d "subject_token=eyJhbGciOiJFUzI1NiIsImtpZCI6IjE2In0.eyJhdWQiOiJodHRwczovL2FzLmV4YW1wbGUuY29tIiwiaXNzIjoiaHR0cHM6Ly9vcmlnaW5hbC1pc3N1ZXIuZXhhbXBsZS5uZXQiLCJleHAiOjE0NDE5MTA2MDAsIm5iZiI6MTQ0MTkwOTAwMCwic3ViIjoiYmNAZXhhbXBsZS5uZXQiLCJzY3AiOlsib3JkZXJzIiwicHJvZmlsZSIsImhpc3RvcnkiXX0.JDe7fZ267iIRXwbFmOugyCt5dmGoy6EeuzNQ3MqDek9cCUlyPhQC6cz9laKjK1bnjMQbLJqWix6ZdBI0isjsTA"
    ```
    
    ```JSON tab="Response"
        {
        "access_token": "eyJ4NXQiOiJNV1E1TldVd1lXWmlNbU16WlRJek16ZG1NekJoTVdNNFlqUXlNalZoTldNNE5qaGtNR1JtTnpGbE1HSTNaRGxtWW1Rek5tRXlNemhoWWpCaU5tWmhZdyIsImtpZCI6Ik1XUTVOV1V3WVdaaU1tTXpaVEl6TXpkbU16QmhNV000WWpReU1qVmhOV000Tmpoa01HUm1OekZsTUdJM1pEbG1ZbVF6Tm1FeU16aGhZakJpTm1aaFl3X1JTMjU2IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJiNWViODFkMC01ZTMxLTQwZDgtYWY0MS03OWMwMjJlNTRhNTciLCJhdXQiOiJBUFBMSUNBVElPTl9VU0VSIiwiYXVkIjoibzRUWGxLQnowYWVJNEJ0d2NjY0V4bnVyNENVYSIsIm5iZiI6MTY2Mzg0MTM5MiwiYXpwIjoibzRUWGxLQnowYWVJNEJ0d2NjY0V4bnVyNENVYSIsInNjb3BlIjoiZGVmYXVsdCIsIm9yZ2FuaXphdGlvbiI6eyJ1dWlkIjoiZWMwZDE5OTgtZTRkNS00NjRlLWFjODgtOTg4ODM5NTYwZDVkIn0sImlzcyI6Imh0dHBzOi8vc3RzLmNob3Jlby5kZXY6NDQzL29hdXRoMi90b2tlbiIsImV4cCI6MTY2Mzg0NDk5MiwiaWRwX2NsYWltcyI6eyJhdXQiOiJBUFBMSUNBVElPTl9VU0VSIiwiYXV0aGVudGljYXRlZF9pZHAiOiJHb29nbGUiLCJuYW1lIjoid3NvMmUiLCJnaXZlbl9uYW1lIjoid3NvMiIsImZhbWlseV9uYW1lIjoid3NvMiIsImVtYWlsIjoid3NvMiJ9LCJpYXQiOjE2NjM4NDEzOTIsImp0aSI6ImM0MmRhNmM5LTYxYjEtNDUwNi1hMmYyLWY4OGE5MjQzNjk1MiJ9.M8FqUMDB1uvRTbz-AAxbl9af_72BLrSnTomGwasCzK4qRwVtV7MENLXFVzTZMW0ayBjPDQ8QeTmaky2LwAFKf0AYdGMtBYL0VLVWtBND7RCip_gxpa1k-Z5V4Cl5qcvKuLpySyE7E2QQqR9lPIHE-bm70nyWQheNMCEnPAKihyP772AEn3xRobHFGB_sidVuRAQm815GFE56NovKeBUM1YLmdnUgIrL1B9ho2q0g8aAI7959ORy9xQXLqxcYN-rd8uUnY5jWXPzxJHDDaozgHUK02IDKx0a-9Bf1Gy2Hj0DSQFRWZbPpfq7oMzCeRprqCYYkPkTjOz-LpyE9Ri-xTZNRbm7hua5PrRJjDD_EbsT8zYolRvIlwl7GcQOQuOtyxS4l_hQmb3DDmyFouv_P4Aknse4FvD8mSeXmvaTelyS5hBp0KNAoxkQBrRVgHVv_Jjyt2s9FwDhmLl4uS46x5Ca58X_wU6kEBP2hmfa98JHcfklog3gsxTm0T0JZlGs23zNqaf-ApC7NU9hWVZ6fwIX3CUzZNZmhF4caNrLJEqUnuxB5fHrHiwMNnoXf3WAILHj5gJPZo9OG18yymIdbRjHYrpD1ZQihIkPBTeMXwfRWIMDfBD3ezDKajktKqM8w1E-sFuGLHWyCqREN4XWA_jcZ6766BtTareAfKH171Ok",
        "refresh_token": "a23h07f9-6ce5-3b7a-9d12-00e7f82f4f46",
        "issued_token_type": "urn:ietf:params:oauth:token-type:jwt",
        "scope": "default",
        "token_type": "Bearer",
        "expires_in": 3600
        }
    ```
    !!! tip
        Save the refresh token and access token since you won't be able to view it again.  

### Access token for testing

You can generate an access token for testing using the Choreo UI by following the steps below:

1. Sign in to Choreo Developer Portal at [https://devportal.choreo.dev](https://devportal.choreo.dev). Alternatively, click the Developer Portal link on the Choreo Console header. 
2. Click **Applications**.
3. In the left pane, click **OAuth 2.0 Tokens**.
4. Expand the **Advanced Configurations** section and review the options. 

    ![Advanced Configurations](../assets/img/developer-portal/manage-applications/advanced-configurations.png){.cInlineImage-half}

    - **Grant Types:** The grant types used to generate the access token.
    - **Public Client:** Identify the application as a public client to allow authentication without a client secret. You can use this for applications running on a browser or mobile device. 
    - **Application access token expiry time:** The access token expiry time (seconds).
    - **Refresh token expiry time:** The refresh token expiry time (seconds).
    - **ID token expiry time:** ID token expiry time (seconds).

5. Click **Generate Credentials** to generate the credentials for the application for the first time. Once you generate the keys, you can find the application's consumer key and consumer secret. 

    ![Credentials](../assets/img/developer-portal/manage-applications/credentials.png){.cInlineImage-half}

    !!! info
        You can use the UI to generate a test token using the consumer key and consumer secret for the application, only for test purposes. We strongly recommend **NOT** using this token in your production environment.

6. Click **:// CURL** to copy the cURL command template for generating the access token. You can generate an API access token by adding the consumer key and consumer secret to the URL and invoking the token endpoint.
  
    ![cURL command](../assets/img/developer-portal/manage-applications/curl-command.png){.cInlineImage-half}

### Renew an access token using the refresh token grant type

!!! note
    - To use this grant type, you will need the refresh token you received when you generated your current access token.
    - Your application must have the **Refresh Token** grant type enabled.
    - You can use a refresh token only once.

You can use a refresh token to obtain a new access token when your current access token expires or when you need to renew the access token due to security reasons. You need to use the [Refresh Token grant type](#refresh-token-grant) for this purpose. 

To generate a new access token and a new refresh token, you can invoke the Token API by replacing the `<refresh-token>` with the refresh token you received with the access token and replacing `<base64(consumerKey:consumerSecret)>` with the application's **Consumer Key** value and **Consumer Secret** value as follows:

``` java tab="Format"
curl -k -d "grant_type=refresh_token&refresh_token=<refresh-token>" -H "Authorization: Basic <base64(consumerKey:consumerSecret)>" -H "Content-Type: application/x-www-form-urlencoded" https://sts.choreo.dev/oauth2/token
```

``` java tab="Example"
curl -k -d "grant_type=refresh_token&refresh_token=3154090c-37f1-3268-90f9-8bd84daf135c" -H "Authorization: Basic UXk3RUZfVEtMbEVLWTlVRFpiWHVscVA4ZVVBYTpKSWN3VTlIX1hGUFdTcW1RQmllZ3lJUzRKazhh" -H "Content-Type: application/x-www-form-urlencoded" https://sts.choreo.dev/oauth2/token
```

``` java tab="Response"
{
    "scope":"default",
    "token_type":"Bearer",
    "expires_in":3600,
    "refresh_token":"7ed6bae2b1d76c041787e8c8e2d6cbf8",
    "access_token":"b7882d23f1f8207f4bc6cf4a20633ab1"
}
```

The above API response grants you a new access token and a refresh token.

## Grant types

Choreo authentication is based on OAuth2.0. In OAuth 2.0, grant types are methods that allow client applications to obtain an access token depending on the type of the resource owner, the type of the application, and the trust relationship between the authorization server and the resource owner. 

### Refresh token grant

A refresh token is a token that you can use to get a new access token when your current access token is expired or when you need a new access token. You can use the refresh token grant type for this purpose. Issuing a refresh token is optional for the authorization server. If the authorization server issues a refresh token, it includes it in the response with the access token. You can use this refresh token and send it to the authorization server to obtain a new access token. Choreo's default authorization server, Asgardeo, issues refresh tokens for all other grant types other than the **client credentials** grant type, as recommended by the OAuth 2.0 specification.

!!! note
    
    - Keep your refresh token private, similar to the access token. 
    - The process to get a new access token using the Refresh Token grant type requires no user interaction.

### Token exchange grant 

Choreo supports the token exchange grant type. This grant type allows the client to obtain a Choreo access token by providing a JWT issued by an external IdP. The token exchange grant type uses the protocol defined in the OAuth 2.0 token exchange specification. The OAuth 2.0 token exchange specification describes how you can request and obtain security tokens from OAuth 2.0 authorization servers. The following diagram depicts the token exchange flow in Choreo:

![Token exchange flow](../assets/img/references/external-identity-provider/token-exchange-flow.png){.cInlineImage-full}

To exchange a JWT issued by an external IdP for a Choreo access token, you must send a request to the Choreo token endpoint with the JWT (referred to as the subject_token in the preceding diagram) in the request body. Upon successful authentication, validation of the request takes place, and the corresponding IdP configuration is retrieved using the issuer. Next, the subject token is validated. Successful validation generates and returns a Choreo access token.


## Revoke access tokens

In the case of theft, security violation, or precaution, Choreo allows an admin to revoke an access token via the revoke token endpoint. You can use a utility like a cURL to invoke this endpoint and revoke your access token.  

1. Sign in to the Choreo Developer Portal at [https://devportal.choreo.dev](https://devportal.choreo.dev). Alternatively, click **Developer Portal** on the Choreo Console header. 

    ![Developer Portal](../assets/img/developer-portal/manage-applications/developer-portal.png){.cInlineImage-half}

2. Click **Applications**.
3. In the left pane, click **OAuth 2.0 Tokens**.
    You will find the **Consumer Key** (client ID), **Consumer Secret** (client secret), **Token Endpoint**, and **Revoke Endpoint** listed here. You can use these values to revoke the access token. 

The parameters required to invoke the revoke token endpoint are as follows:

-   `access_token_to_be_revoked` - The access token to be revoked

-   `<base64 encoded (consumerKey:consumerSecret)>` - Use a base64 encoder to encode your consumer key and consumer secret. Choreo does not recommend the use of online base64 encoders for this purpose.

- `<consumerKey>:<consumerSecret>` Thereafter, enter the encoded value for this parameter.

    ``` tab="Format"
        curl -k -v -d "token=<ACCESS_TOKEN_TO_BE_REVOKED>" -H "Authorization: Basic <base64 encoded (consumerKey:consumerSecret)>" -H "Content-Type: application/x-www-form-urlencoded"  https://sts.choreo.dev/oauth2/revoke
    ```
    
    ``` tab="Examples"
        curl -k -v -d "token=a0d210c7a3de7d548e03f1986e9a5c39" -H "Authorization: Basic OVRRNVJLZWFhVGZGeUpRSkRzam9aZmp4UkhjYTpDZnJ3ZXRual9ZOTdSSzFTZWlWQWx1aXdVVmth" -H "Content-Type: application/x-www-form-urlencoded" https://sts.choreo.dev/oauth2/revoke
    ```
    
    ``` tab="Response"
        You receive an empty response with the HTTP status as 200. The following HTTP headers are returned:

         content-type: text/html
         content-length: 0
         set-cookie: apim=1663574032.947.241.8184|dcb1dc1c03c8f17e5aa485d6222013b8; Path=/oauth2; Secure; HttpOnly
         x-frame-options: DENY
         x-content-type-options: nosniff
         x-xss-protection: 1; mode=block
         Cache-control: no-store
         pragma: no-cache
         revokedaccesstoken: eyJ4NXQiOiJNV1E1TldVd1lXWmlNbU16WlRJek16ZG1NekJoTVdNNFlqUXlNalZoTldNNE5qaGtNR1JtTnpGbE1HSTNaRGxtWW1Rek5tRXlNemhoWWpCaU5tWmhZdyIsImtpZCI6Ik1XUTVOV1V3WVdaaU1tTXpaVEl6TXpkbU16QmhNV000WWpReU1qVmhOV000Tmpoa01HUm1OekZsTUdJM1pEbG1ZbVF6Tm1FeU16aGhZakJpTm1aaFl3X1JTMjU2IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJiNWViODFkMC01ZTMxLTQwZDgtYWY0MS03OWMwMjJlNTRhNTciLCJhdXQiOiJBUFBMSUNBVElPTiIsImF1ZCI6Im80VFhsS0J6MGFlSTRCdHdjY2NFeG51cjRDVWEiLCJuYmYiOjE2NjM1NzM4NDksImF6cCI6Im80VFhsS0J6MGFlSTRCdHdjY2NFeG51cjRDVWEiLCJzY29wZSI6ImRlZmF1bHQiLCJvcmdhbml6YXRpb24iOnsidXVpZCI6ImVjMGQxOTk4LWU0ZDUtNDY0ZS1hYzg4LTk4ODgzOTU2MGQ1ZCJ9LCJpc3MiOiJodHRwczpcL1wvc3RzLmNob3Jlby5kZXY6NDQzXC9vYXV0aDJcL3Rva2VuIiwiZXhwIjoxNjYzNTc3NDQ5LCJpYXQiOjE2NjM1NzM4NDksImp0aSI6ImQ3NDgzNDVlLWFjZmQtNDY4OS04YWUxLTY1NzljOTM4NTA0NCJ9.X0YaQGqjAmz_7h7F1S6s2Esxbn2doViQpsCj-U8_aWOHUIUQS0vs-LZwo_ETwjh_iFqG-Ll6d1M9lxO8bvfFyeyBg5qQ22qErj5Vsaz6z-kCzMbnBrRiVhKq4Gf4VdwK8y88kMR7Q3Xzm4wrEMRdQEIpDlMy-1aXtM4Ed8D7ICfvHf4LwgdAWe5-zUJLTpEMnreuQzMD7H4xEQRRSAGIG1w_oOL8Zh8uODEZVljrmQowlbmkUeoVoH_NShlo60OW-eT_GeMjvfMkig_Oz2NN4M9vPpIABm5ABTIg-kzb_mZ27yc9JzlOfXBpfKErnEQd8Pn4vNMT51eFk-ieT4utKJAZuHto0DYjdSdYiyXw3pLrKFxYRNN0IUGfjLpCuTrU-ss9iviBN_StfbEqQHfa9cohJgaXWtOl_YChFahO86OGwSpF_T76OA3RE9juRBJgN-4AsG0cTdnnyoRS3HllfJe7aQwl6qcRoFqNgUvUJjYU1debFe9FUFK4Kv35b6lPhB9KTrPBUvxfGMNdIRGc6AkrIP5Iet6VtfOb8weQWLjpoSFJ7rC4KCd_c2TXpZPVM5Zb4kRs2IHsZBXrosEUJ9a-6xGBCbdlGH5eP5WWAyLX_yf2sj-4iNDR29go2a_Y74mTjCjHKHoBik-V3Al6Jqu8pb_3C4AEIX2KQfxUc6s
         authorizeduser: b5eb81d0-5e31-40d8-af41-79c022e54a57@carbon.super
         revokedrefreshtoken: fe037a4a-a187-3333-8dbe-032b8326fca4
         strict-transport-security: max-age=15724800; includeSubDomains
    
        Note that if you use an invalid access token, you still receive an empty response with the HTTP status of 400 with the following HTTP headers and error description:
        
        HTTP/2 400 
        content-type: text/html
        content-length: 113
        set-cookie: apim=1663579039.216.277.415949|dcb1dc1c03c8f17e5aa485d6222013b8; Path=/oauth2; Secure; HttpOnly
        x-frame-options: DENY
        x-content-type-options: nosniff
        x-xss-protection: 1; mode=block
        strict-transport-security: max-age=15724800; includeSubDomains
        * Connection #0 to host sts.choreo.dev left intact
        {"error_description":"Error occurred while revoking authorization grant for applications","error":"server_error"}* Closing connection 0
    ```
