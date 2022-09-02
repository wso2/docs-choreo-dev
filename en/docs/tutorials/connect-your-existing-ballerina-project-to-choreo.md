# Connect Your Existing Ballerina Project to Choreo

You can connect your existing Ballerina project repository to Choreo when you create any of the following components:

- REST API
- Manual Trigger
- Scheduled Task
- Webhook

Connecting your existing Ballerina repository to Choreo allows you to keep the source code of a component you create within your control. This capability also allows your development team to work on a particular Choreo component by collaborating via pull requests while adhering to enterprise-specific best practices and development guidelines such as PR checks, code analysis, styling preferences, etc.  

This tutorial walks you through the steps to connect your existing Ballerina project to Choreo when you create a REST API component. 

Let's get started!

1. Sign in to the Choreo Console at [https://console.choreo.dev](https://console.choreo.dev).
2. Create a project to add the REST API component. You can follow the instructions under [step 1 in the Connect Your Own GitHub Repository to Choreo](../connect-your-own-github-repository-to-choreo/#step-1-create-a-project-to-add-the-rest-api-component) tutorial.
3. On the **Components** page, click **+Create**.
4. Click the **REST API** card.
5. Click **Existing code**. This displays the REST API dialog box where you can proceed to connect your existing Ballerina project repository to Choreo.
6. To authorize Choreo to access your GitHub account, click **Continue**, enter your GitHub credentials, and select one or more repositories to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    ![Authorize GitHub app](../assets/img/tutorials/connect-existing-project/authorize-github-app.png){.cInlineImage-half}

    !!!info

         - The **Choreo GitHub App** requires the following permission:
            - Read and write access to code and pull requests.
            - Read access to issues and metadata.
         - You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. Choreo needs write access only to send pull requests to a user repository. Choreo does not directly push any changes to a repository.

7. Select the appropriate **GitHub account**, **GitHub repository**, and **Branch** depending on the existing Ballerina project repository you want to connect to Choreo.

    !!! tip 
        You must select a GitHub repository branch that includes an existing Ballerina project.

8. Click **Create** to proceed with component initialization. This displays a pull request similar to the following:
   ![View pull request](../assets/img/tutorials/connect-existing-project/view-pull-request.png){.cInlineImage-full}

9. Click **View Pull Request**.
10. Review and click **Merge pull request**, and then click **Confirm Merge**. 
   When you merge the pull request, it adds the necessary metadata files to connect your Ballerina project repository to Choreo so that you can proceed to create the component.

Now you have successfully connected your existing Ballerina project repository to Choreo. You can go back to the Choreo Console tab to design the component depending on your requirement.

For details on how you can design, test, and commit a REST API component, follow the instructions from [step 3 onwards in the Connect Your Own GitHub Repository to Choreo](../connect-your-own-github-repository-to-choreo/#step-3-design-the-rest-api) tutorial.
