# Test WebSocket Endpoints via the WebSocket Console

Choreo provides an integrated WebSocket Console to test publicly exposed WebSocket endpoints of Service components you create and deploy. The WebSocket Console allows you to connect to your service and exchange messages interactively. Choreo secures WebSocket APIs with OAuth2.0 by default, and the console automatically generates test keys for authenticated API testing.

Follow the steps below to test a WebSocket endpoint via the WebSocket Console:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).

2. In the **Component Listing** pane, click on the component you want to test.

3. Click **Test** in the left navigation menu and then click **Console**. This opens the **WebSocket Console** pane.

4. In the **WebSocket Console** pane, select the environment from the drop-down list.

5. Select the required endpoint from the **Endpoint** list.

    !!! note
        - The **Network Visibility** of an endpoint is set in the **endpoints.yaml** file. You can set it to **Public**, **Organization**, or **Project**.
        - If you have set the **Network Visibility** of the endpoint to **Public**, proceed to step 7. 
        - If you have set the **Network Visibility** of the endpoint to **Organization**, it won't be accessible from outside the organization. For testing, Choreo allows you to generate a temporary URL that remains active for 15 minutes. Follow step 6 to generate the URL.
        
6. If the **Network Visibility** of the endpoint is set to **Organization**, click **Generate URL** to generate a temporary test URL valid for 15 minutes. Otherwise, skip this step. 

    !!! note
        The temporary test URL is valid for 15 minutes and will expire after that. If you want to manually deactivate it, click **Deactivate URL**.

    ![WebSocket Console](../assets/img/testing/websocket-console.png){.cInlineImage-full}

7. Expand the channel you want to test.

8. Click **Connect** to establish a connection. The connection status will be displayed in the output logs.

9. Send and receive messages to and from the deployed service.
    
    !!! note
         The maximum connection duration is 15 minutes. After that, the connection terminates. To reconnect, click **Connect** again.
