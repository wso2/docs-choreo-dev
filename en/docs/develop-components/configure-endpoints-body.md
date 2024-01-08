
Services and integrations are exposed to other services, integrations, or applications through endpoints. A service or an integration can expose multiple endpoints, each representing a unique entry point into the service. For example, a service may expose a REST API endpoint and a GraphQL endpoint, each providing different ways to interact with the service. Endpoints provide specific details for how the service or the integration can be consumed. For instance, the port number, protocol, and the schema such as open API specification (OAS) or GraphQL schema. By defining these details, endpoints make it possible for other services, integrations, and applications to discover and interact with the service in a standardized way.

Choreo defines endpoints by combining port binding, protocol, endpoint name, network visibility, endpoint schema, and additional protocol-related fields. The following table describes each attribute of an endpoint.

| Field | Description |
| ----- | ----------- |
| Name | A unique identifier for the endpoint within the service component. |
| Port | The network port on which the endpoint is accessible. |
| Type | The endpoint protocol. Supported protocols: REST, GraphQL, gRPC, UDP, and TCP. |
| Network Visibility | Determines the level of visibility of an endpoint. Possible values are: <ul><li>Project: Allows components within the same project to access the endpoint.</li><li>Organization: Allows any component within the same organization to access the endpoint but restricts access to components outside the organization.</li><li>Public: Allows any client to access the endpoint, regardless of location or organization.</li></ul> |
| Schema | Specifies the structure and format of the data exchanged through the endpoint. |
| Context (HTTP and GraphQL only) | A context path that you add to the endpoint's URL for routing purposes. |

## Configure endpoints

The method of defining endpoints depends on the buildpack. For buildpacks other than `Ballerina` and `WSO2 MI`, it is required to have an `endpoints.yaml` file in project root directory to create the Service component.

### Configure endpoints with the Ballerina buildpack

When you create a service component with the `Ballerina buildpack`, Choreo automatically detects the endpoint details for REST APIs. You can override the auto-generated endpoint configuration by providing the `endpoints.yaml` file in the source directory.

!!! note
    Automatic endpoint generation is not supported for dynamic endpoint parameters such as variable ports. Therefore, you must use an `endpoint.yaml` file to define dynamic endpoint parameters.

See [Understanding the endpoints.yaml file](#learn-the-endpointsyaml-file) to learn about the `endpoints.yaml` file.


### Configure endpoints with the WSO2 MI buildpack

WSO2 MI buildpack is where you can deploy integrations developed with WSO2 Micro Integrator as an API. In this preset, you have three different ways to define endpoints. Choreo gives priory to the definition of endpoints in the below-mentioned order. 

1. **Using endpoints.yaml file**
This is the most flexible method to define endpoints. You can configure the endpoint details with the `endpoints.yaml` configuration file. Place this file in the .choreo directory in the project path of the component. 
If the Micro Integrator project has inbound endpoints, you can expose them via different endpoints using the `endpoints.yaml`

    See [Understanding the endpoints.yaml file](#learn-the-endpointsyaml-file) to learn about the `endpoints.yaml` file.


2. **Auto generating endpoints**
If `endpoints.yaml` is not provided and if the source Micro Integrator project has APIs, Choreo scans the project and generates the API endpoints. If the project has few APIs, an endpoint will be generated for each API. The visibility of this auto-generated endpoint is set to `Project` by default. You can change the visibility in the deployment flow.

3. **Provide default endpoints**
If `endpoints.yaml` is not provided and if the source Micro Integrator project doesn't have APIs, Choreo generates a default endpoint which will expose the default micro integrator port (8290) with `Project` visibility and wildcard context.

### Configure endpoints with the other buildpacks

When you build a service component using the other buildpacks(Java, Python, NodeJS, Ruby, PHP, Go, Dockerfile, etc), you can configure the endpoint details with the `endpoints.yaml` configuration file. You must place this file inside the `.choreo` directory at the build context path and commit it to the source repository.

See [Understanding the endpoints.yaml file](#learn-the-endpointsyaml-file) to learn about the `endpoints.yaml` file.

### Learn the endpoints.yaml file

The `endpoints.yaml` file has a specific structure and contains the following details:

| Field                | Required     | Description                                                                      |
|----------------------|--------------|----------------------------------------------------------------------------------|
| **version**          | Required     | The version of the `endpoints.yaml` file.                                           |
| **name**             | Required     | A unique name for the endpoint, which Choreo will use to generate the managed API.|
| **port**             | Required     | The numeric port value that gets exposed via this endpoint.                      |
| **type**             | Required     | The type of traffic this endpoint is accepting, such as `REST`, `GraphQL`, `gRPC`, `UDP`or `TCP`. Currently, the MI preset supports only the `REST` type.                                         |
| **networkVisibility**| Required     | The network level visibility of this endpoint, which defaults to `Project` if not specified. Accepted values are `Project`, `Organization`, or `Public`.|
| **context**          | Required     | The context (base path) of the API that Choreo exposes via this endpoint.        |
| **schemaFilePath**   | Required     |  The swagger definition file path. Defaults to the wildcard route if not provided. This field should be a relative path to the project path when using the **Java**, **Python**, **NodeJS**, **Go**, **PHP**, **Ruby**, and **WSO2 MI** buildpacks. For REST endpoint types, when using the **Ballerina** or **Dockerfile** buildpack, this field should be a relative path to the component root or Docker context.|

#### Sample endpoints.yaml

**File location**:

```bash
<docker-build-context-path>/.choreo/endpoints.yaml
```

!!! note
    - For components built with Ballerina buildpack `docker-build-context-path` should be replaced with `component-root`. 
    For example: `<component-root>/.choreo/endpoints.yaml`

    - For components built with WSO2 MI buildpack `docker-build-context-path` should be replaced with `<Project Path>`. 
    For example: `<Project Path>/.choreo/endpoints.yaml`

**File content**:

```yaml
# +required Version of the endpoint configuration YAML
version: 0.1

# +required List of endpoints to create
endpoints:
  # +required Unique name for the endpoint. (This name will be used when generating the managed API)
- name: Greeting Service
  # +required Numeric port value that gets exposed via this endpoint
  port: 9090
  # +required Type of the traffic this endpoint is accepting. Example: REST, GraphQL, etc.
  # Allowed values: REST, GraphQL, GRPC, UDP, TCP
  type: REST
  # +optional Network level visibility of this endpoint. Defaults to Project
  # Accepted values: Project|Organization|Public.
  networkVisibility: Project
  # +optional Context (base path) of the API that is exposed via this endpoint.
  # This is mandatory if the endpoint type is set to REST or GraphQL.
  context: /greeting
  # +optional Path to the schema definition file. Defaults to wild card route if not provided
  # This is only applicable to REST endpoint types.
  # The path should be relative to the docker context.
  schemaFilePath: greeting_openapi.yaml
```

## Expose endpoints as managed APIs

Exposing endpoints as managed APIs is crucial to ensure secure and controlled access to the services being exposed. When a user wants to expose their written service to the outside world or to the organization at large, there is an inherent security risk involved. To mitigate this risk, the Choreo platform is built with an internal (access within the organization only) or external (publicly accessible) gateway that is protected with Choreo API management making the services secure by design.

!!! note
    This feature is not available for gRPC, UDP, and TCP endpoints.

If you want to expose an endpoint as a managed API in Choreo, you need to set the network visibility to either Organization or Public. This allows the endpoint to be exposed through the Choreo API Gateway, which provides a number of benefits, including:

* Expose APIs to external and internal consumers
* Full lifecycle API Management
* API throttling
* Secure APIs with industry-standard authorization flows
* API analytics and monitoring

Once you deploy the service component, Choreo will expose the endpoint as a managed API through the Choreo API Gateway. You can then use the full lifecycle API management features provided by Choreo to test, deploy, maintain, monitor, and manage your API using the API management features.
