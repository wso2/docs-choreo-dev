# Choreo Marketplace

The Choreo Marketplace promotes and facilitates reusing and sharing services. It allows you to share all the services deployed in Choreo.
You can easily browse and search available services within the Marketplace and refer to the service definitions, documentation, instructions on how you can use it, etc. 

![Internal Marketplace](../assets/img/choreo-concepts/marketplace/internal-marketplace.png){.cInlineImage-full}

## Discover services

The Marketplace includes all services deployed in your organization. This may be a large number of services. Therefore, effective discoverability is desirable.

You can use the search or apply various filter criteria to explore the services available.

### Search

The top search bar provides universal searching to find the services. It allows you to search for a text in the following search attributes:

- **Name**: The service name.
- **Label**: The service labels.
- **Content**: The service content: overview, summary, and documentation.
- **All**: All of the above criteria.

### Filter

The Choreo Marketplace provides a filtering capability through the left-hand side filter panel. It allows you to filter with the following filter attributes:

- **Type**: This filter enables you to categorize services based on their type, with two available options: "Internal" and "Third-party". "Internal" refers to services deployed within Choreo, while "Third-party" refers to services running externally to Choreo, independently added to the Marketplace.

- **Network Visibility**: This filter enables you to categorize services based on their network visibility level, with three choices: "Public," "Organization," and "Project".  "Public" filters services exposed publicly, "Organization" represents services exposed across the entire organization, and "Project" represents services exposed at the project level.


## Explore a service 

You can click on the service card to open the detailed view of the service. The detailed service page features the service name, summary, version, labels, and service icon as the header.

Choreo organizes the service content into four tabs. The four tabs contain information as follows: 

- **Overview**: Choreo displays the service overview provided by the service developer. If the service developer has not provided any content at service creation, this section will be disabled. The service developer can provide the overview content via the Manage → Marketplace section of the component.

- **API definition**: Includes the service's API definition, extracted from the user repository using the `component-config` file or `endpoints.yaml` file. 
If the user does not specify an API definition, this tab remains empty.

- **How to use**: Includes instructions on how to use the selected service. This includes instructions on [creating a connection](../develop-components/sharing-and-reusing-services.md#create-a-connection-to-a-service) and [consuming this service](../develop-components/sharing-and-reusing-services.md#consuming-a-service-through-a-connection). This section is accessible for any connections that you create.

- **Related documents**: Includes any additional content the user has provided as documents through the Manage -> Marketplace section of the component.

## Add a service to the Choreo Marketplace

You can add services to the Marketplace as Choreo services as follows: 

### Add a Choreo service

In Choreo, a service exposed through the platform is termed a Choreo service, with each service being identifiable by an endpoint within a Choreo service component. The Marketplace showcases a service for each endpoint within a service component.

Upon deployment to the initial environment, services get automatically added to the Marketplace. Choreo effortlessly collects essential details such as component name, endpoint name, description, and service definitions during this deployment, utilizing them to generate the corresponding service entries in the Marketplace.

The service name follows the convention of `component name - endpoint name`, while all other details remain unchanged.

## Edit services in the Choreo Marketplace

You can edit services in the Choreo Marketplace. During redeployment to any environment, Choreo automatically updates service definitions, visibility, and descriptions.
