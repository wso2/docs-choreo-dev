# Build Your First Integration

Choreo simplifies the process of building, deploying, and managing integration components, making it easier to integrate APIs, microservices, applications, and data across different languages and formats.

In this tutorial, you will learn how to create a simple integration scenario with Choreo using Ballerina. You will call an HR endpoint to get an employee ID list, then call the employee details endpoint, aggregate the results, and send back a response. 

## Prerequisites

Before you try out this tutorial, complete the following:

1. Create an empty public repository in your GitHub account to save the component implementation.
2. If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

## Step 1: Create an integration as an API component

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step. 
3. Go to the **Integration as an API** card and click **Create**.
4. Enter a unique name and a description for the component. You can enter the name and description given below:

    | **Field**       | **Value**                        |
    |-----------------|----------------------------------|
    | **Name**        | `FirstIntegration`               |
    | **Description** | `First integration as a service` |

5. Click **Next**.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**.
7. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials, and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:<br/><br/>- Read and write access to code and pull requests.<br/><br/>- Read access to issues and metadata.<br/><br/>You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only used to send pull requests to a user repository. Choreo will not directly push any changes to a repository.


8. In the **Connect Repository** pane, enter the following information:

    | **Field**             | **Value**                                                                |
    |-----------------------|--------------------------------------------------------------------------------|
    | **GitHub Account**    | Your account                                                                   |
    | **GitHub Repository** | The repository you created by following the steps in the prerequisites section |
    | **Branch**            | **`main`**                                                                     |
    | **Buildpack**      | **Ballerina**.                                                           |
    | **Project Path**      | Since the path is empty, select **Start with a sample**.                       | 

	!!! tip
    	    - **Buildpack** specifies the type of build to run depending on the implementation of the component. It converts the integration code into a Docker image that can run on Choreo cloud. If an integration is developed using [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/), select **Micro Integrator** as the buildpack. If an integration is developed using the [Ballerina language](https://ballerina.io), select **Ballerina** as the buildpack. 

          - **Project Path** specifies the location of the project to build the component.

9. Click **Create**. Choreo initializes the component with the sample implementation and sends a pull request to the GitHub repository you connected.
10. Review and merge the pull request.

## Step 2: Implement the integration logic

 1. To clone your GitHub repository to your local machine, run the following command in your terminal or command prompt:
    
    `git clone <repository\_url>`

     Replace `<repository\_url>` with the URL of your GitHub repository.

 2. Open the cloned repository in Visual Studio Code. To do this, launch Visual Studio Code, click **File** > **Open Folder**, and select the cloned GitHub repository folder.
 3. Edit the `service.bal` file as follows to implement the integration logic:

     a. Add the following line to create a configurable variable for the HR endpoint:

       ```
       configurable string hrEndpoint = ?;

       ```
  
     b. Add the following lines of code to define ballerina records related to the integration use case:

       ```
       type Request record {|
       int[] employeeIds;
       |};

       type Employees record {|
       json[] employeeDetails;
       |};

       ```
 
     c. Implement the rest of the logic to call invoke the HR endpoint, retrieve the employee ID list, call the employee details endpoint, and aggregate the results before responding. The code should resemble the following structure:

       ```ballerina
       import ballerina/http;

       // Define configurable variables, including the HR endpoint
       configurable string hrEndpoint = ?;

       type Request record {|
           int[] employeeIds;
       |};

       type Employees record {|
           json[] employeeDetails;
       |};

       service / on new http:Listener(9090) {
           // Define your resource functions here
           resource function post employees(@http:Payload Request payload) returns Employees|error? {
               http:Client locationEP = check new (hrEndpoint);
               int[] idList = payload.employeeIds;
               json[] employeInfoList = [];
               foreach int id in idList {
                   json empResponseJson = check locationEP->get(string `/employee/${id}`);
                   employeInfoList.push(empResponseJson);
               }
               Employees aggregatedResponse = {employeeDetails: employeInfoList};
               return aggregatedResponse;
           }
       }
       ```
       
 4. Save your changes in Visual Studio Code.
 5. To commit the changes to the GitHub repository, run the following commands in your terminal or command prompt:

    ```git
       git add  
       git commit -m "Implement integration logic" 
       git push
    ``` 

## Step 3: Deploy the Ballerina integration

To deploy the Ballerina integration, follow the steps given below:

1. In the Choreo Console left navigation menu, click **Deploy**.
   Since auto-deploy on commit is enabled by default for the component, you will see a failed build caused by a missing configuration.
2. To resolve the failure, follow the steps given below:
   1. In the **Build Area** card, click **Configure & Deploy**.
   2. In the **Configure & Deploy** pane, add `https://samples.choreoapps.dev/company/hr` as the **hrEndpoint** configurable variable.
   3. Click **Next**. This displays details of the endpoint ready to be deployed. 
   4. Click the edit icon next to the endpoint name.
   5. Change the **Network Visibility** to **Public** and click **Update**.
   4. Click **Deploy**. This deploys the updated Ballerina integration to the development environment.

      The **Development** card indicates the **Deployment Status** as **Active** when the Ballerina integration is deployed.

## Step 4: Test the Ballerina integration

To test the Ballerina integration, follow the steps given below:

1. In the Choreo Console left navigation menu, click **Test** and then click **Console**.
2. In the **OpenAPI Console** pane, select **Development** from the environment drop-down list.
3. Click to expand the **POST /employees** operation.
4. Click **Try it out** and enter the following as the request body:

    `{  "employeeIds": [ 1, 2, 3 ] } `
 
5. Click **Execute**. This sends a request to your deployed integration.
6. Observe the response returned by the integration in the **Response body** section. If the integration works as expected, you should see a successful response with the aggregated employee data.

Choreo also allows you to test your integration using Postman. For instructions, see [Test with Postman](../testing/test-apis-using-postman.md).

## Step 5: Observe the Ballerina integration

To observe the Ballerina integration, click **Observability** in the Choreo Console left navigation menu. 

On the **Observe** page, you can see various graphs and charts that display the performance metrics of your integration, such as throughput and latency. These metrics can help you monitor and analyze the behavior of your integration.

The graphs are updated in real-time. You can observe how your test request from Step 4 above is reflected in the metrics. This allows you to identify any performance bottlenecks or issues with your integration. Additionally, you can view log messages generated by your integration. This can help you identify and troubleshoot any errors or issues in the implementation.

If necessary, you can configure alerts for your integration by setting up specific alert conditions based on metrics or log patterns. This can help you stay informed about any critical issues affecting your integration.

## Step 6: Publish the integration component

To publish the integration component, follow the steps given below:

1. In the Choreo Console left navigation menu, click **Manage** and then click **Lifecycle**. This opens the **Lifecycle Management** pane, where you can see the different lifecycle stages that an API can be in. You can see that the current lifecycle stage is **Created**.
2. In the **Lifecycle Management** pane, click **Publish**. This changes the lifecycle stage to **Published**. 

   Now the integration is published and is available for subscription in the Choreo Developer Portal. To access the Developer Portal via the **Lifecycle Management** pane, click **Go to Devportal**.

In the Choreo Developer Portal, you can view your published integration component, manage subscriptions for it, and generate access tokens for testing purposes.

Now you have successfully created, implemented, deployed, tested, observed, and published a Ballerina-based integration component using Choreo.
