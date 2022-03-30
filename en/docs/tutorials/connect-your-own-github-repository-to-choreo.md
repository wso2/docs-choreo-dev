# Connect Your Own GitHub Repository to Choreo

By default, Choreo provides a source repository from an internal Choreo-managed GitHub organization to maintain the configuration of a Choreo component at the time you create it.

Choreo also allows you to connect your own GitHub repository to maintain the configurations of components you create. This capability enables multiple developers to work together on a particular Choreo component by collaborating with pull requests on a shared repository.

You can opt to connect your own GitHub repository to Choreo when you create any of the following components:
- REST API
- Manual Trigger
- Scheduled Task
- Webhook

If you want to connect your own GitHub repository during component creation in Choreo, you must follow these steps:

!!! tip
        As of now, Choreo does not allow you to connect GitHub repositories that have any existing Ballerina projects.

1. Authorize Choreo to access your GitHub account.

    !!! info
        You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account.

2. Select one or more repositories to install the **Choreo GitHub App**.
3. Select a **GitHub Account** and a **GitHub Repository** that has a **Choreo GitHub App** installation.

    !!! info
        If the selected repository is already integrated with Choreo to create a component, you cannot reuse it to create another Choreo component.

4. Click **Create** to proceed with component initialization. This displays the following request:.

    ![Pull request](../assets/img/tutorials/connect-own-repo/pull-request.png){.cInlineImage-full}

5. Click **View Pull Request**.
6. Review and merge the pull request.
   When you merge the pull request, it adds the necessary metadata files to connect your GitHub repository to Choreo so that you can proceed to create the component.
7. Navigate back to the Choreo Console window and click **Edit Code** to develop the component depending on your requirement.

When you connect your GitHub repository to Choreo, multiple developers can either use the same upstream repository to create a shared component in the same organization, develop and push changes to the respective tracking branch and eventually send a PR to the main branch. Developers can also create their forks from the shared upstream repository, connect to individual components, commit and send PRs to the upstream repository.
