# Get Started with the Choreo CLI 

This guide walks you through the following sample use case:

- Create a web application
- Build the web application
- Deploy the web application in the development environment
- Promote the web application to the production environment

This guide utilized a simple to-do app built with Next.js and two basic environments: Development and Production.

## Prerequisites

Follow the steps below to install the CLI:  

1. Install the Choreo CLI by running the command specific to your operating system:

    - For Linux and Mac OS
        ``` sh
        curl -o- https://cli.choreo.dev/install.sh | bash
        ```

    - For Windows (via PowerShell)
        ``` sh
        iwr https://cli.choreo.dev/install.ps1 -useb | iex
        ```

2. Verify the installation by running the following command:

    ``` sh
    choreo --version
    ```

## Step 1: Login to Choreo 

Run the following command to login to Choreo:  

``` bash
choreo login
```

Follow the instructions on the console to open the link in the browser and login to Choreo.

## Step 2: Create a project 

A project in Choreo is a logical group of related components that typically represent a single cloud-native application. A project consists of one or more components.

Create a multi-repository project named ‘web-app-project’ by running the following command:

``` sh
choreo create project web-app-project --type=multi-repository
```
## Step 3: Create a Web Application component

In Choreo, a component within your project represents a singular unit of work in a cloud-native application. It can be a microservice, API, web application, or job/task. Each component is associated with a directory path in a Git repository containing the source code for the program.

!!! info "Note"

          The Choreo CLI currently supports the following component types:

          - Service
          - Web Application
          - Webhook
          - Scheduled Task
          - Manual Task

1. Fork the repository [https://github.com/wso2/choreo-sample-todo-list-app](https://github.com/wso2/choreo-sample-todo-list-app). This contains a sample web application that you can use for this guide. 

2. To initiate the creation of a Web Application component within your project, use the following command:
This triggers a wizard prompting you to provide details for your Git repository and other configurations for your component.

    ``` sh
    choreo create component my-web-app --project=web-app-project --type=webApp
    ```

3. Select the option `Enter remote repository URL manually`.
4. Enter the following values for the prompts.

    | Prompt                      | value                                  |
    |-----------------------------|----------------------------------------|
    | Configure source repository | `Enter remote repository URL manually` |
    | Remote repository URL       | Your forked repository                       |
    | Branch                      | `main`                                 |
    | Directory                   | `. `                                   |
    | Build-pack                  | `nodejs`                               |
    | Language Version            | `20.x.x`                               |
    | Port                        | `8080`                                 |
    
    !!! note
        The prompts may vary based on the type of component and the chosen build pack. 


## Step 4: View component details

To view comprehensive information about the component, including basic details and service endpoint URLs once the services are deployed, you can use the following command:

``` sh
choreo describe component "my-web-app" --project="web-app-project"
```

## Step 5: Build the component

You must build the components before deploying them to a specific environment. Execute the following command to trigger the build:

``` sh
choreo create build "my-web-app" --project="web-app-project"
```

### Step 5.1: View build status

To check the status of a specific build, run the following command, replacing <build-id> with the actual build ID obtained from the previous command:

!!! note 
    Typically, a build takes approximately 2 to 5 minutes to complete.

``` sh
choreo describe build <build-id> --project="web-app-project" --component="my-web-app"
```

### Step 5.2: View build logs

Once the build is complete, you can view the build logs for verification or debugging purposes. In the unlikely case, the build encounters any issues, the logs will help you troubleshoot.

``` sh
choreo logs --type=build --project="web-app-project" --component="my-web-app" --deployment-track="main" --build-id=<build_id>
```

## Step 6: Deploy to the Development environment

Once the build status indicates `successful` you can deploy the component in the Development environment by running the following command:

``` sh
choreo create deployment "my-web-app" --env=Development --project="web-app-project" --build-id=<build-id>
```

### Step 6.1: Verify the deployment in the Development environment

After deploying the component, you can retrieve the URL of the deployed web application and open the publicly available web page to verify its behavior. Use the following command to retrieve the URL:

``` bash
choreo describe component "my-web-app" --project="web-app-project"
```

### Step 6.2: View runtime logs

To observe runtime application logs of the web application in the Development environment, execute the following command:

``` sh
choreo logs --type component-application --component my-web-app --project web-app-project --env Development --follow
```

## Step 7: Deploy to the Production environment

Once you verify your application in the Development environment, you can proceed to deploy it to the Production environment with the following command: 

- Be sure to substitute <build-id> with the id obtained after triggering the build.

``` sh
choreo create deployment "my-web-app" --env=Production --project="web-app-project" --build-id=<build-id>
```

### Step 7.1: Verify the deployment in the Production environment

To ensure a successful deployment to the Production environment, retrieve the URL of the deployed web application using the following command:

``` sh
choreo describe component "my-web-app" --project="web-app-project"
``` 

Congratulations! You successfully deployed your web application in Choreo using the Choreo CLI. 

## View all CLI functions

Discover other functionalities of Choreo by running the following command.

``` sh
choreo --help
```
