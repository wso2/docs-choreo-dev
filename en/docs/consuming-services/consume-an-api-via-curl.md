You can invoke the API using the access token you generated above. Use the following cURL command template,  fill in the required fields, and invoke the API. 

=== "Format"
    ``` curl
         curl -H "Authorization: Bearer <access_token>" <API_invocation_URL>
    ```
    


=== "Example"
    ``` curl
    curl -H "Authorization: Bearer eyJ4NXQiOiJaR1F6WXpaaE5XVTJZbVE1T0RBM....TueTTZ1qTwLgUL2Sivkjg" https://<instance_id>-dev.e1-us-east-azure.choreoapis.dev/xaxz/reading-list-service/readinglist-a57/1.0.0/books
    ```