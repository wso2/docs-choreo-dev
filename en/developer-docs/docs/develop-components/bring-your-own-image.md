# Bring Your Own Image (BYOI)

Choreo allows you to deploy and manage prebuilt container images from external container registries as Choreo components. This enables you to deploy and effectively manage your container images within the Choreo environment.

!!! info
    This feature is currently only available on [private data planes (PDPs)](../choreo-concepts/data-planes.md#private-data-planes) for the following component types:

    - Service
    - Web Application
    - Scheduled Task
    - Manual Task


## Prerequisites

Before you try out this guide, ensure you have the following:

- **A container registry**: Ensure you have a container registry containing the images you want to deploy. Choreo is compatible with various container registries, including but not limited to GCR (Google Container Registry), ACR (Azure Container Registry), GitHub Container Registry, and Docker Hub.

- **An image in the registry**: You need an image ready for deployment.

- **(Optional) An external build/CI pipeline**:  This is to initiate automatic deployments during the build process outside of Choreo.

When using a container registry to deploy a component, Choreo cannot create an image from the source code (Git) or initiate a new deployment when a new image is ready. However, you can use your existing build process to trigger a deployment on Choreo by sending an HTTP POST request to a webhook with the new image details.

This feature is currently only available on private data planes (PDPs). You can find this option under **Deploy an image from a container registry** in the **Select Source** step during component creation for service components, web applications, scheduled tasks, and manual tasks.

## Step 1: Register a container registry

To get started, establish a connection between your container registry and Choreo. 

!!! Info
    When you use your Choreo credentials, Choreo does not *pull* your images into its control plane. Instead, it functions as an orchestrator, facilitating your data plane's ability to retrieve images from an external container registry. Choreo passes on these credentials to the data plane for authentication and access.

To register your container registry, follow these steps:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console header, go to the **Organization** list and select your organization. 
3. In the left navigation menu, click **Settings**. This opens the organization settings page. 
4. Click the **Credentials** tab and then click the **Container Registries** tab. 
5. Click **+Add Registry** to configure the Git repository connection.
6. Specify a **Registry Display Name**.
7. Select the **Authentication Type**. Fill in the required information depending on your authentication type. For details on each authentication type, see [Authentication types](#authentication-types).
8. Click **Save**.

### Authentication types

Choreo provides the following authentication options:  

#### Public (anonymous) access
    
You can use this option to establish a connection with a container registry that permits unrestricted public or anonymous access (for example, Public Docker Hub). In this case, only the registry host information is necessary.

For example, the following are the Docker Hub registry hosts for reference:

| Vendor                           | Registry host                |
|----------------------------------|------------------------------|
|Docker Hub (public repositories)  | `registry.hub.docker.com`    |
|Docker Hub (private repositories) | `registry.docker.com`        |

\* If necessary, you can use other mirrors instead of the above.

#### Basic authentication

To use basic authentication to authenticate to the container registry, you must provide the username and password.

#### Docker config

You can provide a Docker config in JSON format to authenticate to the container registry. This option only allows you to register one container registry. That is, it **only allows a single registry under `auths`**.

You must provide the credentials directly within the configuration. Choreo cannot utilize references to executable authentication plugins.

Sample Docker config format:

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

This option is specifically for private data planes, where your cloud provider manages authentication at the Kubernetes level. Choreo requires knowledge of the registry host because the data plane already possesses implicit (preconfigured) access to the registry.

Follow the guidelines below based on your container registry:

=== "ACR"
    **Azure Container Registry** 

    Recommended authentication options:

    * [**Service principal-based basic authentication**](https://learn.microsoft.com/en-us/azure/container-registry/container-registry-auth-service-principal)

    * **Vendor-specific authentication on Azure private data planes**
        
        Contact Choreo support to enable infrastructure-level private access to your registry from your Azure private data plane on AKS. If you are on a self-managed PDP on Azure, follow [this guide](https://learn.microsoft.com/en-us/azure/aks/cluster-container-registry-integration?toc=%2Fazure%2Fcontainer-registry%2Ftoc.json&bc=%2Fazure%2Fcontainer-registry%2Fbreadcrumb%2Ftoc.json&tabs=azure-cli).

=== "GAR"
    **Google Artifact Registry**

    Recommended authentication options:

    * [**Service account-based basic authentication**](https://cloud.google.com/artifact-registry/docs/docker/pushing-and-pulling#key)

        Use the service account key in JSON format ([`_json_key`](https://cloud.google.com/artifact-registry/docs/docker/authentication#:~:text=of%20the%20following%3A-,_json_key,-if%20you%20are)) as the username and specify the minified JSON contents of the service account key as the password.

         You can use `jq` as follows to minify the service account JSON key file:

         `jq -c . <service-account.json>`

    * **Vendor-specific authentication on GCP private data planes**

        Contact Choreo support to enable infrastructure-level private access to your registry from your GCP private data plane on GKE. If you are on a self-managed PDP on GCP, see [https://cloud.google.com/artifact-registry/docs/access-control#grant-project](https://cloud.google.com/artifact-registry/docs/access-control#grant-project).

=== "AWS ECR"
    **Elastic Container Registry**

    ECR does not allow the creation of static access passwords for basic authentication. The passwords (that is, access tokens) provided by AWS are only valid for 10 hours and must be manually regenerated. However, when an ECR is attached to an EKS cluster at an infrastructure level, this limitation does not apply because the authentication is handled by AWS internally. For details, see [https://docs.aws.amazon.com/AmazonECR/latest/userguide/registry_auth.html](https://docs.aws.amazon.com/AmazonECR/latest/userguide/registry_auth.html).

    Choreo recommends using ECR when you are exclusively on an AWS private data plane using the vendor-specific authentication option. Contact Choreo support to enable a private connection between your ECR and the underlying EKS clusters on your data plane. If you are on a self-managed PDP, you can follow [this guide](https://docs.aws.amazon.com/AmazonECR/latest/userguide/ECR_on_EKS.html).    

=== "Docker Hub (Private)"

    Recommended authentication options:

    * **Basic authentication**

          Use your Docker Hub username/password or an access token. You can generate an access token from your Docker Hub account settings and use it in place of the password. For details, see [https://docs.docker.com/docker-hub/access-tokens/](https://docs.docker.com/docker-hub/access-tokens/).

    * **Docker config**

          Sign in to the Docker CLI and copy the contents of the docker config JSON. Note that external credential stores and multiple repositories within the same config object are not supported. For more information, see [https://docs.docker.com/engine/reference/commandline/login/](https://docs.docker.com/engine/reference/commandline/login/).

=== "GHCR"
    **GitHub Container Registry**

    Recommended authentication option:

    * **Basic authentication using a PAT token**

          Create a personal access token (PAT) and use it in place of the password. You cannot use your own GitHub password. You must provide a [PAT token](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry#authenticating-with-a-personal-access-token-classicn).
  
## Step 2: Create a component in Choreo

1. In the left navigation, click **Overview** and select your project. Alternatively, select your project from the **Project** list in the Choreo Console header. 
2. Under **Component Listing** click **+ Create**.
3. Select your component type (BYOI is only available for Service, Web Application, Scheduled Task, or Manual Task components). 
4. From the Create Component pane, select the **Container Registry** under the **Connect a Docker Image** section.
5. Under **Deploy an image from Container Registry/Docker Hub**, select the container registry you have registered in [Step 1](#step-1-register-a-container-registry).
6. Enter the full image URL. The image URL format in general is as follows:
   `[container-registry-host]/[repository-name]/[image-name]:[tag]`

    !!! tip
        When a public image from Docker Hub lacks a specified repository name, it typically defaults to `/library/`. For example, you can access the public Nginx image `https://hub.docker.com/_/nginx` as `registry.hub.docker.com/library/nginx:1.25`.

7. Enter a display name, a unique name, and a description for the component.
    
    !!! info
         In the **Component Name** field, you must specify a name to uniquely identify the component in various contexts. The value is editable only at the time you create the component. You cannot change the name after you create the component.


## Step 3: Deploy the component in Choreo

To deploy the component and bring your image to Choreo, follow the steps given below: 

1. In the left navigation menu, click **Deploy**.
2. In the **Build Area** card, click **Update Image & Deploy**. 
3. In the **Endpoint** pane that opens, you can see the endpoint ready to be deployed. Click the edit icon next to the endpoint name. Optionally, you can define the endpoints for your service when you manually deploy the service. For more information, see [Expose service endpoints](#step-4-expose-service-endpoints).
4. Change the **Network Visibility** to **Public**. This setting securely exposes the endpoint for consumption.
5. Click **Update**.

    !!! info
         In this example, you deploy a Ballerina service as a REST endpoint. Therefore, Choreo generated the REST endpoint automatically. If you deploy a non-Ballerina service, you must manually add the REST endpoint and set the network visibility to **Public**.

6. Select your update image option. Refer to the update options listed below.

    You have the capability of updating the image when you are deploying the component in Choreo in one of the following three ways:

    | Option                   |Description                                                                                             |
    |--------------------------|--------------------------------------------------------------------------------------------------------|
    | **Update Image Tag**     | This option allows you to update the tag of the image.                                                 |
    | **Update Image URL**     | With this option, you can change the image name, tag, and the image repository of the image URL.|
    | **Previous Images**      | This option allows you to select a previous image and redeploy the image.                               |

7. Click **Deploy**. This deploys the service to the development environment.

## Step 4: Expose service endpoints 

!!! info
    This section only applies to service components.

After creating a service component in Choreo, you have the option to define the endpoints for your service when manually deploying a new image.

1. In the left navigation menu, click **Deploy**.
2. In the **Build Area** card, click **Update Image & Deploy**. 
3. In the **Endpoint** pane that opens, optionally, you can define the endpoints for your service when you manually deploy the service. 
4. Click  **Create/Update Endpoints**.
5. Under the **Edit endpoints.yaml** section, you can edit the endpoints YAML file in the provided editor. 
   Alternatively, you can upload the associated API specification/schema files (OpenAPI/GraphQL schemas). Instead of specifying the file path, you can also reference this file in the `endpoints.yaml` file by its file name, similar to the Git-based Choreo components. The endpoints template follows the standard definitions for defining endpoints in Choreo. For more details, see [Configure Endpoints](../develop-components/configure-endpoints.md).

## Auto-deploy images in Choreo with an external CI/build pipeline

Choreo does not have automatic detection and deployment for newly added images or tags in the linked container registry. To overcome this limitation, Choreo allows you to integrate your own CI pipelines and initiate deployments manually. This approach enables you to use your existing CI setup or build a pipeline for image creation and pushing. You can then trigger automatic deployments using a webhook.

Follow the steps below to configure your CI/build pipeline:

1. Build and push the container image associated with a Choreo component to your container registry.
2. In the left navigation menu, click **DevOps** and then click **External CI**.
3. Generate a token for your CI pipeline from the **Manage Tokens** section. 

    !!! note
        - The tokens are bound to a specific component.
        - It is recommended to reference the token from a secure location available to your CI pipeline. For example, use a GitHub secret if you are using GitHub Actions.

4. To trigger an automatic deployment to your development environment, you can initiate an HTTP POST request to the Choreo webhook endpoint with the updated image details. Alternatively, you can use the provided Webhook snippets. This action will seamlessly deploy the image to the development environment.
