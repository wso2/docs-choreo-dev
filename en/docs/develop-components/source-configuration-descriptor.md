Services and integrations are exposed to other services, integrations, or applications through endpoints. A service or an integration can expose multiple endpoints, each representing a unique entry point into the service. For example, a service may expose a REST API endpoint and a GraphQL endpoint, each providing different ways to interact with the service. Endpoints provide specific details for how the service or the integration can be consumed. For instance, the port number, protocol, and the schema such as open API specification (OAS) or GraphQL schema. By defining these details, endpoints make it possible for other services, integrations, and applications to discover and interact with the service in a standardized way.

!!! note
    - Choreo prioritizes configuration files in the following order: `component.yaml` takes precedence, followed by `component-config.yaml`, and lastly `endpoints.yaml`.
    - File schema is validated during the `Source Configuration Validation` step of the component build pipeline.

### Component Configuration Descriptor
#### component.yaml (Recommended)

The new component configuration descriptor `(component.yaml)` simplifies and enhances the configuration of components within Choreo, providing a cleaner and organized schema structure for `endpoints` and `dependencies` for better readability, reducing complexity, and adopting a descriptor-based approach. Versioned schema ensures backward compatibility providing a seamless transition.

#### What's new with `component.yaml`?

- **Multiple Network Visibilities**: Support for defining multiple network visibilities for endpoints, allowing fine-grained access control.
- **Display Name and Name (Unique Identifier)**: Ability to assign a display name and unique identifier to endpoints for better management.

The `component.yaml` file has a defined structure and includes the following elements:

| Field                | Required     | Description                                                             |
|----------------------|--------------|-------------------------------------------------------------------------|
| **schemaVersion**    | Required     | The version of the `component.yaml` file defaults to latest version.    |
| **endpoints**        | Optional     | The list of endpoint configurations.                                    |
| **dependencies**     | Optional     | The list of dependency configurations.                                  |

#### Endpoint configurations (`endpoints`)
In the `endpoints` configuration section, you can specify mulitple service endpoint configurations. Each endpoint must have a unique name and expected field as defined in the schema overview. Newly intoduces `endpoints.service` section is to define user service endpoint details.

!!! tip "Why have a unique name?"
       When defining multiple endpoints, the `endpoint.name` is appended to the Choreo-generated URL. Choosing a clear and descriptive name ensures that it is easily recognizable and readable within the endpoint URL.
       

| Field                | Required     | Description                                                                      |
|----------------------|--------------|----------------------------------------------------------------------------------|
| **name**             | Required     | A unique identifier for the endpoint within the service component.               |
| **displayName**      | Optional     | A display name for the endpoint.                                                 |
| **service**          | Required     | Defines the service details for the endpoint.                                    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**.basePath** | Required     | The base path of the API that Choreo exposes via this endpoint.                  |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**.port**     | Required     | The numeric port value that gets exposed via this endpoint.                      |
| **type**             | Required     | The type of traffic this endpoint is accepting, such as `REST`, `GraphQL`, `gRPC`, `WS`, `UDP` or `TCP`.|
| **networkVisibilities** | Required | The network-level visibilities of the endpoint (e.g., Project, Organization, Public). |
| **schemaFilePath** | Required | The swagger definition file path. Defaults to the wildcard route if not provided. This field should be a relative path to the project path when using the Java, Python, NodeJS, Go, PHP, Ruby, and WSO2 MI buildpacks. For REST endpoint types, when using the Ballerina or Dockerfile buildpack, this field should be a relative path to the component root or Docker context. |

#### Dependency configurations (`dependencies`)

In the `dependencies` configuration section, you can specify multiple service connection configurations under `dependencies.serviceReferences`. Use the service references generated in the Internal Marketplace when creating a service connection. To copy the [connection configurations](https://wso2.com/choreo/docs/develop-components/sharing-and-reusing/use-a-connection-in-your-service/), see the developer guide that is available when you create a connection.

he `serviceReferences` schema has a specific structure and contains the following details:

| Field                | Required     | Description                                                                      |
|----------------------|--------------|----------------------------------------------------------------------------------|
| **name**             | Required     | A unique name for the service reference.                                         |
| **connectionConfig** | Required     | A unique name for the connection instance.                                       |
| **env**              | Required     | The list of environment variable mappings that get injected into the container.  |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**.from**         | Required     | The key name of the connection configuration.                                    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;**.to**           | Required     | The environment variable that gets injected into the container.                  |

!!! note
    Choreo automatically generates connection configurations upon the creation of a connection within the internal marketplace. The properties such as **name**, **connectionConfig**, and **env.from** are automatically generated. However, you must manually set the **env.to** value.

**File location**:

```bash
<build-context-path>/.choreo/component.yaml
```

``` yaml
# +required The configuration file schema version
schemaVersion: 1.0

# +optional Incoming connection details for the component
endpoints:
  # +required Unique name for the endpoint.
  # This name will be used when generating the managed API
  - name: greeter-sample
    # +optional Display name for the endpoint.
    displayName: Go Greeter Sample
    # +required Service section has the user service endpoint details
    service:
      # +optional Base path of the API that gets exposed via the endpoint.
      # This is mandatory if the endpoint type is set to REST or GraphQL.
      basePath: /greeting-service
      # +required Numeric port value that gets exposed via the endpoint
      port: 9090
    # +required Type of traffic that the endpoint is accepting.
    # Allowed values: REST, GraphQL, GRPC, TCP, UDP.
    type: REST
    # +optional Network level visibilities of the endpoint.
    # Takes priority over networkVisibility if defined. 
    # Accepted values: Project|Organization|Public(Default).
    networkVisibilities: 
      - Public
      - Organization
    # +optional The path to the schema definition file.
    # Defaults to wildcard route if not specified.
    # This is only applicable to REST endpoint types.
    # The path should be relative to the docker context.
    schemaFilePath: openapi.yaml
  
  # +optional Outgoing connection details for the component.
  dependencies:
    # +optional Defines the service references from the Internal Marketplace.
    serviceReferences:
      # +required Name of the service reference.
      - name: choreo:///apifirst/mttm/mmvhxd/ad088/v1.0/PUBLIC
        # +required Name of the connection instance.
        connectionConfig: 19d2648b-d29c-4452-afdd-1b9311e81412
        # +required Environment variables injected to the component for connection configuration.
        env:
          # +required Key name of the connection configuration.
          - from: ServiceURL
            # +required Environment variable injected to the container.
            to: SERVICE_URL
```

### Migration Guide

For users currently using component-config.yaml or endpoints.yaml, transitioning to component.yaml is straightforward. The new schema retains backward compatibility while improving clarity. All legacy configurations will continue to work (will be depreceated eventually), but the use of component.yaml is strongly encouraged for new features and improvements.


=== "component-config.yaml"
    Inbound and Outbound configurations in component-config.yaml file needs to moved under endpoints and dependencies sections.

    - The `spec.inbound.context` value should be copied to `endpoints.service.basePath`
    - The `spec.inboound.port` value should be copied to `endpoints.service.port`
    - Move `serviceReferences` section to `dependencies` section.

    Below is a sample of the migrated schema from `component-config.yaml` to `component.yaml`:
    <table style="font-size: 0.8rem">
      <tr>
        <td>
          ```yaml
          apiVersion: core.choreo.dev/v1beta1
          kind: ComponentConfig
          spec:
            inbound:
              - name: Greeting Service
                port: 9090
                type: REST
                networkVisibility: Public
                context: /greeting
                schemaFilePath: greeting_openapi.yaml
            outbound:
              serviceReferences:
                - name: choreo:///apifirst/mttm/mmvhxd/ad088/v1.0/PUBLIC
                  connectionConfig: 19d2648b-d29c-4452-afdd-1b9311e81412
                  env:
                    - from: ServiceURL
                      to: SERVICE_URL
          ```
        </td>
        <td>
          ```yaml
          schemaVersion: 0.9
          endpoints:
            - name: greeting-service
              displayName: Greeting Service
              service:
                basePath: /greeting
                port: 9090
              type: REST
              networkVisibilities: 
                - Public
              schemaFilePath: greeting_openapi.yaml
          dependencies:
            serviceReferences:
              - name: choreo:///apifirst/mttm/mmvhxd/ad088/v1.0/PUBLIC
                connectionConfig: 19d2648b-d29c-4452-afdd-1b9311e81412
                env:
                  - from: ServiceURL
                    to: SERVICE_URL
          ```
        </td>
      </tr>
    </table>

=== "endpoints.yaml"

    - `endpoints.context` value should be copied to `endpoints.service.basePath`
    - `endpoints.port` value should be copied to `endpoints.service.port`

    Given below is a sample on migrated schema from componrnt-config.yaml to component.yaml:
    <table style="font-size: 0.8rem">
      <tr>
        <td>
          ```yaml
          version: 0.1
          endpoints:
          - name: Greeting Service
            port: 9090
            type: REST
            networkVisibility: Organization
            context: /greeting
            schemaFilePath: greeting_openapi.yaml
          ```
        </td>
        <td>
          ```yaml
          schemaVersion: 0.9
          endpoints:
            - name: greeting-service
              displayName: Greeting Service
              service:
                basePath: /greeting
                port: 9090
              type: REST
              networkVisibilities: 
                - Organization
              schemaFilePath: greeting_openapi.yaml
          ```
        </td>
      </tr>
    </table>

### Component Configuration Kind 
#### component-config.yaml (Deprecated)

The `component-config.yaml` file extends the capabilities of `endpoints.yaml` by providing advanced inbound and outbound connection configurations. It enhances the existing endpoint configuration process by allowing you to define how your service's endpoints (inbound connections) are exposed and how your service connects to external services or components (outbound connections).

The `component-config.yaml` file has a defined structure and includes the following elements:

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
        # +required Environment variables injected to the component for connection configuration.
        env:
          # +required Key name of the connection configuration.
          - from: ServiceURL
            # +required Environment variable injected to the container.
            to: SERVICE_URL
```


### Endpoint Configuration Descriptor
#### endpoints.yaml (Deprecated)
