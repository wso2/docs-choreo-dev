# Secure Communication Between the Choreo Gateway and Your Backend with OAuth2

OAuth2 is an industry-standard authorization framework that enables secure, delegated access to protected resources without exposing user credentials. By issuing scoped access tokens, OAuth2 ensures that clients can authenticate to backend services in a controlled, auditable manner.

## Configure OAuth2 to establish secure connectivity

When you need the Choreo Gateway to call your backend over OAuth2, use the Outbound OAuth2 mediation policy. This policy obtains an access token from your Identity Provider (IdP) and injects it into the outbound request.

### Prerequisites

- **Protected Backend**: The target endpoint must enforce OAuth2 (e.g., bearer-token validation).
- **IdP Token Endpoint**: The URL of the IdP's token endpoint that protects your backend.
- **OAuth2 Application Credentials**: A client ID and secret for an OAuth2 application dedicated to the Choreo Gateway's outbound calls.

!!! note
    Only the client-credentials grant type is currently supported.

### Step 1: Configure the OAuth2 policy

To configure the OAuth2 policy, follow the steps given below:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the **Component Listing** pane, click on the API proxy for which you want to configure OAuth2. For instructions on how to create an API proxy component, see [Develop an API Proxy: Step 1](../develop-components/develop-an-api-proxy.md#step-1-create-an-api-proxy).
3. In the left navigation menu, click **Develop** and then click **Policies**.
4. Choose the resource you want to secure, then click **Attach Mediation Policy**.
5. In the policy picker, select **Outbound OAuth (1.0.1)**.
6. Enter the **Token URL**, **Client ID**, and **Client Secret**.
7. Check **Apply to all resources** to apply the same settings across every resource, then click **Add**.

!!! tip
    Use `${parameterName}` syntax to reference environment-specific values for the Token URL, client ID, or secret.

!!! note
    By default, the token is set to the Authorization header. However, it is possible to set the token to any preferred header through the given input field.

### Step 2: Deploy the API

Once your OAuth2 policy is in place, deploy your API proxy so the changes take effect.

To deploy the API, follow the steps given below:

1. In the left navigation menu, click **Deploy**.
2. In the **Build Area** card, click **Configure & Deploy**.
3. Once the mediation application generation phase is complete, verify the endpoint URL populated for the environment and then click **Save & Deploy**.

Once the deployment is complete, you can [test the API](../testing/test-rest-endpoints-via-the-openapi-console.md) to confirm that the Gateway successfully obtains and forwards OAuth2 tokens.

### Step 3: Promote to higher environments

To promote the proxy to higher environments, use the **Promote** button.

!!! note
    If you used environment-specific parameters in your policy, you'll be prompted to supply each parameter during deploy and promotion steps.