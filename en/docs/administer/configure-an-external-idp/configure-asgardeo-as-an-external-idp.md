# Configure Asgardeo as an External Identity Provider (IdP)

Asgardeo is an identity-as-a-soluton (IDaaS) that is designed to create seamless login experiences for your apps. Asgardeo is Choreo's default Identity provider offering powerful API access control. This control hinges on the use of API scopes. That is, it enables the restriction of access to a designated group of users. Choreo allows you to bring your user stores in Asgardeo as an external IdP and provide API access control seamlessly. This document guides you step-by-step to configure Asgardeo AD as your external IdP.

## Prerequisites

Before you try out this guide, be sure you have the following:

- Create an Asgardeo application as follows: 
https://wso2.com/asgardeo/docs/guides/applications/register-standard-based-app/#register-an-application

- Find the well-known URL
  Click on the **info** tab of your application to view the endpoints and copy the **Discovery** endpoint.

- Find the Client ID :
  Click on the **info** tab of your application to view the endpoints and copy the **Client ID**.

## Add Azure Active Directory as IdP in Choreo

Follow the steps below to add Asgardeo as an external IdP in Choreo:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev).
2. In the left navigation menu, click **Settings**.
3. In the **API Management** tab, under **Identity Providers**, click **+ Identity Provider**.
4. Select  **Asgardeo** as the Identity Provider. 
5. Provide a name and a description for the IdP. 
6. Enter the `Well-Known URL` of your Asgardeo instance. You copied this in the prerequisites. 
7. Leave the **Apply to all environments** checkbox selected. However, if you want to restrict the use of the external IdP to a certain environment, you can select them from the **Environments** list.
8. Click **Next**.
