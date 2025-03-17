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
 - Fork the [Choreo examples repository](https://github.com/wso2/choreo-samples), which contains the [test runner sample](https://github.com/wso2/choreo-samples/tree/main/test-runner-go) for this guide.

You can develop tests in a language of your choice and execute them within the Choreo platform.

## Create a test runner component using a buildpack

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. Click **+Create** in the **Component Listing** section. 
3. Click the **Test Runner** card.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, select the **Use Public GitHub Repository** option and paste the [Choreo samples repository](https://github.com/wso2/choreo-samples) URL in the **Provide Repository URL** field to connect to it without requiring authorization from the [Choreo Apps](https://github.com/marketplace/choreo-apps) GitHub application. However, authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps) is necessary if you want to enable [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) for the component.

    !!! note
           The **Choreo GitHub App** requires the following permissions:

           - Read and write access to code and pull requests.
           - Read access to issues and metadata.
             
           You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is exclusively utilized for sending pull requests to a user repository. Choreo will not directly push any changes to a repository.

7. Under **Connect a Git Repository**, enter the following information:

    | **Field**              | **Value**          |
    |------------------------|--------------------|
    | **Organization**       | Your GitHub account|
    | **GitHub Repository**  | choreo-samples     |
    | **Branch**             | **`main`**         |
    | **Component Directory**| `/test-runner-go`  |

    You also can select a different language:


    === "Go"

        | Field                | Value           |
        | -------------------- | --------------- |
        | Component Directory | /test-runner-go |
        | Language Version     | 1.x             |
    
    === "JavaScript"

        | Field             | Value                   |
        | ----------------- | ----------------------- |
        | Component Directory | /test-runner-javascript |
        | Language Version  | 18.x.x                  |

    === "Python"

        | Field             | Value                   |
        | ----------------- | ----------------------- |
        | Component Directory | /test-runner-python |
        | Language Version  | 3.10.x                  |

    === "Java"

        | Field             | Value                   |
        | ----------------- | ----------------------- |
        | Component Directory | /test-runner-java |
        | Language Version  | 17                 |

    === "Dockerfile"

        For programming languages that doesn't support buildpacks or for advanced use cases, you can define your own Dockerfile and run test workloads. Refer to <BYOC link> for more information.

    === "Postman"

        | Field             | Value                   |
        | ----------------- | ----------------------- |
        | Component Directory | /postman-collection-dir |


6. Under **Buildpack**, select a buildpack based on the language of your choice.
7. Based on the buildpack you chose, go to the relevant tab above and enter the respective values for the **Language version** field

4. Enter a display name, unique name, and description for the test runner component. For this guide, enter the following values:
    
    !!! info
         In the **Component Name** field, you must specify a name to uniquely identify the component in various contexts. The value is editable only at the time you create the component. You cannot change the name after you create the component.

    | Field                    | Value                           |
    | -------------------------|-------------------------------- |
    | Component Display Name   | `Go Test Runner`                |
    | Component Name           | `go-test-runner`                |
    | Description              | `Test Runner implemented in Go` |

8. Click **Create**. 

You have successfully created a Test Runner component using a buildpack of your choice. Now let's build and run the tests.

## Build and deploy the test runner component to execute the tests

1. In the left navigation menu, click **Build**.
2. In the **Builds** pane, click **Build Latest**. 
3. On the left navigation, click **Deploy**.
4. In the **Set Up** card, click **Deploy** to deploy the test runner component. If you want to provide additional configuration or secrets, use the **Configure and Deploy** option from the list.
5. Once the deployment is successful, click **Execute** in the left navigation menu.
6. Select the environment from the environment list and click **Run Now** to trigger a test execution.
7. Once the execution is completed it is listed on the execution page. This may take some time. Once the execution is listed, you can click on a particular execution to view the test result(s).

    !!! info "Inject dynamic values into your application as command-line arguments"
         If you want to inject dynamic values into your application as command-line arguments when you run a test runner component, follow the steps given below:

           1. Click the drop-down icon next to **Run Now** and then click **Run with Arguments**. 
           2. In the **Runtime Arguments** pane that opens, enter the arguments you want to pass to your application. 
           3. Click **Execute**. This triggers the test runner with the specified arguments.
    
        !!! tip
            For `Postman Collections`:

            -  The default behavior is to run all the collections in the specified directory.
            -  To run specific collections in the directory, use `-f` or `--files` as the first argument, followed by a comma-separated list of collections.
            -  Since Choreo uses [Newman](https://www.npmjs.com/package/newman) internally to run Postman Collections, you can pass any valid Newman arguments.
