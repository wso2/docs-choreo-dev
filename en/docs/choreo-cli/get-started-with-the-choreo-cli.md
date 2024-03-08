# Get Started with the Choreo CLI 

This guide walks you through a the following sample usecase:

- Create a webapp
- Build the webapp
- Deploy the webapp in the Development environment
- Promote the webapp to the production environment

This guide utilized a simple to-do app built with Next.js and two basic environments: Development and Production.

## Prerequisites

Follow the steps below to install the CLI:  

1. Install the Choreo CLI by running the command specific to your operating system:

    - For Linux and Mac OS
        ```
        curl -o- https://cli.choreo.dev/install.sh | bash
        ```

    - For Windows (via PowerShell)
        ```
        iwr https://cli.choreo.dev/install.ps1 -useb | iex
        ```

2. Verify the installation by running the following command:

    ```
    choreo --version
    ```

## Step 1: Login to Choreo 

Run the following command to login to Choreo:  

```
choreo login
```

## Step 2: Create a project 

A project in Choreo is a logical group of related components that typically represent a single cloud-native application. A project consists of one or more components.

Create a multi-repository project names ‘default-project’ by running the following command:

```
choreo create project default-project --type=multi-repository
```

### Step 2.1: Provide access to your repository

Choreo requires access to your repository to read the source code for building and deploying. To provide this access, open the following link:
https://github.com/apps/choreo-dev/installations/new

!!! note
    Currently, in the CLI, only public GitHub repositories are supported in the free developer tier. Support for additional version control platforms, like Bitbucket, will be included in the future.

## Step 3: Create a Web Application component

In Choreo, a component within your project represents a singular unit of work in a cloud-native application. It can be a microservice, API, web application, or job/task. Each component is associated with a directory path in a Git repository, containing the source code for the program.

To initiate the creation of a web application component within your project, use the following command:
This triggers a wizard prompting you to provide details for your Git repository and other configurations for your component.

```
choreo create component my-web-app --project=default-project --type=webApp
```

For a sample Next.js web application, the configurations are as follows. However,  the prompts may vary based on the type of component and the chosen build pack. 

```
Remote repository URL: <your-repository-url>
Branch: main
Directory: .
Build Pack: nodejs
Language Version: 20.x.x
Port: 8080
```

## Step 4: View component details

To view comprehensive information about the component, including basic details, and service endpoint URLs once the services are deployed, you can use the following command:

```
choreo describe component "my-web-app" --project="default-project"
```

## Step 5: Build the component

Components must be built before deployment to specific environments. Execute the following command to trigger the build:

```
choreo create build "my-web-app" --project="default-project"
```

### Step 5.1: View build status

To check the status of a specific build, run the following command, replacing <build-id> with the actual build ID obtained from the previous command:

!!! note 
    Typically, a build takes approximately 2 to 5 minutes to complete.

```
choreo describe build <build-id> --project="default-project" --component="my-web-app"
```

### Step 5.2: View build logs

Once the build is complete, you can view the build logs for verification or debugging purposes. This will help you troubleshoot in the unlikely case the build encounters any issues.

```
choreo logs --type=build --project="default-project" --component="my-web-app" --build-id=<build-id>
```

## Step 6: Deploy to the Development environment

Once the build status indicates `successful`, you can deploy the component in the Development environment by runing the following command:

```
choreo create deployment "my-web-app" --env=Development --project="default-project" --build-id=<build-id>
```

### Step 6.1: Verify the deployment in the Development environment

After deploying the component,  you can retrieve the URL of the deployed web application and open the publicly available web page to verify its behavior. Use the following command to retrieve the URL:

```
choreo describe component "my-web-app" --project="default-project"
```

### Step 6.2: View logs

To observe runtime application logs of the web application in the Development environment, execute the following command:

```
choreo logs --type component-application --component my-web-app --project default-project --env Development --follow
```

## Step 7: Deploy to the Production environment

Once you verify your application in the development environment, you can proceed to deploy it to the production environment with the following command: 

- Be sure to substitute <build-id> with the id obtained after triggering the build.

```
choreo create deployment "my-web-app" --env=Production --project="default-project" --build-id=<build-id>
Verify the deployment to the Production environment
```

### Step 7.1: Verify the deployment in the Production environment

To ensure a successful deployment to the production environment, retrieve the URL of the deployed web application using the following command:

```
choreo describe component "my-web-app" --project="default-project"
```

Congratulations! You've successfully deployed your web application in Choreo using the Choreo CLI. 

## View all CLI functions

Discover other functionalities of Choreo by running the following command.

```
choreo
```
