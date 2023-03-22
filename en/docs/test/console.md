# Console

Choreo allows you to test your APIs using the integrated test console. Once you have created a service with public endpoint(s) and deployed it, it is ready to be tested. 

You can follow the steps below to test your service using the integrated Console:

!!! note
    If you are on the **Deployment** tab, click **Test** in the **Development** card and continue from step 4 onward.

1. Sign in to the Choreo Console at[https://console.choreo.dev/](https://console.choreo.dev/).

2. In the component list, find the API component that you want to test and click it.

3. To open the test view, click the Test on the left navigation menu.

4. Click **Console** to open the test console.

5. Select the **Environment** you want to test from the Environment list.

6. Select the required **Endpoint** from the Public Endpoint list.

7. In here you can see the endpoints you have configured with public visibility.

8. There are 2 main test console types available depending on your **Endpoint type**. For **REST** endpoints, the test view will display an OpenAPI Console and for **GraphQL** endpoints the test view will display a GraphQL Console

# OpenAPI console (REST Endpoints)

The OpenAPI Console is an interactive UI that guides you to execute REST operations using the Swagger UI. Choreo uses OAuth2.0 authentication to secure REST APIs by default. Therefore, the OpenAPI Console allows you to generate test keys to test the APIs.

1. Expand the resource that you want to test.

2. Click **Try it out**.

3. Enter any parameter values if necessary.

4. Click **Execute**.

5. You can view the response body under Responses.

# GraphQL console (GraphQL Endpoints)

The GraphQL Console is an interactive UI that guides you to write queries and mutations to test the GraphQL API. Choreo uses OAuth2.0 authentication to secure GraphQL APIs by default. Therefore, the GraphQL Console allows you to generate test keys to test the APIs.

1. Enter the API path and the query or mutation you want to test.

2. Click the **Play** Button.


Congratulations! You have now successfully created and tested a GraphQL in Choreo!
