# Bring Your Own Image (BYOI)

Deploy and manage (prebuilt) container images from external container registries as Choreo Components. This enables you to deploy and effectively manage your container images directly within the Choreo environment.

!!! info
    This feature is currently only available on [Private Data Planes (PDP)](https://wso2.com/choreo/docs/choreo-concepts/data-planes/#private-data-plane) and supports the following component types:

    - service
    - web applications
    - scheduled tasks
    - manual tasks.


## Prerequisites

Before you try out this guide, be sure you have the following:

- **A container registry**:  A container registry that contains the images you want to deploy. 

Choreo provides compatibility with various container registries, including but not limited to well-known options such as GCR (Google Container Registry), ACR (Azure Container Registry), GitHub Container Registry, and Docker Hub.

- **An image in the registry**: You will need an image to be in the registry that you can deploy.

- **(Optional) An external build/CI pipeline**:  This refers to a pipeline outside of Choreo that initiates automatic deployments during the build process.

When using a Container Registry to deploy a Component, Choreo can not make an image from the source code (Git) or start a new deployment when a new image is ready. However, you can achieve this by using the same build process you used to create the image. This process involves triggering a deployment on Choreo by sending an HTTP POST request to a webhook that includes the details of the new image.

This feature is currently only available on Private Data Planes (PDP). You can find this option under "Deploy an image from a container registry" under the "Select Source" step in the component creation wizard for service components, web apps, scheduled triggers, and manual triggers.

## Step 1: Register a container registry


To get started, establish a connection between your container registry and Choreo. 

!!! Info
    Using the credentials entered in Choreo, Choreo does not "pull" your images into its control plane. Instead, it functions as an orchestrator, facilitating your data plane's ability to retrieve images from an external container registry. Choreo passes on these credentials to the data plane for authentication and access.

You can register your container registry by following the steps outlined below:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. Select the organization from the **Organization** list in the header. 
3. Remove any project selection from the **Project** list in the header. 
4. In the left navigation, click **Settings**.
5. In the header, click the **Organization** list. This will open the organization level settings page. 
6. Click on the **Credentials** tab and then click on the **Container Registries** tab. 
7. Click **+Add Registry** to configure the Git repository connection.
8. Enter a **Registry Display Name**.
9. Select the **Authentication Type**. Fill in the required information depending on your authentication type. Refer to the section [Authentication types](#authentication-types) for more information.
10. Click **Save**.

### Authentication types

Choreo provides the following authentication options:  

#### Public (Anonymous) Access
    
You can use this option to establish a connection with a container registry that permits unrestricted public or anonymous access (e.g., Public Docker Hub). In this case, only the registry host information is necessary.

For example, Docker Hub’s registry hosts for reference:

| Vendor                           | Registry Host                       |
|----------------------------------|-------------------------------------|
|Docker Hub (Public Repositories)  | registry.hub.docker.com             |
|Docker Hub (Private Repositories) | registry.docker.com                 |

* Note that you can use other mirrors instead of the above.

#### Basic Authentication
You can use basic authentication by providing the username and password to authenticate to the container registry.

#### Docker Config
You can provide a Docker config in JSON format to authenticate to the container registry. With this you can only register one container registry, that is, **only a single registry under “auths” is allowed**.

You must provide the credentials directly within the configuration; Choreo cannot utilize references to executable authentication plugins.

Sample Docker Config format:

```json
    {
    "auths": {
        "index.docker.io/v1/": {
        "auth": "c3R...zE2"
        }
    }
    }
```

#### Vendor-specific authentication

This option is specifically for Private Data Planes, where your cloud provider manages authentication at the Kubernetes level. Choreo requires knowledge of the registry host, as the data plane already possesses implicit (preconfigured) access to the registry.

Follow the guidelines below based on your container registry:

=== "ACR"
    **Azure Container Registry** 

    Recommended authentication options:

    * [**Service Principal based basic authentication**](https://learn.microsoft.com/en-us/azure/container-registry/container-registry-auth-service-principal)

    * **Vendor-specific authentication on Azure Private Data Planes**
        
        Contact Choreo Support to enable infrastructure-level private access to your registry from your Azure Private Data Plane on AKS or follow [this guide](https://learn.microsoft.com/en-us/azure/aks/cluster-container-registry-integration?toc=%2Fazure%2Fcontainer-registry%2Ftoc.json&bc=%2Fazure%2Fcontainer-registry%2Fbreadcrumb%2Ftoc.json&tabs=azure-cli) if you are on a self-managed PDP on Azure.

=== "GAR"
    **Google Artifact Registry**

    Recommended authentication options:

    * [**Service Account based basic authentication**](https://cloud.google.com/artifact-registry/docs/docker/pushing-and-pulling#key)
       Use any name (or the name of the service account) for the ‘username’ and pass in minified JSON contents of the service account key as the ‘password’.
       You can use ‘jq’ to minfiy the service account JSON key file as follows:
       `jq -c . <service-account.json>`

    * **Vendor-specific authentication on GCP Private Data Planes**
        Contact Choreo Support to enable infrastructure-level private access to your registry from your GCP Private Data Plane on GKE or if you are on a self-managed PDP on GCP, refer to [https://cloud.google.com/artifact-registry/docs/access-control#grant-project](https://cloud.google.com/artifact-registry/docs/access-control#grant-project)

=== "AWS ECR"
    **Elastic Container Registry**

    ECR does not allow the creation of static access passwords (for basic authentication). The passwords (that is, access tokens) provided by AWS are only valid for 10 hours and must be manually regenerated - However, when an ECR is attached to an EKS cluster (at an infrastructure level) this limitation does not apply as the authentication is handled by AWS internally.
    [https://docs.aws.amazon.com/AmazonECR/latest/userguide/registry_auth.html](https://docs.aws.amazon.com/AmazonECR/latest/userguide/registry_auth.html)

    Choreo recommends using ECR when you are exclusively on an AWS Private Data Plane using the vendor-specific authentication option. Contact Choreo support to enable a private connection between your ECR and the underlying EKS Clusters on your data plane. (If you are on a self-managed PDP, you can refer [this guide](https://docs.aws.amazon.com/AmazonECR/latest/userguide/ECR_on_EKS.html))    

=== "Docker Hub (Private)"

    Recommended authentication options:

    * **Basic Authentication**
          Use your Docker Hub username/password or an access token. You can generate an access token from your Docker Hub account settings and use it in place of the password. [https://docs.docker.com/docker-hub/access-tokens/](https://docs.docker.com/docker-hub/access-tokens/)

    * **Docker Config**
          Log in to the Docker CLI, and copy the contents of the docker config JSON. Note that external credential stores and multiple repositories within the same config object are not supported. [https://docs.docker.com/engine/reference/commandline/login/](https://docs.docker.com/engine/reference/commandline/login/)

=== "GHCR"
    **GitHub Container Registry**

    Recommended authentication option:

    * **Basic Authentication using a PAT Token**
          Create a Personal Access Token (PAT) and use it in place of the password. You cannot use your own GitHub password. You must provide a PAT token. [https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry#authenticating-with-a-personal-access-token-classicn](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry#authenticating-with-a-personal-access-token-classicn)

  
## Step 2: Create a component in Choreo

1. In the left navigation, click **Overview** and select your project. Alternatively, select the project from the **Project** list in the header. 
2. Under **Component Listing** click **+ Create**.
3. Select your component type. (BYOI is only available for Service, Web App, Scheduled Task, or Manual Trigger components) 
4. Enter a **name** and **Description**.
5. Select either the **DockerHub** tab or the **Container Registry** tab depending on your vendor.
6. Under the **Deploy an image from a Container Registry/DockerHub** section, select the container registry you have registered in [Step 1](#step-1-register-a-container-registry).
7. Enter the full image URL. Image URL format in general is as follows:
   `[container-registry-host]/[repository-name]/[image-name]:[tag]`

!!! tip
    When a public image from Docker Hub lacks a specified "repository name," it typically defaults to "/library/." For instance, you can access the public Nginx image (https://hub.docker.com/_/nginx) as registry.hub.docker.com/library/nginx:1.25.

## Step 3: Deploy the component in Choreo

You can deploy the component and thereby bring your image to Choreo by following the steps below: 

1. In the left navigation menu, click **Deploy**.
2. In the **Build Area** card, click **Update Image & Deploy**. 
3. In the **Endpoint** pane that opens, you can see the **Readinglist** endpoint ready to be deployed. Click the edit icon next to the **Readinglist** endpoint. Optionally, you can define the endpoints for your service when you manually deploy the service. Refer to section [Expose service endpoints](#step-4-expose-service-endpoints).
4. Change the **Network Visibility** to **Public**. This setting securely exposes the endpoint for consumption.
5. Click **Update**.

    !!! info
         In this example, you deploy a Ballerina service as a REST endpoint. Therefore, Choreo generated the REST endpoint automatically. If you deploy a non-Ballerina service, you must manually add the REST endpoint and set the network visibility to **Public**.

6. Select your update image option. Refer to the update options listed below.

    You have the capability of updating the image when you are deploying the component in Choreo in one of the following three ways:

    | Option                   |Description                                                                                             |
    |--------------------------|--------------------------------------------------------------------------------------------------------|
    | **Update Image Tag**     | This option allows you to update the tag of the image.                                                 |
    | **Update Image Url**     | With this option, you can change the image name, tag, and the image repository of the image URL.|
    | **Previous Images**      | This option allows you to select a previous image and redeploy the image.                               |

7. Click **Deploy**. This deploys the service to the development environment.

## Step 4: Expose service endpoints 

!!! info
    This section only applies to Service components.

After creating a Service component in Choreo, you have the option to define the endpoints for your service when manually deploying a new image.

1. In the left navigation menu, click **Deploy**.
2. In the **Build Area** card, click **Update Image & Deploy**. 
3. In the **Endpoint** pane that opens, optionally, you can define the endpoints for your service when you manually deploy the service. 
4. Click  **Create/Update Endpoints**.
5. Under the **Edit endpoints.yaml** section, you can edit the endpoints YAML file in the provided editor. 
   Alternatively, you can upload the associated API specification/schema files (OpenAPI/GraphQL schemas). Reference this file in the endpoints.yaml file by its file name, instead of the file path. Similar to the Git-based Choreo components. The endpoints template follows the standard definitions for defining endpoints in Choreo. [Learn more](https://wso2.com/choreo/docs/develop-components/develop-services/develop-a-service/#learn-the-endpointsyaml-file)

## Auto-deploy images in Choreo with an external CI/Build pipeline

Choreo does not have automatic detection and deployment for newly added images or tags in the linked container registry. To address this limitation, we provide the flexibility for users to bring their own CI pipelines and initiate deployments manually. This means you can harness your existing CI or build a pipeline responsible for image creation and pushing to trigger automatic deployments using a webhook.

Follow the steps below to configure your CI/Build pipeline:

1. Build and push the container image associated with a Choreo component to your container registry.
2. In the left navigation menu, click **DevOps** and then click **External CI**.
3. Generate a token for your CI pipeline from the **Manage Tokens** section. 

    !!! note
        - The tokens are bound to a specific component.
        - It is recommended to reference the token from a secure location available to your CI pipeline. For example,  use a GitHub Secret if you are using GitHub Actions.

4. To trigger an automatic deployment to your development environment, you can initiate an HTTP POST request to the Choreo webhook endpoint with the updated image details. Alternatively, you can use the provided Webhook Snippets. This action will seamlessly deploy the image to the development environment.

