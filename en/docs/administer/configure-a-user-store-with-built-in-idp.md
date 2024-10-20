# Configure a User Store with the Built-In Identity Provider

Developers looking to experiment with a complete application development process that includes user authentication and authorization can utilize Choreo's built-in identity provider (IdP). Choreo's built-in identity provider allows you to seamlessly test your application's authentication by setting up test users and groups within Choreo. 

!!! note
     Although the built-in IdP facilitates user management support, it is limited to adding users with attributes and groups. Therefore, the built-in IdP user management capabilities are not recommended for use in production.

## Prerequisites

Before you try out the steps in this guide, be sure you have administrator rights to your Choreo organization. This permission is essential to configure a user store with the built-in IdP.

## Configure a Choreo built-in IdP user store

Follow the steps given below to configure a Choreo built-in IdP user store for an environment:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in.
2. In the Choreo Console top navigation menu, click the **Organization** list and then click on your organization.
3. In the left navigation menu, click **Settings**. This takes you to your organization settings.
4. Click the **Application Security** tab and then click the **Identity Providers** tab.
5. On the **Identity Providers** tab, click **Manage** in the **Choreo Built-in Identity Provider** pane.
6. In the Manage IdP pane, click on a specific environment tab depending on where you want to configure the built-in IdP user store.
7. You can download the sample **User store template file(.csv )** from the **User Store** section. The template file content is similar to the following:

   ```csv
   username,password,groups,first_name,last_name,email
   "demouser","password1","[manager, engineering]","John","Doe","john@acme.org"
   ```

!!! note
     The provided template file includes a sample user with associated attributes. To add new users, insert additional rows in the `.csv` file. To include more user attributes, add columns as required in the `.csv` file.
   
8. Specify appropriate user details in the template file and save it.
9. Select the template file that you saved and click **Upload**. A successful upload creates the user store and displays the configured users in the **Users** section.
