# Develop a Webhook

Choreo allows developers to design high-quality webhooks. To explore this capability, let's consider a scenario where a team of software engineers in an organization should be notified via email whenever someone creates a GitHub issue with the `bug` label in a specific repository.

In this tutorial, you will address the requirement by doing the following:

- Create a webhook by connecting to a forked GitHub repository where you have the webhook implementation that addresses the described requirement.
- Deploy the webhook to the development environment.
- Modify the webhook implementation to connect the webhook to GitHub, enabling it to act in response to selected GitHub-related events.
- Test the webhook.
- Promote the webhook to the production environment.

## Prerequisites

1. If you're signing in to the Choreo Console for the first time, create an organization:
    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

2. Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples), which contains the sample integration for this guide.

## Step 1: Create a webhook component

To create a Webhook component, follow these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in using your preferred method.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **Webhook** card.
4. Click **Authorize with GitHub** to connect Choreo to your GitHub account. If you haven't connected your GitHub repository to Choreo, enter your credentials and select the repository you forked earlier to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, select the **Use Public GitHub Repository** option and paste the [Choreo samples repository](https://github.com/wso2/choreo-samples) URL in the **Provide Repository URL** field. However, enabling [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) requires authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! note
        The **Choreo GitHub App** requires:
        - Read and write access to code and pull requests.
        - Read access to issues and metadata.

        You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) at any time. Write access is only used for sending pull requests; Choreo will not push changes directly to your repository.

5. Enter the following information:

    | **Field**                     | **Description**    |
    |-------------------------------|--------------------|
    | **GitHub Account**            | Your account       |
    | **GitHub Repository**         | choreo-samples     |
    | **Branch**                    | main               |
    | **Component Path**              | `github-event-to-email-webhook` |

6. Select **Ballerina** as the build pack.
7. Provide a display name, a unique name and description for the component.
8. Click **Create**.

Choreo creates the Webhook component with the sample implementation and opens the component **Overview** page.

## Step 2: Deploy

To deploy the webhook to the development environment, follow these steps:

1. In the Choreo Console left navigation menu, click **Deploy**.
2. In the **Build Area** card, click **Configure & Deploy**.
3. In the **Configurations** pane, enter the following information:
    1. In the **webhookSecret** field, enter any value.

        !!! note
            Save this value for later use.

    2. In the **toEmail** field, enter the email address to send notification emails.
    3. Click **Deploy**.

    You can monitor the deployment progress in the **Console** pane that opens on the right of the page.

Once Choreo completes the deployment, the **Development** card indicates the **Deployment Status** as **Active**.

!!! note
    In the deployment card, you can click the icon corresponding to configurables to open the **Configurations** pane and edit configurations.

## Step 3: Connect the webhook to the GitHub repository

To allow the webhook to read the labels of issues in a specific GitHub repository, connect the webhook to the GitHub repository:

1. In the Choreo Console left navigation menu, click **Overview**.
2. On the **Overview** page, copy the invoke URL by clicking the copy icon within the **URL** field.
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
8. Select the **Issues** checkbox in the list of events displayed. This ensures that GitHub issues trigger the webhook.
9. Clear the **Pushes** checkbox to ensure that GitHub does not trigger your webhook when the team pushes changes to the selected GitHub repository.
10. Click **Add webhook** to save the configuration.

Now you have integrated Choreo with GitHub via the webhook you created and deployed. You can proceed to test the webhook.

## Step 4: Test

To test your webhook, create a GitHub issue with the `Bug` label in the repository that you connected to the webhook. You will receive an email similar to the following at the email address you provided in [Step 2](#step-2-deploy).

## Step 5: Promote

To promote the webhook to the Production environment, follow these steps:

1. On the **Deploy** page, go to the **Development** card and click **Promote**.
2. In the **Configuration Types** pane, leave the default selection (i.e., **Use default configuration values**) unchanged.

    !!! note
        If you have configured any default values for the configurable variables, selecting **Use default configuration values** allows you to proceed with those values. If not, specify values for the configurable variables.

3. Click **Next**.
4. In the **webhookSecret** field, enter any value.

    !!! note
        Save this value for later use.

5. In the **toEmail** field, enter the email address to send notification emails.
6. Click **Promote**.

    Once the component is promoted to production, the **Production** card displays the deployment status as **Active**.

Now you have successfully created, deployed, and tested a Webhook component and promoted it to production.
