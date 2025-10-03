# Integrate Unit Tests into the Build Pipeline

Choreo allows you to integrate unit tests into the build pipeline, enabling code validation before deployment. This ensures early testing of code changes, reducing the risk of defects in production.

Currently, Choreo supports unit tests only for the `WSO2 MI` and `Ballerina` build presets.

## Write tests

You can create unit tests in your source code using standard testing libraries. For step-by-step instructions on creating unit tests for WSO2 Micro Integrator projects, see [Creating a Unit Test Suite](https://mi.docs.wso2.com/en/latest/develop/creating-unit-test-suite/). For step-by-step instructions on writing unit tests for Ballerina projects, see [Test Ballerina Code](https://ballerina.io/learn/test-ballerina-code/structure-tests/). 

Once you have written your unit tests, commit them to your source code repository.

## Enable unit tests

To enable unit tests in the build pipeline for a component that you have created with source code containing unit tests, follow these steps:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the **Component Listing** pane, click the component for which you want to enable unit tests.
3. In the left navigation menu, click **Build**.
4. On the **Build** page, click to edit **Build Configurations**.
5. Turn on the **Unit Test** toggle.
6. Click **Save**.

## Trigger a new build

Once you have enabled unit tests, trigger a new build to run them by clicking **Build Latest**. This starts a new build with unit tests enabled. You can monitor its progress in the **Build Details** pane.

## View failed unit tests

If a unit test fails during the build, Choreo will fail and stop the build to prevent deploying faulty code. You can view the detailed logs for troubleshooting by clicking **View Details** corresponding to the failed build, then expanding the failed unit test step to view the logs.

## Explore a sample

To see unit tests in action within the build pipeline, try out the [WSO2 MI helloworld](https://github.com/wso2/choreo-samples/tree/main/hello-world-mi) sample or the [Ballerina hello world](https://github.com/wso2/choreo-samples/tree/main/greeting-service) sample.


