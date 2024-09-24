Configure service endpoints and connections through the Choreo source configuration YAML file (`component.yaml`). Choreo expects the `component.yaml` file to ensure the necessary configurations for initializing endpoints and connections, as these configurations cannot be automatically inferred from the user source code. 

The source configuration file must be committed to the user's repository and placed in the `.choreo` folder at the root of the project directory. This ensures the ability to version the configuration files alongside repository commits, enabling better tracking and management of configurations.

!!! note
    -  The `component-config.yaml` and `endpoints.yaml` files will eventually be deprecated. For more details, refer to the Migration Guides for [component-config.yaml](http://www.google.com) and [endpoints.yaml](http://www.google.com).
    - Choreo prioritizes configuration files in the following order: `component.yaml` takes the highest precedence, followed by `component-config.yaml`, and then `endpoints.yaml`.



## component.yaml 

The `component.yaml` descriptor-based approach simplifies and streamlines overall endpoint and connection configuration management. The use of versioned schemas ensures backward compatibility, providing a seamless transition for future updates.

The `component.yaml` file allows the following root-level fields for defining configurations:

| Field                | Required     | Description                                                             |
|----------------------|--------------|-------------------------------------------------------------------------|
| **schemaVersion**    | Required     | The version of the `component.yaml` file defaults to latest version.    |
| **endpoints**        | Optional     | The list of endpoint configurations.                                    |
| **dependencies**     | Optional     | The list of dependency configurations.                                  |

#### Endpoint configurations (`endpoints`)
In the `endpoints` configuration section, you can define multiple service endpoint configurations. Each endpoint must have a unique name and the required fields as specified in the schema overview.

!!! tip "Why have a unique name?"
       When defining multiple endpoints, the `endpoint.name` is appended to the Choreo-generated URL. Choosing a clear and concise name ensures that the endpoint is easily recognizable and readable within the URL. Avoid overly lengthy names to maintain clarity.
       

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

In the `dependencies` configuration section, you can define multiple service connection configurations under `dependencies.serviceReferences`. Use the service references generated in the Internal Marketplace when creating a service connection. For instructions on copying [connection configurations](https://wso2.com/choreo/docs/develop-components/sharing-and-reusing/use-a-connection-in-your-service/), refer to the developer guide available during the connection creation process.

The `dependencies.serviceReferences` schema expects following fields:

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

!!! note
    - For components built using **Buildpacks**, replace `build-context-path` with the `<project-directory>`. 
    For example, `<project-directory>/.choreo/component.yaml`.
    - For components built using **Docker**, replace `build-context-path` with the `<docker-context-path>`. 
    For example, `<docker-context-path>/.choreo/component.yaml`.

**File content**:

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

## Migration Guide 
### component-config.yaml

!!! info "Migrate to component.yaml"
    Endpoint configurations under the `spec.inbound` section should be moved to the `endpoints ` section, and dependency configurations under the `spec.outbound` section should be moved to the `dependencies` section.

    - Copy the value of `spec.inbound.context` to `endpoints.service.basePath`.
    - Copy the value of `spec.inbound.port` to `endpoints.service.port`.
    - Copy the value of `spec.inbound.networkVisibility` to `endpoints.networkVisibilities`.
    - Add the `schemaVersion` field to component.yaml and remove `apiVersion` and `kind` fields.

The `component-config.yaml` file complements and enhances the existing endpoint configuration process. It allows you to define how your service's endpoints (inbound connections) are exposed and how your service connects to external services or components (outbound connections).

The `component-config.yaml` file allows the following root-level fields for defining configurations:

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

The `serviceReferences` schema expects following fields:

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
<build-context-path>/.choreo/component-config.yaml
```

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

### endpoints.yaml

!!! info "Migrate to component.yaml"
    Endpoint configurations should be moved to the `endpoints ` section.

    - Copy the value of `context` to `endpoints.service.basePath`.
    - Copy the value of `port` to `endpoints.service.port`.
    - Copy the value of `networkVisibility` to `endpoints.networkVisibilities`.
    - Add the `schemaVersion` field to component.yaml and remove `version` field.

The `endpoints.yaml` configuration file allows users to define configurations for multiple endpoints necessary for Choreo service components. This schema is essential for identifying the context, port binding, network exposure level, and other attributes required to generate a Choreo endpoint.

The `endpoints.yaml` file allows the following root-level fields for defining configurations:

| Field                | Required     | Description                                                                      |
|----------------------|--------------|----------------------------------------------------------------------------------|
| **version**          | Required     | The version of the `endpoints.yaml` file.                                           |
| **name**             | Required     | A unique name for the endpoint, which Choreo will use to generate the managed API.|
| **port**             | Required     | The numeric port value that gets exposed via this endpoint.                      |
| **type**             | Required     | The type of traffic this endpoint is accepting, such as `REST`, `GraphQL`, `gRPC`, `UDP`or `TCP`. Currently, the MI preset supports only the `REST` type.                                         |
| **networkVisibility**| Required     | The network level visibility of this endpoint, which defaults to `Public` if not specified. Accepted values are `Project`, `Organization`, or `Public`.|
| **context**          | Required     | The context (base path) of the API that Choreo exposes via this endpoint.        |
| **schemaFilePath**   | Required     | The swagger definition file path. Defaults to the wildcard route if not provided. This field should be a relative path to the project path when using the **Java**, **Python**, **NodeJS**, **Go**, **PHP**, **Ruby**, and **WSO2 MI** buildpacks. For REST endpoint types, when using the **Ballerina** or **Dockerfile** buildpack, this field should be a relative path to the component root or Docker context.|

**File location**:

```bash
<build-context-path>/.choreo/endpoints.yaml
```

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
