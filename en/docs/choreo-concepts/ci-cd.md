# CI/CD

Choreo provides a streamlined continuous integration and continuous-development experience to deploy applications and services efficiently across multiple environments.

Choreo created environments for each project. All components in a project share environments. An environment is an isolated deployment area with restricted network and resource access. Services deployed in one particular environment cannot communicate with or reach services deployed in another.

The Choreo cloud data plane provides two default environments (i.e., development and production). However, if you are in a private data plane organization, you can customize the environments and have multiple based on your requirements. 

Choreo adopts a *Build Once, Deploy Many* strategy to manage components across multiple environments. An application is built only once (i.e., per commit if auto-build on commit is enabled or based on the selected commit during a manual build), and then it is promoted to subsequent environments. Lower, non-production environments such as development allow you to test changes before promoting a selected build into a production environment.

Choreo injects configurations and secrets you maintain at the environment level into components at runtime. This ensures a strict separation of environment-specific configurations from source code. Although configurations can vary across environments, the code and the built container do not change. Configurations and secrets include values such as: 

 - Resource credentials to a database, cache, or other backing services.
 - Credentials to external cloud services such as Amazon S3 or an external API.

All configurations and secrets are encrypted at rest and in transit and stored in a secure vault. In a private data plane organization, you can store configurations and secrets in your infrastructure.

## Build

Choreo auto-generated build pipelines can slightly differ depending on the component type that you create. However, at a high level, all build- pipelines work as follows:

- Builds a container image either from the provided source code or from a given Dockerfile for a specific commit.
- Runs security and vulnerability scans if applicable, depending on the component type.
- Pushes the container image to a container registry. In the cloud data plane, Choreo pushes the image to a Choreo-managed registry. In a private data plane organization, Choreo pushes it to a registry that you own.
- Updates service endpoints and API specifications from the provided repository if applicable. 

### Repeatable Builds

Choreo can replicate builds from an identical code version (Git commit). This means that multiple builds initiated from the same Git commit will generate Docker images with the same behavior.

!!! note
    In the event of multiple builds from the same code version, Choreo will preserve only the most recent version of the Docker image created from that particular code version.

### Triggering a build

In the **Build** view, click on ** Build**, select a commit, and build an image. 

### Build Logs

You can view the build logs for a particular build on the **Build** page.
 
You can click on **View Details** for a build to open its details, including build steps and logs in the build console.

## Deploy

Once you build an image in Choreo, you can deploy it many times through the **Deploy** page. You can deploy an image in the following two approaches: 

- Manually deploy: In the **Deploy** view, go to **Build Area** card and click **Deploy Manually**. This triggers a manual build on demand.

- Automatically deploy on GitHub commit: In the **Build** page, under **Builds** if you click the **Auto Deploy** toggle, you enable auto deployment on GitHub commit. Choreo creates the necessary webhooks on your connected GitHub repository to enable automatic deployment. When you commit or merge a pull request to a branch in your GitHub repository, Choreo automatically triggers a build pipeline and deploys it to your development environment.


!!! note
    - You must trigger the first build in a Ballerina component manually to ensure that Choreo applies the required configurations to the development environment. You can enable automatic builds subsequently.
    - Choreo automatically checks the configurable defined in your source code against the configurable values applied in an environment.  Choreo requests the configurable values on deployment and promotion. If you have changed the configurables in your Ballerina component, auto-build pipelines can fail as a precaution to avoid a component crash at runtime due to missing configurables.
    - The configurable verifying capability is only available for Ballerina components. For Dockerfile-based components, ensure to manage and update the configurations and secrets in environments ahead of time. You must also ensure backward compatibility between at least one release if you change the configurations.


### Set up Area and Initial Deployment

In the deployment phase, Choreo utilizes an allocated setup area (formerly referred to as the build area). Here, Choreo merges the constructed Docker image with its environment-independent configurations. Choreo then deploys this composite to the environment. This is referred to as the initial deployment. 

### Immutable Deployments

Once Choreo deploys a component with configurations, the configurations become immutable. Any subsequent changes will result in a new deployment.

### Promoting to higher environments

Choreo builds a container once per GitHub commit and then promotes it to subsequent (higher) environments. 

As of now, you can go to the **Deploy** page of a component and promote a component manually across environments.

## Configurations

Choreo allows you to define environment-independent configurations and environment-dependent configurations.

### Environment-independent configurations

Environment-independent configurations apply to all environments. To change environment-independent configurations, go to the setup area, make the necessary adjustments, and trigger a new deployment to the initial environment. From there, you can proceed with the promotion process to higher environments.

### Environment-specific configurations

Environment-dependent configurations apply to a particular environment. To change the environment-specific configurations, you directly change them in the targeted environment and trigger a new deployment.

To learn more about managing these configurations, see [Configuration Management](https://wso2.com/choreo/docs/choreo-concepts/configuration-management/).

### Task Execution

Users can easily access information about currently running executions as well as a detailed history of previously executed processes. Additionally, the system offers a quick snapshot of recent activity by providing the total count of executions within the last 30 days. Each execution is presented with vital details, including a unique Execution ID, the time it was triggered, and relevant revision information. Furthermore, users can dive deeper into the details by clicking on a specific execution to access its associated logs. This feature-rich system enhances transparency, troubleshooting capabilities, and overall execution management, making it a valuable tool for monitoring and analyzing workflows.


## Zero-downtime deployments

Choreo performs a rolling update that can ensure zero downtime between deployments and promotions when configured correctly.

A new version (i.e., a build) of your component is brought up in the environment for a health check before sending traffic over to the new build from the current build that is running. If you have configured the necessary health checks for a component, it can prevent deploying and promoting an unhealthy version of a component.
