# Configure a User Store with Built-In Identity Provider

Developers looking to experiment with a complete application development process that includes user authentication and authorization can utilize Choreo's Built-In Identity Provider (IDP). This feature allows you to easily test your application's authentication by setting up test users and groups within Choreo.

User management support in built-in IdP will be limited to adding users with attributes and groups. It is important to note that these user management capabilities are not recommended for use in production.

## Prerequisites

Before you try out this guide, be sure you have the following:
- Administrator rights to your Choreo organization: You need this to configure a user store in Built-in IdP.

## Configure Choreo Built-in IdP user store

Follow the below steps to configure Choreo built-in IdP user store for an environment.

1. Sign in to the Choreo Console at https://console.choreo.dev.
2. In the top menu, click on your **Organization**.
3. In the left navigation menu, click **Settings**.
4. From the tab set, select **Application Security**, and from the subsequent tab navigation menu, select **Identity Providers**
5. On the Identity Providers page, you can see a banner on the top to manage Choreo Built-In IdP. Click on the **Manage** button.
6. Choose the environment you want to configure the built-in IdP user store from the tabs.
7. Click on the **User Store Template file** and download the template file for a user store.
8. Fill the template file with the user details and save it.
9. Select the file from the **File Uploader** and click on **Upload**.
10. Upon successful upload, the user store will be created and your users will be displayed below.
