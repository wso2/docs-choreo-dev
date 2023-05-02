# Builds and Deployments

Choreo offers a streamlined continuous build and deployment experience to quickly and efficiently deploy applications and services across multiple environments.

Environments are created for each project where all components in the project share the environments. Environments are isolated deployment areas where network and resource access is restricted between them (i.e., a service in one environment cannot communicate or reach a service deployed in another environment).
 - The cloud data plane provides two environments (i.e., development and production) by default. 
 - If you are in a private data plane organization, you can have customizable environments. 

Choreo takes a *build once and promote* strategy to manage components across multiple environments. An application is only built once (per commit if auto-build on commit is enabled - or based on the commit you select when you build manually) and then promoted into subsequent environments. Lower, non-production environments like *development* allows you to test changes before promoting a selected build into a production environment.

Configurations and secrets are maintained at an environment level and are injected at runtime into components. This ensures a strict separation of environment-specific configurations from source code (configurations vary across environments, but the code and the built container does not). Configurations and secrets include values such as, 

 - Resource credentials to a database, cache, and other backing services.
 - Credentials to external cloud services such as Amazon S3 or an external API.

All configurations and secrets are encrypted at rest and in transit and stored in a secure vault. (In your own infrastructure in private data planes).

## Build pipelines

Choreo’s auto-generated build pipelines slightly differ based on the ‘type’ of the component that you create but at a high-level, all build pipelines 
Build a container image either from the provided source code or from a given Dockerfile.
Run security and vulnerability scans (if applicable, depending on the component type).
This container image is then pushed to a container registry (to a registry that you own in private data planes and to a Choreo-managed registry on the cloud data plane).
Update service endpoints and API specifications from the provided repository (if applicable)
Deploy the newly built container to the development environment (or the first environment in your environment promotion order).

### Triggering a build

There are two ways to trigger a new build in Choreo.

- Manually build and deploy
  Select a commit by clicking on the first card in the ‘Build Area’ (the latest commit is always selected by default) and click ‘Deploy Manually’. This will trigger a manual build on demand.


- Auto deploy on GitHub commit
  Enable the toggle named ‘Auto Deploy on Commit’ under the ‘Build Area’. Choreo will automatically create the necessary webhooks on your connected git repository to enable this feature.

Once you commit (or merge a pull request) to the chosen branch in your development track, Choreo will automatically trigger a build pipeline and deploy it to your development environment.

!!! note
    - You will need to trigger the first build in a Ballerina component manually to ensure that the required configurations are applied to the development environment. Auto builds can be enabled afterwards.
    - Choreo automatically checks the ‘configurables’ defined in your source code against the configurable values you have provided to an environment. The configurable values will be requested on deployment and promotion. If you have changed the configurables in your Ballerina component, auto build pipelines will fail to ensure that the component does not crash at runtime due to missing configurables.
    - The configurable checking feature is only available for Ballerina components. For Dockerfile-based components, you are responsible for managing and updating configs and secrets in environments ahead of time - and ensuring backward compatibility between at least one release when changing the required configs for your application, Choreo cannot perform this check for you. See: Managing configurations and secrets

### Build history

The build history can be found at the bottom of the ‘Build Area’.
 
You can click on a build to open the build console with the build steps and logs.


## Promotions

Choreo builds a container once (per git commit) and then promotes it to subsequent (higher) environments. 

Promotions can be done manually (automatic release management will be available soon) by clicking on the ‘Promote’ button in an environment, which will trigger the promotion to the next environment in the promotion order.


## Zero-downtime deployments

Choreo performs a rolling update that can ensure zero downtime between deployments and promotions when configured correctly.

A new version (i.e., a build) of your component is brought up in the environment and checked if it’s healthy before sending traffic over to the new build from the previous (currently running) build. If you have correctly configured health checks for the component, an unhealthy version will not be deployed or promoted - the existing build will continue to run.
