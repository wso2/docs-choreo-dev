# Manage Applications

An application in Choreo is a logical representation of a physical application such as a mobile app, web app, device, etc. To consume an API in Choreo, you need to create an application that maps to your physical application and then subscribes to the required API over a usage policy plan which gives you a usage quota. A single application can have multiple subscriptions to APIs. Using the consumer key and consumer secret, you can generate an access token that you can use to invoke all the APIs subscribed to the same application.

This section will walk you through the steps to create an application in Choreo.

Let's get started!

## Create an application

1. Sign in to Choreo Developer Portal at [https://devportal.choreo.dev](https://devportal.choreo.dev). Alternatively, you can click on the Developer Portal link on the Choreo console header. 

    ![Developer Portal](../assets/img/developer-portal/manage-applications/developer-portal.png){.cInlineImage-half}
    
2. Click **Applications** and then, click **+Create**.
3. Enter the application name, select the usage policy, and optionally, add the application description. Click **Create**.
4. You are redirected to the application overview page. You can view the throttling tier, the token type, workflow status, and the application owner of the API. 

## Generate Keys

Choreo provides an OAuth 2.0 bearer token-based authentication for API access. An API access token/key is a string, passed as an HTTP header of an API request to authenticate the API access. 

Once you create an application in Choreo, you can then generate the credentials for the application. When you generate the credentials for the first time, Choreo gives you a consumer key and consumer secret for the application. The consumer key becomes the application's unique identifier, similar to a user's user name, and is used to authenticate the application or user. You can use this consumer key and consumer secret to generate an API access token by invoking the token endpoint. You can also revoke the access token by invoking the revoke endpoint. For testing purposes, you can generate a test token via the UI. However, we strongly recommend not to use the test token in your production environment.

This section will walk you through the steps to generating an API access token in Choreo. 

Let's get started!

1. Sign in to Choreo Developer Portal at [https://devportal.choreo.dev](https://devportal.choreo.dev). Alternatively, click the Developer Portal link on the Choreo console header. 

    ![Developer Portal](../assets/img/developer-portal/manage-applications/developer-portal.png){.cInlineImage-half}

2. Click **Applications**.
3. Click **Production Keys** -> **OAuth 2.0 Tokens** on the left panel.
4. Expand the **Advanced Configurations** section and review the options. 

    ![Advanced Configurations](../assets/img/developer-portal/manage-applications/advanced-configurations.png){.cInlineImage-half}

   - **Grant Types:** The grant types used to generate the access token.
   - **Public Client:** Identify the application as a public client to allow authentication without a client secret. You can use this for applications running on a browser or mobile device. 
   - **Application access token expiry time:** The access token expiry time (seconds).
   - **Refresh token expiry time:** The refresh token expiry time (seconds).
   - **ID token expiry time:** ID token expiry time (seconds).

5. Click **Generate Credentials** to generate the credentials for the application for the first time. 
6. Once you generate the keys, you can find the application's consumer key and consumer secret. 

    ![Credentials](../assets/img/developer-portal/manage-applications/credentials.png){.cInlineImage-half}

7. You can use the UI to generate a test token using the consumer key and consumer secret for the application, only for test purposes. We strongly recommend **NOT** using this token in your production environment. 
8. Click **:// CURL** to copy the cURL command template for generating the access token. You can add the consumer key and consumer secret to the URL and invoke the token endpoint to generate an API access token.

    ![cURL command](../assets/img/developer-portal/manage-applications/curl-command.png){.cInlineImage-half}


## Revoke access tokens

In the event of a theft, security violation, or a precaution, an admin can revoke an access token. Choreo provides you with a revoke token endpoint. You can use a utility like a cURL to invoke this endpoint and revoke your access token.  

1. Sign in to Choreo Developer Portal at [https://devportal.choreo.dev](https://devportal.choreo.dev). Alternatively, click the Developer Portal link on the Choreo console header. 

    ![Developer Portal](../assets/img/developer-portal/manage-applications/developer-portal.png){.cInlineImage-half}

2. Click **Applications**.
3. Click **Production Keys** -> **OAuth 2.0 Tokens** on the left panel.
4. You will find the **Consumer Key** (client ID), **Consumer Secret** (client secret), **Token Endpoint**, and **Revoke Endpoint** listed here. You can use these values to revoke the access token. 

The parameters required to invoke the following API are as follows:

-   `access_token_to_be_revoked` - The access token to be revoked

-   `<base64 encoded (consumerKey:consumerSecret)>` - Use a base64 encoder to encode your consumer key and consumer secret using the following format. Choreo does not recommend the use of online base64 encoders for this purpose.

- `<consumerKey>:<consumerSecret>` Thereafter, enter the encoded value for this parameter.

    ``` tab="Format"
        curl -k -v -d "token=<ACCESS_TOKEN_TO_BE_REVOKED>" -H "Authorization: Basic <base64 encoded (consumerKey:consumerSecret)>" -H "Content-Type: application/x-www-form-urlencoded"  https://sts.choreo.dev/oauth2/revoke
    ```
    
    ``` tab="Examples"
        curl -k -v -d "token=a0d210c7a3de7d548e03f1986e9a5c39" -H "Authorization: Basic OVRRNVJLZWFhVGZGeUpRSkRzam9aZmp4UkhjYTpDZnJ3ZXRual9ZOTdSSzFTZWlWQWx1aXdVVmth" -H "Content-Type: application/x-www-form-urlencoded" ttps://sts.choreo.dev/oauth2/revoke
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
