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
