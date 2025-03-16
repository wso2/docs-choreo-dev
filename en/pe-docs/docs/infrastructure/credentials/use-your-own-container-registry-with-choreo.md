# Use Your Own Container Registry with Choreo

Choreo allows you to deploy and manage prebuilt container images from external container registries as Choreo components. This enables you to deploy and effectively manage your container images within the Choreo environment.

!!! info
    This feature is currently only available on [private data planes (PDPs)](../../choreo-concepts/data-planes.md#private-data-planes) for the following component types:

    - Service
    - Web Application
    - Scheduled Task
    - Manual Task

## Prerequisites

Before you try out this guide, ensure you have the following:

- **A container registry**: Ensure you have a container registry containing the images you want to deploy. Choreo is compatible with various container registries, including but not limited to GCR (Google Container Registry), ACR (Azure Container Registry), GitHub Container Registry, and Docker Hub.

- **(Optional) An external build/CI pipeline**: This is to initiate automatic deployments during the build process outside of Choreo.

When using a container registry to deploy a component, Choreo cannot create an image from the source code (Git) or initiate a new deployment when a new image is ready. However, you can use your existing build process to trigger a deployment on Choreo by sending an HTTP POST request to a webhook with the new image details.

This feature is currently only available on private data planes (PDPs).

## Register a container registry

To get started, establish a connection between your container registry and Choreo.

!!! info
    When you use your Choreo credentials, Choreo does not _pull_ your images into its control plane. Instead, it functions as an orchestrator, facilitating your data plane's ability to retrieve images from an external container registry. Choreo passes on these credentials to the data plane for authentication and access.

To register your container registry, follow these steps:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console header, go to the **Organization** list and select your organization.
3. In the left navigation menu, click **Infrastructure** and then click **Credentials**.
4. Click the **Container Registries** tab.
5. Click **+Add Registry** to configure the Git repository connection.
6. Specify a **Registry Display Name**.
7. Select the **Authentication Type**. Fill in the required information depending on your authentication type. For details on each authentication type, see [Authentication types](#authentication-types).
8. Click **Save**.

### Authentication types

Choreo provides the following authentication options:

#### Public (anonymous) access

You can use this option to establish a connection with a container registry that permits unrestricted public or anonymous access (for example, Public Docker Hub). In this case, only the registry host information is necessary.

For example, the following are the Docker Hub registry hosts for reference:

| Vendor                            | Registry host             |
| --------------------------------- | ------------------------- |
| Docker Hub (public repositories)  | `registry.hub.docker.com` |
| Docker Hub (private repositories) | `registry.docker.com`     |

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

  <!-- TODO: (VirajSalaka) We need deployment related specifics to be added to Deploy -->

## Auto-deploy images in Choreo with an external CI/build pipeline

Choreo does not have automatic detection and deployment for newly added images or tags in the linked container registry. To overcome this limitation, Choreo allows you to integrate your own CI pipelines and initiate deployments manually. This approach enables you to use your existing CI setup or build a pipeline for image creation and pushing. You can then trigger automatic deployments using a webhook.

Follow the steps below to configure your CI/build pipeline:

1. Build and push the container image associated with a Choreo component to your container registry.
2. In the Choreo Console top navigation menu, select the **Organization**, then the **Project**, and finally the relevant **Component**.
3. In the left navigation menu, click **DevOps** and then click **External CI**.
4. Generate a token for your CI pipeline from the **Manage Tokens** section.

!!! note -
      - The tokens are bound to a specific component. 
      - It is recommended to reference the token from a secure location available to your CI pipeline. For example, use a GitHub secret if you are using GitHub Actions.

5. To trigger an automatic deployment to your development environment, you can initiate an HTTP POST request to the Choreo webhook endpoint with the updated image details. Alternatively, you can use the provided Webhook snippets. This action will seamlessly deploy the image to the development environment.
