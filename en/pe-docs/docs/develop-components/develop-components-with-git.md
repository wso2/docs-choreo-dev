# Set Up Git Provider Authentication for Choreo Deployments

Choreo enables you to develop components by connecting your GitHub, Bitbucket, or GitLab repository. You have the flexibility to either connect an existing repository or start with an empty repository and commit the source code later. By integrating your repositories with Choreo, you can automate tasks and optimize workflows across multiple systems, all within the Choreo platform.  Choreo currently supports GitHub, Bitbucket, and GitLab as Git providers. 

!!! tip
    Choreo supports both Bitbucket Server and Bitbucket Cloud. The currently supported Bitbucket Server version is 8.9.2.

In Choreo, you can connect a Git repository that contains some source code or a Docker project. Once you connect your Git repository to Choreo, you can build, deploy, and manage your application easily with choreo-console. 

## Connect a Git repository to Choreo

You can connect Bitbucket, Gitlab repositories to choreo organization using a Personal Access Token(PAT). For github, the developers can install  [WSO2 Cloud App](https://github.com/marketplace/choreo-apps) during component creation and get necessary permissions via Github itself.

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console header, go to the **Organization** list and select your organization. 
3. In the left navigation menu, click **Infrastructure** and then click **Credentials**. 
5. Under **Git Credentials** tab, click **+Import Credentials** to configure the Git repository connection.
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

<!-- TODO: (VirajSalaka) Decide if auto pull latest sub-module configuration is required -->

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
