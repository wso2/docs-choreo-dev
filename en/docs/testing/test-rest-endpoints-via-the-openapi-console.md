# Test REST Endpoints via the OpenAPI Console

Choreo provides an integrated OpenAPI Console to test REST endpoints of Service components that you create and deploy.
As Choreo uses OAuth2.0 authentication to secure REST APIs, the OpenAPI Console generates test keys to test APIs.

## Public Endpoints

1. Sign in to the [Choreo Console](https://console.choreo.dev/).

2. In the **Component Listing** pane, click on the component you want to test.

3. Click **Test** in the left navigation menu and then click **Console**. This opens the **OpenAPI Console** pane.

4. In the **OpenAPI Console** pane, select the environment from the drop-down list.

5. Select the required endpoint from the **Endpoint** list.

    !!! note
        You have set the **Network Visibility** to **Public** in the **endpoints.yaml** to declare an endpoint as a public endpoint.

6. Expand the resource you want to test.

7. Click **Try it out**.

8. Enter values for parameters if applicable.

9. Click **Execute**. You can see the response body under **Responses**.

![OpenAPI Console](../assets/img/testing/openapi-console.png){.cInlineImage-full}

## Organization endpoints

1. Sign in to the [Choreo Console](https://console.choreo.dev/).

2. In the **Component Listing** pane, click on the component you want to test.

3. Click **Test** in the left navigation menu and then click **Console**. This opens the **OpenAPI Console** pane.

4. In the **OpenAPI Console** pane, select the environment from the drop-down list.

5. Select the required endpoint from the **Endpoint** list.

    !!! note
        You have to set the **Network Visibility** to **Organization** in the **endpoints.yaml** to declare an endpoint as an organization endpoint.

6. Click on **Generate URL** to generate a temporay test URL that will be active for 15 minutes.

7. Expand the resource you want to test.

8. Click **Try it out**.

9. Enter values for parameters if applicable.

10. Click **Execute**. You can see the response body under **Responses**.

    !!! note
        The temporay test URL will be available **only for 15 minutes** and it will get expired after. You can deactivate the test URL by clicking on the **Deactivate URL** button.

![OpenAPI Console](../assets/img/testing/openapi-console-org.png){.cInlineImage-full}
