# Builds and Deployments

Choreo provides a streamlined continuous build and deployment experience to deploy applications and services efficiently across multiple environments.

In Choreo, environments are created for each project. All components in a project share environments. An environment is an isolated deployment area with restricted network and resource access. Services deployed in one environment cannot communicate with or reach services deployed in another environment.

The Choreo cloud data plane provides two default environments (i.e., development and production). However, if you are in a private data plane organization, you can customize the environments and have multiple environments based on your requirements. 

Choreo adopts a *build-once and promote* strategy to manage components across multiple environments. An application is built only once (i.e., per commit if auto-build on commit is enabled or based on the selected commit during a manual build), and then it is promoted to subsequent environments. Lower, non-production environments such as development allow you to test changes before promoting a selected build into a production environment.

Configurations and secrets are maintained at the environment level and are injected at runtime into components. This ensures a strict separation of environment-specific configurations from source code. Although configurations can vary across environments, the code and the built container do not change. Configurations and secrets include values such as: 

 - Resource credentials to a database, cache, or other backing services.
 - Credentials to external cloud services such as Amazon S3 or an external API.

All configurations and secrets are encrypted at rest and in transit and stored in a secure vault. If you are in a private data plane organization, configurations and secrets are stored in your own infrastructure.

## Build pipelines

Choreo auto-generated build pipelines can slightly differ depending on the component type that you create. However, at a high level, all build pipelines work as follows:

- Builds a container image either from the provided source code or from a given Dockerfile.
- Runs security and vulnerability scans if applicable, depending on the component type.
- Pushes the container image to a container registry. In the cloud data plane, the image is pushed to a Choreo-managed registry. In a private data plane organization, it is pushed to a registry that you own.
- Updates service endpoints and API specifications from the provided repository if applicable. 
-  Deploys the newly built container to the development environment or the first environment in your environment promotion order.

### Triggering a build

There are two approaches to trigger a new build in Choreo:

- Manually build and deploy: In the **Deploy** view, go to **Build Area** card and click **Deploy Manually**. This triggers a manual build on demand.

- Automatically deploy on GitHub commit: In the **Deploy** view, go to **Build Area** card and click the **Auto Deploy on Commit** toggle to enable the setting. Choreo creates the necessary webhooks on your connected GitHub repository to enable automatic deployment. When you commit or merge a pull request to a branch in your GitHub repository, Choreo automatically triggers a build pipeline and deploys it to your development environment.

!!! note
    - You must trigger the first build in a Ballerina component manually to ensure that the required configurations are applied to the development environment. You can enable automatic builds subsequently.
    - Choreo automatically checks the configurables defined in your source code against the configurable values applied in an environment. The configurable values are requested on deployment and promotion. If you have changed the configurables in your Ballerina component, auto-build pipelines can fail as a precaution to avoid a component crash at runtime due to missing configurables.
    - The configurable verifying capability is only available for Ballerina components. For Dockerfile-based components, you must make sure to manage and update the configurations and secrets in environments ahead of time. You must also ensure backward compatibility between at least one release if you change the configurations.

### Build history

The **Build Area** card in the **Deploy** view of a component displays its build history.
 
You can click on a build to open its details, including build steps and logs in the build console.


## Promotions

Choreo builds a container once per GitHub commit and then promotes it to subsequent (higher) environments. 

As of now, you can go to the **Deploy** view of a component and promote a component manually across environments.


## Zero-downtime deployments

Choreo performs a rolling update that can ensure zero downtime between deployments and promotions when configured correctly.

A new version (i.e., a build) of your component is brought up in the environment for a health check before sending traffic over to the new build from the current build that is running. If you have configured the necessary health checks for a component, it can prevent deploying and promoting an unhealthy version of a component.
