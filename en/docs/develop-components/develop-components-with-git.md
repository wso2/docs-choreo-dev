# Develop Components With Git

Choreo enables you to develop components by connecting your GitHub repository. You have the flexibility to either connect an existing repository, or start with an empty repository and commit the source code later. By integrating your Git repositories with Choreo, you can automate tasks and optimize workflows across multiple systems, all within the Choreo platform. As of now, Choreo only supports GitHub as its Git provider. 

In Choreo, you can connect a Git repository that contains Ballerina source code or a Docker project. To connect your Git repository as a Docker project, your Git repository should contain the Dockerfile and the build's context. The Dockerfile specifies the instructions for building the Docker image, while the build context is a set of files located in the specified path used to build the image.

In Choreo, you can connect a Git repository that contains Ballerina source code or a Docker project. To connect your Git repository to Choreo as a Docker project, your Git repository must contain the following:

A Dockerfile
A build context
The Dockerfile specifies the instructions for building the Docker image. The build context is a set of files located in the specified path that are used to build the image.

Once you have connected your Git repository to Choreo, you can build, deploy, and manage your application easily. 

## Authorize GitHub with Choreo 

Authorizing Choreo as a GitHub application provides Choreo the following permissions to perform the respective actions on your behalf within the repository:

|Permission   | Read| Write| Description                                   |
|-------------|-----|------|-----------------------------------------------|
|Issues       | Y   | N    | Read component id label to filter pull request|
|Metadata     | Y   | N    | List repositories                             |
|Contents     | Y   | Y    | List branches and create a branch to commit sample code|
|Pull Request | Y   | Y    | Create a pull request if you have chosen to start with a Choreo sample|
|Webhooks     | Y   | Y    | Trigger automatic deployment and configuration generation|

