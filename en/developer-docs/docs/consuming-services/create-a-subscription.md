
To use a published API in your application, you must subscribe to it. When you subscribe to an API, your subscription covers all minor versions within the API's major version.

The subscription process ensures secure authentication of API requests using application keys. While you can generate credentials for an API without subscribing to an application, this approach limits advanced configuration options such as access token expiry time, revoke token expiry time, ID token expiry time, and enabling access to the API without a secret. Generating keys directly in the API is suitable for testing or short-term use but is not recommended for long-term production usage.

To subscribe to an API via an application, follow these steps:

1. Go to the [Choreo Developer Portal](https://devportal.choreo.dev) and sign in.

2. To navigate to applications, in the Developer Portal header, click **Applications**.

3. On the **My Applications** page, click on the application you want to use to subscribe to an API.

4. In the left navigation menu, click **Subscriptions**.

5. In the **Subscription Management** pane, click **+ Add APIs**.

6. Click **Add** to subscribe to an API. You can subscribe to one or more APIs based on your requirements.

    !!! tip
        When a new minor version of an API is published, the major version-based invocation URL automatically routes to the latest minor version within the subscribed API's major version. This ensures that existing client applications continue to function without disruption while benefiting from improvements or additions in the newer minor version.

    ![Add APIs](../assets/img/consume/add-apis.png)

Once you subscribe to an API, you can invoke it using the application keys.
