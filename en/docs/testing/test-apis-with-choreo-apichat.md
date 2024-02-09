# Test APIs with Choreo API Chat

Testing APIs is an essential step in the API development process. Choreo simplifies the testing task by allowing you to engage with your APIs using natural language through Choreo API Chat. It eliminates manual test scenario creation and JSON payload accuracy concerns. It enables easy API communication using human language and requires no coding for handling sequences with multiple resource calls. This makes your API testing efficient and user-friendly.


!!! info
    This feature is available for REST API Proxy components and Service components with REST endpoints.

## Prerequisites

Before you try out this guide, be sure that you have the following:
  
- A REST API Proxy component or a Service component that exposes a REST API with a valid OpenAPI specification. 

    !!! info
        - If you are creating a REST API Proxy component, you can create it either by using a valid OpenAPI specification or by defining the resources manually.
        - If you are creating a service component with the Dockerfile preset,  make sure to [expose the OpenAPI specification from your component](https://wso2.com/choreo/docs/develop-components/configure-endpoints/#learn-the-endpointsyaml-file).

         

    - If you do not already have a component, you can choose to create one from the following sources:

    | Component type |Sample source                                       | Reference documentation      | 
    |----------------|----------------------------------------------------|------------------------------|
    | REST API Proxy| [https://raw.githubusercontent.com/wso2/choreo-sample-apps/main/rest-api-proxy/pet-store/openapi.yaml](https://raw.githubusercontent.com/wso2/choreo-sample-apps/main/rest-api-proxy/pet-store/openapi.yaml ) | [Develop an API Proxy](../develop-components/develop-an-api-proxy.md) | 
    | Service (Exposing a REST API endpoint)| [https://github.com/wso2/choreo-sample-apps/tree/main/java/pet-store ](https://github.com/wso2/choreo-sample-apps/tree/main/java/pet-store ) |[Develop a Service](../develop-components/develop-services/develop-a-service.md) |

## Test your APIs

Follow the steps below to test an API with API Chat: 

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the **Component Listing** pane, click on the component you want to test.
3. On the left navigation, click **Test** and then click **API Chat**. This opens the **API Chat** pane.
4. You can enter your query in natural language and execute it. For example, if you have a resource like "/pet/findByStatus," you can input the query, such as "Get the pets that are available," and execute it to obtain results. The resources are executed sequentially based on your query. Alternatively, you can use the sample queries available to try out API Chat. 
