# Integrate Unit Tests into the Build Pipeline

Choreo allows you to integrate unit tests into the build pipeline, enabling code validation before deployment. This ensures early testing of code changes, reducing the risk of defects in production.

Choreo supports unit testing for the `Java`, `Python`, `Node.js`, `Go`, `.Net`, `WSO2 Micro Integrator (MI)` and `Ballerina` build presets.

## Write tests

You can create unit tests in your source code using standard testing libraries for your respective programming language or framework. Below are examples for WSO2 MI and Ballerina projects.

For step-by-step instructions on creating unit tests for WSO2 Micro Integrator projects, see [Creating a Unit Test Suite](https://mi.docs.wso2.com/en/latest/develop/creating-unit-test-suite/). For step-by-step instructions on writing unit tests for Ballerina projects, see [Test Ballerina Code](https://ballerina.io/learn/test-ballerina-code/structure-tests/). 

Once you have written your unit tests, commit them to your source code repository.

## Enable unit tests

To enable unit tests in the build pipeline for a component with unit tests in its source code, follow these steps:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the **Component Listing** pane, click the component for which you want to enable unit tests.
3. In the left navigation menu, click **Build**.
4. On the **Build** page, click to edit **Build Configurations**.
5. Turn on the **Unit Test** toggle.
6. Click **Save**.

## Trigger a new build

Once you have enabled unit tests, trigger a new build to run them by clicking **Build Latest**. This starts a new build with unit tests enabled. You can monitor its progress in the **Build Details** pane.

## View failed unit tests

If a unit test fails during the build, Choreo will stop the build to prevent faulty code from being deployed. To troubleshoot, click **View Details** for the failed build and expand the failed unit test step to review the test output logs.

## Explore samples

To see unit tests in action within the build pipeline, try out these samples:

| Build Preset | Repository Link |
|--------------|-----------------|
| WSO2 MI | [Helloworld MI Service](https://github.com/wso2/choreo-samples/tree/main/hello-world-mi) |
| Ballerina | [Ballerina Greeting Service](https://github.com/wso2/choreo-samples/tree/main/greeting-service) |
| Python | [Hello World Python Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-python-task) |
| NodeJS | [Hello World NodeJS Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-nodejs-task) |
| Java | [Hello World JAVA Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-java-task) |
| .Net | [Hello World .NET Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-dotnet-task) |
| Go | [Go Greeting Service](https://github.com/wso2/choreo-samples/tree/main/greeting-service-go) |

## Supported Test Frameworks

| Build Preset | Supported Test Frameworks |
|--------------|--------------------------|
| WSO2 MI | • Built-in test framework |
| Ballerina | • Built-in test framework |
| Python | • pytest<br>• unittest (default) |
| Java | • JUnit (via Maven)<br>• JUnit (via Gradle)<br>• TestNG (via Maven/Gradle) |
| Node.js | • Jest<br>• Mocha<br>• Any framework with `npm test` or `yarn test` script |
| Go | • Go's built-in testing package |
| .NET | • MSTest<br>• NUnit<br>• xUnit |
