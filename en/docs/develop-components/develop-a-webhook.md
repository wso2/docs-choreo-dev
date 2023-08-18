# Develop a Webhook

Choreo allows developers to design high-quality webhooks. To explore this capability, let's consider a scenario where a team of software engineers in an organization wants to be notified via email whenever someone creates a GitHub issue with the `bug` label in a specific repository. 

In this tutorial, you will address this requirement by doing the following:

- Create a webhook component by connecting to your GitHub repository containing the webhook implementation that addresses the described requirement.
- Deploy the webhook you have created to the development environment.
- Modify the webhook implementation to connect the webhook implementation to GitHub, enabling it to act in response to selected GitHub-related events.
- Test the webhook.
- Promote the webhook to the production environment.

## Step 1: Create a Webhook component

To create a project, add a Webhook component to it, design the webhook, test it, and then commit it to make it available in the Choreo Console, follow these sub-steps:

First, let's create a Webhook component as follows:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Go to the **Webhook** card and click **Create**.
4. This opens the **Create a Webhook** pane, where you can give your component a name and a description. Enter the relevant information. Select the **Access Mode** and click **Next**.
5. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**.
6. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials, and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:
         
        - Read and write access to code and pull requests.
        - Read access to issues and metadata.
             
        You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is exclusively utilized for sending pull requests to a user repository. Choreo will not directly push any changes to a repository.

7. In the **Connect Repository** pane, enter the following information:

    | **Field**                     | **Description**    |
    |-------------------------------|--------------------|
    | **GitHub Account**            | Your account       |
    | **GitHub Repository**         | choreo-examples |
    | **Branch**                    | **`main`**         |
    | **Build Preset**              | Ballerina           |
    | **Build Context Path**        | send-email-on-github-event-webhook|

8. Click **Create**.
9. In the **Create a Webhook** pane, click on the trigger type you want to create, for example, **GitHub**. Click **Next**.
10. Review the trigger type you selected and click **Create**. 

The webhook opens on a separate page.

## Step 2: Deploy

Let's deploy your webhook to the development environment to make it invokable:

1. In the Choreo Console, click **Deploy** for your Webhook component, and click **Config & Deploy**.

2. In the **Configure & Deploy** pane, enter the following information:

    1. In the **toEmail** field, enter the email address to send the notification emails.
   
    2. In the **webhookSecret** field, enter any value.
   
        !!! note
            You must save this value for later use.
   
    3. Click **Deploy**.

    You can monitor the deployment progress in the **Console** pane that opens on the right of the page.

Once Choreo completes the deployment, the **Deploy** page displays the **Active** deployment status for the webhook.

!!! notes
    In the deployment card, when you click on the configurable edit icon, you can view the following information:

    - The **Invoke URL** shows the URL via which GitHub can invoke the webhook. Alternatively, you can find this on the component **Overview** page.
    - The **Configurables** field shows the number of times you have updated the webhook secret. You will see **2 keys configured** text displayed in this field, as shown in the image if you specified a different webhook secret when you tested the webhook in the Web Editor. If you want to edit the webhook secret again, follow these steps:

        1. Click the edit icon within the **Configurables** field.
        2. In the **Configure & Deploy** panel, enter a webhook secret different from the previously specified value.
        3. Click **Deploy**.
You can now connect the webhook to the required GitHub repository.

## Step 3: Connect the webhook to the GitHub repository

To allow the webhook to read the labels of the required GitHub repository, you need to connect the webhook to this repository. To do this, follow these steps:

1. On the **Overview** page, copy the invoke URL by clicking the copy icon within the **URL** field.

2. Access your GitHub account and open the repository for which you want to generate notification emails.

3. In the top menu, click **Settings**.

4. In the left navigation, click **Webhooks**.

5. Click **Add Webhook** and enter the following information:

    | **Field**        | **Value**                                                          |
    |------------------|--------------------------------------------------------------------|
    | **Payload URL**  | The invoke URL you copied in Step 3, sub-step 1.                   |
    | **Content Type** | Select `application/json`                                        |
    | **Secret**       | The last webhook secret you configured in [Step 2](#step-2-deploy).|

6. Under **Which events would you like to trigger this webhook?**, select **Let me select individual events**.

7. Select the **Issues** checkbox in the list that appears. 

    By doing so, you select GitHub issues as events that need to trigger this webhook. 

8. In the same list, clear the **Pushes** check box to ensure that GitHub does not trigger your webhook when the team pushes changes to the selected GitHub repository.

9. Click **Add Webhook** to save the configuration.

Now you have integrated Choreo with GitHub via the webhook you created and deployed. Proceed to step 4 to test it.

## Step 4: Test

To test your webhook, create a GitHub issue with the `Bug` label in the repository to which you connected the webhook.

You will receive a mail similar to the following to the email address you provided in [Step 2](#step-2-deploy).

## Step 5:  Promote

To promote the webhook to the Production environment, follow these sub-steps:

1. On the **Deploy** page, click **Promote**

2. In the **Configure & Deploy** pane, leave the default selection (i.e., **Use default configuration values**) unchanged. 

    If you have configured any default values for the configurable variables, selecting **Use default configuration values** allows you to proceed with those values.

    However, you did not configure any default values for configurable variables in this tutorial. Therefore, you need to enter new values.

3. To enter values for configurable variables, click **Next**.

4. In the **toEmail** field, enter the email address to send the notification emails.

5. In the **webhookSecret** field, enter any value.

    !!! note
        You must save this value for later use.

6. Click **Promote** in the pane.

    Once the system completes the promoting process, the **Production** card displays the deployment status as **Active**.
