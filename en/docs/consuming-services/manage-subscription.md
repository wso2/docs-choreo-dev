# Manage Subscriptions

You must subscribe to a published API to use it in your application. The subscription process enables secure authentication of API requests using application keys.

Alternatively, you can generate credentials for an API without an explicit subscription to an application. However, this does not let you control advanced configurations such as access token expiry time, revoke token expiry time, ID token expiry time, and enabling access to the API without a secret. Generating keys in the API is recommended for testing or short-term usage but not for long-term production usage. 

To subscribe to an API via an application, follow the steps given below: 

1. Sign in to the [Choreo Developer Portal](https://devportal.choreo.dev).
2. In the Developer Portal header, click **Applications**.
3. On the **My Applications** page, click on the application with which you want to subscribe to an API.
4. In the left navigation menu, click **Subscriptions**. 
5. In the **Subscription Management** pane that opens, click **+ Add APIs**. 
6. Click **Add** to subscribe to an API. Depending on your requirement, you can subscribe to one or more APIs.

    !!! info "Note"
          When a new version of an API is published, applications that have subscribed to the API will automatically update to the latest version.

    ![Add APIs](../assets/img/consume/add-apis.png)

Once you subscribe to an API, you can invoke the API using the application keys. 
