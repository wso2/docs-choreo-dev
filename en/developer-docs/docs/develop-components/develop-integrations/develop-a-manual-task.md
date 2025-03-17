# Develop a Manual Task

This guide walks you through the steps to develop, deploy, test, and observe a manual task using Choreo.

In this guide, you will:

- Develop a manual task to fetch the weather forecast for a specified location for the next 24 hours from the [OpenWeatherMap](https://openweathermap.org) API.
- Process the weather data into a specific format.
- Send the formatted data to a specified email address.

## Prerequisites

1. If you're signing in to the Choreo Console for the first time, create an organization:
    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

2. Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples), which contains the sample integration for this guide.

    !!! info "Repository File Structure"
        The sample code for this guide is in the `<sample-repository-dir>/weather-to-email-integration` directory. The following table describes the key files in the repository:

        | **File Path**         | **File Content**                                                     |
        | --------------------- | -------------------------------------------------------------------- |
        | **Ballerina.toml**    | Contains metadata about the project                                  |
        | **Dependencies.toml** | Lists the dependencies required for the project                      |
        | **main.bal**          | Contains the entry point of the project, including the main function |
        | **types.bal**         | Contains custom data types used in the project                       |
        | **utils.bal**         | Contains utility functions and helper functions used in the project  |

3. Go to [OpenWeatherMap](https://openweathermap.org/) and sign up to obtain an API key. For details, see the [OpenWeatherMap documentation](https://openweathermap.org/appid#signup).

## Step 1: Create a manual task component

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **Manual Task** card.
6. Click **Authorize with GitHub** to connect Choreo to your GitHub account. If you haven't connected your GitHub repository to Choreo, enter your credentials and select the repository you forked earlier to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, select the **Use Public GitHub Repository** option and paste the [Choreo samples repository](https://github.com/wso2/choreo-samples) URL in the **Provide Repository URL** field. However, enabling [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) requires authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! note
        The **Choreo GitHub App** requires:

        - Read and write access to code and pull requests.
        - Read access to issues and metadata.

        You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) at any time. Write access is only used for sending pull requests; Choreo will not push changes directly to your repository.

7. Enter the following information:

    | **Field**             | **Description**                                      |
    | ----------------------| -----------------------------------------------------|
    | **Organization**      | Your GitHub account                                  |
    | **Repository**        | `choreo-samples`                                     |
    | **Branch**            | `main`                                               |
    | **Component Directory** | `weather-to-email-integration`                       |

8. Select **Ballerina** as the **Build Pack**.

    !!! tip
        - **Buildpack** specifies the type of build to run depending on the implementation of the component. It converts the integration code into a Docker image that can run on Choreo cloud. If an integration is developed using [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/), select **Micro Integrator** as the buildpack. If an integration is developed using the [Ballerina language](https://ballerina.io), select **Ballerina** as the buildpack.
        - **Component Directory** specifies the location of the project to build the component.

4. Enter a unique name and description for the component. You can use the following values:

    | **Field**          | **Value**                        |
    | ------------------ | -------------------------------- |
    | **Component Name** | `WeatherToEmail`                 |
    | **Description**    | `My first manual task`           |


8. Click **Create**. Choreo initializes the component with the sample implementation and opens the **Overview** page of the component.

## Step 2: Build the manual task

1. In the left navigation menu, click **Build**.
2. In the **Builds** pane, click **Build**. This opens the **Commits** pane, where you can see all the commits related to the component.
3. Select the latest commit and click **Build**. This triggers the build process and displays the build progress in the **Build Logs** pane.

    !!! info
        The build process may take some time. Once complete, the build status will be listed in the **Builds** pane. Here, you will see the build status as **Success**.

## Step 3: Deploy the manual task

1. In the left navigation menu, click **Deploy**.
2. In the **Set Up** card, click **Configure & Deploy**.
3. In the **Configurations** pane, specify values for the configurable variables:

    !!! tip
        The configurable variables populated here are defined in the sample Ballerina project used in this guide. To learn how to declare configurable variables in Ballerina, see the [Ballerina documentation on declaring configurable variables](https://ballerina.io/learn/by-example/configurable-variables/).

    | **Field**     | **Value**                                                               |
    | ------------- | ----------------------------------------------------------------------- |
    | **apiKey**    | The API key you obtained in the prerequisites section                   |
    | **latitude**  | Latitude of the location to get the weather forecast                    |
    | **longitude** | Longitude of the location to get the weather forecast                   |
    | **email**     | The email address to receive the formatted weather forecast information |

    !!! note
        If you use **Ballerina** as the buildpack and want to set a configurable variable as a secret, click the lock icon corresponding to the configurable variable. This marks it as a secret and conceals the input value.

        For example, if you set the **apiKey** as a secret, its input value will be concealed. To update the input value later, click **Update Secret Content** and specify a new value.

4. Click **Deploy**.

## Step 4: Execute the manual task

1. In the left navigation menu, click **Execute**.
2. Click **Run Now**. This triggers the task.

    !!! info "Inject Dynamic Values into Your Application as Command-Line Arguments"
        If you want to inject dynamic values into your application as command-line arguments when you run a manual task, follow these steps:
        1. Click the drop-down icon next to **Run Now** and then click **Run with Arguments**.
        2. In the **Runtime Arguments** pane, enter the arguments you want to pass to your application.
        3. Click **Execute**. This triggers the task with the specified arguments.

        The capability to run a manual task with arguments is supported for the following buildpacks:

        === "Dockerfile"
            To explore a Dockerfile-based manual task with arguments, try out the [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/docker-hello-world-manual-task) sample. For instructions, see the `readme.md` file in the sample repository.

            !!! info
                When working on Docker projects, the **Run with Arguments** capability is not supported if the Dockerfile contains `CMD`. In such scenarios, use `ENTRYPOINT` to define your default commands.

        === "Go"
            To explore a Go-based manual task with arguments, try out the [Hello World Go Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-go-task) sample. For instructions, see the `readme.md` file in the sample repository.

        === "Java"
            To explore a Java-based manual task with arguments, try out the [Hello World Java Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-java-task) sample. For instructions, see the `readme.md` file in the sample repository.

            !!! info
                When working on Java projects:
                - The **Run with Arguments** capability is not supported if `Procfile` is available in the project.
                - The `Main` class should be defined in the `manifest` file.
                - If Maven files such as `mvn.cmd` exist in the project without the `.mvn` directory, the build will fail. To ensure a successful build, either commit the `.mvn` directory along with any Maven files or exclude Maven files if you choose not to commit the `.mvn` directory.

        === "NodeJS"
            To explore a NodeJS-based manual task with arguments, try out the [Hello World NodeJS Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-nodejs-task) sample. For instructions, see the `readme.md` file in the sample repository.

            !!! info
                When working on NodeJS projects:
                - The **Run with Arguments** capability is not supported if `Procfile` is available in the project.
                - The project root must contain the `package.json` file with the `main` attribute defined.

        === "WSO2 MI"
            To explore a WSO2 MI-based manual task with arguments, try out the [Weather to Logs Task](https://github.com/wso2/choreo-samples/tree/main/weather-to-logs-mi-manual-task) sample. For instructions, see the `readme.md` file in the sample repository.

            !!! info
                When working on WSO2 MI projects and deploying a WSO2 MI integration as a manual task in Choreo, use the WSO2 MI automation mode. For details, see [Running the Micro Integrator in Automation Mode](https://apim.docs.wso2.com/en/latest/install-and-setup/install/running-the-mi-in-automation-mode/).

        === "Ballerina"
            To explore a Ballerina manual task with arguments, try out the [Weather to Email Task](https://github.com/wso2/choreo-samples/tree/main/weather-to-email-integration) sample. For instructions, see the README.md file in the sample repository.

            !!! info
                If you want to pass arguments to Ballerina main functions, use the **Run with Arguments** capability. For details on the arguments you can pass, see the [Ballerina documentation](https://ballerina.io/learn/by-example/main-function/). You can also override configurable values in the same manner. For more information, see [Provide values to configurable variables](https://ballerina.io/learn/provide-values-to-configurable-variables/#provide-via-command-line-arguments).

## Step 5: Test the manual task

Once the task is triggered, an email with the subject `[WSO2 Choreo Demo] Next 24H Weather Forecast` is sent from `choreo.demo@gmail.com` to the email address specified as the **email** configurable variable value in [Step 3](#step-3-deploy-the-manual-task).

If the manual task ran successfully, you should receive an email similar to the following:

![Received email](../../assets/img/develop-components/develop-a-scheduled-integration/Received-email.png)

## Step 6: Observe the manual task

The observability view in Choreo displays graphs that depict details such as throughput, latency, diagnostic data, and logs to identify and troubleshoot anomalies in components you deploy.

1. In the left navigation menu, click **Observability**.
2. Observe the following:
    - The throughput and latencies of requests served over a given period.
    - The logs generated over a given period.
    - The flame graph (Diagnostics View) generated over a given period.
    - The low-code diagram.

To learn more about the observability details you can view via Choreo observability, see [Observability Overview](../../monitoring-and-insights/observability-overview.md).

## Step 7: Monitor executions

To track and monitor executions associated with the deployed manual task, go to the left navigation menu and click **Execute**.

!!! tip
    The **Execute** view is applicable to both scheduled and manual tasks.

You can view the following information:

- **Total executions**: The total number of executions within the past 30 days.

    ![Total Execution](../../assets/img/develop-components/develop-a-scheduled-integration/total_executions.png)

- **Execution history**: The currently active executions and those that are already complete. You can view information such as the execution ID, the revision of the execution, and the time it was triggered.

    ![Execution History](../../assets/img/develop-components/develop-a-scheduled-integration/execution_history.png)

- **Detailed execution logs**: Click on an execution to view detailed logs related to it.

    !!! info
        It may take a few minutes for the logs to appear. You may need to manually refresh to view the latest logs.

    ![Execution Logs](../../assets/img/develop-components/develop-a-scheduled-integration/execution_logs.png)
