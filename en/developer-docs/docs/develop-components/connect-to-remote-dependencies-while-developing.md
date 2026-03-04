To streamline local development and verification of {{ product_name }} components that depend on [{{ product_name }} connections](../../{{ product_name }}-concepts/connections/), you can bridge your local environment with the remote/deployed project. This is facilitated by the {{ product_name }} CLI and VS Code extension, which create a secure connected environment enabling real-time testing and validation.

This connected environment acts as a temporary link to your {{ product_name }} deployment. It provides your local component with the necessary connection configurations injected as environment variables. Furthermore, any outgoing HTTP requests from this local environment are securely routed to your deployed {{ product_name }} environment. This enables you to:

- **Securely Access Dependencies**: Local components automatically get connection details for {{ product_name }} services, third-party services, and databases.
- **Test Integrations Early**: Verify your local component's interaction with {{ product_name }} services before pushing code, using the same environment configuration.
- **Consistent Testing Across Environments**: Ensure reliable and secure test results in both local and CI environments.

## Using CLI

The {{ product_name }} CLI allows you to create a local subshell that is bridged to your deployed {{ product_name }} environment.

### Prerequisites

1. Install the {{ product_name }} CLI. See the [setup guide](../../{{ product_name }}-cli/get-started-with-the-{{ product_name }}-cli/) for instructions.
2. Login to {{ product_name }} via CLI

    ``` sh
    {{ cli_root_name }} login
    ```

!!! tip
    For testing in CI environments, login to {{ product_name }} CLI using a [personal access token](../../{{ product_name }}-cli/manage-authentication-with-personal-access-tokens/).    

### Connect to a project

1. The following command will create a subshell that will be connected to your remote project environment.

    ``` sh
    {{ cli_root_name }} connect --project default-project
    ```

2. Execute the command to run your component locally within the {{ product_name }} subshell. For example:

    ``` sh
    go run main.go
    ```

Once your application is running within the {{ product_name }} subshell:

- All connection configurations belonging to the selected project will be automatically injected into your application as environment variables.
- Outgoing network requests made by your application to its dependent services will be securely redirected by the {{ product_name }} CLI to the relevant services in your remote environment.

Your application will now run connected to its remote dependencies for local development.


### Advanced examples

- **Focusing Connection on a Specific Component**

    By default, all connection configurations within your project are injected into the subshell. If you only want to focus on a particular component's connections, you can pass it as a flag:

    ``` sh
    {{ cli_root_name }} connect --project default-project --component my-component
    ```

- **Connecting to Different Environments**

    By default, connection is established with your development environment. To connect to other *non-critical* environments, use the `{{ cli_root_name }} connect` command with the following flag:

    ``` sh
    {{ cli_root_name }} connect --project default-project --env test-env
    ```

- **Skipping Remote Dependencies**

    Sometimes, you might run certain dependencies locally. By default, `{{ cli_root_name }} connect` injects all connection configurations. To use your locally running dependencies instead and skip injecting specific configurations, use the {{ product_name }} connect command with the following flags:

    ``` sh
    {{ cli_root_name }} connect --project default-project --skip-connection test-conn1 --skip-connection test-conn2 
    ```

- **Combining Connect with Application Startup**

    You might want to combine the `{{ cli_root_name }} connect` command and your application's start command into a single command. This automates the creation of the subshell and the launch of your application, reducing manual steps in your development process:

    ``` sh
    {{ cli_root_name }} connect --project default-project -- go run main,go
    ```

!!! tip
    Execute the command `{{ cli_root_name }} connect --help` to explore all available advanced options and flags.

             
## Debug using VS Code

The {{ product_name }} VS Code extension allows you to ensure that applications you launch and debug via VS Code are connected to your {{ product_name }} deployed project environment.

!!! info "Note"
     This section applies when launching your local application through the VS Code debugger. For users starting their application in a terminal within VS Code, please see the ["Using CLI"](./#using-cli) section for the relevant instructions.

### Prerequisites

1. Install the {{ product_name }} VS Code Extension [(Refer Guide)](../develop-components-using-vs-code/).
2. Login to {{ product_name }} via VS Code Extension.
3. Link your workspace directory to an existing {{ product_name }} project [(More information)](../develop-components-using-vs-code/#understand-the-project-context).

### Launch Application (Connected to Project)

1. Configure Your Application's [Launch Configuration](https://code.visualstudio.com/docs/debugtest/debugging-configuration).
2. Add the `"{{ product_name }}": true` property to your launch configuration to instruct the {{ product_name }} VS Code extension to launch your application in a subshell that's connected to the {{ product_name }} project that the workspace is associated with.

    ``` json
    {
        "version": "0.2.0",
        "configurations": [
            {
                "request": "launch",
                // ...Rest of your configuration
                // Add the following new property
                "choreoConnect": true
            }
        ]
    }
    ```

3. Launch your application using VS Code [(Refer Guide)](https://code.visualstudio.com/docs/debugtest/debugging-configuration#_start-a-debugging-session-with-a-launch-configuration).

### Advanced Configurations

You can customize the connection behavior to the remote project by modifying the launch configuration as shown below.

``` json
{
      "version": "0.2.0",
      "configurations": [
          {
              "request": "launch",
              // ...Rest of your configuration
              // Customize {{ product_name }} connect configurations
              "choreoConnect": {
                  "project": "default-project",
                  "component": "my-component",
                  "env": "test-env",
                  "skipConnection": ["test-conn1"]
              }
          }
      ]
}
```
    