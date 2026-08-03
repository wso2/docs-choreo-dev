# Get Started with the {{ product_name }} CLI 

This guide walks you through the following sample use case:

- Create a web application
- Build the web application
- Deploy the web application in the development environment
- Promote the web application to the production environment

This guide utilized a simple to-do app built with Next.js and two basic environments: Development and Production.

## Prerequisites

Follow the steps below to install the CLI:  

1. Install the {{ product_name }} CLI by running the command specific to your operating system:

    - For Linux and Mac OS
        ``` sh
        curl -o- https://raw.githubusercontent.com/wso2/wdp-cli/main/scripts/install.sh | bash
        ```

    - For Windows (via PowerShell)
        ``` sh
        iwr https://raw.githubusercontent.com/wso2/wdp-cli/main/scripts/install.ps1 -useb | iex
        ```

2. Verify the installation by running the following command:

    ``` sh
    {{ cli_root_name }} --version
    ```

## Step 1: Login to {{ product_name }} 

Run the following command to login to {{ product_name }}:  

``` bash
{{ cli_root_name }} login
```

Follow the instructions on the console to open the link in the browser and login to {{ product_name }}.

## Step 2: Create a project 

A project in {{ product_name }} is a logical group of related components that typically represent a single cloud-native application. A project consists of one or more components.

Create a multi-repository project named ‘web-app-project’ by running the following command:

``` sh
{{ cli_root_name }} create project web-app-project --type=multi-repository
```
## Step 3: Create a Web Application component

In {{ product_name }}, a component within your project represents a singular unit of work in a cloud-native application. It can be a microservice, API, web application, or job/task. Each component is associated with a directory path in a Git repository containing the source code for the program.

!!! info "Note"

          The {{ product_name }} CLI currently supports the following component types:

          - Service
          - Web Application
          - Webhook
          - Scheduled Task
          - Manual Task
          - API Proxy

1. Fork the repository [https://github.com/wso2/choreo-sample-todo-list-app](https://github.com/wso2/choreo-sample-todo-list-app). This contains a sample web application that you can use for this guide. 

2. To initiate the creation of a Web Application component within your project, use the following command:
This triggers a wizard prompting you to provide details for your Git repository and other configurations for your component.

    ``` sh
    {{ cli_root_name }} create component my-web-app --project=web-app-project --type=webApp
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
        The prompts may vary based on the type of component and the chosen build preset. 


## Step 4: View component details

To view comprehensive information about the component, including basic details and service endpoint URLs once the services are deployed, you can use the following command:

``` sh
{{ cli_root_name }} describe component "my-web-app" --project="web-app-project"
```

## Step 5: Build the component

You must build the components before deploying them to a specific environment. Execute the following command to trigger the build:

``` sh
{{ cli_root_name }} create build "my-web-app" --project="web-app-project"
```

### Step 5.1: View build status

To check the status of a specific build, run the following command, replacing <build-id> with the actual build ID obtained from the previous command:

!!! note 
    Typically, a build takes approximately 2 to 5 minutes to complete.

``` sh
{{ cli_root_name }} describe build <build-id> --project="web-app-project" --component="my-web-app"
```

### Step 5.2: View build logs

Once the build is complete, you can view the build logs for verification or debugging purposes. In the unlikely case, the build encounters any issues, the logs will help you troubleshoot.

``` sh
{{ cli_root_name }} logs build --project="web-app-project" --component="my-web-app" --deployment-track="main" --build-id=<build_id>
```

## Step 6: Deploy to the Development environment

Once the build status indicates `successful` you can deploy the component in the Development environment by running the following command:

``` sh
{{ cli_root_name }} create deployment "my-web-app" --env=Development --project="web-app-project" --build-id=<build-id>
```

### Step 6.1: Verify the deployment in the Development environment

After deploying the component, you can retrieve the URL of the deployed web application and open the publicly available web page to verify its behavior. Use the following command to retrieve the URL:

``` bash
{{ cli_root_name }} describe component "my-web-app" --project="web-app-project"
```

### Step 6.2: View runtime logs

To observe runtime application logs of the web application in the Development environment, execute the following command:

``` sh
{{ cli_root_name }} logs application --component my-web-app --project web-app-project --env Development --follow
```

## Step 7: Deploy to the Production environment

Once you verify your application in the Development environment, you can proceed to deploy it to the Production environment with the following command: 

- Be sure to substitute <build-id> with the id obtained after triggering the build.

``` sh
{{ cli_root_name }} create deployment "my-web-app" --env=Production --project="web-app-project" --build-id=<build-id>
```

### Step 7.1: Verify the deployment in the Production environment

To ensure a successful deployment to the Production environment, retrieve the URL of the deployed web application using the following command:

``` sh
{{ cli_root_name }} describe component "my-web-app" --project="web-app-project"
``` 

Congratulations! You successfully deployed your web application in {{ product_name }} using the {{ product_name }} CLI. 

## Configure command output

All `list` and `describe` commands print output as a formatted table by default. If you want to process the output with other tools or scripts, you can get it as JSON instead.

To set the format for a single command, use the global `--output` flag or its `-o` shorthand. The flag accepts `table` and `json` — no other formats, such as `xml` or `yaml`, are supported.

``` sh
{{ cli_root_name }} list organizations --output json
```

``` sh
{{ cli_root_name }} describe component "my-web-app" --project="web-app-project" -o json
```

If you regularly work with JSON output, set the `OUTPUT_FORMAT` environment variable instead of passing the flag to every command:

``` sh
export OUTPUT_FORMAT=json
```

Every subsequent `list` and `describe` command returns JSON. The `--output` flag takes precedence over the environment variable, so running a command with `-o table` prints a table.

To go back to the default table output, unset the environment variable:

``` sh
unset OUTPUT_FORMAT
```

## View all CLI functions

Discover other functionalities of {{ product_name }} by running the following command.

``` sh
{{ cli_root_name }} --help
```
