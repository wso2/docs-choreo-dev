# Develop an API Proxy from Open API Document

An API proxy acts as an intermediary between an existing API and Choreo, intercepting all requests made to the API. It also functions as a managed API, allowing you to apply essential API management features such as security policies and rate limiting.

In this guide, you will:

- Create an API proxy component to expose an existing API.
- Deploy the API proxy.
- Test the API proxy to verify its functionality.
- Manage the API.
- Consume the API.

## Prerequisites

- If you're signing in to the Choreo Console for the first time, create an organization:
    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

## Step 1: Create an API proxy

To create an API proxy, you can either upload an OpenAPI document or provide a hosted OpenAPI document's URL. In this guide, you will specify a URL to an OpenAPI definition of a sample API.

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **API Proxy** card. This opens the **Create an API Proxy** pane.
4. Select **Upload API Specification** option. 
5. Click on **Try with sample URL** to go ahead with the sample Open API Document.
        

6. Specify the following values as Proxy Metadata:

    !!! note
        While most values are auto-generated from the specification, the context value has been explicitly set to **petstore** to enhance clarity


    | **Field**       | **Value**                                  |
    |-----------------|--------------------------------------------|
    | **Context**     | `petstore`                                   |
    | **Version**     | `v1.0`                                      |
    | **Target**      | `https://petstore3.swagger.io/api/v3` |

7. Enter following details for the Component Details:

    !!! info
        The **Component Name** field must be unique and cannot be changed after creation.

    | **Field**                 | **Value**          |
    |---------------------------|--------------------|
    | **Component Display Name**| `Petstore Service`          |
    | **Component Name**        | `pet-store-service`          |
    | **Description**           | `Sample proxy for Petstore service`     |

8. Click **Create**. This creates the API proxy component and takes you to the **Deploy** page.

## Step 2: Deploy the API proxy

1. In the left navigation menu, click **Deploy**.
2. In the **Build Area** card, click **Configure & Deploy**. This opens the **Configure & Deploy** pane.
3. Select **External** as the API access mode and click **Deploy**. The **Development** card indicates the **Deployment Status** as **Active** when the API proxy is successfully deployed.

Now you are ready to test the API proxy.

## Step 3: Test the API proxy

Choreo allows you to test your API proxy using either the [integrated OpenAPI Console](../../testing/test-rest-endpoints-via-the-openapi-console.md) or [cURL](../../testing/test-apis-with-curl.md). In this guide, you will use the OpenAPI Console.

!!! tip
    Choreo enables OAuth 2.0 to secure APIs by default. Therefore, you need an access token to invoke an API.

    - Choreo automatically generates a key to test the API via the OpenAPI Console. To view the key, click the show key icon in the **Security Header** field.
    - To disable security for the entire API or a specific resource:
        1. In the left navigation menu, click **Deploy**.
        2. Go to the **Build Area** card and click **Security Settings**.
        3. In the **Security Settings** pane:
            - To disable security for the entire API, clear the **OAuth2** checkbox.
            - To disable security for a specific resource, expand the relevant resource and turn off the **Security** toggle.    
        4. Click **Apply**.
        5. Then click **Deploy**.

1. In the left navigation menu, click **Test** and then click **OpenAPI Console**.
2. Select **Development** from the environment drop-down list.
3. Expand the `GET /pet/findByStatus` resource and click **Try it Out**.
4. Select the status **available** from the dropdown and click **Execute**. You will see a response similar to the following:

    ![API proxy response](../../assets/img/develop-components/develop-a-rest-api-proxy/try-out-pet-store-response.png){.cInlineImage-full}

    This indicates that your API proxy is working as expected.

## Step 4: Manage the API proxy

Now that you have a tested API proxy, you can publish it and make it available for application developers to consume. In this guide, you will apply rate limiting to the API and publish it.

### Step 4.1: Apply rate limiting to the API proxy

1. In the left navigation menu, click **Deploy**.
2. Go to the required environment card and click the settings icon corresponding to **CORS, Rate Limiting and Resiliency**.
3. In the **CORS, Rate Limiting and Resiliency** pane, click **Rate Limiting** to expand the section.
4. Select **API Level** as the **Rate Limiting Level**.
5. Specify appropriate values for the **Request Limit** and **Time Unit** fields. You can proceed with the default values.
6. Click **Apply**. This applies the rate limiting level to the API proxy and redeploys it.

### Step 4.2: Publish the API proxy


 **Publishing** makes your API available in the **Choreo Developer Portal**, enabling application developers to access and use it.

1. In the left navigation menu, click **Lifecycle** under **Manage**. This takes you to the **Lifecycle** page.
2. Click **Publish**.
3. In the **Publish API** dialog, click **Confirm** to proceed with publishing the API. If you want to change the display name, make the necessary changes and then click **Confirm**. This changes the API lifecycle state to **Published**.


## Step 5: Invoke the API From DevPortal

To generate credentials for the published API and invoke it via the Choreo Developer Portal, follow these steps:

1. In the **Lifecycle** page, click **Go to Devportal**. This takes you to the `Petstore Service` in the Choreo Developer Portal.

2. Invoke the API:
    1. In the Developer Portal left navigation menu, click **Try Out**.
    2. In the **Endpoint** list, select **Development** as the environment to try out the API.
    3. Click **Get Test Key** to generate an access token.
    4. Expand the `GET /pet/findByStatus` resource and click **Try it out**.
    5. Select the status **available** from the dropdown and click **Execute**. You will see a response similar to the following:

        ![Try out response](../../assets/img/develop-components/develop-a-rest-api-proxy/try-out-pet-store-response.png){.cInlineImage-full}

Now, you have gained hands-on experience creating, deploying, testing, and publishing an API proxy using Choreo.