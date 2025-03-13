# CI/CD

Choreo provides a streamlined continuous integration and continuous deployment (CI/CD) experience to deploy applications and services efficiently across multiple environments.

Choreo creates environments for each project, where all components within the project share the environments. An environment is an isolated deployment area with restricted network and resource access. Services deployed in one environment cannot communicate with services deployed in another.

The Choreo cloud data plane provides two default environments (i.e., development and production). However, if you are in a private data plane organization, you can customize and create multiple environments based on your requirements.

Choreo adopts a *build once, deploy many* strategy to manage components across multiple environments. An application is built only once (i.e., per commit if automatic build on commit is enabled or based on the selected commit during a manual build). Then it is promoted to subsequent environments. This allows testing changes in lower, non-production environments like development before promoting the build to production.

Choreo injects configurations and secrets that you maintain at the environment level into components at runtime. This ensures a strict separation of environment-specific configurations from source code. Although configurations can vary across environments, the code and the built container remain unchanged. Configurations and secrets include:

- Resource credentials to a database, cache, or other backing services.
- Credentials to external cloud services such as Amazon S3 or external APIs.

All configurations and secrets are encrypted at rest and in transit and stored in a secure vault. In a private data plane organization, you can store configurations and secrets in your infrastructure.

## Build

Choreo auto-generates build pipelines that may slightly differ depending on the component type you create. Generally, all build pipelines work as follows:

- Builds a container image either from the provided source code or from a given Dockerfile for a specific commit.
- Runs security and vulnerability scans if applicable, depending on the component type.
- Pushes the container image to a container registry. In the cloud data plane, Choreo pushes the image to a Choreo-managed registry. If it is a private data plane organization, Choreo pushes the image to a registry that you own.
- Updates service endpoints and API specifications from the provided repository if applicable.

In addition to these steps, some buildpacks support integrating unit tests into the build pipeline. For more details, see [Integrate Unit Tests into the Build Pipeline](../develop-components/integrate-unit-tests-into-the-build-pipeline.md).

### Repeatable builds

Choreo can replicate builds from an identical code version (Git commit). This means that multiple builds initiated from the same Git commit will generate Docker images with the same behavior.

!!! note
    In the event of multiple builds from the same code version, Choreo preserves only the most recent version of the Docker image created from the particular code version.

### Trigger a build

On the **Build** page, click **Build Latest**. If necessary, you have the option to select a particular commit and build an image.

!!! note
    Admin and Choreo DevOps users can trigger builds using specific tags from the connected Git repository. However, this action bypasses the standard branch-based deployment process and should only be used for critical, time-sensitive scenarios, as it can disrupt deployment track integrity.

If you want to automatically trigger a build with each commit, you can enable **Auto Build on Commit**.

### Build logs

You can view build logs for specific builds on the **Build** page.

To view details of a specific build, click **View Details** corresponding to the build.

## Deployment

Once you build an image in Choreo, you can deploy it via the **Deploy** page. To deploy an image, you can follow one of the approaches given below:

- **Manually deploy**: In the **Deploy** page, go to the **Set Up** card and click **Deploy**.
- **Automatically deploy on build**: In the **Deploy** page, go to the **Set Up** card and enable **Auto Deploy on Build**. This automatically initiates deployment upon the completion of an automatic build.

!!! info
    To enable **Auto Deploy on Build**, you must enable **Auto Build on Commit**. This is because automatic deployment is not necessary or useful in scenarios where automatic build is not enabled.

!!! note
    - You must trigger the first build in a Ballerina component manually to ensure that Choreo applies the required configurations to the development environment. You can enable automatic builds subsequently.
    - Choreo automatically checks the configurable defined in your source code against the configurable values applied in an environment. Choreo requests the configurable values on deployment and promotion. If you have changed the configurables in your Ballerina component, auto-build pipelines can fail as a precaution to avoid a component crash at runtime due to missing configurables.
    - The configurable verifying capability is only available for Ballerina components. For Dockerfile-based components, ensure to manage and update the configurations and secrets in environments ahead of time. You must also ensure backward compatibility between at least one release if you change the configurations.

### Set up area and initial deployment

In the deploy phase, Choreo uses a setup area to merge the Docker image with its environment-independent configurations. Choreo then deploys this composite to the environment. This is known as the initial deployment.

### Immutable deployments

Once Choreo deploys a component with configurations, the configurations become immutable. Any subsequent change results in a new deployment.

### Promote a component to a higher environment

Choreo builds a container once per GitHub commit and then promotes it to subsequent higher environments.

You can go to the **Deploy** page of a component and promote it manually across environments.

## Configurations

Choreo allows you to define both environment-independent configurations and environment-specific configurations.

### Environment-independent configurations

These configurations apply to all environments.

To change environment-independent configurations, go to the **Deploy** page of the component, make the necessary configuration changes via the **Set Up** card, and then trigger a new deployment to the initial environment. From there, you can proceed to promote the component to higher environments.

### Environment-specific configurations

These configurations apply to a particular environment.

To change environment-specific configurations, go to the **Deploy** page of the component, make the necessary configuration changes via the specific environment card, and trigger a new deployment.

To learn more about managing these configurations, see [Configuration Management](https://wso2.com/choreo/docs/choreo-concepts/configuration-management/).

## Task execution

The information on the **Execute** page is only applicable to scheduled and manual task components.

To track and monitor executions associated with a deployed scheduled task or manual task, go to the left navigation menu and click **Execute**.

You can view current and historic execution details along with a quick snapshot of recent activity via the total count of executions within the last 30 days. For each execution, you can view vital details such as the unique execution ID, the time it was triggered, and relevant revision information. Furthermore, you can dive deeper into the details by clicking on a specific execution to access its associated logs. This information enhances transparency, troubleshooting capabilities, and overall execution management, allowing you to easily monitor and analyze workflows.

## Zero-downtime deployments

Choreo performs rolling updates to ensure zero downtime between deployments and promotions.

A new build undergoes a health check before traffic is switched to it from the current build.

If you configure the necessary health checks for a component, it can prevent deploying and promoting unhealthy versions of a component.
