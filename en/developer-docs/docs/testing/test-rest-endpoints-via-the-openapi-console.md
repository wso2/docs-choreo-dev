# Test REST Endpoints via the OpenAPI Console

Choreo offers an integrated OpenAPI Console to test REST endpoints for the Service components you create and deploy. Since Choreo secures REST APIs with OAuth 2.0 authentication, the OpenAPI Console generates test keys to help you test your APIs.

Follow these steps to test a REST endpoint using the OpenAPI Console:

1. Go to the [Choreo Console](https://console.choreo.dev/) and log in.
2. In the **Component Listing** pane, click on the component you want to test.
3. Click **Test** in the left navigation menu, then select **Console**. This will open the **OpenAPI Console** pane.
4. In the **OpenAPI Console** pane, select the desired environment from the drop-down menu.
5. Choose the endpoint you want to test from the **Endpoint** list.

    !!! note
        - The **Network Visibility** of an endpoint is configured in the **component.yaml** file. You can set it to **Public**, **Organization**, or **Project**.
        - If the **Network Visibility** is set to **Public**, skip to step 7.
        - If the **Network Visibility** is set to **Organization**, the endpoint is not publicly accessible. However, Choreo allows you to generate a temporary test URL for testing purposes. This URL is publicly accessible (secured with OAuth 2.0) and remains active for 15 minutes. To generate the temporary URL, proceed to step 6.

6. Generate a temporary test URL (if applicable):  
   If the **Network Visibility** is set to **Organization**, click **Generate URL** to create a temporary test URL. This URL will be active for 15 minutes. If the visibility is not set to **Organization**, skip this step.

    !!! note
        The temporary test URL is valid for **15 minutes only** and will expire afterward. You can manually deactivate it by clicking the **Deactivate URL** button.

    ![OpenAPI Console](../assets/img/testing/openapi-console-org.png){.cInlineImage-full}

7. Expand the resource you want to test.
8. Click the **Try it out** button to enable testing.
9. Provide values for any parameters, if applicable.
10. Click **Execute**. The response will be displayed under the **Responses** section.

    ![OpenAPI Console](../assets/img/testing/openapi-console.png){.cInlineImage-full}
