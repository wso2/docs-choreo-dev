# Integrate and Manage Third-Party Services

Third-party services are external applications, platforms, or APIs you can integrate with your system to enhance its functionality, extend capabilities, or provide specialized features. These services are developed and maintained by external entities, providing expertise in specific domains outside your application's core focus.

The following topics walk you through the steps to seamlessly register, manage, and consume third-party services, adhering to API-first principles.

## Register a third-party service in Choreo

To use a third-party service in Choreo, you need to register it. Once registered, the service becomes available in the Internal Marketplace, allowing you to consume it via a Connection.

You can register a third-party service at two levels:

  - Organization level: This makes the service accessible from any project within the organization.
  - Project level: This restricts the service to a specific project.

Choreo allows you to register the following third-party service types:
  
  - REST APIs
  - GraphQL APIs
  - Asynchronous APIs
  - SOAP
  - gRPC

### Prerequisites

Before registering a third-party service, obtain the following details from the service provider:

 - API specification. For example, OpenAPI or GraphQL schema.
 - Service URL.
 - Other necessary parameters. For example, client credentials, API keys, etc.

To register a third-party service, follow these steps:

!!! note 
     Ensure you register the service at an appropriate level depending on the service usage. If the service must be shared among multiple projects, register it at the organization level. Otherwise, register it at the project level.

### Step 1: Provide basic details

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. Follow one of these steps depending on your requirement:
    - To register a third-party service at the organization level, go to the Choreo Console header and select your organization from the **Organization** list. 
    - To register a third-party service at the project level, go to the Choreo Console header and select your project from the **Project** list. 
3. In the left navigation menu, click **Dependencies** and then click **Third-Party Services**.
4. Click **+ Register**.
5. Provide details for the service:
    - Enter a **Name** and **Version**.
    - Upload the service definition file. This automatically detects the service type.
    - Verify the **Service Type**.
6. Click **Define Endpoints**.

Now you are ready to define endpoints for the service.

### Step 2: Define service endpoints

An endpoint is a set of parameters required to connect to a service. The required parameters are service URL, API key header, etc.
These parameters provide the necessary information for a client application to interact with the service.

To define service endpoints, follow these steps:

1. Under **Define New Endpoint**, 
    - Enter a **Name** for the endpoint.
    - Enter the **Endpoint URL**.

2. Under **Additional Parameters**, add any other parameters required to connect to the service. 
   For example, API key, authorization token, etc.

    !!! note 
         - If you want to keep a parameter confidential, select the **Secret** checkbox.
         - If there are multiple endpoints, the parameter names will be the same for all the endpoints.

3. Select the environments where the endpoint should be accessible.

    !!! note 
         Service consumers can create connections to the endpoint only from the selected environments.

4. Click **OK**.

5. If you want to add more endpoints, click **+ New Endpoint** and repeat steps 1 to 4.

6. Click **Register**.

When you define all required parameter values for at least one endpoint, the service will be automatically listed in the Internal Marketplace. Otherwise, you must [add the service to the Internal Marketplace](#add-a-third-party-service-to-the-internal-marketplace) after providing the endpoint values.

If you want to remove a third-party service from the Internal Marketplace, see [Remove a third-party service from the Internal Marketplace](#remove-a-third-party-service-from-the-internal-marketplace).
 
For details on managing third-party services, see [Manage third-party service](#manage-third-party-services).

## Discover third-party services

The third-party services you register are discoverable via the Internal Marketplace to consume via a Connection.

For details on consuming a third-party service via a connection, see [Create a Connection](../develop-components/sharing-and-reusing/create-a-connection.md#create-a-connection-to-an-external-service).

## Manage third-party services

When you create a third-party service, it gets listed in the **Third-Party Services** list. 

### View or update third-party service details

To view or update a third-party service, follow these steps.

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the left navigation menu, click **Dependencies** and then click **Third-Party Services**. This lists all the third-party services you have created.
3. Click on a service to view or update its details.
    - **General Details**: Displays service metadata. For example, the service name, overview, labels, etc. 
    - **Service Definition**: Displays the service definition. To update the definition, click **Upload** and select the new definition file.
    - **Endpoints**: Displays service endpoint details. You can add, modify, or delete service endpoints.

### Add a third-party service to the Internal Marketplace

!!! info
    To add a third-party service to the Internal Marketplace, you must provide all required parameters for at least one endpoint.

1. In the Choreo Console left navigation menu, click **Dependencies** and then click **Third-Party Services**.
2. Click on the service you want to add to the Internal Marketplace.
3. Click **Add to Marketplace**.

### Remove a third-party service from the Internal Marketplace

1. In the Choreo Console left navigation menu, click **Dependencies** and then click **Third-Party Services**.
2. Click on the service you want to remove from the Internal Marketplace.
3. Click **Remove from Marketplace**.

This removes the service from the Internal Marketplace. Therefore, the service will not be available to consume via a Connection. However, the connections created before removal will continue to work as expected.
