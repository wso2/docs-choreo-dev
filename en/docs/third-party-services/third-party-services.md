# Integrate and Manage Third Party Services

## Overview

Third-party services are external applications, platforms, or APIs that integrate with your primary system to enhance its functionality, extend its capabilities, or provide specialized features. These services are developed and maintained by separate entities, offering expertise in specific domains that may not be core to your main application.

Choreo allows you to connect to external services, such as APIs, applications, and other resources, to integrate them with your applications. This enables you to leverage the functionality and data of these external systems within your Service.

In order to connect to a third-party service, you need to register it with Choreo. The registered services will be discoverable via the Internal Marketplace, which will allow you to search, browse, and select the services you want to integrate with your services.

The required third party service can be consumed via a Connection.

## Register a Third Party Service

A third party service can be registered at,

1. the Organization level : Use this option if you want to share the service across all the projects in the organization.
2. the Project level : Use this option if you want to restrict the service usage for components of a specific project.

Following service types are supported:

- REST APIs
- GraphQL APIs
- Asynchronous APIs
- SOAP
- gRPC

Follow the steps below to register a third party service:

### Prerequisites

Before registering a third party service, make sure that you have obtained the required parameters for the third party service. The required parameters may include the URL, authentication credentials, and other configurations required to access the service. These parameters are specific to each third party service and are typically provided by the service provider.

### Step 1: Provide the General Details of the Service.

1. Sign in to the Choreo Console at https://console.choreo.dev/.
2. From the Organization home page, click on the project which you want to register the third party service. (Skip this step if you want to register the service at the Organization level.)
3. From the left navigation menu, click **Dependencies** and then **Third Party Services**.
4. Click **Register** button in the Third Party Services page.
5. Provide the service name, version, and summary.
6. Upload the api definition file (.yaml) of the third party service.
7. Verify the service type. 
8. Click **Define Endpoints** button to move into the next step.


### Step 2: Define Service Endpoints

1. In the Define New Endpoint section, provide a name for the endpoint.
2. Provide the URL of the third party service.
3. In the Additional Parameters section, define the other parameters that are required to access the third party service. e.g. API Key, Authorization Token, etc. If a parameter should be kept secret, check the **Secret** checkbox.

    !!! info
        The same parameter names will be shared will all the endpoints of the service.

4. Select the Allowed Environments for the endpoint. This will determine the choreo environments where the endpoint can be used.
5. Click **OK** button to add the endpoint.
6. If there are multiple endpoints, click **Add Endpoint** button to add more endpoints.
7. Click **Register** button to register the third party service.

!!! info
    When all the values for the additional parameters are provided at least for one endpoint, the service will be automatically added to the marketplace. Otherwise, you will have to manually add the service to the marketplace after providing the values for the endpoints.

## Manage Third Party Services

The registered third party services will be listed in the **Dependencies > Third Party Services** page.

To view the details of a third party service, click on the service from the list.

### Viewing Service Details

The service details page will have the following information:

- General Details: Service name, summary, overview , and labels.

- Service Definition: View/ edit the service definition. This will show the api definition of the third party service. The swagger ui will be displayed for REST APIs and the text content will be displayed for other service types. To edit the definition, click the **Upload** button and select the new definition file.

- Endpoints: Add/ modify or delete endpoints.


### Add the service to the Internal Marketplace

Click on the **Add to Marketplace** button to add the service to the Internal Marketplace.

!!! info
    The service can be added to the Internal Marketplace only if all the required parameters are provided for at least one endpoint.

### Remove the service from the Internal Marketplace

If the service should be removed from the Internal Marketplace, click on the **Remove from Marketplace** button.

This will remove the service from the Internal Marketplace and will not be discoverable or consumable via a Connection.
The existing connections will continue to work as before.

## Discovering Third Party Services

The reistered third party services can be discovered via the Internal Marketplace.

## Consuming a Third Party Service

A third party service can be consumed via a Connection. For information on consuming a third party service, see [Create a Connection to a Third Party Service](../develop-components/sharing-and-reusing/create-a-connection.md#create-a-connection-to-a-third-party-service).