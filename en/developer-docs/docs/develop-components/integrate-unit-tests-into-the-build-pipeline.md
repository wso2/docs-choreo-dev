# Integrate Unit Tests into the Build Pipeline

Choreo allows you to integrate unit tests into the build pipeline, enabling code validation before deployment. This ensures early testing of code changes, reducing the risk of defects in production.

!!! note
        Currently, Choreo supports unit tests only for the `WSO2 MI` buildpack.

## How it works

### Step 1: Write tests

You can create unit tests in your source code using standard testing libraries. For step-by-step instructions on creating unit tests for WSO2 Micro Integrator projects, see [Creating a Unit Test Suite](https://mi.docs.wso2.com/en/latest/develop/creating-unit-test-suite/).

### Step 2: Enable unit tests

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the **Component Listing** pane, click the component for which you want to enable unit tests.
3. In the left navigation menu, click **Build**.
4. On the **Build** page, click to edit **Build Configurations**.
5. Turn on the **Unit Test** toggle.
6. Click **Save**.

### Step 3: Trigger a new build

- On the **Build** page, click **Build Latest**.

This starts a new build with unit tests enabled. You can monitor its progress in the **Build Details** pane.

## View failed unit tests

If a unit test fails during the build, you can view its detailed logs for troubleshooting. Click **View Details** corresponding to the failed build, then expand the failed unit test step to view the logs.

## Explore a sample

To see unit tests in action within the build pipeline, try out the [WSO2 MI helloworld](https://github.com/wso2/choreo-samples/tree/main/hello-world-mi) sample. 
