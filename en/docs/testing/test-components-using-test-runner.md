# Test applications using Test Runner Component

Test Runner Component simplifies the process of running automated tests against your applications hosted in Choreo. It enables developers to test applications with different configurations in different environments, improving their confidence and providing a clearer view of the application state.

Tests can be implemented in widely used programming languages such as Go, Java, Javascript, Python etc. Also you can create a test runner component by providing a Dockerfile with a set of test scripts or by simply providing a set of Postman Collections.

=== "Go"
    Let's write some tests in Go and run those tests in Choreo.

    ### Create a test runner component using a Go buildpack

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/cloud-native-app-developer) and sign in. This opens the project home page.
    2. Click **Create** button in the Component Listing section. Choose **Test Runner** component type.
    3. Enter a unique name and a description for the test runner component. For this guide, let's enter the following values:

       | Field       | Value                         |
       | ----------- | ----------------------------- |
       | Name        | Go Test Runner                |
       | Description | Test Runner implemented in Go |

    4. Click **Github** tab and provide the required source repository details.

       | Field        | Value          |
       | ------------ | -------------- |
       | Organization | wso2           |
       | Repository   | choreo-samples |
       | Branch       | main           |

    5. Choose the **Go** buildpack from the buildpack tiles.
    6. Choose Project directory and Go version. For this example, let's consider the following values.

       | Field                | Value           |
       | -------------------- | --------------- |
       | Go Project Directory | /test-runner-go |
       | Language Version     | 1.x             |

       !!! note
       By default, **main.go** file is considered as the entrypoint. Default entrypoint can be overriden by providing a Procfile.

    7. Click **Create**. Once the component creation is complete, you will see the component overview page.

    You have successfully created a Test Runner component using a Go buildpack. Now let's build and run the tests.

    ### Building the test runner component and executing the tests

    1. Go to **Deploy** page from the left main menu.
    2. Click **Deploy** to build and deploy the test runner component. If you want to provide additional configuration or secrets, use the **Configure and Deploy** option.
    3. Once the deployment is successful, go to **Execute** page from the left main menu.
    4. Choose the environment from the environment drop down and click **Run Now** button to trigger a test execution.
    5. A new execution will be shown in the execution list and click on the execution list item to view the test results.

=== "Javascript"
    Let's write some tests in Javascript and run those tests in Choreo.

    ### Create a test runner component using a NodeJS buildpack

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/cloud-native-app-developer) and sign in. This opens the project home page.
    2. Click **Create** button in the Component Listing section. Choose **Test Runner** component type.
    3. Enter a unique name and a description for the test runner component. For this guide, let's enter the following values:

       | Field       | Value                                 |
       | ----------- | ------------------------------------- |
       | Name        | Javascript Test Runner                |
       | Description | Test Runner implemented in Javascript |

    4. Click **Github** tab and provide the required source repository details.

       | Field        | Value          |
       | ------------ | -------------- |
       | Organization | wso2           |
       | Repository   | choreo-samples |
       | Branch       | main           |

    5. Choose the **NodeJS** buildpack from the buildpack tiles.
    6. Choose Project directory and Go version. For this example, let's consider the following values.

       | Field             | Value                   |
       | ----------------- | ----------------------- |
       | Project Directory | /test-runner-javascript |
       | Language Version  | 18.x.x                  |

    7. Click **Create**. Once the component creation is complete, you will see the component overview page.

    You have successfully created a Test Runner component using a NodeJS buildpack. Now let's build and run the tests.

    ### Building the test runner component and executing the tests

    1. Go to **Deploy** page from the left main menu.
    2. Click **Deploy** to build and deploy the test runner component. If you want to provide additional configuration or secrets, use the **Configure and Deploy** option.
    3. Once the deployment is successful, go to **Execute** page from the left main menu.
    4. Choose the environment from the environment drop down and click **Run Now** button to trigger a test execution.
    5. A new execution will be shown in the execution list and click on the execution list item to view the test results.

=== "Python"
    Let's write some tests in Python and run those tests in Choreo.

    ### Create a test runner component using a Python buildpack

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/cloud-native-app-developer) and sign in. This opens the project home page.
    2. Click **Create** button in the Component Listing section. Choose **Test Runner** component type.
    3. Enter a unique name and a description for the test runner component. For this guide, let's enter the following values:

       | Field       | Value                                 |
       | ----------- | ------------------------------------- |
       | Name        | Python Test Runner                |
       | Description | Test Runner implemented in Python |

    4. Click **Github** tab and provide the required source repository details.

       | Field        | Value          |
       | ------------ | -------------- |
       | Organization | wso2           |
       | Repository   | choreo-samples |
       | Branch       | main           |

    5. Choose the **Python** buildpack from the buildpack tiles.
    6. Choose Project directory and Go version. For this example, let's consider the following values.

       | Field             | Value                   |
       | ----------------- | ----------------------- |
       | Project Directory | /test-runner-python |
       | Language Version  | 3.10.x                  |

    7. Click **Create**. Once the component creation is complete, you will see the component overview page.

    You have successfully created a Test Runner component using a NodeJS buildpack. Now let's build and run the tests.

    ### Building the test runner component and executing the tests

    1. Go to **Deploy** page from the left main menu.
    2. Click **Deploy** to build and deploy the test runner component. If you want to provide additional configuration or secrets, use the **Configure and Deploy** option.
    3. Once the deployment is successful, go to **Execute** page from the left main menu.
    4. Choose the environment from the environment drop down and click **Run Now** button to trigger a test execution.
    5. A new execution will be shown in the execution list and click on the execution list item to view the test results.

=== "Java"
    Let's write some tests in Java and run those tests in Choreo.

    ### Create a test runner component using a Java buildpack

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/cloud-native-app-developer) and sign in. This opens the project home page.
    2. Click **Create** button in the Component Listing section. Choose **Test Runner** component type.
    3. Enter a unique name and a description for the test runner component. For this guide, let's enter the following values:

       | Field       | Value                                 |
       | ----------- | ------------------------------------- |
       | Name        | Java Test Runner                |
       | Description | Test Runner implemented in Java |

    4. Click **Github** tab and provide the required source repository details.

       | Field        | Value          |
       | ------------ | -------------- |
       | Organization | wso2           |
       | Repository   | choreo-samples |
       | Branch       | main           |

    5. Choose the **Java** buildpack from the buildpack tiles.
    6. Choose Project directory and Jaav version. For this example, let's consider the following values.

       | Field             | Value                   |
       | ----------------- | ----------------------- |
       | Project Directory | /test-runner-java |
       | Language Version  | 17                 |

    7. Click **Create**. Once the component creation is complete, you will see the component overview page.

    You have successfully created a Test Runner component using a Java buildpack. Now let's build and run the tests.

    ### Building the test runner component and executing the tests

    1. Go to **Deploy** page from the left main menu.
    2. Click **Deploy** to build and deploy the test runner component. If you want to provide additional configuration or secrets, use the **Configure and Deploy** option.
    3. Once the deployment is successful, go to **Execute** page from the left main menu.
    4. Choose the environment from the environment drop down and click **Run Now** button to trigger a test execution.
    5. A new execution will be shown in the execution list and click on the execution list item to view the test results.

=== "Dockerfile"
    For programming languages that doesn't support buildpacks or for advanced use cases, you can define your own Dockerfile and run test workloads. Refer to <BYOC link> for more information.
    
=== "Postman"
    Let's write some tests using Postman collections and run those tests in Choreo.

    ### Create a test runner component using a Postman buildpack

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/cloud-native-app-developer) and sign in. This opens the project home page.
    2. Click **Create** button in the Component Listing section. Choose **Test Runner** component type.
    3. Enter a unique name and a description for the test runner component. For this guide, let's enter the following values:

       | Field       | Value                                            |
       | ----------- | ------------------------------------------------ |
       | Name        | Postman Test Runner                              |
       | Description | Test Runner implemented with Postman Collections |

    4. Click **Github** tab and provide the required source repository details.

       | Field        | Value          |
       | ------------ | -------------- |
       | Organization | wso2           |
       | Repository   | choreo-samples |
       | Branch       | main           |

    5. Choose the **Postman** buildpack from the buildpack tiles.
    6. Choose a directory with Postman collections. For this example, let's consider the following values.

       | Field             | Value                   |
       | ----------------- | ----------------------- |
       | Postman Directory | /postman-collection-dir |

    7. Click **Create**. Once the component creation is complete, you will see the component overview page.

    You have successfully created a Test Runner component using Postman buildpack. Now let's build and run the tests.

    ### Building the test runner component and executing the tests

    1. Go to **Deploy** page from the left main menu.
    2. Click **Deploy** to build and deploy the test runner component. If you want to provide additional configuration or secrets, use the **Configure and Deploy** option.
    3. Once the deployment is successful, go to **Execute** page from the left main menu.
    4. Choose the environment from the environment drop down and click **Run Now** button to trigger a test execution.
    5. A new execution will be shown in the execution list and click on the execution list item to view the test results.


