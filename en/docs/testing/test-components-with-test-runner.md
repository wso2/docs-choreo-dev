# Test Components with Test Runner

Test Runner is a component type in Choreo that simplifies the process of running automated tests against your components deployed in Choreo. This capability helps developers evaluate applications in different setups and environments, leading to more confidence and improved visibility into the application's status.

You can create tests using popular programming languages like Go, Java, JavaScript, Python, and more. Additionally, you have the option to build a test runner component by supplying a Dockerfile containing a series of test scripts or by offering a set of Postman Collections.

## Prerequisites

Before you try out the steps in this guide, complete the following:

 - If you are signing in to the Choreo Console for the first time, create an organization as follows:
    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.
       This creates the organization and opens the **Project Home** page of the default project created for you.
 - Fork the [Choreo examples repository](https://github.com/wso2/choreo-samples), which contains the sample for this guide.


We can develop tests in a language of our choice and execute them within the Choreo platform.

## Create a test runner component using a buildpack

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. Click **+Create** in the **Component Listing** section. 
3. Click the **Test Runner** card.
4. Enter a unique name and a description for the test runner component. For this guide, enter the following values:

    | Field       | Value                         |
    | ----------- | ----------------------------- |
    | Name        | Go Test Runner                |
    | Description | Test Runner implemented in Go |

5. Click **GitHub** tab and enter the following information:

    | **Field**                     | **Description**    |
    |-------------------------------|--------------------|
    | **GitHub Account**            | Your account       |
    | **GitHub Repository**         | choreo-samples |
    | **Branch**                    | **`main`**         |

6. Under **Buildpack**, select a buildpack based on the language of your choice.
7. Based on the buildpack you chose, select the relevant tab below and enter the respective values for the **Project directory** and **Go version** fields:

    === "Go"

        | Field                | Value           |
        | -------------------- | --------------- |
        | Go Project Directory | /test-runner-go |
        | Language Version     | 1.x             |
    
    === "JavaScript"

        | Field             | Value                   |
        | ----------------- | ----------------------- |
        | Project Directory | /test-runner-javascript |
        | Language Version  | 18.x.x                  |

    === "Python"

        | Field             | Value                   |
        | ----------------- | ----------------------- |
        | Project Directory | /test-runner-python |
        | Language Version  | 3.10.x                  |

    === "Java"

        | Field             | Value                   |
        | ----------------- | ----------------------- |
        | Project Directory | /test-runner-java |
        | Language Version  | 17                 |

    === "Dockerfile"

        For programming languages that doesn't support buildpacks or for advanced use cases, you can define your own Dockerfile and run test workloads. Refer to <BYOC link> for more information.

    === "Postman"

        | Field             | Value                   |
        | ----------------- | ----------------------- |
        | Postman Directory | /postman-collection-dir |
    
8. Click **Create**. 

You have successfully created a Test Runner component using a buildpack of you choice. Now let's build and run the tests.

## Building the test runner component and executing the tests

1. On the left navigation, click **Build**.
2. Under **Builds**, click **Build**.
3. Select the latest commit and click **Build**. Once Choreo completes the build process, you can proceed to the next step. 
4. On the left navigation, click **Deploy**.
4. In the **Set Up** card, click **Deploy** to deploy the test runner component. If you want to provide additional configuration or secrets, use the **Configure and Deploy** option from the list.
3. Once the deployment is successful, on the left menu, click **Execute**.
4. Select the environment from the environment list and click **Run Now** to trigger a test execution.
5. Once the execution is completed it is listed in the execution page. This may take sometime. Once the execution is listed, you can click on a particular execution to view the test result(s).
