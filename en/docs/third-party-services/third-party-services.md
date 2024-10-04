# Integrate and Manage Third-Party Services 

## Overview

Third-party services are external applications, platforms, or APIs that integrate with your primary system to enhance its functionality, extend its capabilities, or provide specialized features. These services, developed and maintained by separate entities, offer expertise in specific domains that may not be core to your main application.

Choreo enables seamless integration with external services, such as APIs, applications, and other resources, allowing you to leverage their functionality and data within your own applications. To connect to a third-party service, you must first register it with Choreo. Once registered, these services become discoverable via the Internal Marketplace, where you can search, browse, and select the services you want to integrate with your components.

Third-party services are consumed through Connections, which provide a standardized way to interact with external resources.

## Register a Third-Party Service

You can register a third-party service at two levels:

1. Organization level: This option allows you to share the service across all projects in the organization.
2. Project level: This option restricts the service usage to components within a specific project.

Choreo supports the following service types:

- REST APIs
- GraphQL APIs
- Asynchronous APIs
- SOAP
- gRPC

Follow these steps to register a third-party service:

### Prerequisites

Before registering a third-party service, ensure you have obtained the necessary parameters for the service. These may include the URL, authentication credentials, and other configurations required to access the service. These parameters are specific to each third-party service and are typically provided by the service provider.

### Step 1: Provide General Service Details

1. Sign in to the Choreo Console at https://console.choreo.dev/.
2. From the Organization home page, click on the project where you want to register the third-party service. (Skip this step if registering at the Organization level.)
3. In the left navigation menu, click **Dependencies**, then **Third Party Services**.
4. Click the **Register** button on the Third Party Services page.
5. Enter the service name, version, and summary.
6. Upload the API definition file (.yaml) for the third-party service.
7. Verify the service type.
8. Click the **Define Endpoints** button to proceed to the next step.

### Step 2: Define Service Endpoints

1. In the Define New Endpoint section, provide a name for the endpoint.
2. Enter the URL of the third-party service.
3. In the Additional Parameters section, define any other parameters required to access the third-party service (e.g., API Key, Authorization Token). Check the **Secret** checkbox for parameters that should be kept confidential.

    !!! info
        Parameter names are shared across all endpoints of the service.

4. Select the **Allowed Environments** for the endpoint. This determines which Choreo environments can use the endpoint.
5. Click the **OK** button to add the endpoint.
6. For multiple endpoints, click the **Add Endpoint** button to add more.
7. Click the **Register** button to complete the registration process.

!!! info
    The service is automatically added to the marketplace when all required parameter values are provided for at least one endpoint. Otherwise, you'll need to manually add the service to the marketplace after providing the endpoint values.

## Manage Third-Party Services

Registered third-party services are listed on the **Dependencies > Third Party Services** page. Click on a service to view its details.

### Viewing Service Details

The service details page displays the following information:

- General Details: Service name, summary, overview, and labels.
- Service Definition: View or edit the service definition. For REST APIs, a Swagger UI is displayed; for other service types, the text content is shown. To edit the definition, click the **Upload** button and select a new definition file.
- Endpoints: Add, modify, or delete endpoints.

### Add the Service to the Internal Marketplace

Click the **Add to Marketplace** button to make the service available in the Internal Marketplace.

!!! info
    A service can only be added to the Internal Marketplace if all required parameters are provided for at least one endpoint.

### Remove the Service from the Internal Marketplace

To remove a service from the Internal Marketplace, click the **Remove from Marketplace** button. This action makes the service undiscoverable and unconsumable via a Connection, but existing connections will continue to function as before.

## Discovering Third-Party Services

Registered third-party services can be discovered through the Internal Marketplace.

## Consuming a Third-Party Service

Third-party services are consumed via Connections. For detailed information on consuming a third-party service, refer to [Create a Connection to a Third-Party Service](../develop-components/sharing-and-reusing/create-a-connection.md#create-a-connection-to-a-third-party-service).