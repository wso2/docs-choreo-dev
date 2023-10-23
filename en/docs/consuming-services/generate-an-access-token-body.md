### Generate an access token in cURL

You can follow the steps below to generate an access token for your application via cURL: 

1. In the [Choreo Developer Portal](https://devportal.choreo.dev) header, click **Applications**.
2. On the **My Applications** page, click on the application for which you want to generate the token.
3. In the left navigation menu, click the required environment under **Credentials**. This opens the **Application Keys** pane of the specific environment.
4. Copy the **Consumer Key**, **Consumer Secret**, and **Token Endpoint** values.
5. Use the following template and compile the cURL command with the values you copied in the above step. 

    === "Format"
    ```
    curl -k -X POST <token_endpoint> -d "grant_type=client_credentials" -H "Authorization: Basic <base64encode(consumer-key:consumer-secret)>"
    ```
6. Execute the  cURL command to generate an access token. 


### Generate an access token in the Choreo Console (for testing purposes)

You follow the steps below to generate an access token for **testing purposes**: 

1. In the [Choreo Developer Portal](https://devportal.choreo.dev) header, click **Applications**.
2. On the **My Applications** page, click on the application for which you want to generate keys and tokens.
3. In the left navigation menu, click the required environment under **Credentials**. This opens the **Application Keys** pane of the specific environment.
5. Click **Generate Token**.

