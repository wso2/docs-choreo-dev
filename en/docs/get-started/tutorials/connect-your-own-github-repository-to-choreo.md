# Connect Your Own GitHub Repository to Choreo

Choreo allows you to either proceed with a Choreo-managed repository or connect your own GitHub repository to maintain the source code of a component when you create any of the following Choreo components:

- REST API
- Manual Trigger
- Scheduled Task
- Webhook

The Choreo-managed repository is in a Choreo-owned GitHub organization that is not accessible to external users. Therefore, it is not ideal for scenarios where multiple developers need to develop a component in collaboration. To overcome this limitation, Choreo provides the capability to connect your own GitHub repository to maintain the source code of a Choreo component you create. This enables multiple developers to work on a particular Choreo component by collaborating via pull requests on a shared repository. Furthermore, this lets developers keep the source repository within their control and adhere to enterprise-specific best practices and development guidelines such as PR checks, code analysis, styling preferences, etc.

This tutorial walks you through the steps to connect your own GitHub repository when creating a component. In this tutorial, you will,

- Create a REST API component and connect it to your own GitHub repository.
- Design the REST API and try it out via the Web Editor.
- Commit the source code of the REST API component to the GitHub repository you connected.

## Step 1: Create a project to add the REST API component

1. Sign in to the Choreo Console at [https://console.choreo.dev](https://console.choreo.dev).
2. Expand the **Default Project** drop-down list and click **+ Create New**.
   ![Create project](../../assets/img/tutorials/connect-own-repo/create-new-project.png){.cInlineImage-full}
3. Enter a unique name and description for your project. In this tutorial, let's use the following values:

      | **Field**       | **Value**                    |
      |-----------------|------------------------------|
      | **Name**        | COVID-19 Statistics          |
      | **Description** | Maintain COVID-19 Statistics |

4. Click **Create**. This takes you to the **Components** page.

## Step 2: Add a REST API component and connect it to your own GitHub repository

1. On the **Components** page, click **+Create**.
   ![Create component](../../assets/img/tutorials/connect-own-repo/create-component.png){.cInlineImage-full}
2. Click the **REST API** card.
3. Click **Create an API from scratch** and enter a unique name and a description for the API. In this tutorial, let's enter the following values:

      | **Field**       | **Value**                   |
      |-----------------|-----------------------------|
      | **Name**        | COVID-19 Statistics         |
      | **Description** | Retrieve COVID-19 Statistics|
      
4. Click **Next**.
5. Select **Connect your own GitHub repository**.
6. To authorize Choreo to access your GitHub account, click **Continue**, enter your GitHub credentials, and select one or more repositories to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    ![Authorize GitHub app](../../assets/img/tutorials/connect-own-repo/authorize-github-app.png){.cInlineImage-half}

    !!!info

         - The **Choreo GitHub App** requires the following permission:
            - Read and write access to code and pull requests.
            - Read access to issues and metadata.
         - You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only used to send pull requests to a user repository. Choreo will not directly push any changes to a repository.

7. Select a GitHub account and a GitHub repository that includes a **Choreo GitHub App** installation.

    !!! tip 
        Choreo does not allow you to connect GitHub repositories that include existing Ballerina projects.

    !!! info
        If a selected repository is already integrated with Choreo to create a component, you cannot reuse it to create another Choreo component.

8. Click **Create** to proceed with component initialization. This displays a pull request similar to the following:
   ![View pull request](../../assets/img/tutorials/connect-own-repo/view-pull-request.png){.cInlineImage-full}

9. Click **View Pull Request**.
10. Review and click **Merge pull request**, and then click **Confirm Merge**. When you merge the pull request, it adds the necessary metadata files to connect your GitHub repository to Choreo so that you can proceed to create the component.

11. To configure the pre-commit hook that Choreo uses to extract configurables defined in the Ballerina code, follow the steps given below:

     !!! info
         When you develop a Choreo component via the Web Editor, Choreo configures this pre-commit hook automatically. However, if you prefer to develop the component locally, you need to configure this pre-commit hook manually.

     1. Carry out a GitHub pull locally to update your clone with the latest changes.

     2. In your terminal, navigate to the root directory of your GitHub repository and issue the following commands:

         !!! note
             Before issuing these commands, you need to check whether the `<MODULE_ROOT>/.githooks/pre-commit` file exists. If it does not, contact our Support team.

         - `chmod +x <MODULE_ROOT>/≈`
         - `git config core.hooksPath <MODULE_ROOT>/.githooks`

         !!! info
             If the Ballerina module resides at the repository root, exclude `<MODULE_ROOT>/` from the commands. If the Ballerina module resides in a subdirectory, replace `<MODULE_ROOT>/` with the name of that subdirectory.<br/><br/>For example, if the Ballerina module resides in the root directory, the commands can be as follows:<br/><br/> `chmod +x .githooks/pre-commit`<br/> `git config core.hooksPath .githooks`<br/><br/>If the Ballerina module resides in a subdirectory named `foo`, the commands should be as follows:<br/><br/> `chmod +x foo/.githooks/pre-commit`<br/>`git config core.hooksPath foo/.githooks`

     Once you have completed this configuration, your commits to update the component implementation will trigger the pre-hook and generate logs similar to the following extract.

      ```
       Compiling source
         johnsmith/foo:0.1.0
    
       Generating executable
         .choreo/build/bin/foo.jar
       [main ea3deab] test
       3 files changed, 7 insertions(+), 3 deletions(-)
      ```
12. Navigate to the **Choreo Console** tab to develop the component depending on your requirement.

## Step 3: Design the REST API

Follow the step-by-step instructions under [step 1.3](https://wso2.com/choreo/docs/get-started/tutorials/create-your-first-rest-api/#step-13-design-the-rest-api) in the Create Your First REST API tutorial. 

## Step 4: Run and try out the REST API

Follow the step-by-step instructions under [step 1.4](https://wso2.com/choreo/docs/get-started/tutorials/create-your-first-rest-api/#step-14-run-and-test-the-rest-api) in the Create Your First REST API tutorial.

## Step 5: Commit the source code of the REST API component to your GitHub repository

The REST API component you designed is currently available only in the Web Editor. To allow multiple developers to develop it in collaboration, you must commit the source code of the REST API component to the GitHub repository you connected.
Follow these steps:

1. Click **Sync with Choreo Upstream** in the bottom pane of the page.
   ![Sync with upstream](../../assets/img/tutorials/connect-own-repo/sync-with-choreo-upstream.png){.cInlineImage-full}
2. In the displayed message, click **Sync my changes with Choreo**.
3. In the left pane, enter a commit message (for example, `Implement REST API`) and click the tick to commit.
   ![Enter commit message](../../assets/img/tutorials/connect-own-repo/enter-commit-message.png){.cInlineImage-full}

    This displays a message requesting you to confirm that you want to stage all your changes and commit them directly.

4. Click **Yes**.

    !!! info
        If you are connecting your own repository to Choreo for the first time, you will see a message requesting you to configure your user name and email address in GitHub. Click Open Git Log, go to the **Terminal** tab, and run the following commands to configure your username and email address for GitHub commits:

         `git config --global user.email "<email@example.com>"`

         `git config --global user.name "<Your User Name>"` 

5. To push changes to your GitHub repository, click **0↓ 1↑** in the bottom pane.
   ![Push changes to repository](../../assets/img/tutorials/connect-own-repo/push-changes-to-repository.png){.cInlineImage-full}

    !!! info 
        This icon is displayed only when you complete the commit process.

Once the changes get pushed to the GitHub repository, the bottom pane of the page displays **In sync with Choreo upstream**.
![In sync](../../assets/img/tutorials/connect-own-repo/in-sync-with-choreo-upstream.png){.cInlineImage-full}

You have now successfully synced your component source code to your own GitHub repository to allow multiple developers to develop the component in collaboration via pull requests.

Developers can collaborate via any of the following approaches:

- Use the same upstream repository to create a shared component in the same organization, develop, push changes to the respective tracking branch, and eventually send a PR to the main branch.
- Create their forks from the shared upstream repository, connect to individual components, commit, and send PRs to the upstream repository.

Once you collaborate and complete developing the REST API component, you can deploy it and test it. For step-by-step instructions, see [steps 2 and 3 in the Create Your First REST API tutorial](https://wso2.com/choreo/docs/get-started/tutorials/create-your-first-rest-api/#step-2-deploy).
