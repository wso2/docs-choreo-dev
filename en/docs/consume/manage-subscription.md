# Subscribe to an API

Subscribing an API to an application allows Choreo to authenticate the APIs requests with the application keys. You have to subscribe to a **published** API to an application to invoke it using the application credentials.

Alternatively, you can generate credentials for an API without an explicit subscription to an application. However, this will not let you control advanced configuration such as access token expiry time, revoke token expiry time, ID token expiry time, and enabling access to the API without a secret. Generating keys in the API is recommended for testing or short-term usage but not for long-term production usage. 

This guide takes you through the steps to subscribe to APIs in Choreo. 

Let's get started!

1. Sign in to Choreo Developer Portal at [https://devportal.choreo.dev](https://devportal.choreo.dev). Alternatively, click **Go to DevPortal** on the Choreo Console or the Developer Portal icon on the left navigation menu.

    ![Developer Portal](../assets/img/developer-portal/manage-applications/developer-portal.png){.cInlineImage-small}

2. Click **Applications**. 
3. Select and click on the application you want to subscribe to from the list. 
4. Click **Subscriptions** from the left panel. 
5. Click **+ Add APIs**. 
6. Select the usage plan and click **Add** to subscribe to an API.  You can subscribe to multiple APIs. You can change the usage plan after subscription as well. 

   ![Add APIs](../assets/img/developer-portal/manage-subscriptions/add-apis.png){.cInlineImage-half}

You can now invoke the API using the application keys. 