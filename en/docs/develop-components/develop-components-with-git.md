# Develop Components With Git

Choreo enables you to develop components by connecting your GitHub or Bitbucket repository. You have the flexibility to either connect an existing repository or start with an empty repository and commit the source code later. By integrating your repositories with Choreo, you can automate tasks and optimize workflows across multiple systems, all within the Choreo platform. As of now, Choreo supports GitHub and Bitbucket as its Git provider. 

In Choreo, you can connect a Git repository that contains Ballerina source code or a Docker project. To connect your Git repository as a Docker project, your Git repository should contain the Dockerfile and the build's context. The Dockerfile specifies the instructions for building the Docker image, while the build context is a set of files located in the specified path used to build the image.

In Choreo, you can connect a Git repository that contains Ballerina source code or a Docker project. To connect your Git repository to Choreo as a Docker project, your Git repository must contain the following:

A Dockerfile
A build context
The Dockerfile specifies the instructions for building the Docker image. The build context is a set of files located in the specified path used to build the image.

Once you have connected your Git repository to Choreo, you can build, deploy, and manage your application easily. 

## Connect a Git repository to Choreo

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. Select the organization from the **Organization** list in the header. 
3. Remove any project selection from the **Project** list in the header. 
4. In the left navigation, click **Settings**.
5. In the header, click the **Organization** list. This will open the organization level settings page. 
6. Click on the **Credentials** tab. 
7. Click **+Add Credentials** to configure the Git repository connection.
8. Enter a **Credential name**, select the Git provider, and provide the **Personal Access Token** obtained from the Git provider.
9. Click **Save**.  

## Authorize GitHub with Choreo 

Authorizing Choreo as a GitHub application provides Choreo the following permissions to perform the respective actions on your behalf within the repository:

|Permission   | Read| Write| Description                                   |
|-------------|-----|------|-----------------------------------------------|
|Issues       | Y   | N    | Read component id label to filter the pull requests|
|Metadata     | Y   | N    | List repositories                             |
|Contents     | Y   | Y    | List branches and create a branch to commit sample code|
|Pull Request | Y   | Y    | Create a pull request if you have chosen to start with a Choreo sample|
|Webhooks     | Y   | Y    | Trigger automatic deployment and configuration generation|


## Authorize Bitbucket with Choreo

Authorizing Choreo as a Bitbucket application provides Choreo the following permissions to perform the respective actions on your behalf within the repository:

|Permission    | Read| Write| Description                                                            |
|--------------|-----|------|------------------------------------------------------------------------|
|Account       | Y   | N    | Get user information and workspace information                         |
|Repositories  | Y   | Y    | List branches and create a branch to commit sample code                |
|Pull Requests | Y   | Y    | Create a pull request if you have chosen to start with a Choreo sample |
|Webhooks      | Y   | Y    | Trigger automatic deployment and configuration generation              |




