# Troubleshoot Choreo

This page walks you through common problems you may encounter when building and deploying components with Choreo, along with the recommended solutions to resolve each issue.

## Troubleshoot component build errors

- ### Deploying an Angular web application displays the Nginx welcome page instead of the application's homepage.

      This occurs due to specifying an incorrect build output directory when you set up your Angular application in Choreo.
To resolve the issue, follow the guidelines given below:

       - Ensure that the build output directory correctly points to where your Angular build script outputs the files. The deafult output directory is `dist/<project-name>`.
       - Make sure to reconfigure the build settings if the current configuration is incorrect.

- ### An error occurs in the container Trivy scan when building a BYOC component.
      
      The recommended approach to address this issue is to fix the identified vulnerability and rebuild the component.

      However, if you want to add a `.trivyignore` file to overcome the issue, ensure to add it to the Docker build context path specified when creating the component. For example, `{buildContextPath}./trivyignore`. 

- ### The `config.js` file is not properly mounted in a web application.
      
      To resolve this issue, follow the steps given below:

       1. Add the `config.js` file to the `app/public` directory in your repository.
       2. Reference it from the `index.html` file by adding a script tag as follows:

           `<script src="public/config.js"></script>`

## Troubleshoot component deployment errors

- ### The `config.js` file is not properly integrated during the deployment of a React application, causing it to render with unexpected HTML instead of the expected JavaScript configuration.

      To ensure correct loading of the `config.js` file, follow the steps given below:

       - Reference the `config.js` file from the `index.html` file of your application by adding a script tag as follows:  

          `<script src="public/config.js"></script>` 

       - Verify that the path in the script tag matches the location where the `config.js` file is stored in your repository.
       - Make sure the script tag is placed within the `<body>` tag in your `index.html` file. You must ensure that it is not mistakenly placed within another HTML element.
 
- ### I'm not aware of the commits that can trigger an automatic build in Choreo.

      Merge commits and commits pushed directly to the branch can trigger a build in Choreo.

## Troubleshoot web application issues

- ### After building a web application, the Nginx welcome page is displayed instead of the web application home page.

      This can happen if an incorrect build output directory is specified during component creation. 

      During the build process, output files including the `index.html` are copied to the Nginx root directory. To ensure that the correct files are copied during the build process, you must check the Docker build logs. 

- ### The language I prefer to use is not available as a buildpack.

      In such scenarios, you can use the Dockerfile buildpack to create the component.

- ### I mistakenly used an incorrect build command when creating a web application. How can I update it before triggering a build?

      You can go to the build page of the component and update the build command in the build configurations section.

## Troubleshoot managed-authentication issues

- ### After securing a web application with managed authentication,  I’m not able to add users who can sign in to the application.

      For step-by-step instructions on how to manage users with Choreo's built-in identity provider (IdP), see [Configure a User Store with the Built-In IdP](../administer/configure-a-user-store-with-built-in-idp.md).
      
      For details on setting up other OpenID Connect (OIDC) supported IdPs, see [Manage OAuth Keys](../authentication-and-authorization/secure-web-applications-with-managed-authentication/#step-3-manage-oauth-keys).

## Troubleshoot Tailscale proxy issues

- ### Where can I find logs to troubleshoot Tailscale proxy issues?

      To troubleshoot Tailscale proxy issues, you can view the [Runtime Logs](https://wso2.com/choreo/docs/monitoring-and-insights/view-logs/#runtime-logs) of the running container for your Tailscale proxy deployment. These logs can help you diagnose most of the issues. 
      You can also view real-time container logs via the **Runtime** page under **DevOps**. For more details, see [Observe real-time container logs](https://wso2.com/choreo/docs/devops-and-ci-cd/view-runtime-details/#observe-real-time-container-logs).

- ### I'm not able to connect the Tailscale proxy node to my Tailscale network due to an authentication failure.

      - If you encounter the following log lines in your Tailscale proxy deployment, it indicates a misconfiguration of the `TS_AUTH_KEY`:

         ```
         2024-06-04T10:38:53.885800940Z To authenticate, visit:
         2024-06-04T10:38:53.885802684Z 
         2024-06-04T10:38:53.885815708Z https://login.tailscale.com/a/696841f011517
         2024-06-04T10:38:53.885817457Z 
         2024-06-04T10:38:55.194344862Z Waiting for tailscale up to complete...
         2024-06-04T10:38:57.198970796Z Waiting for tailscale up to complete...
         2024-06-04T10:38:59.203265659Z Waiting for tailscale up to complete...
         ```
        To resolve this, you must re-check your authentication key and ensure you have entered the correct key.

      - If you encounter the following log lines in your Tailscale proxy deployment, it indicates that your authentication key is invalid or expired.
         ```
         2024-06-04T11:33:58.762363181Z 2024/06/04 11:33:58 Received error: invalid key: unable to validate API key
         2024-06-04T11:33:58.762458209Z backend error: invalid key: unable to validate API key
         ```
       
         To resolve this, you must verify the correctness of your authentication key. If the key has expired, you must generate a new key from Tailscale admin console. 


- ### I'm not able to access private endpoints although the Tailscale proxy is properly connected to my Tailscale network.

      To resolve this, do the following:

       - Ensure your on-premises setup is properly connected to the Tailscale network and that specific services, database servers, etc., are running as expected in your on-premises setup.
       - Verify that the IP addresses and ports specified in your `Config.yaml` file (mounted to Tailscale proxy during deployment) match the IP addresses in your Tailscale network.
       - Cross-check the ports defined in the `endpoints.yaml` file with the port mappings in the `Config.yaml` file.

