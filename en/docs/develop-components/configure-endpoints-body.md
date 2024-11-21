
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

### Configure endpoints with buildpacks (except Ballerina)

When you build a service component using any other buildpacks(Java, Python, NodeJS, Ruby, PHP, Go, Dockerfile, etc) other than Ballerina and WSO2 MI, you can configure the endpoint details with the `endpoints.yaml` configuration file. You must place this file inside the `.choreo` directory at the build context path and commit it to the source repository.

See [Understanding the endpoints.yaml file](#learn-the-endpointsyaml-file) to learn about the `endpoints.yaml` file.

### Configure endpoints with the Ballerina buildpack

When you create a service component with the `Ballerina buildpack`, Choreo automatically detects the endpoint details for REST APIs. You can override the auto-generated endpoint configuration by providing the `endpoints.yaml` file in the source directory.

!!! note
    Automatic endpoint generation is not supported for dynamic endpoint parameters such as variable ports. Therefore, you must use an `endpoint.yaml` file to define dynamic endpoint parameters.

See [Understanding the endpoints.yaml file](#learn-the-endpointsyaml-file) to learn about the `endpoints.yaml` file.

### Learn the endpoints.yaml file

The `endpoints.yaml` file has a specific structure and contains the following details:

| Field                | Required     | Description                                                                      |
|----------------------|--------------|----------------------------------------------------------------------------------|
| **version**          | Required     | The version of the `endpoints.yaml` file.                                           |
| **name**             | Required     | A unique name for the endpoint, which Choreo will use to generate the managed API.|
| **port**             | Required     | The numeric port value that gets exposed via this endpoint.                      |
| **type**             | Required     | The type of traffic this endpoint is accepting, such as `REST`, `GraphQL`, `gRPC`, `UDP`or `TCP`. Currently, the MI preset supports only the `REST` type.                                         |
| **networkVisibility**| Required     | The network level visibility of this endpoint, which defaults to `Public` if not specified. Accepted values are `Project`, `Organization`, or `Public`.|
| **context**          | Required     | The context (base path) of the API that Choreo exposes via this endpoint.        |
| **schemaFilePath**   | Required     | The swagger definition file path. Defaults to the wildcard route if not provided. This field should be a relative path to the project path when using the **Java**, **Python**, **NodeJS**, **Go**, **PHP**, **Ruby**, and **WSO2 MI** buildpacks. For REST endpoint types, when using the **Ballerina** or **Dockerfile** buildpack, this field should be a relative path to the component root or Docker context.|

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
  # +optional Network level visibility of this endpoint. Defaults to Public
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

### Apply advanced component connection configurations

The `component-config.yaml` file extends the capabilities of `endpoints.yaml` by introducing enhancements that allow you to apply advanced inbound and outbound connection configurations.

!!! note "Beta release"
      - The current version of the `component-config.yaml` file is considered stable. However, it is important to note that the configuration schema may undergo changes and improvements based on user feedback. 
      - Support for the current schema will remain even when new versions are introduced.

The `component-config.yaml` file complements and enhances the existing endpoint configuration process. It allows you to define how your service's endpoints (inbound connections) are exposed and how your service connects to external services or components (outbound connections).

- **Inbound configurations:** This configuration section is for you to define inbound connections, Similar to `endpoints.yaml`, you can define how your service endpoints are exposed. It aligns seamlessly with the existing endpoint schema structure.

- **Outbound configurations:** This configuration section is for you to specify outbound connection details, including service connections. The Choreo Internal Marketplace facilitates creating connections with existing services. To learn more about Choreo Marketplace, see [Choreo Marketplace](https://wso2.com/choreo/docs/choreo-concepts/choreo-marketplace/#choreo-marketplace).

!!! note
    - If both `component-config.yaml` and `endpoints.yaml` are defined in the `.choreo` path, the `component-config.yaml` file takes priority.
    - Outbound connections are not supported for deprecated components and WSO2 MI buildpack components.

### Learn the `component-config.yaml` file

The `component-config.yaml` file has a specific structure and contains the following details:

| Field                | Required     | Description                                                                           |
|----------------------|--------------|---------------------------------------------------------------------------------------|
| **apiVersion**       | Required     | The version of the `component-config.yaml` file defaults to `core.choreo.dev/v1beta1`.|
| **kind**             | Required     | The resource type of the file defaults to `ComponentConfig`.                          |
| **spec.inbound**     | Optional     | The list of inbound connection configurations.                                        |
| **spec.outbound**    | Optional     | The list of outbound connection configurations.                                       |


#### Inbound connection configurations (`spec.inbound`)

In the `spec.inbound` configuration section, you can specify endpoints to set up inbound connections. To specify endpoints, you can follow the existing endpoints schema structure. For details on the endpoints schema structure, see the [endpoints schema documentation](#learn-the-endpointsyaml-file).

#### Outbound connection configurations (`spec.outbound`)

In the `spec.outbound` section, you can define `serviceReferences`. To define `serviceReferences`, you can use the service references generated in the Internal Marketplace when creating a service connection. To copy the [outbound connection configurations](https://wso2.com/choreo/docs/develop-components/sharing-and-reusing-services/#sharing-and-reusing-services), see the inline developer guide that is available when you create a connection.

The `serviceReferences` schema has a specific structure and contains the following details:

| Field                | Required     | Description                                                                      |
|----------------------|--------------|----------------------------------------------------------------------------------|
| **name**             | Required     | A unique name for the service reference.                                         |
| **connectionConfig** | Required     | A unique name for the connection instance.                                       |
| **env**              | Optional     | The list of environment variable mappings that get injected into the container.  |
| **env.from**         | Required     | The key name of the connection configuration.                                    |
| **env.to**           | Required     | The environment variable that gets injected into the container.                  |

!!! note
    Choreo automatically generates outbound connection configurations upon the creation of a connection within the internal marketplace. The properties such as **name**, **connectionConfig**, and **env.from** are automatically generated. However, you must manually set the **env.to** value.

#### Sample component-config.yaml

**File location**:

```bash
<docker-build-context-path>/.choreo/component-config.yaml
```

!!! note
    - For components built using the **Ballerina** buildpack, you must replace `docker-build-context-path` with the `component-root`. 
    For example, `<component-root>/.choreo/component-config.yaml`.
    - For components built using the **WSO2 MI** buildpack, you must replace `docker-build-context-path` with the `<Project Path>`. 
    For example, `<Project Path>/.choreo/component-config.yaml`.

**File content**:

```yaml
apiVersion: core.choreo.dev/v1beta1
kind: ComponentConfig
spec:
  # +optional Incoming connection details for the component (AKA endpoints).
  inbound:
    # +required Unique name for the endpoint. (This name will be used when generating the managed API)
    - name: Greeting Service
      # +required Numeric port value that gets exposed via the endpoint
      port: 9090
      # +required Type of traffic that the endpoint is accepting. For example: REST, GraphQL, etc.
      # Allowed values: REST, GraphQL, GRPC, TCP, UDP.
      type: REST
      # +optional Network level visibility of the endpoint. Defaults to Public
      # Accepted values: Project|Organization|Public.
      networkVisibility: Public
      # +optional Context (base path) of the API that gets exposed via the endpoint.
      # This is mandatory if the endpoint type is set to REST or GraphQL.
      context: /greeting
      # +optional The path to the schema definition file. Defaults to wildcard route if not specified.
      # This is only applicable to REST endpoint types.
      # The path should be relative to the Docker context.
      schemaFilePath: greeting_openapi.yaml
  # +optional Outgoing connection details for the component.
  outbound:
    # +optional Defines the service references from the Internal Marketplace.
    serviceReferences:
      # +required Name of the service reference.
      - name: choreo:///apifirst/mttm/mmvhxd/ad088/v1.0/PUBLIC
        # +required Name of the connection instance.
        connectionConfig: 19d2648b-d29c-4452-afdd-1b9311e81412
        # +optional Environment variables injected to the component for connection configuration.
        env:
          # +required Key name of the connection configuration.
          - from: ServiceURL
            # +required Environment variable injected to the container.
            to: SERVICE_URL
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

## Understand the default component URL

The default URL of a component corresponds to the default endpoint of the component and is structured as follows:

`<domain>/<project-name>/<component-name>`

This URL does not include the default endpoint name. For all other endpoints, the URL structure includes the endpoint name, as follows:

`<domain>/<project-name>/<component-name>/<endpoint-name>`

If a component has multiple endpoints, Choreo allows you to change the endpoint corresponding to the default component URL. For a component with a single endpoint, the default URL automatically corresponds to that endpoint.

### Change the default endpoint of a component

To change the default endpoint of a component, follow the steps given below:

!!! note
     - You cannot change the default endpoint if it has associated published APIs. You must go to the **Lifecycle** page and unpublish the relevant APIs before updating the default endpoint. For instructions on unpublishing an API, see [Lifecycle Management](https://wso2.com/choreo/docs/api-management/lifecycle-management/).
     - Choreo does not allow you to change the default endpoint of a component if you create multiple deployment tracks within its current major version, or promote the component beyond its initial environment.

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the **Component Listing** pane, click on the component for which you want to change the default endpoint.
3. In the left navigation menu, click **Deploy**.
4. On the **Deploy** page, go to the **Set Up** card and click **Configure & Deploy**.
5. In the **Environment Configurations** pane that opens, click **Next**.
6. In the **File Mount** pane that opens, click **Next**.
7. In the **Endpoint Details** pane that opens, click the **Default Endpoint** list, select the endpoint you want to set as the default endpoint, and then click **Update**.
8. Click **Deploy**. This deploys the component with the selected endpoint as the default, and the default URL will now correspond to this endpoint.  
