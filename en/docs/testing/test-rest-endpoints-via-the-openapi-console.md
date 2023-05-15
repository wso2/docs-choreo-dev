# Test REST Endpoints via the OpenAPI Console

Choreo provides an integrated OpenAPI Console to test publicly exposed REST endpoints of Service components that you create and deploy.
As Choreo uses OAuth2.0 authentication to secure REST APIs, the OpenAPI Console generates test keys to test APIs.

Follow the steps below to test a REST endpoint via the integrated OpenAPI Console:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).

2. In the **Component Listing** pane, click on the component you want to test.

3. Click **Test** in the left navigation menu and then click **Console**. This opens the **OpenAPI Console** pane.

3. In the **OpenAPI Console** pane, select the environment from the drop-down list.

4. Select the required endpoint from the **Public Endpoint** list.

    !!! note 
        The **Public Endpoint** list only includes REST endpoints for which you have set the **Network Visibility** to **Public**.

5. Expand the resource you want to test.

6. Click **Try it out**.

7. Enter values for parameters if applicable.

8. Click **Execute**. You can see the response body under **Responses**.

     ![OpenAPI Console](../assets/img/testing/openapi-console.png){.cInlineImage-full}
