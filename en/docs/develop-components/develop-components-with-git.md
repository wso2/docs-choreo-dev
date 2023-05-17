# Develop Components With Git

Choreo enables you to develop components by connecting your GitHub or Bitbucket repository. You have the flexibility to either connect an existing repository and use your current source code to create the component, or start with an empty repository and develop the source code later. By integrating your Git repositories with Choreo, you can automate tasks and optimize workflows across multiple systems, all within the Choreo platform. As of now, Choreo only supports GitHub and Bitbucket as its Git providers. 

In Choreo, you can connect a Git repository that contains Ballerina source code or a Docker project. To connect your Git repository to Choreo as a Docker project, your Git repository should contain the Dockerfile and the build's context. The Dockerfile specifies the instructions for building the Docker image, while the build context is a set of files located in the specified path used to build the image.

Once you have connected your Git repository to Choreo, you can build, deploy, and manage your application easily. 

## Authorize GitHub with Choreo 

Authorizing Choreo as a GitHub application to GitHub provides GitHub the following permissions to perform the following actions on your behalf within the repository:

|Permission   | Read| Write| Description                                   |
|-------------|-----|------|-----------------------------------------------|
|Issues       | Y   | N    | Read component id label to filter pull request|
|Metadata     | Y   | N    | List repositories                             |
|Contents     | Y   | Y    | List branches and create a branch to commit sample code|
|Pull Request | Y   | Y    | Create a pull request if you have chosen to start with a Choreo sample|
|Webhooks     | Y   | Y    | Trigger automatic deployment and configuration generation|


## Authorize Bitbucket with Choreo

You can Authorize Choreo to Bitbucket by following the steps below:

1. Sign in to the Choreo Console at [https://console.choreo.dev](https://console.choreo.dev).
2. Click **Settings** on the left navigation menu. 
3. Click **Credentials** and under **Git Credentials**  click **+ Add Credentials**.
4. Select **Bitbucket** as the **Service Provider** and enter the credentials. 
5. Click **+ Add**.

Authorizing Choreo to Bitbucket provides Bitbucket the following permissions to perform the following actions on your behalf within the repository:

| Permission    | Read | Write | Description                                   |
|---------------|------|-------|-----------------------------------------------|
|Account        | Y    | N     | Get user information and workspace information|
|Repositories   | Y    | Y     | List branches and create a branch to commit sample code |
|Pull Requests  | Y    | Y     | Create a pull request if you have chosen to start with a Choreo sample |
|Webhooks       | Y    | Y     | Trigger automatic deployment and configuration generation |