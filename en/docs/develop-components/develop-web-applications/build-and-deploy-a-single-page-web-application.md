# Build and Deploy a Single-Page Web Application

A web application is an application program hosted on a server and serves ingress traffic through a browser. In Choreo, you can deploy a web application by creating a Web Application component and connecting it to a Git repository that contains the implementation of the web application. Web applications can fall into one of the following categories:

- **Single page applications (SPAs)**: Examples include React, Angular, Vue, Svelte, etc.
- **Web servers**: These serve static content or provide server-side rendering/static site generation (SSR/SSG).
- **Static content**: Examples include websites and other static resources that do not require a backend, usually comprising static HTML/JS/CSS files.

## Prerequisites

- To deploy a Web Application component, you must have a GitHub account with a repository containing the web application's implementation. For this guide, fork the [https://github.com/wso2/choreo-samples](https://github.com/wso2/choreo-samples) repository, which contains the sample web application implementation.

## Create a web application  

You can create a web application in Choreo as follows:

- Build and deploy a SPA from the source code.
    - Create a Web Application component by connecting a repository that includes the web application source code.
    - Select the relevant buildpack. Default buildpacks include **React**, **Angular**, and **Vue.js**. If you are using a different SPA framework, you can try one of these buildpacks because the configurations can be overridden to support most Javascript-based SPAs.
    - Enter the build command based on your package manager. 
      NPM, yarn, and pnpm are supported and the relevant package manager is run based on the dependency lock file in your repository (defaults to NPM if no lock file is present).
    - Specify the build output directory.
    - Specify the NodeJS version. Choreo does not pick the Nodejs version from the `package.json` engine property. The required Node version must be explicitly set in the build configuration.

    Once you create the Web Application component, Choreo automatically generates a build pipeline for your single-page application and deploys it.

- Bring your Dockerfile.
    - Create a Web Application component by connecting a repository that contains the Dockerfile for your containerized web application.
    - Commit a Dockerfile to your connected git repository to have full control over your build process.
    
     This approach is recommended if you are deploying a web server and not just a single-page application (or a single-page application with a complex build process). 

- Host static websites.
    - To host static web content, you can create a Web Application component and connect it to the GitHub repository that contains the required static assets. 
    - Select the **Static Websites** buildpack. This buildpack does not trigger a build process. It only fetches the files from the path specified in the repository and serves as it is.

Now let's take a look at how you can build and deploy a SPA from source code:

Follow the steps below to create a sample Web Application component and deploy it in Choreo:

1. Sign in to the Choreo Console at [https://console.choreo.dev/login/](https://console.choreo.dev/login/). This opens the **Project Home** page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **Web Application** card.
4. This opens the **Create a Web Application** pane, where you can specify a name and description for the component. Enter a unique name and description.
5. Go to the **GitHub** tab.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, you can paste the [Choreo samples repository](https://github.com/wso2/choreo-samples) URL in the **Provide Repository URL** field to connect to it without requiring authorization from the [Choreo Apps](https://github.com/marketplace/choreo-apps) GitHub application. However, authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps) is necessary if you want to enable [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) for the component.

    !!! note
           The **Choreo GitHub App** requires the following permissions:

           - Read and write access to code and pull requests.
           - Read access to issues and metadata.
             
           You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is exclusively utilized for sending pull requests to a user repository. Choreo will not directly push any changes to a repository.

7. Enter the following information:

    | **Field**                 | **Description**        |
    |---------------------------|------------------------|
    | **GitHub Account**        | Your account           |
    | **GitHub Repository**     | choreo-samples         |
    | **Branch**                | **main**               |

8. Select **React** as the **Buildpack**.
9. Enter the following information:

    | **Field**                 | **Description**        |
    |---------------------------|------------------------|
    | **Project Directory**     | /react-single-page-app |
    | **Build Command**         | `npm run build`        |
    | **Build Path**            | `build`                |
    | **Node Version**          | `18`                   |

    !!! tip
         Managed authentication is enabled by default when you create a web application using **React**, **Angular**, or **Vue.js** buildpacks. To learn how to set up authentication for your web application with Choreo's managed authentication, see [Secure Web Applications with Managed Authentication](../../authentication-and-authorization/secure-web-applications-with-managed-authentication.md).
         
10. Click **Create**. Choreo initializes the component with the sample implementation and opens the **Overview** page of the component.

You have successfully created a Web Application component from the source code. Now let's build and deploy the web application.

## Build your web application

To build the web application, follow the steps given below:

1. In the left navigation menu, click **Build**.
2. In the **Builds** pane, click **Build**. This opens the **Commits** pane, where you can see all the commits related to the component.
3. Select the latest commit and click **Build**. This triggers the build process and displays the progress in the **Build Logs** pane.
    
    You can access the following scans in the **Build Logs** pane: 

    - **The Dockerfile scan**: Choreo performs a scan to check if a non-root user ID is assigned to the Docker container to ensure security. If no non-root user is specified, the build will fail.
    - **Container (Trivy) vulnerability scan**: This detects vulnerabilities in the final docker image. 
    - **Container (Trivy) vulnerability scan**: The details of the vulnerabilities open in a separate pane. If this scan detects critical vulnerabilities, the build will fail.

    !!! info
        If you have Choreo environments on a private data plane, you can ignore these vulnerabilities and proceed with the deployment.


    !!! note
         The build process can take a while to complete. When the build process is complete, the build will be listed in the **Builds** pane along with its status. 

   Here, you will see the build status as **Success**.

## Deploy and access your web application

1. In the left navigation menu, click **Deploy**.
2. In the **Set Up** card, click **Configure and Deploy**. This opens the **Configure & Deploy** pane, where you can add a file mount if necessary. In this guide, you will not add a file mount.
3. Click **Next**. This takes you to the **Authentication Settings** pane.
4. Keep the default settings and click **Deploy**. 

    !!! note
         - The deployment process can take a few minutes to complete. On successful deployment, you will see the **Deployment Status** as **Active** in the **Development** card.

5. To verify that you have successfully hosted the web application, click the **Web App URL** in the **Development** card. This takes you to the web application.

## Create a short URL for your web application in production

!!! info
    This feature is only available on the Choreo cloud data plane.

When you promote your component to the Production environment, you have the option to create a personalized short URL for your web application. The URL follows the `https://{your-short-prefix}.choreoapps.dev` structure, where you can select a name of your preference for `{your-short-prefix}`.

To create a short URL for your web application, follow the steps given below:

1. Click **Promote** in the **Development** card and promote your web application to production. 
2. In the **Production** card, click **Create a short URL**.
3. Specify a **Short URL prefix** of your choice and click **Save**.
   
    !!! note
        Short URL names/prefixes are subject to availability, provided on a first-come-first-serve basis. 

## Manage runtime configurations for web applications

For web applications with a backend server, Choreo allows you to mount runtime configurations and secrets as environment variables and/or file mounts for a specific environment. Alternatively, you can also inject them into the client application during server-side rendering or when serving static content.

However, in the case of SPAs that run completely on the browser, Choreo does not support *baking-in* environment variables or other configurations. This is because Choreo follows a multi-environment deployment model, where configuration should be kept separate from the build and injected at runtime. For example, in most React SPA frameworks, the `process.env` stub is available during build time. However, it does not actually read in variables from the runtime. Instead, it is baked into the final JavaScript output during the build process. 

To manage runtime configurations, Choreo recommends the following approach (although this is a React example, the approach is generally applicable to other SPA frameworks as well).

!!! note
    - With SPAs, anything you mount as a runtime config will be available to your users in the browser. 
    - **Do not include sensitive secrets that are not browser-safe**.


Follow the steps given below to manage runtime configurations for the React application you created above:

1. Go to your forked [Choreo samples repository](https://github.com/wso2/choreo-samples).
2. Open the public directory.
3. Create and commit a new file named `config.js` in the public directory of your React application. This file should contain the runtime configuration variables you want to expose to your application, such as API endpoints or feature flags. Let's expose the API URL and a feature flag as follows:

    ```
    window.config = {
    apiUrl: 'https://api.example.com',
        featureFlags: {
            enableNewFeature: true,
            enableExperimentalFeature: false,
        },
    };
    ```
4. In your `index.html` file inside the public directory, add a script tag as follows to include the `config.js` file inside the <body> tag. The `config.js` file will be accessible via JavaScript at runtime.

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
This makes the `window.config` object available to your app at runtime.

1. In your React component, you can access the configuration variables by referencing the `window.config` object as follows:

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

2. When you deploy your component to Choreo, create a config file mount in the specified path for each environment (where your `index.html` expects the `config.js` file as in this example).
[https://wso2.com/choreo/docs/devops-and-ci-cd/manage-configurations-and-secrets/#apply-a-file-mount-to-your-container](https://wso2.com/choreo/docs/devops-and-ci-cd/manage-configurations-and-secrets/#apply-a-file-mount-to-your-container).

## Limitations

The following limitations are specific to the Choreo cloud data plane:

   - Request size limit, including headers, cookies, and payloads: 256KB
   - Response body size limit: 20MB
   - Only one open port is permitted per web application. While you can have multiple ports open for project-level communication within a data plane, incoming traffic from the internet can only be directed to one port. This differs from the Service type components, which support multiple endpoints.

## Troubleshoot a web application

- **I’m seeing a blank page or a 502 error after I deploy my web application**

    If you encounter a blank page or a 502 error after deploying your web application, it typically indicates that the wrong directory is being served. To resolve this issue, follow these steps:

     - Double-check the build output directory, especially if you are using a Dockerfile-less buildpack. Ensure that the specified output directory matches the actual output directory generated during the build process. For example, if you have erroneously entered `public/` as the output directory when it should have been `build/`.

    By verifying and correcting the output directory alignment, you should be able to address the issue of encountering a blank page or experiencing a 502 error on deploying your web application.
