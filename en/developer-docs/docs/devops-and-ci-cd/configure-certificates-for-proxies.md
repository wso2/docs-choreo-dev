
# Configure Certificates for Proxies

!!! note
    mTLS certificates follow a different configuration flow. For details, see [Secure Communication Between the Gateway and Your Backend with Mutual TLS](../authentication-and-authorization/secure-communication-between-the-choreo-gateway-and-your-backend-with-mutual-tls.md).

You can apply organization-level TLS certificates to Proxy components to ensure secure communication with external services. Certificates configured at the organization level are available for selection when configuring Proxy endpoints and during deployment. To learn how to add and manage certificates at the organization level, see [Manage Certificates](./manage-certificates.md).

## Configure endpoint certificates

To configure TLS certificates for a Proxy component's endpoints, follow the steps given below:

1. Navigate to the Proxy component you want to configure.
2. On the **Endpoints** page, locate the endpoint you want to secure.
3. Select a TLS/endpoint certificate from the dropdown. The dropdown lists certificates available at the organization level.
4. Optionally, configure a separate certificate for the sandbox endpoint.

## Deploy a proxy with endpoint certificates

To deploy a Proxy component with an endpoint certificate, follow the steps given below:

1. Navigate to the Proxy component's **Deploy** page.
2. Click **Configure & Deploy**. This opens the configuration and deployment wizard.
3. In the wizard, you can select or change TLS certificates for main endpoint and sandbox endpoint.
4. Click **Save and Deploy** to deploy the component with the selected certificates.

## Promote a proxy with certificates

When promoting a Proxy component from a lower environment to a higher environment, you can change the certificates applied to the component:

1. Navigate to the Proxy component's **Deploy** page.
2. Click **Promote** on the environment card of the lower environment.
3. In the promotion wizard, update the endpoint certificates as needed for the target environment.
4. Complete the promotion.

## Update certificates on a deployed proxy

To update certificates on an already deployed Proxy component, follow the steps given below:

1. Navigate to the Proxy component's **Deploy** page.
2. On the deployed environment card, click **Environment Variables**.
3. Click **Endpoint Configuration**.
4. Click **Update** and select or change the relevant certificate from the dropdown.
5. Click **Save and Deploy** to redeploy the component for the changes to take effect.
