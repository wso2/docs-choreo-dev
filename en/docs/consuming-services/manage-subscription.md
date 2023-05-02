# Manage Subscriptions

Subscribing an API to an application allows Choreo to authenticate the APIs requests with the application keys. An application must subscribe to a **published** API to invoke it using the application credentials.

Alternatively, you can generate credentials for an API without an explicit subscription to an application. However, this does not let you control advanced configuration such as access token expiry time, revoke token expiry time, ID token expiry time, and enabling access to the API without a secret. Generating keys in the API is recommended for testing or short-term usage but not for long-term production usage. 

To subscribe to an APIs via an application, follow the steps given below: 

1. Sign in to the [Choreo Developer Portal](https://devportal.choreo.dev).
2. In the Developer Portal header, click **Applications**.
2. In the **My Applications** page, click on the application with which you want to subscribe to an API.
3. In the left navigation menu, click **Subscriptions**. 
5. In the **Subscription Management** pane that opens, click **+ Add APIs**. 
6. Click **Add** to subscribe to an API. Depending on your requirement, you can subscribe to one or more APIs. 

    ![Add APIs](../assets/img/consume/add-apis.png){.cInlineImage-half}

Once you subscribe to an API, you can invoke the API using the application keys. 