# Configure Asgardeo as an External Identity Provider (IdP)

Asgardeo is an identity-as-a-service (IDaaS) solution designed to create seamless login experiences for your applications. Asgardeo seamlessly integrates with Choreo, providing powerful API access control through the use of API scopes. This enables restricting API access to designated user groups. By configuring Asgardeo as an external IdP in Choreo, you can leverage your Asgardeo user stores to manage API access control effectively. This guide walks you through the steps to set up Asgardeo as your external IdP.

!!! note
    These identity provider settings are for consuming the components deployed within your organization, not for authenticating developers who signing in to the Choreo Console.
## Prerequisites

Before you proceed, be sure to complete the following:

- Create an Asgardeo application. You can follow the Asgardeo guide to [register a standard-based application](https://wso2.com/asgardeo/docs/guides/applications/register-standard-based-app/#register-an-application).

- Find the well-known URL:
  Go to the **info** tab of the Asgardeo application to view the endpoints and copy the **Discovery** endpoint.

- Find the Client ID:
  Go to the **Protocol** tab of the Asgardeo application and copy the **Client ID**.

## Add Asgardeo as an external IdP in Choreo

Follow the steps below to add Asgardeo as an external IdP in Choreo:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev).
2. In the Choreo Console header, go to the **Organization** list and select your organization.
3. In the left navigation menu, click **API Management** and then click on **Consumer IdPs**.
4. Click the **Identity Providers** tab.
5. To add an identity provider, click **+ Identity Provider** 
6. Click **Asgardeo**. 
7. In the Asgardeo dialog that opens, specify a name and a description for the IdP. 
8. In the **Well-Known URL** field, paste the well-known URL that you copied from your Asgardeo instance by following the prerequisites. 
9. Leave the **Apply to all environments** checkbox selected. This allows you to use the tokens generated via this IdP to invoke APIs across all environments.

    !!! note
         If you want to restrict the use of tokens generated via this IdP to invoke APIs in specific environments, clear the **Apply to all environments** checkbox and select the necessary environments from the **Environments** list.

10. Click **Next**. This displays the server endpoints that are useful to implement and configure authentication for your application.
11. Click **Add**. 

Now you have configured Asgardeo as an external IdP in Choreo.
