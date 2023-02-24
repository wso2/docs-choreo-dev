# Open API Console

Choreo allows you to test your APIs using the integrated OpenAPI Console, the generated cURL command, or by integrating Postman to Choreo. Once you have created an API and deployed it, it is ready to be tested. 

You can follow the steps below to test your REST API using the integrated OpenAPI Console:

# Test your REST API

!!! note
    If you are on the **Deployment** tab, click **Test** in the **Development** card and continue from step 4 onward. 

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev/).

2. In the component list, find the API component that you want to test and click it. 

3. To open the test view, click the **Test** on the left navigation menu.

4. Select the required endpoint from the **API Endpoint** list.

    If you want to test the API in your sandbox environment before trying it in the development environment, select the sandbox endpoint. If not, select the API endpoint

    !!! info
        You can provide a sandbox endpoint for testing purposes when you deploy your REST API. For more information, see [Deploy Your Component](../deploy/deploy-your-component.md).

5. Expand the resource that you want to test.

6. Click **Try it out**.

7. Enter any parameter values if necessary.

8. Click **Execute**.

    You can view the response body under **Responses**.

    ![Response for the Deployed API](../assets/img/tutorials/rest-api/deployed-api-response.png){.cInlineImage-full}

Congratulations! You have now successfully created and tested a REST API in Choreo!
