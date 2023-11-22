# Develop a Webhook

Choreo allows developers to design high-quality webhooks. To explore this capability, let's consider a scenario where a team of software engineers in an organization should be notified via email whenever someone creates a GitHub issue with the `bug` label in a specific repository. 

In this tutorial, you will address the requirement by doing the following:

- Create a webhook by connecting to a forked GitHub repository where you have the webhook implementation that addresses the described requirement.
- Deploy the webhook to the development environment.
- Modify the webhook implementation to connect the webhook to GitHub, enabling it to act in response to selected GitHub-related events.
- Test the webhook.
- Promote the webhook to the production environment.

## Prerequisites

Before you try out the steps in this guide, complete the following:

 - If you are signing in to the Choreo Console for the first time, create an organization as follows:
    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.
       This creates the organization and opens the **Project Home** page of the default project created for you.

 - Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples), which contains the sample integration for this guide.

## Step 1: Create a Webhook component

To create a project, add a Webhook component to it, design the webhook, test it, and then commit it to make it available in the Choreo Console, follow these sub-steps:

First, let's create a Webhook component as follows:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Go to the **Webhook** card and click **Create**.
4. This opens the **Create a Webhook** pane, where you can specify a name and description for the component. Enter a unique name and description. 
5. Select **GitHub** tab.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**.
7. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:
         
        - Read and write access to code and pull requests.
        - Read access to issues and metadata.
             
        You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is exclusively utilized for sending pull requests to a user repository. Choreo will not directly push any changes to a repository.

8. Select the **Access Mode** depending on your requirement.
9. Enter the following information:

    | **Field**                     | **Description**    |
    |-------------------------------|--------------------|
    | **GitHub Account**            | Your account       |
    | **GitHub Repository**         | choreo-samples |
    | **Branch**                    | **`main`**         |
    | **Buildpack**              | Ballerina           |
    | **Project Path**              | `github-event-to-email-webhook`|

9. Click **Create**.

Choreo creates the Webhook component with the sample implementation and opens the component **Overview** page.

## Step 2: Deploy

Let's deploy the webhook to the development environment to make it invokable:

1. In the Choreo Console left navigation menu, click **Deploy**.
2. In the **Build Area** card, click **Configure & Deploy**.
3. In the **Configurations** pane, enter the following information:

    1. In the **webhookSecret** field, enter any value.
   
        !!! note
            You must save this value for later use.
   
    2. In the **toEmail** field, enter the email address to send notification emails.
    3. Click **Deploy**.

    You can monitor the deployment progress in the **Console** pane that opens on the right of the page.

Once Choreo completes the deployment, the **Development** card indicates the **Deployment Status** as **Active**.

!!! notes
    In the deployment card, you can click the icon corresponding to configurables to open the **Configurations** pane and edit configurations:


## Step 3: Connect the webhook to the GitHub repository

To allow the webhook to read the labels of issues in a specific GitHub repository, you must connect the webhook to the GitHub repository. To do this, follow these steps:

1. In the Choreo Console left navigation menu, click **Overview**.

2. On the **Overview** page, copy the invoke URL. You can click the copy icon within the **URL** field.

3. Go to your GitHub account and open the repository for which you want to generate notification emails.

4. In the top menu, click the **Settings** tab.

5. In the left navigation menu, click **Webhooks**.

6. Click **Add webhook** and enter the following information:

    | **Field**        | **Value**                                                          |
    |------------------|--------------------------------------------------------------------|
    | **Payload URL**  | The invoke URL you copied in Step 3, sub-step 1.                   |
    | **Content Type** | Select `application/json`                                          |
    | **Secret**       | The last webhook secret you configured in [Step 2](#step-2-deploy).| 

	!!! tip
    	  - **Buildpack** specifies the type of build to run depending on the implementation of the component. It converts the integration code into a Docker image that can run on Choreo cloud. If an integration is developed using [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/), select **Micro Integrator** as the buildpack. If an integration is developed using the [Ballerina language](https://ballerina.io), select **Ballerina** as the buildpack. 

          - **Project Path** specifies the location of the project to build the component.

7. Under **Which events would you like to trigger this webhook?**, select **Let me select individual events**.

8. Select the **Issues** checkbox in the list of events displayed. 

    By doing so, you select GitHub issues as events that need to trigger this webhook. 

9. In the same list of events, clear the **Pushes** checkbox to ensure that GitHub does not trigger your webhook when the team pushes changes to the selected GitHub repository.

10. Click **Add webhook** to save the configuration.

Now you have integrated Choreo with GitHub via the webhook you created and deployed. You can proceed to test the webhook.

## Step 4: Test

To test your webhook, create a GitHub issue with the `Bug` label in the repository that you connected to the webhook.

You will receive a mail similar to the following to the email address you provided in [Step 2](#step-2-deploy).

## Step 5:  Promote

To promote the webhook to the Production environment, follow these steps:

1. On the **Deploy** page, go to the **Development** card and click **Promote**.

2. In the **Configuration Types** pane, leave the default selection (i.e., **Use default configuration values**) unchanged. 

    If you have configured any default values for the configurable variables, selecting **Use default configuration values** allows you to proceed with those values.

    If you have not configured any default values for configurable variables, follow the steps given below to specify values.

3. Click **Next**.

4. In the **webhookSecret** field, enter any value.

    !!! note
        You must save this value for later use.

5. In the **toEmail** field, enter the email address to send notification emails.
6. Click **Promote**.

   Once the component is promoted to production, the **Production** card displays the deployment status as **Active**.

Now you have successfully created, deployed, and tested a Webhook component and promoted it to production.
