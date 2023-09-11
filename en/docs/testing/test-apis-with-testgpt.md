# Test APIs with TestGPT

Testing APIs is an essential step in the API development process. Choreo simplifies the testing task by allowing you to test your APIs using natural language through Choreo's TestGPT. It eliminates manual test scenario creation and JSON payload accuracy concerns. It enables easy API communication using human language and requires no coding for handling sequences with multiple resource calls. This makes your API testing efficient and user-friendly.


!!! info
    This feature is available for REST API Proxy components and Service components with REST endpoints.

## Prerequisites

Before you try out this guide, be sure that you have the following:
  
- A REST API Proxy component or a Service component that exposes a REST API. 
If you do not already have a component, you can choose to create one from the following sources:

    | Component type |Sample source                                       | Reference documentation      |
    |----------------|----------------------------------------------------|------------------------------|
    | REST API Proxy| [https://raw.githubusercontent.com/wso2/choreo-sample-apps/main/rest-api-proxy/pet-store/openapi.yaml](https://raw.githubusercontent.com/wso2/choreo-sample-apps/main/rest-api-proxy/pet-store/openapi.yaml ) | [Develop a REST API Proxy ](../develop-components/develop-a-rest-api-proxy.md) | 
    | Service (Exposing a REST API endpoint)| [https://github.com/wso2/choreo-sample-apps/tree/main/rest-api-proxy/pet-store ](https://github.com/wso2/choreo-sample-apps/tree/main/rest-api-proxy/pet-store ) |[Develop a REST API](../develop-components/develop-services/develop-a-rest-api.md) |

- [Expose an OpenAPI specification from your component](https://wso2.com/choreo/docs/develop-components/configure-endpoints/#learn-the-endpointsyaml-file).
## Test your APIs

Follow the steps below to test an API with TestGPT: 

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the **Component Listing** pane, click on the component you want to test.
3. On the left navigation, click **Test** and then click **TestGPT**. This opens the **TestGPT** pane.
4. You can enter your query in natural language and execute it. For example, if you have a resource like "/pet/findByStatus," you can input the query, such as "Get the pets that are available," and execute it to obtain results. The resources are executed sequentially based on your query. Alternatively, you can use the sample queries available to try out TestGPT. 



