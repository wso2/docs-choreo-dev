# Develop a Web Application

A Web Application is an application program that is hosted on a server and serves ingress traffic through the browser. In Choreo, you can deploy a web application by creating a Web Application component and connecting it to a Git repository that includes the implementation of the Web Application. A Web Application can be one of the following:

- Single Page Applications (SPAs) like React, Angular, Vue, Svelte, etc.
- Web servers that serve static content or provide server-side rendering/static site generation (SSR/SSG).
- Static content, such as websites and other static resources that do not require a backend. These resources are usually just static HTML/JS/CSS files that need to be hosted.

## Prerequisites

1. To deploy a web application component, you will need a GitHub account with a repository that contains a web application. Fork the [Choreo sample apps repository](https://github.com/wso2/choreo-sample-apps/), which contains the sample for this guide.
2. The Choreo GitHub App requires the following permissions:
    - Read access to issues and metadata.
    - Read and write access to code, pull requests, and repository hooks.

## Creating a web application  

You can create a Web Application in Choreo as follows:

1. Build and deploy a SPA from source code.
    - Create a Web Application component by linking your repository that includes the Web Application source code.
    - Select the relevant buildpack. Default presets include React, Angular, and Vue.js. If you’re using a different SPA framework, you can try one of these presets as the configurations can be overridden to support most Javascript-based SPAs.
    - Enter the build command based on the your package manager. NPM, yarn, and pnpm are supported and the relevant package manager is run based on the dependency lock file in your repository (defaults to NPM if no lock file is present).
    - Specify the build output directory.
    - Specify the NodeJS version. Choreo does not pick the Nodejs version from the package.json engine property. The required Node version must be explicitly set in the build configuration.

    Once you create the Web Application component, Choreo will automatically generate a build pipeline for your Single Page Application and deploy it.

 2. Bring your Dockerfile.
    - Create a Web Application component by linking your repository that includes the Dockerfile to your containerized web application.
    - Commit a Dockerfile to the connected git repository to have full control over your build process.
    
     This option is recommended if you are deploying a web server and not just a SPA (or a SPA with a more complex build process). 

 3. Host static Websites.
     - To simply host some static web contents, create a Web Application component by linking your repository that includes the required static assets. 
     - Select the **Static Websites** preset. 
     
     This preset does not trigger a ‘build’ process unlike the SPA presets, the files are taken from the path specified in the repository and served as it is.   

Let's follow an example on how you can build and deploy a SPA from source code. 

Follow the steps below to create a sample Web Application component and deploy it in Choreo:

1. Sign in to the Choreo Console at [https://console.choreo.dev/login/](https://console.choreo.dev/login/). This opens the **Project Home** page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click **Web Application** card.
4. This opens the **Create a Web Application** pane, where you can give your component a name and a description.
5. If you have not already connected your GitHub repository to Choreo, to allow Choreo to connect to your GitHub account, click **Authorize with GitHub** and enter your GitHub credentials, and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:
         
        - Read and write access to code and pull requests.
        - Read access to issues and metadata.
             
        You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only used to send pull requests to a user repository. Choreo will not directly push any changes to a repository.

6. Enter the following information:

    | **Field**                     | **Description**    |
    |-------------------------------|--------------------|
    | **GitHub Account**            | Your account       |
    | **GitHub Repository**         | choreo-sample-apps |
    | **Branch**                    | **`main`**         |
    | **Buildpack**              | ReactSPA           |
    | **Build Context Path**        | web-apps/react-spa |
    | **Build Command**             | npm run build      |
    | **Build output directory**    | build              |
    | **Node Version**              | 18                 |

    !!! tip
         Managed authentication is enabled by default when you create a web application using **React**, **Angular**, or **Vue.js** buildpacks. To learn how to set up authentication for your web application with Choreo's managed authentication, see [Secure Web Applications with Managed Authentication](../authentication-and-authorization/secure-web-applications-with-managed-authentication.md).
         
7. Click **Create**. Once the component creation is complete, you will see the **Build and Deploy** page.

You have successfully created a Web Application component from the source code. Now let's build and deploy the Web Application.

## Deploy and access your web application

1. On the Deploy page, click **Deploy Manually**. 
Deploying the component initiates the build pipeline, and upon completion, your application will become accessible in your Development environment through a generated URL. Choreo takes care of automatic TLS/SSL management for your apps.

    !!! note
        The deployment of the Web Application component may require some time. You can monitor the progress by observing the logs. Once the deployment is finished, the deployment status in the corresponding environment card will change to **Active**.


2. Check the deployment progress by observing the console logs on the right of the page.
    You can access the following scans under **Build**. 

    - **The Dockerfile scan**: Choreo performs a scan to check if a non-root user ID is assigned to the Docker container to ensure security. If no non-root user is specified, the build will fail.
    - **Container (Trivy) vulnerability scan**: This detects vulnerabilities in the final docker image. 
    - **Container (Trivy) vulnerability scan**: The details of the vulnerabilities open in a separate pane. If this scan detects critical vulnerabilities, the build will fail.

    !!! info
        If you have Choreo environments on a private data plane, you can ignore these vulnerabilities and proceed with the deployment.

    The DevOps configurations related to scaling, health checks and configuration & secret management are available to all Web Application components regardless of how they were created, similar to other Choreo components. 

3. To access your Web application, copy the **Web App URL** on the environment card and paste it in a browser. You will see your React App that you created. 

## Create a short URL for your Web Application in Production

!!! info
    This feature is only available on the Choreo Cloud Data Plane.

Upon promoting your component to the Production environment, you have the choice to create a personalized short URL for your Web Application. The URL will follow this template: `https://{your-short-prefix}.choreoapps.dev`, allowing you to select a name of your preference for `{your-short-prefix}`.

To create a short URL for your Web Application, follow the steps below:

1. Click **Promote** on the **Development** card and promote your Web Application to production. 
2. On the **Production** card click **Create a short URL**.
3. Provide a **Short URL prefix** of your choice and click **Save**.
   
    !!! note
        Short URL names/prefixes are subject to availability, provided on a first-come-first-serve basis. 

## Manage runtime configuration for Web Applications

For web apps with a backend server, Choreo allows you to mount runtime configurations and secrets as environment variables and/or file mounts for a specific environment. Alternatively, you can also inject them into the client app during SSR or when serving static content.

However, in the case of SPAs that run completely on the browser, Choreo does not support ‘baking-in’ environment variables or other configurations. This is because Choreo follows a multi-environment deployment model, where configuration should be kept separate from the build and injected at runtime. For example, in most React SPA frameworks, the process.env stub is available during build time. However, it does not actually read in variables from the runtime. Instead, it is baked into the final JavaScript output during the build process. 

For managing runtime configurations, Choreo recommends the following approach (this is a React example, but will be generally applicable for other SPA frameworks).

!!! note
    - With SPAs, anything you mount as a runtime config will be available to your users in the browser. 
    - **Do not include sensitive secrets that are not browser-safe**.


Follow the steps below to manage runtime configurations for the React application you created above:

1. Go to your forked [Choreo sample apps repository](https://github.com/wso2/choreo-sample-apps/).
2. Open the public directory.
3. Create and commit a new file in the public directory of your React app called `config.js`. This file should contain any runtime configuration variables you want to expose to your app, such as API endpoints or feature flags. Let's expose the API URL and a feature flag as follows:

    ```
    window.config = {
    apiUrl: 'https://api.example.com',
        featureFlags: {
            enableNewFeature: true,
            enableExperimentalFeature: false,
        },
    };
    ```
2. In your index.html file inside the public directory , add a script tag as follows to include the config.js file inside the <body> tag, so that this config.js file will be accessible via JavaScript at runtime.

    ```
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="utf-8" />
        <title>My React App</title>
    </head>
    <body>
        <div id="root"></div>
        <script src="%PUBLIC_URL%/config.js"></script>
    </body>
    </html>

    ```
This will make the `window.config` object available to your app at runtime.

3. In your React component, you can access the configuration variables by referencing the window.config object as follows:

    ```
    import React from 'react';

    function MyComponent() {
    const apiUrl = window.config.apiUrl;
    const enableNewFeature = window.config.featureFlags.enableNewFeature;
    const enableExperimentalFeature = window.config.featureFlags.enableExperimentalFeature;

    // ...
    }
    ```
Now you can deploy your component. 

4. When you deploy your component to Choreo, create a config file mount at the specified path in each environment (where your index.html expects the config.js file as in this example).
[https://wso2.com/choreo/docs/devops-and-ci-cd/manage-configurations-and-secrets/#apply-a-file-mount-to-your-container](https://wso2.com/choreo/docs/devops-and-ci-cd/manage-configurations-and-secrets/#apply-a-file-mount-to-your-container).

## Limitations

The following limitations are specific to the Choreo Cloud Data Plane:

   - Request size limit, including headers, cookies, and payloads: 256KB
   - Response body size limit: 20MB
   - Only one open port is permitted per web application. While you can have multiple ports open for project-level communication within a data plane, incoming traffic from the internet can only be directed to one port. This differs from the Service type components, which support multiple endpoints.

## Troubleshooting Web Application

- **I’m seeing a blank page or a 502 error after I deploy my web application**

    If you encounter a blank page or a 502 error after deploying your web application, it typically indicates that the wrong directory is being served. To resolve this issue, follow these steps:

    - Double-check the build output directory, especially if you are using a Dockerfile-less preset. Ensure that the specified output directory matches the actual output directory generated during the build process. For example, erroneously if you have entered `public/` as the output directory when it should have been `build/`.

By verifying and correcting the output directory alignment, you should be able to address the issue of seeing a blank page or experiencing a 502 error after deploying your web application.
