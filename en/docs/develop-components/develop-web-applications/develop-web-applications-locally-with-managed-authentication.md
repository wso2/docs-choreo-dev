# Develop Web Applications Locally with Choreo’s Managed Authentication

Choreo’s managed authentication capability exposes a set of backend for frontend (BFF) endpoints, facilitating authentication and authorization for web applications. These endpoints are readily available for single-page web applications deployed on Choreo.

As a web application developer, there may be instances where it becomes necessary to run the application on your personal workstation. In such situations, the expectation is for authentication and authorization during local development to work in the same manner as it would when the application is deployed to Choreo, eliminating the need to modify the source code.

Choreo's local development capability ensures a seamless local application development experience where the authentication and authorization process aligns with Choreo’s deployment environment. This facilitates a smoother transition from development to deployment, improving workflow efficiency.

## How local development works in Choreo

Local development uses a proxy that sits in front of the locally running web application. This proxy intercepts all incoming requests. The requests to `/choreo-apis/*` and `/auth/*` are then forwarded to Choreo, while all other requests are forwarded to the locally running web application. The proxy runs on HTTPS using a self-signed certificate, crucial for secure, HTTP-only cookie-based managed authentication. Upon running the proxy, you can access the web application using the proxy's URL and start developing the application locally. You will observe that the behavior of the web application, in terms of the managed authentication functionality, is similar to how it would be after deploying to Choreo.

## Configure local development

### Prerequisites

- Create a single-page application (SPA) with managed authentication enabled. For details on how to set up managed authentication for your web application, see [Secure Web Applications with Managed Authentication](../../authentication-and-authorization/secure-web-applications-with-managed-authentication.md#step-1-set-up-managed-authentication-for-your-web-application).  
- Promote and deploy the application to the environment where you want to enable local development.

    !!! note
         - The web application that you run locally will use the same managed authentication configuration as the environment where you are configuring local development.
         - Local development is only allowed in non-critical environments.


### Apply configurations

1. Sign in to the Choreo Console.
2. In the **Component Listing** pane, click on the component you created and deployed by following the prerequisites.
3. In the left navigation menu, click **Deploy**.
4. Go to the respective environment card depending on the environment where you want to enable local development and click **Local Development**.
5. In the **Local Development** pane that opens, click the **Local Development** toggle to enable it.
6. If you want to update the port on which the local development proxy server runs, click to expand **Advanced Configurations** and specify a value for **Proxy Port**. 
7. Click **Apply**.

Now you have done the necessary configurations to set up local development. The next section walks you through the steps to access your web application so that you can develop it locally.

## Access your web application to develop it locally

### Prerequisites

- Ensure that local development is enabled in the environment where you want to proceed with local development.
- Ensure that your web application is running locally on `http://localhost` on a specific port.

### Access your web application locally 

You can either use the Choreo built-in identity provider or external identity provider to access your web application locally.

=== "Use the Choreo built-in identity provider"

    1. Sign in to the Choreo Console.
    2. In the **Component Listing** pane, click on the component you created and deployed by following the prerequisites.
    3. In the left navigation menu, click **Deploy**.
    4. Go to the respective environment card depending on the environment where you want to enable local development and click **Local Development**.
    5. In the **Local Development** pane that opens, copy the command given under **Step 1**.  
    6. Paste the command in a terminal, ensure you replace [APP_PORT] with the port on which your application is running locally, and run the command.
    7. To access the application and proceed to develop it, go to the URL given under **Step 2** in the **Local Development** pane.

        !!!note
            The local development proxy runs on HTTPS using a self-signed certificate. Your browser may warn that the certificate is not valid. Accept the risk and proceed.  


=== "Use an external identity provider"

    1. Sign in to the Choreo Console.
    2. In the **Component Listing** pane, click on the component you created and deployed by following the prerequisites.
    3. In the left navigation menu, click **Deploy**.
    4. Go to the respective environment card depending on the environment where you want to enable local development and click **Local Development**.
    5. In the **Local Development** pane that opens, copy the redirect URLs given under **Step 1**.
    6. Go to the settings in the OAuth application in your identity provider and specify the copied URLs as allowed redirect URLs.
    7. Go to the **Local Development** pane in the Choreo Console and copy the command given under **Step 2**. 
    8. Paste the command in a terminal, ensure you replace [APP_PORT] with the port on which your application is running locally, and run the command.
    10. To access the application and proceed to develop it, go to the URL given under **Step 3** in the **Local Development** pane.

        !!!note
            The local development proxy runs on HTTPS using a self-signed certificate. Your browser may warn that the certificate is not valid. Accept the risk and proceed.  

