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

You can configure the external consumer to work with the Choreo built-in identity provider or any external identity provider that supports OIDC/OAuth 2.0.

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

=== "An external identity provider"

     **Step 2.1: Create and configure an OIDC/OAuth 2.0 application in the external identity provider**

     1. Create an OIDC/OAuth 2.0 application in your external identity provider.
     2. Configure the OIDC/OAuth 2.0 application accordingly. (The access token type should be configured as JWT)

     **Step 2.2: Link the OIDC/OAuth 2.0 application to the Choreo component**

     3. In the **Overview** page of the component, under **Authentication Configurations**, click **Configure** corresponding to the environment for which you want to configure an identity provider.
     4. In the **Identity Provider** list, select your identity provider.
     5. Paste the **Client ID** of the OIDC/OAuth 2.0 application you created in your external identity provider.
     6. Click **Add Keys**.

## Step 3: Connect the external consumer to a service deployed in Choreo 

To establish connections from the external consumer to services deployed in Choreo, you can create connections. For step-by-step instructions on creating a connection, see [Create a Connection](./sharing-and-reusing/create-a-connection.md).
