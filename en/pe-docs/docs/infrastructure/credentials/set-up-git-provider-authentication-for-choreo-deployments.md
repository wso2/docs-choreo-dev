# Set Up Git Provider Authentication for {{ product_name }} Deployments

{{ product_name }} enables you to develop components by connecting your GitHub, Bitbucket, GitLab or Azure DevOps repository. You have the flexibility to either connect an existing repository or start with an empty repository and commit the source code later. By integrating your repositories with {{ product_name }}, you can automate tasks and optimize workflows across multiple systems, all within the {{ product_name }} platform. {{ product_name }} currently supports GitHub, Bitbucket, GitLab and Azure DevOps as Git providers.

!!! tip
    {{ product_name }} supports both Bitbucket Server and Bitbucket Cloud. The currently supported Bitbucket Server version is 8.9.2.

In {{ product_name }}, you can connect a Git repository that contains some source code or a Docker project. Once you connect your Git repository to {{ product_name }}, you can build, deploy, and manage your application easily with {{ product_name }}-console.

## Connect a Git repository to {{ product_name }}

You can connect Bitbucket, GitLab, Azure DevOps repositories to {{ product_name }} organization using access tokens (API tokens for Bitbucket, Personal access tokens for GitLab, Azure DevOps). For GitHub, the developers can install [WSO2 Cloud App](https://github.com/marketplace/choreo-apps) during component creation and get necessary permissions via GitHub itself.

1. Sign in to the [{{ product_name }} Console](https://console.choreo.dev/).
2. In the {{ product_name }} Console header, go to the **Organization** list and select your organization.
3. In the left navigation menu, click **Infrastructure** and then click **Credentials**.
4. Under **Git Credentials** tab, click **+Add Credentials** to configure the Git repository connection.
5. Enter a **Credential Name**, select the Git provider, and enter the **Credential** you obtained from the Git provider.
6. Click **Save**.

## Authorize GitHub with {{ product_name }}

Authorizing {{ product_name }} as a GitHub application grants {{ product_name }} the following permissions to perform the respective actions on your behalf within the repository:

|Permission   | Read| Write| Description                                                           |
|-------------|-----|------|-----------------------------------------------------------------------|
|Issues       | Y   | N    | Read component ID label to filter the pull requests                   |
|Metadata     | Y   | N    | List repositories                                                     |
|Contents     | Y   | Y    | List branches and create a branch to commit sample code               |
|Pull Request | Y   | Y    | Create a pull request if you start with a {{ product_name }} sample               |
|Webhooks     | Y   | Y    | Trigger automatic deployment and configuration generation             |

## Authorize Bitbucket with {{ product_name }}

Authorizing using an API token from Bitbucket grants {{ product_name }} the following permissions to perform the respective actions on your behalf within the repository.

| Permission    | Read | Write | Delete | Description                                               |
| ------------- | ---- | ----- | ------ | --------------------------------------------------------- |
| Workspace     | Y    | N     | N      | Get workspace details                                     |
| User          | Y    | N     | N      | Get user information                                      |
| Repositories  | Y    | Y     | N      | List branches and create a branch to commit sample code   |
| Pull Requests | Y    | Y     | N      | Create a pull request if you start with a {{ product_name }} sample   |
| Webhooks      | Y    | Y     | Y      | Trigger automatic deployment and configuration generation |

## Authorize self-managed GitLab with {{ product_name }}

Authorizing using a personal access token (PAT) obtained from your GitLab self-managed server grants {{ product_name }} the following permissions to perform the respective actions on your behalf within the repository.

|Permission    | Description                                                                         |
|--------------|-------------------------------------------------------------------------------------|
|API           | Grants full read/write access to the API, covering all groups and projects, as well as read/write access to the repository.|

## Authorize Azure DevOps with {{ product_name }}

Authorizing {{ product_name }} using a Personal Access Token (PAT) from Azure DevOps grants {{ product_name }} the following permissions to perform actions on your behalf within the selected organization and project.

| Permission   | Read | Write | Description                                                                                           | 
|--------------|------|-------|------------------------------------------------------------------------------------------------------ |
| Project      | Y    | N     | Read project metadata to identify and validate the selected Azure DevOps project                      |
| Team         | Y    | N     | Read team and organization-level information required for repository access                           |
| Code         | Y    | Y     | Read repository content and branches, and create branches or commits when initializing components     |
