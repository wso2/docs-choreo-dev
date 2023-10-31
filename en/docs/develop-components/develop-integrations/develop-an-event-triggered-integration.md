# Develop an Event-Triggered Integration

An event-triggered integration executes predefined actions in response to specific events. Choreo simplifies the process of creating and deploying such integrations.

This guide walks you through the steps to create and deploy an event-triggered integration using Ballerina and Choreo. 

In this guide, you will build a simple integration that sends an email notification whenever a new issue is created in a GitHub repository.

## Prerequisites

Before you try out the steps in this guide, complete the following:

1. Create a GitHub account if you don't have an account already.
2. Sign in to your GitHub account and create a new repository with an appropriate name. For example, `github-email-integration`.
2. Install [Visual Studio Code](https://code.visualstudio.com/) on your local machine and add the [Ballerina extension](https://marketplace.visualstudio.com/items?itemName=WSO2.ballerina) to it.
3. Familiarize yourself with the [Ballerina programming language](https://ballerina.io/) and basic GitHub commands.
4. If you are signing in to the Choreo Console for the first time, create an organization as follows:
    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.
       This creates the organization and opens the **Project Home** page of the default project created for you.

## Step 1: Create an event-triggered integration component

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page. 
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Go to the **Event-Triggered Integration** card and click **Create**.
4. Enter a unique name and a description for the component. You can use the name and description given below:

    | **Field**       | **Value**                             |
    |-----------------|---------------------------------------|
    | **Name**        | `GitHubToEmail`                       |
    | **Description** | `My first event-triggered integration`|

5. Click **Next**.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**.
7. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials, and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:<br/><br/>- Read and write access to code and pull requests.<br/><br/>- Read access to issues and metadata.<br/><br/>You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only used to send pull requests to a user repository. Choreo will not directly push any changes to a repository.


8. In the **Connect Repository** pane, enter the following information:

    | **Field**             | **Description**                               |
    |-----------------------|-----------------------------------------------|
    | **GitHub Account**    | Your account                                  |
    | **GitHub Repository** | The repository you created by following the steps in the prerequisites section |
    | **Branch**            | **`main`**                               |
    | **Buildpack**      | **Ballerina** |
    | **Access Mode**       | **External**  |
    | **Project Path**      | Since the project repository is empty, select **Start with a sample**.  | 

	!!! tip
    	    - **Buildpack** specifies the type of build to run depending on the implementation of the component. It converts the integration code into a Docker image that can run on Choreo cloud. If an integration is developed using [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/), select **Micro Integrator** as the buildpack. If an integration is developed using the [Ballerina language](https://ballerina.io), select **Ballerina** as the buildpack. 

          - **Project Path** specifies the location of the project to build the component.

8. Click **Next**.
9. Select **GitHub** as the trigger type and click **Next**.
10. In the **Trigger Channel** list, select **IssuesService** and click **Create**. Choreo initializes the component with the sample implementation and sends a pull request to the GitHub repository you connected.
11. Review and merge the pull request to add the sample files to your GitHub repository.
    

## Step 2: Implement the integration logic

To implement the integration logic for the component, follow the steps given below: 

1. To clone your GitHub repository to your local machine, run the following command in your terminal or command prompt:
    
    `git clone <repository\_url>.git`

     Replace `<repository\_url>` with the URL of your GitHub repository.

 2. Open the cloned repository in Visual Studio Code. To do this, launch Visual Studio Code, click **File** > **Open Folder**, and select the cloned GitHub repository folder.
 3. Edit the `webhook.bal` file and do the following to implement the integration logic:

     a. Import the `wso2/choreo.sendemail` module.
     b. Add a configurable variable for the email recipient address.
     c. Implement the logic to send an email notification when a new issue is created. 
     
     The code should resemble the following structure:

       ```ballerina
       import ballerinax/trigger.github;
       import ballerina/http;
       import wso2/choreo.sendemail as email;
       import ballerina/log;

       configurable github:ListenerConfig config = ?;

       // Email recepient address
       configurable string recipientAddress = ?;

       listener http:Listener httpListener = new(8090);
       listener github:Listener webhookListener =  new(config,httpListener);

       service github:IssuesService on webhookListener {
          
           // This function is invoked when a new issue is created
           remote function onOpened(github:IssuesEvent payload ) returns error? {
             string issueTitle = payload.issue.title;
             string issueBody = payload.issue.body ?: "";

             email:Client emailClient = check new ();
             string sendEmailResponse = check emailClient->sendEmail(recipientAddress, "New Issue Created", "Issue Title: " + issueTitle + " Issue Body: " + issueBody);
             log:printInfo("Email sent to " + recipientAddress + " with response: " + sendEmailResponse);
           }
           remote function onClosed(github:IssuesEvent payload ) returns error? {
             //Not Implemented
           }
           remote function onReopened(github:IssuesEvent payload ) returns error? {
             //Not Implemented
           }
           remote function onAssigned(github:IssuesEvent payload ) returns error? {
             //Not Implemented
           }
           remote function onUnassigned(github:IssuesEvent payload ) returns error? {
             //Not Implemented
           }
           remote function onLabeled(github:IssuesEvent payload ) returns error? {
             //Not Implemented
           }
           remote function onUnlabeled(github:IssuesEvent payload ) returns error? {
             //Not Implemented
           }
       }

       service /ignore on httpListener {}
       ```
       
 4. Save your changes in Visual Studio Code.
 5. To commit the changes to the GitHub repository, run the following commands in your terminal or command prompt:

    ```git
       git add  
       git commit -m "Implement GitHub to Email integration logic" 
       git push
    ``` 

## Step 3: Configure and deploy the integration

To deploy the integration, follow the steps given below:

1. In the Choreo Console left navigation menu, click **Deploy**.
   Since auto-deploy on commit is enabled by default for the component, you will see a failed build caused by a missing configuration.
2. To resolve the failure, follow the steps given below:
   1. In the **Build Area** card, click **Configure & Deploy**.
   2. In the **Configure & Deploy** pane, specify values for the configurable variables as follows:

    | **Field**            | **Value**                                   |
    |----------------------|---------------------------------------------|
    | **webhookSecret**    | An appropriate string. For example, `choreo`|
    | **recipientAddress** | your email address                          |

   3. Click **Deploy**. This deploys the scheduled integration to the development environment.

      The **Development** card indicates the **Scheduled Status** as **Active** when the integration is deployed.

## Step 4: Add a webhook in GitHub to set up the integration

To trigger an email notification whenever a new issue is created in the GitHub repository, you must add a webhook in GitHub and register the URL of the integration deployed in Choreo.

To add a webhook in GitHub and set up the integration, follow the steps given below:

1. In the Choreo Console left navigation menu, click **Overview**.
2. On the **Overview** page, copy the URL of the deployed integration. 
3. To configure a webhook in GitHub, follow the steps given below:

    1. In your GitHub repository, click the **Settings** tab.
    2. In the left navigation menu, click **Webhooks**.
    3. Click **Add webhook**.
    4. In the **Payload URL** field, paste the URL you copied from the component overview page in Choreo.
    5. In the **Content type** list, select **application/json**.
    6. In the **Secret** field, specify the value you provided as **webhookSecret** when you configured the variable in [Step 3](#step-3-configure-and-deploy-the-integration) above.
    7. Click **Add webhook**.

Now you can trigger an event to test the integration.

## Step 5: Test the integration

To test the integration, follow the step given below:

- In your GitHub repository, create a new issue. This should trigger an event that results in sending an email notification to the recipient specified in step 3 above.

  Verify that an email notification has been sent to the specified recipient with the details of the new issue.

Now you have gained hands-on experience in creating, configuring, and deploying an event-triggered integration, while also exploring how to implement event-driven logic.
 
