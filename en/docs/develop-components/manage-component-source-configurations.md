# Manage Component Source Configurations

In Choreo, you can configure service endpoints and connections via the `component.yaml` source configuration file. This file ensures that the configurations required to initialize endpoints and connections are defined, as they cannot be inferred from the source code. This guide provides an overview of how to configure and manage these settings effectively.

The source configuration file must be committed to your repository within the `.choreo` directory at the root of the project directory. This ensures the ability to version the configuration files alongside repository commits, enabling better tracking and management of configurations.

!!! note
    -  The `component-config.yaml` and `endpoints.yaml` files will eventually be deprecated and replaced by the `component.yaml` file. 
        - For details on how to seamlessly update to the latest `component.yaml` file`, see [Update to the latest source configuration file](#update-to-the-latest-source-configuration-file).
    - Choreo prioritizes configuration files in the following order: `component.yaml` takes the highest precedence, followed by `component-config.yaml`, and then `endpoints.yaml`.

## Overview of the `component.yaml` file 

**File location**:

```bash
<build-context-path>/.choreo/component.yaml
```

!!! note
    - For components built using **Buildpacks**, replace `build-context-path` with the `<project-directory>`. 
    For example, `<project-directory>/.choreo/component.yaml`.
    - For components built using **Docker**, replace `build-context-path` with the `<docker-context-path>`. 
    For example, `<docker-context-path>/.choreo/component.yaml`.

**Sample `component.yaml` file content**:

Click the respective tab to view the structure for your current configuration file version:

=== "Version 1.1"

    ``` yaml
    # +required The configuration file schema version
    schemaVersion: 1.1

    # +optional Incoming connection details for the component
    endpoints:
      # +required Unique name for the endpoint.
      # This name will be used when generating the managed API
      - name: greeter-sample
        # +optional Display name for the endpoint.
        displayName: Go Greeter Sample
        # +required Service section has the user service endpoint details
        service:
          # +optional Context (base path) of the API that gets exposed via the endpoint.
          basePath: /greeting-service
          # +required Numeric port value that gets exposed via the endpoint
          port: 9090
        # +required Type of traffic that the endpoint is accepting.
        # Allowed values: REST, GraphQL, WS, GRPC, TCP, UDP.
        type: REST
        # +optional Network level visibilities of the endpoint.
        # Accepted values: Project|Organization|Public(Default).
        networkVisibilities: 
          - Public
          - Organization
        # +optional Path to the schema definition file. Defaults to wild card route if not provided
        # This is only applicable to REST or WS endpoint types.
        # The path should be relative to the docker context.
        schemaFilePath: openapi.yaml
      
      # +optional Outgoing connection details for the component.
      dependencies:
        # +optional Defines the connection references from the Internal Marketplace.
        connectionReferences:
          # +required Name of the connection.
          - name: hr-connection
            # +required service identifer of the dependent component.
            resourceRef: service:/HRProject/UserComponent/v1/ad088/PUBLIC
      # +optional Defines runtime configurations
      configurations:
        # +optional List of environment variables to be injected into the component.
        env:
          # +required Name of the environment variable
          - name: HR_SERVICE_URL
            # +required value source
            # Allowed value sources: connectionRef
            valueFrom:
              # +required Choreo connection value source
              connectionRef:
                # +required Choreo connection name to refer the value from
                name: hr-connection
                # +required Choreo connection configuration key to refer the value from
                key: ServiceURL
    ```

    The descriptor-based approach of the `component.yaml` file simplifies and streamlines endpoint and connection configuration management. The use of versioned schemas ensures backward compatibility, providing a seamless transition with future updates.

    You can define the following root-level configurations via the `component.yaml` file:

    | Configuration        | Required     | Description                                                              |
    |----------------------|--------------|--------------------------------------------------------------------------|
    | **schemaVersion**    | Required     | The version of the `component.yaml` file. Defaults to the latest version.|
    | **endpoints**        | Optional     | The list of endpoint configurations.                                     |
    | **dependencies**     | Optional     | The list of dependency configurations.                                   |
    | **configurations**    | Optional     | The runtime configuration definitions.                                   |

    ### Endpoint configurations
    In the `endpoints` section of the `component.yaml` file, you can define multiple service endpoint configurations. Each endpoint must have a unique name and the required fields specified in the schema overview.

    !!! tip "Why have a unique name?"
          When you define multiple endpoints, the `endpoint.name` is appended to the Choreo-generated URL. A unique name ensures the endpoint is easily recognizable and readable within the URL.
          
    | Configuration        | Required     | Description                                                                                             |
    |----------------------|--------------|---------------------------------------------------------------------------------------------------------|
    | **name**             | Required     | A unique identifier for the endpoint within the service component. Avoid using excessively long names.  |
    | **displayName**      | Optional     | A display name for the endpoint.                                                                        |
    | **service**          | Required     | Service details for the endpoint.                                                                       |
    | **.basePath**        | Required     | The base path of the API exposed via this endpoint.                                                     |
    | **.port**            | Required     | The numeric port value exposed via this endpoint.                                                       |
    | **type**             | Required     | The type of traffic the endpoint accepts. For example, `REST`, `GraphQL`, `WS`, `gRPC`, `UDP`, or `TCP`.|
    | **networkVisibilities** | Required | The network-level visibility of the endpoint. For example, project, organization, or public.             |
    | **schemaFilePath** | Required | The file path to the swagger definition  or AsyncAPI 2.0 specification file. Defaults to the wildcard route if not specified. This field should be a relative path to the project path when using **Java**, **Python**, **NodeJS**, **Go**, **PHP**, **Ruby**, or **WSO2 MI** buildpacks. For REST or WebSocket endpoint types, when using the **Ballerina** or **Dockerfile** buildpack, the path should be relative to the component root or Docker context. |

    ### Dependency configurations

    In the `dependencies` section of the `component.yaml` file, you can define multiple connection configurations under `dependencies.connectionReferences`. You can use the connection reference generated in the inline developer guide when creating a connection. For instructions on copying [connection configurations](https://wso2.com/choreo/docs/develop-components/sharing-and-reusing/use-a-connection-in-your-service/), see the inline developer guide displayed during connection creation.

    You must include the following configurations in the `dependencies.connectionReferences` schema:

    | Configuration        | Required     | Description                                                                      |
    |----------------------|--------------|----------------------------------------------------------------------------------|
    | **name**             | Required     | The name given to the connection.                                                |
    | **resourceRef**      | Required     | A unique, human-readable identifier for the service you are connecting.          |


    !!! note
        Choreo automatically generates connection configurations when you create a connection. The properties such as **name** and **resourceRef** are automatically generated. The configurations required to establish the connection will be injected into Choreo-defined environment variables.

        To use custom environment variable names instead of Choreo's default ones, add the dependency as a `serviceReference` in your `component.yaml v1.1` file. You can copy the `serviceReference` section from the `component.yaml v1.0` tab and paste it under `dependencies` in your `component.yaml v1.1` file, which maintains backward compatibility with the v1.0 format.

    ### Runtime configurations
    In the `configurations` section of the `component.yaml` file, you can define runtime configurations for the component. These configurations currently support environment variable injection related to dependencies.
    
    | Configuration                 | Required     | Description                                                                       |
    |-------------------------------|--------------|-----------------------------------------------------------------------------------|
    | **env**                       | Optional     | An array of env variable configurations.                                          |
    | **name**                      | Required     | A unique name for the environment variable, starting with a letter or an underscore, and containing only letters, numbers, or underscores. |
    | **valueFrom**                 | Required     | The source of the environment variable value.                                     |
    | **connectionRef**             | Required     | Connection reference value source definition.                                     |
    | **name**                      | Required     | The name of the Choreo connection to reference the value from.                    |
    | **key**                       | Required     | The Choreo connection configuration key to reference the value from. For details on available keys, see [connection configurations](https://wso2.com/choreo/docs/develop-components/sharing-and-reusing/use-a-connection-in-your-service/) or the inline developer guide. |

    !!! note
        Runtime configurations are supported starting from `component.yaml v1.1`.

        When an environment variable value is specified using `connectionRef`, the connection's environment variable is renamed to the environment variable name defined in the `configurations` section. For example, in the sample `component.yaml` file given above, the `CHOREO_HR_CONNECTION_SERVICEURL` variable in the `hr-connection` is renamed to `HR_SERVICE_URL`.

=== "Version 1.0"

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
          # +optional Context (base path) of the API that gets exposed via the endpoint.
          basePath: /greeting-service
          # +required Numeric port value that gets exposed via the endpoint
          port: 9090
        # +required Type of traffic that the endpoint is accepting.
        # Allowed values: REST, GraphQL, WS, GRPC, TCP, UDP.
        type: REST
        # +optional Network level visibilities of the endpoint.
        # Accepted values: Project|Organization|Public(Default).
        networkVisibilities: 
          - Public
          - Organization
        # +optional Path to the schema definition file. Defaults to wild card route if not provided
        # This is only applicable to REST or WS endpoint types.
        # The path should be relative to the docker context.
        schemaFilePath: openapi.yaml
      
    # +optional Outgoing connection details for the component.
    dependencies:
      # +optional Defines the service references from the Internal Marketplace.
      serviceReferences:
        # +required Name of the service reference.
        - name: choreo:///apifirst/HRProject/UserComponent/ad088/v1/PUBLIC
          # +required Name of the connection instance.
          connectionConfig: 19d2648b-d29c-4452-afdd-1b9311e81412
          # +required Environment variables injected into the component for connection configuration.
          env:
            # +required Key name of the connection configuration.
            - from: ServiceURL
              # +required Environment variable injected into the container.
              to: SERVICE_URL
    ```

    The descriptor-based approach of the `component.yaml` file simplifies and streamlines endpoint and connection configuration management. The use of versioned schemas ensures backward compatibility, providing a seamless transition with future updates.

    You can define the following root-level configurations via the `component.yaml` file:

    | Configuration        | Required     | Description                                                              |
    |----------------------|--------------|--------------------------------------------------------------------------|
    | **schemaVersion**    | Required     | The version of the `component.yaml` file. Defaults to the latest version.|
    | **endpoints**        | Optional     | The list of endpoint configurations.                                     |
    | **dependencies**     | Optional     | The list of dependency configurations.                                   |

    <h3> Endpoint configurations </h3>
    In the `endpoints` section of the `component.yaml` file, you can define multiple service endpoint configurations. Each endpoint must have a unique name and the required fields specified in the schema overview.

    !!! tip "Why have a unique name?"
          When you define multiple endpoints, the `endpoint.name` is appended to the Choreo-generated URL. A unique name ensures the endpoint is easily recognizable and readable within the URL.
          
    | Configuration           | Required     | Description                                                                                             |
    |-------------------------|--------------|---------------------------------------------------------------------------------------------------------|
    | **name**                | Required     | A unique identifier for the endpoint within the service component. Avoid using excessively long names.  |
    | **displayName**         | Optional     | A display name for the endpoint.                                                                        |
    | **service**             | Required     | Service details for the endpoint.                                                                       |
    | **.basePath**           | Required     | The base path of the API exposed via this endpoint.                                                     |
    | **.port**               | Required     | The numeric port value exposed via this endpoint.                                                       |
    | **type**                | Required     | The type of traffic the endpoint accepts. For example, `REST`, `GraphQL`, `gRPC`, `WS`, `UDP`, or `TCP`.|
    | **networkVisibilities** | Required     | The network-level visibility of the endpoint. For example, project, organization, or public.            |
    | **schemaFilePath**      | Required     | The file path to the swagger definition or AsyncAPI 2.0 specification file. Defaults to the wildcard route if not specified. This field should be a relative path to the project path when using **Java**, **Python**, **NodeJS**, **Go**, **PHP**, **Ruby**, or **WSO2 MI** buildpacks. For REST or WebSocket endpoint types, when using the **Ballerina** or **Dockerfile** buildpack, the path should be relative to the component root or Docker context. |

    <h3> Dependency configurations </h3>

    In the `dependencies` section of the `component.yaml` file, you can define multiple service connection configurations under `dependencies.serviceReferences`. You can use the service references generated in the inline developer guide when creating a service connection. For instructions on copying [connection configurations](https://wso2.com/choreo/docs/develop-components/sharing-and-reusing/use-a-connection-in-your-service/), see the inline developer guide displayed during connection creation.

    You must include the following configurations in the `dependencies.serviceReferences` schema:

    | Configuration        | Required     | Description                                                                      |
    |----------------------|--------------|----------------------------------------------------------------------------------|
    | **name**             | Required     | A unique name for the service reference.                                         |
    | **connectionConfig** | Required     | A unique name for the connection instance.                                       |
    | **env**              | Required     | The list of environment variable mappings to inject into the container.          |
    | **.from**            | Required     | The key name of the connection configuration.                                    |
    | **.to**              | Required     | The environment variable to inject into the container.                           |

    !!! note
        Choreo automatically generates connection configurations when you create a connection. The properties such as **name**, **connectionConfig**, and **env.from** are automatically generated. However, you must manually set the **env.to** value.

## Overview of the `component-config.yaml` file 

**File location**:

```bash
<build-context-path>/.choreo/component-config.yaml
```

**Sample `component-config.yaml` file content**:

```yaml
apiVersion: core.choreo.dev/v1beta1
kind: ComponentConfig
spec:
  # +optional Incoming connection details for the component (AKA endpoints).
  inbound:
    # +required Unique name for the endpoint.
    # This name will be used when generating the managed API
    - name: Greeting Service
      # +required Numeric port value that gets exposed via the endpoint
      port: 9090
      # +required Type of traffic that the endpoint is accepting.
      # Allowed values: REST, GraphQL, GRPC, TCP, UDP, WS.
      type: REST
      # +optional Network level visibility of the endpoint. Defaults to Public
      # Accepted values: Project|Organization|Public.
      networkVisibility: Public
      # +optional Context (base path) of the API that gets exposed via the endpoint.
      # This is mandatory if the endpoint type is set to REST, GraphQL or WS.
      context: /greeting
      # +optional The path to the schema definition file. Defaults to wildcard route if not specified.
      # This is only applicable to REST and WS endpoint types.
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

The `component-config.yaml` file complements and enhances the existing endpoint configuration process. It allows you to define how the endpoints (inbound connections) in your service are exposed and how your service connects to external services or components (outbound connections).

You can define the following root-level configurations via the `component-config.yaml` file:

| Configuration        | Required     | Description                                                                           |
|----------------------|--------------|---------------------------------------------------------------------------------------|
| **apiVersion**       | Required     | The version of the `component-config.yaml` file defaults to `core.choreo.dev/v1beta1`.|
| **kind**             | Required     | The resource type of the file defaults to `ComponentConfig`.                          |
| **spec.inbound**     | Optional     | The list of inbound connection configurations.                                        |
| **spec.outbound**    | Optional     | The list of outbound connection configurations.                                       |

#### Inbound connection configurations (`spec.inbound`)

In the `spec.inbound` configuration section, you can specify endpoints to set up inbound connections. To specify endpoints, you can follow the existing endpoints schema structure. For details on the endpoints schema structure, see the [endpoints schema documentation](#overview-of-the-endpointsyaml-file).

#### Outbound connection configurations (`spec.outbound`)

In the `spec.outbound` section, you can define `serviceReferences`. To define `serviceReferences`, you can use the service references generated in the Internal Marketplace when creating a service connection. To copy the [outbound connection configurations](../sharing-and-reusing/use-a-connection-in-your-service/#use-a-connection-in-your-service), see the inline developer guide displayed when you create a connection.

You must include the following configurations in the `serviceReferences` schema:

| Configuration        | Required     | Description                                                                      |
|----------------------|--------------|----------------------------------------------------------------------------------|
| **name**             | Required     | A unique name for the service reference.                                         |
| **connectionConfig** | Required     | A unique name for the connection instance.                                       |
| **env**              | Optional     | The list of environment variable mappings to inject into the container.          |
| **env.from**         | Required     | The key name of the connection configuration.                                    |
| **env.to**           | Required     | The environment variable to inject into the container.                           |

## Overview of the `endpoints.yaml` file

**File location**:

```bash
<build-context-path>/.choreo/endpoints.yaml
```

**Sample `endpoints.yaml` file content**:

```yaml
# +required Version of the endpoint configuration YAML
version: 0.1

# +required List of endpoints to create
endpoints:
  # +required Unique name for the endpoint.
  # This name will be used when generating the managed API
- name: Greeting Service
  # +required Numeric port value that gets exposed via this endpoint
  port: 9090
  # +required Type of the traffic this endpoint is accepting.
  # Allowed values: REST, GraphQL, GRPC, UDP, TCP, WS.
  type: REST
  # +optional Network level visibility of this endpoint. Defaults to Public
  # Accepted values: Project|Organization|Public.
  networkVisibility: Project
  # +optional Context (base path) of the API that is exposed via this endpoint.
  # This is mandatory if the endpoint type is set to REST, GraphQL or WS.
  context: /greeting
  # +optional Path to the schema definition file. Defaults to wild card route if not provided
  # This is only applicable to REST and WS endpoint types.
  # The path should be relative to the docker context.
  schemaFilePath: greeting_openapi.yaml
```

The `endpoints.yaml` configuration file allows you to define configurations for multiple endpoints necessary for Choreo service components. This schema is essential to identify the context, port binding, network exposure level, and other attributes required to generate a Choreo endpoint.

You can define the following root-level configurations via the `endpoints.yaml` file:

| Configuration        | Required     | Description                                                                       |
|----------------------|--------------|-----------------------------------------------------------------------------------|
| **version**          | Required     | The version of the `endpoints.yaml` file.                                         |
| **name**             | Required     | A unique name for the endpoint, which Choreo will use to generate the managed API.|
| **port**             | Required     | The numeric port value that gets exposed via this endpoint.                       |
| **type**             | Required     | The type of traffic this endpoint accepts, such as `REST`, `GraphQL`, `gRPC`, `WS`, `UDP`, or `TCP`. Currently, the MI preset supports only the `REST` type.                                         |
| **networkVisibility**| Required     | The network level visibility of this endpoint, which defaults to `Public` if not specified. Accepted values are `Project`, `Organization`, or `Public`.|
| **context**          | Required     | The context (base path) of the API that Choreo exposes via this endpoint.         |
| **schemaFilePath**   | Required     | The swagger definition file path. Defaults to the wildcard route if not provided. This field should be a relative path to the project path when using the **Java**, **Python**, **NodeJS**, **Go**, **PHP**, **Ruby**, and **WSO2 MI** buildpacks. For REST endpoint types, when using the **Ballerina** or **Dockerfile** buildpack, this field should be a relative path to the component root or Docker context.|

## Update to the latest source configuration file

With the upcoming deprecation of the `component-config.yaml` file and the `endpoints.yaml` file, Choreo provides a seamless process to move to the latest `component.yaml`  version of the `component.yaml` file. Follow these steps to transition seamlessly:

### Step 1: Identify whether your current source configuration file is outdated

You can determine whether you’re using an outdated configuration file in the following ways:

- **During a build:**
     - On the **Build** page, after triggering a build, a warning will appear if your source configuration file is outdated. Click **View Details** in the **Initialization** -> **Source Config Validation** step. You will see an information banner with an option to update the file.

- **In existing deployments:**
     - If an existing deployment uses a build with an outdated source configuration file, you will see a warning icon on the build card of the relevant environment card.


### Step 2: Generate the latest `component.yaml` file

- In either of the scenarios above, click **Update** to initiate the process of generating the latest `component.yaml` file. The updated file will retain your existing configurations to ensure a smooth update process.


### Step 3: Add the generated configuration file to your repository

1. Download the generated `component.yaml` file.
2. Replace the existing source configuration file in the `.choreo` folder within your source repository.
    - Ensure the file name is `component.yaml`.


### Step 4: Trigger a new build

1. Commit the new `component.yaml` file to your repository. Push changes to the remote Git repository.
2. Trigger a build using the latest commit.


### Step 5: Verify the update

Once the build is complete:

  - Deploy the build.
  - Confirm that the warning messages no longer appear.

You can benefit from the latest features and enhancements provided by the updated source configuration file.
