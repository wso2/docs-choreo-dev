
Services and integrations are exposed to other services, integrations, or applications through endpoints. A service or an integration can expose multiple endpoints, each representing a unique entry point into the service. For example, a service may expose a REST API endpoint and a GraphQL endpoint, each providing different ways to interact with the service. Endpoints provide specific details for how the service or the integration can be consumed. For instance, the port number, protocol, and the schema such as open API specification (OAS) or GraphQL schema. By defining these details, endpoints make it possible for other services, integrations, and applications to discover and interact with the service in a standardized way.

Choreo defines endpoints by combining port binding, protocol, endpoint name, network visibility, endpoint schema, and additional protocol-related fields. The following table describes each attribute of an endpoint.

| Field | Description |
| ----- | ----------- |
| Name | A unique identifier for the endpoint within the service component. |
| Port | The network port on which the endpoint is accessible. |
| Type | The endpoint protocol. Supported protocols: REST, GraphQL, gRPC, WS, UDP, and TCP. |
| Network Visibility | Determines the level of visibility of an endpoint. Possible values are: <ul><li>Project: Allows components within the same project to access the endpoint.</li><li>Organization: Allows any component within the same organization to access the endpoint but restricts access to components outside the organization.</li><li>Public: Allows any client to access the endpoint, regardless of location or organization.</li></ul> |
| Schema | Specifies the structure and format of the data exchanged through the endpoint. |
| Context (HTTP and GraphQL only) | A context path that you add to the endpoint's URL for routing purposes. |

## Configure endpoints
The method of defining endpoints depends on the buildpack.

* For `Ballerina` and `WSO2 MI` buildpacks, Choreo automatically detects the endpoint details for REST APIs.
* For all other buildpacks (Java, Python, NodeJS, Ruby, PHP, Go, Dockerfile, etc.), you can configure endpoints in one of the following ways:
  
    * **Using the Choreo Console**: If a `component.yaml` file is not present, you can define a basic endpoint configuration during component creation.
    * **Using the component.yaml file**: You can manually configure endpoint details by defining them in a `component.yaml` file, placing it inside the `.choreo` directory at the build context path, and committing it to the source repository.

You can override UI-defined and auto-generated endpoints by providing a `component.yaml` file in the `.choreo` directory, which will take priority over other configurations.

To learn about the `component.yaml` file, see [Overview of the component.yaml file](../develop-components/manage-component-source-configurations.md#overview-of-the-componentyaml-file).
!!! note
    - Automatic endpoint generation is not supported for dynamic endpoint parameters such as variable ports. Therefore, you must use an `component.yaml` file to define dynamic endpoint parameters.

    - If you're configuring REST API and WebSocket endpoints in the same component, they must use separate ports. Sharing a port between these two endpoint types is not supported.

To learn about the `component.yaml` file, see [Overview of the component.yaml file](../develop-components/manage-component-source-configurations.md#overview-of-the-componentyaml-file).

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

### Edit a UI-Defined Endpoint
If you defined an endpoint during component creation, you can edit it later by following these steps:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the **Component Listing** pane, click on the component for which you want to update the endpoint.
3. In the left navigation menu, click **Deploy**.
4. On the **Deploy** page, go to the **Set Up** card and click **Configure & Deploy**.
5. In the **Environment Configurations** pane that opens, click **Next**.
6. In the **File Mount** pane that opens, click **Next**.
7. In the **Endpoint Details** pane that opens, locate the endpoint you want to edit.
8. Click the **Edit** icon next to the endpoint, modify the editable fields, and click **Update**.
9. Click **Deploy** to apply the changes.

!!! note
     If you commit a `component.yaml` file, build the component, and proceed with deployment, the endpoints will be generated from the `component.yaml` file and will take priority. In this case, the endpoint cannot be edited through the UI. To modify the endpoint, you must update the`component.yaml` file.