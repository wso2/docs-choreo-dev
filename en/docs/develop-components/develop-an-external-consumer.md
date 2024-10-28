# Develop an External Consumer

An external consumer in Choreo is any client that can interact with services deployed in Choreo, as an entity hosted outside of the Choreo infrastructure. It is a generic component type that allows you to represent and manage any client, such as a mobile application, web application, server-to-server integration, bot, microservices, etc.

In this guide, you will:

 - Create an external consumer component.
 - Manage authentication for the external consumer.
 - Connect the external consumer to a service deployed in Choreo.
 
## Prerequisites

Before you try out this guide, complete the following steps:

- If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

## Step 1: Create an external consumer component
 
To create an external consumer component, follow the steps given below:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **External Consumer** card. 
4. In the **Create an External Consumer** pane that opens, enter a display name, a unique name, and a description for the component.
5. Click **Create**. This creates the component and takes you to its **Overview** page.
   
## Step 2: Manage authentication for the external consumer

You can configure the external consumer to work with the Choreo built-in identity provider, Asgardeo, or any external identity provider that supports OIDC/OAuth2.0.

Click the respective tab for details depending on the identity provider you want to configure: 

=== "Choreo built-in identity provider"

     Follow the steps given below to configure the built-in identity provider by generating authentication keys:

    !!! note
         Choreo built-in identity provider is configured by default. Therefore, this step is optional.

     1. In the **Overview** page of the component, under **Authentication Configurations**, click **Configure** corresponding to the environment for which you want to configure an identity provider.
     2. In the **Identity Provider** list, select **Choreo Built-In Identity Provider**.
     3. Click **Generate Keys**. 

        !!! Note
             If you see **Regenerate Secret** instead of **Generate Keys**, it indicates that OAuth keys are already generated for the component in the selected environment.

=== "Asgardeo"

     **Step 2.1: Create and configure an OIDC/OAuth2.0 application in Asgardeo**

     1. Sign in to [Asgardeo](https://console.asgardeo.io/).
     2. In the top navigation menu, click the **Organization** list and select your organization.
     3. In the Asgardeo Console left navigation menu, click **Applications**.
     4. Click **+ New Application**.
     5. Click **Standard-Based Application**.
     6. Specify a name for the application and select **OAuth2.0 OpenID Connect** as the protocol.
     7. Click **Register**.
     8. Click the **Protocol** tab and follow these steps:

         1. Select `Code` and `Refresh Token` as the **Allowed grant types**.
         2. In the **Access Token** section, select `JWT` as the **Token type**.
         3. Click **Update**. 
         4. Copy the **Client ID** and **Client Secret** of the application. You will need to use these values in the next step to link the OIDC/OAuth2.0 application to your Choreo component.

     **Step 2.2: Link the OIDC/OAuth2.0 application to the Choreo external consumer component**

     1. In the **Overview** page of the component, under **Authentication Configurations**, click **Configure** corresponding to the environment for which you want to configure an identity provider.
     2. In the **Identity Provider** list, select **Asgardeo - [your-org-name]**.
     3. Paste the **Client ID** and **Client Secret** of the OIDC/OAuth2.0 application you created in Asgardeo. 
     4. Click **Add Keys**.

=== "An external identity provider"

     **Step 2.1: Create and configure an OIDC/OAuth2.0 application in the external identity provider**

     1. Create an OIDC/OAuth2.0 application in your external identity provider.
     2. Configure the OIDC/OAuth2.0 application as follows:

         1. Set `Code` and `Refresh Token` as allowed grant types.
         2. Specify the access token type as JWT.

     **Step 2.2: Link the OIDC/OAuth2.0 application to the Choreo component**

     1. In the **Overview** page of the component, under **Authentication Configurations**, click **Configure** corresponding to the environment for which you want to configure an identity provider.
     2. In the **Identity Provider** list, select your identity provider.
     3. Paste the **Client ID** and **Client Secret** of the OIDC/OAuth2.0 application you created in your external identity provider.
     4. Click **Add Keys**.

## Step 3: Connect the external consumer to a service deployed in Choreo 

To establish connections from the external consumer to services deployed in Choreo, you can create connections. For step-by-step instructions on creating a connection, see [Create a Connection](./sharing-and-reusing/create-a-connection.md).
