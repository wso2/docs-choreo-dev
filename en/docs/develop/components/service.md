# Service Components

Explore how you can design, develop, manage, and observe services in Choreo to implement your business API strategy.

## What is a service component?

A service component in Choreo is a type of component that can be used to write web services and expose APIs as REST APIs, GraphQL APIs, or gRPC services. It is a fundamental building block for creating cloud-native applications in Choreo.

Service components can be used to encapsulate business logic and provide a endpoints (standardized interfaces) for communication with other components or systems. They can be deployed and scaled independently, making them highly flexible and adaptable to changing workloads.

With the help of the service component, developers can quickly create APIs and microservices, making it easier to implement and manage complex software systems. Service components can also be integrated with other Choreo components, such as message processors, connectors, and data sources, to create powerful, end-to-end solutions.

## What are endpoints in service components?

Endpoints in service components are the access points through which other components or systems can interact with the functionalities provided by a service component. Endpoints represent the specific locations where the service component can be reached and provide a standardized interface for communication with the service.

Endpoints in Choreo are defined by a combination of port binding, protocol, endpoint name, network visibility, endpoint schema and additional protocol related fields. Following table provides details about each field

| Field | Description |
| ------- | ------- |
| name | A unique identifier for the endpoint within the service component. |
| port | The network port on which the endpoint is accessible. |
| protocol | The communication protocol used by the endpoint (e.g., HTTP, HTTPS, gRPC, etc.). |
| Network Visibility | The level of visibility granted to the endpoint. Determines the level of visibility granted to the endpoint, with the following options: <ul><li>Project: the endpoint can only be accessed by other components within the same project.</li><li>Organization: the endpoint can be accessed by any component within the same organization, but not by components outside of the organization.</li><li>Public: the endpoint can be accessed by any component, regardless of its location or organization.</li></ul> |
| Schema | Defines the structure and format of the data that is exchanged through the endpoint. |
| Context (HTTP and GraphQL only) | A path prefix that is added to the URL of the endpoint for routing purposes. |

## Configuring endpoints

When building a service component with a Dockerfile build preset, you can configure the endpoint details using an endpoints.yaml configuration file. This file needs to be placed in the .choreo directory at the root of the build context path, and committed to the source repository.

The endpoints.yaml file has a specific structure, and contains the following details:

- name: A unique name for the endpoint, which will be used when generating the managed API.
- port: The numeric port value that gets exposed via this endpoint.
- type: The type of traffic this endpoint is accepting, such as REST, GraphQL, or gRPC.
- visibility (optional): The network level visibility of this endpoint, which defaults to "Project" if not specified. Accepted values are "Project", "Organization", or "Public".
- context (optional): The context (base path) of the API that is exposed via this endpoint. This field is mandatory if the endpoint type is set to REST or GraphQL.
- schemaFilePath (optional): The path to the schema definition file, which defaults to the wild card route if not provided. This field is only applicable to REST endpoint types, and should be a relative path to the Docker context.

> **Note** that when building a service component with Ballerina build preset, the endpoint details are automatically detected by Choreo and there is no need to provide an endpoints.yaml configuration file.

### Sample endpoints.yaml

File location:
```bash
<docker-build-context>/.choreo/endpoints.yaml
```

File Content:
```yaml
# +required Version of the endpoint configuration YAML
version: 0.1

# +required List of endpoints to create
endpoints:
  # +required Unique name for the endpoint. (This name will be used when generating the managed API)
- name: Greeting 9090
  # +required Numeric port value that gets exposed via this endpoint
  port: 9090
  # +required Type of the traffic this endpoint is accepting. Example: REST, GraphQL, etc.
  # Allowed values: REST, GraphQL, GRPC
  type: REST
  # +optional Network level visibility of this endpoint. Defaults to Project
  # Accepted values: Project|Organization|Public.
  visibility: Project
  # +optional Context (base path) of the API that is exposed via this endpoint.
  # This is mandatory if the endpoint type is set to REST or GraphQL.
  context: /greeting
  # +optional Path to the schema definition file. Defaults to wild card route if not provided
  # This is only applicable to REST endpoint types.
  # The path should be relative to the docker context.
  schemaFilePath: greeting_openapi.yaml
```

## exposing endpoint as managed APIs

If you want to expose an endpoint as a managed API in Choreo, you need to set the network visibility to either Organization or Public. This allows the endpoint to be exposed through the Choreo API Gateway, which provides a number of benefits, including:

- **Full Lifecycle API Management**: Exposing your endpoint through the Choreo API Gateway gives you access to full lifecycle API management features, including design, testing, deployment, and maintenance. This allows you to create high-quality APIs that meet the needs of your organization and your customers.

- **API Management Features**: In addition to full lifecycle API management, exposing your endpoint through the Choreo API Gateway gives you access to a range of API management features, including analytics, monitoring, and security policies. This allows you to monitor and control the usage of your endpoint, and ensure that it meets your organization's performance and security requirements.

- **Internal or External Gateway**: Depending on the network visibility of your endpoint, it will be exposed through either the internal or external gateway of Choreo API Gateway. Endpoints with Organization visibility will be exposed through the internal gateway, while endpoints with Public visibility will be exposed through the external gateway. This allows you to choose the appropriate level of access control for your API, based on the intended audience.

### To expose an endpoint as a managed API, follow these steps:

> NOTE: This feature is not available for gRPC endpoints.

1. Create a service component with the endpoint that you want to expose.
2. Set the network visibility of the endpoint to either Organization or Public.
3. Deploy the service component navigating to the deploy tab.
4. Go to manage then lifecycle section and select the endpoint from the drop down
5. Publish the API
4. Configure the API management settings, such as security policies and access controls, as required using the usage plans, permissions, and settings tabs.

Once the service component is deployed, the endpoint will be exposed as a managed API through the Choreo API Gateway. You can then use the full lifecycle API management features provided by Choreo to design, test, deploy, and maintain your API, as well as monitor and manage it using the API management features.
