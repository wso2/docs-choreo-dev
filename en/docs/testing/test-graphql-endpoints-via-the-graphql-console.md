# Test GraphQL Endpoints via the GraphQL Console

Choreo provides an integrated GraphQL Console to test publicly exposed GraphQL endpoints of Service components you create and deploy. The GraphQL Console allows you to write queries and mutations interactively. As Choreo uses OAuth2.0 authentication by default to secure GraphQL APIs, the GraphQL Console generates test keys to test APIs.

Follow the steps below to test a GraphQL endpoint via the GraphQL Console:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).

2. In the **Component Listing** pane, click on the component you want to test.

3. Click **Test** in the left navigation menu and then click **Console**. This opens the **GraphQL Console** pane.

3. In the **GraphQL Console** pane, select the environment from the drop-down list.

4. Select the required endpoint from the **Public Endpoint** list.

    !!! note 
        The Public Endpoint list only includes GraphQL endpoints for which you have set the **Network Visibility** to **Public**.

5. Enter the API path and the query or mutation you want to test.

6. Click the play icon.

    !!! tip
        If you want to generate a required query or mutation for testing via the GraphQL Explorer, click **Explorer** to open the GraphQL Explorer.

    ![GraphQL Console](../assets/img/testing/graphql-console.png){.cInlineImage-full}



