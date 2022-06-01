# Choreo Enterprise Login

Choreo's enterprise login feature allows your users residing in an external IdP (Identity Provider) to login to Choreo seamlessly without changing their credentials.

This guide takes you through the steps you need to follow to configure enterprise login for your organization in Choreo. 

Let's begin...

## Step 1: Create an organization in Choreo

1. Sign in to the Choreo Console at <a>https://console.choreo.dev/</a>. using a Google / GitHub / Microsoft account.
2. If you are a new user, enter an unique organization name and create an organization. For example: "Stark Industries".
 ![Create an organization in Choreo](../assets/img/references/enterprise-login/create-choreo-organization.png){.cInlineImage-small}
 
## Step 2: Configure enterprise login for your Choreo organization

1. Expand the drop-down for your profile and click **Settings**.
2. Click the **Copy Handle** button to copy the organization handler  into the clip board.
 ![Copy organization name](../assets/img/references/enterprise-login/copy-organization-name.png){.cInlineImage-half}

3. Next, As Choreo reserves the organization name to that specific user account, we need to create an organization on the Choreo IdP(Asgardeo) with the same name. Sign up to Asgardeo <a>https://asgardeo.io/signup?utm_source=console</a> using the credentials that was used to register to Choreo. 

4. Paste the value copied in step 2 as the organization name in Asgardeo and click **Create**. 
 ![Create an organization in Asgardeo](../assets/img/references/enterprise-login/create-asgardeo-organization.png){.cInlineImage-half}

5. Finally, send an email to `choreo-help@wso2.com` requesting to configure enterprise login to your organization. 
 Mention the following information in the request:
 1. Organization name or handle. For example: “Stark Industries” or “starkindustries”
 2. Email domains specific to your organization. For example: “@stark.com”, “@starkindustries.com”, “@stark.eu”.

That's it!  
Once our support team configures enterprise login for your organization, you will receive an Email.

Next, you can create a connection to the federated identity provider (For example: Auth0, ADFS, Keycloak, etc.), following the steps below:

## Step 3: Configure a federated identity provider

1. Sign in to Asgardeo at <a>https://asgardeo.io/signup?utm_source=console</a>.

2. Follow the steps in Asgardeo documentation on [Add Standard-Based Login](https://wso2.com/asgardeo/docs/guides/authentication/enterprise-login/) to configure a federated enterprise identity provider to your Asgardeo organization.

3. Next, navigate to **Develop** and select **Applications** from the left navigation. You will see an application prefixed “WSO2_LOGIN”. 

     ![Asgardeo applications](../assets/img/references/enterprise-login/asgardeo-application.png){.cInlineImage-half}

4. Click on the application and select the **Sign-in Method** tab. You can observe the connection you configured at step 1 is shown here.

     ![Asgardeo applications](../assets/img/references/enterprise-login/sign-in-method.png){.cInlineImage-half}

You are all set! Your users in the enterprise IdP can now log into the Choreo Console using their user credentials.

