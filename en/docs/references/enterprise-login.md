# Enterprise Login

Choreo's Enterprise Login feature allows your users residing in an external IdP (Identity Provider) to login into Choreo seamlessly without changing their credentials.

This guide takes you through the steps you need to follow to configure an enterprise login for your organization in Choreo. 

Let's begin...

## Step 1: Create an organization in Choreo

1. Sign in to the Choreo Console at <a>https://console.choreo.dev/</a>. using a Google / GitHub / Microsoft account.
   If you are a:
    - **New user**: enter a unique organization name and create an organization. For example, "Stark Industries".

        ![Create an organization in Choreo](../assets/img/references/enterprise-login/create-choreo-organization.png){.cInlineImage-small}
 
    - **Returning user**: expand the drop-down for your profile and click **Settings**. Under **Organizations**, you can view the organization you created at sign-up. 
 
## Step 2: Configure enterprise login for your Choreo organization

1. Expand the drop-down for your profile and click **Settings**.
2. Click **Copy Handle** to copy the organization handle to the clipboard.
     ![Copy organization name](../assets/img/references/enterprise-login/copy-organization-name.png){.cInlineImage-half}

When you add a Choreo organization, Choreo reserves the organization name for your user account. Therefore, you need to create an organization of the same name on the Choreo IdP (i.e., Asgardeo).

3. To create your organization on the Choreo IdP, follow these steps:
   1. sign up to Asgardeo <a>https://asgardeo.io/signup?utm_source=console</a> with the same credentials you used to  create your Choreo account.
   2. Paste the value copied in step 2 as the organization name in Asgardeo and click **Create**. 
     ![Create an organization in Asgardeo](../assets/img/references/enterprise-login/create-asgardeo-organization.png){.cInlineImage-half}

4. Finally, send an email to `choreo-help@wso2.com` requesting to configure enterprise login to your organization. 
 Mention the following information in the request:
   1. Organization name or handle. For example, “Stark Industries” or “starkindustries”
   2. Email domains specific to your organization. For example, “@stark.com”, “@starkindustries.com”, and “@stark.eu”.

!!!info "Sample Email"       
              Subject : [Stark Industries] Configure enterprise login
              Content:
                     Hi CS team,
                     Configure enterprise login to my organization and please find the relevant information below.
                     Organization name or handle
                     Eg: “Stark Industries” or “starkindustries”


                     Email domains specific to your organization
                     Eg: “@stark.com”, “@starkindustries.com”, “@stark.eu”
                     Thank you!

That's it! 
Once our support team configures an enterprise login for your organization, you will receive an Email.

Next, you can create a connection to the federated identity provider (For example, Auth0, ADFS, Keycloak, etc.), following the steps below:

## Step 3: Bring your own identity

Bring your own identity to Choreo by configuring a federated enterprise IdP on Asgardeo to your organization. Now that you have created an organization in Asgardeo with the same name as your Choreo organization in [Step 2](#step-2-configure-enterprise-login-for-your-choreo-organization), Choreo can authenticate users signing in to that organization. Follow the steps below to configure the federated IdP:

1. Sign in to Asgardeo at <a>https://asgardeo.io/signup?utm_source=console</a>.

2. To configure a federated enterprise identity provider to your Asgardeo organization, follow the steps in [Asgardeo documentation - Add Standard-Based Login](https://wso2.com/asgardeo/docs/guides/authentication/enterprise-login/) .

3. Next, navigate to **Develop** and select **Applications** from the left navigation. You will see an application prefixed “WSO2_LOGIN”. 

 ![Asgardeo applications](../assets/img/references/enterprise-login/asgardeo-application.png){.cInlineImage-half}

4. Click on the application and select the **Sign-in Method** tab. You can observe the connection you configured in step 2 of this section.

 ![Asgardeo applications](../assets/img/references/enterprise-login/sign-in-method.png){.cInlineImage-half}

You are all set! Your users in the enterprise IdP can now log into the Choreo Console using their user credentials.

