# Develop Components With Git

Choreo enables you to develop components by connecting your GitHub, Bitbucket, or GitLab repository. You have the flexibility to either connect an existing repository or start with an empty repository and commit the source code later. By integrating your repositories with Choreo, you can automate tasks and optimize workflows across multiple systems, all within the Choreo platform.  Choreo currently supports GitHub, Bitbucket, and GitLab as Git providers. 

!!! tip
    Choreo supports both Bitbucket Server and Bitbucket Cloud. The currently supported Bitbucket Server version is 8.9.2.

In Choreo, you can connect a Git repository that contains Ballerina source code or a Docker project. To connect a Git repository to Choreo as a Docker project, your Git repository must include the following:

 - A Dockerfile: Specifies the instructions to build the Docker image. 
 - A build context: A set of files in the specified path used to build the image.

Once you connect your Git repository to Choreo, you can build, deploy, and manage your application easily. 

## Connect a Git repository to Choreo

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console header, go to the **Organization** list and select your organization. 
3. In the left navigation menu, click **Settings**. This opens the organization-level settings page. 
4. Click the **Credentials** tab. 
5. Click **+Add Credentials** to configure the Git repository connection.
6. Enter a **Credential Name**, select the Git provider, and enter the **Personal Access Token** you obtained from the Git provider.
7. Click **Save**.  

## Authorize GitHub with Choreo 

Authorizing Choreo as a GitHub application grants Choreo the following permissions to perform the respective actions on your behalf within the repository:

|Permission   | Read| Write| Description                                                           |
|-------------|-----|------|-----------------------------------------------------------------------|
|Issues       | Y   | N    | Read component ID label to filter the pull requests                   |
|Metadata     | Y   | N    | List repositories                                                     |
|Contents     | Y   | Y    | List branches and create a branch to commit sample code               |
|Pull Request | Y   | Y    | Create a pull request if you start with a Choreo sample               |
|Webhooks     | Y   | Y    | Trigger automatic deployment and configuration generation             |

### Add Git submodules to a project

Choreo provides [Git submodule](https://git-scm.com/book/en/v2/Git-Tools-Submodules) support when you connect your GitHub repository to Choreo. This allows you to manage and include external repositories effectively within Choreo build pipelines. Key benefits of this capability include:

  - **Code sharing without duplication**: Use submodules to maintain shared libraries across multiple projects, ensuring a single source of truth.
  - **Efficient third-party library management**: Manage third-party libraries as submodules to update them independently and track changes easily, avoiding direct code integration.

For example, when you [work with the Micro Integrator (MI) runtime in Choreo](./work-with-the-micro-integrator-runtime-in-choreo.md), you can use Git submodules to reuse MI templates and sequences across components without duplication.

!!! info "Note" 
    If you encounter an error stating that you cannot clone a submodule due to insufficient permissions, follow the instructions below to grant the necessary permissions:

      - For a personal account:

         1. Sign in to your personal GitHub account.
         2. In the upper-right corner, click your profile picture, and then click **Settings**.
         3. In the left navigation menu, go to the **Integrations** section and click **Applications**.
         4. Under the **Installed GitHub Apps** tab, click **Configure** corresponding to **choreo.dev**.
         5. Under **Repository Access**, grant access to the necessary repositories.

      - For an organization account:

         1. Sign in to your organization's GitHub account.
         2. In the upper-right corner, click your profile picture, and then click **Settings**.
         3. In the left navigation menu, go to the **Third-Party Access** section and click **GitHub Apps**.
         4. Click **Configure** corresponding to **choreo.dev**.
         5. Under **Repository Access**, grant access to the necessary repositories.

            !!! note
                Choreo currently does not support accessing private repositories in other organizations.

#### Automatically pull latest versions of Git submodules

Choreo lets you automatically pull the latest versions of Git submodules from their respective repositories. To enable this feature, follow these steps:

!!! note 
    Choreo currently supports this feature only for components where the buildpack is **WSO2 MI**.

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the **Component Listing** pane, click the component for which you want to pull the latest versions of Git submodules.
3. In the left navigation menu, click **Build**.
4. On the **Build** page, click to edit **Build Configurations**.
5. Turn on the **Pull Latest Submodules** toggle.

    !!! info "Note"
        If you rebuild a previously built commit and it doesn’t reflect the latest changes, follow these steps to ensure the changes are applied to the deployed environment:

         1. In the Choreo Console left navigation menu, click **DevOps**, then click **Containers**.
         2. Click **Edit** to update the container settings.
         3. Select **Always** as the **Image Pull Policy**.
         4. Click **Save Changes**.

## Authorize Bitbucket with Choreo

Authorizing using a personal access token (PAT) from Bitbucket grants Choreo the following permissions to perform the respective actions on your behalf within the repository.

|Permission    | Read| Write| Description                                                        |
|--------------|-----|------|--------------------------------------------------------------------|
|Account       | Y   | N    | Get user information and workspace details                         |
|Repositories  | Y   | Y    | List branches and create a branch to commit sample code            |
|Pull Requests | Y   | Y    | Create a pull request if you start with a Choreo sample            |
|Webhooks      | Y   | Y    | Trigger automatic deployment and configuration generation          |

## Authorize self-managed GitLab with Choreo

Authorizing using a personal access token (PAT) obtained from your GitLab self-managed server grants Choreo the following permissions to perform the respective actions on your behalf within the repository.

|Permission    | Description                                                                         |
|--------------|-------------------------------------------------------------------------------------|
|API           | Grants full read/write access to the API, covering all groups and projects, as well as read/write access to the repository.|
