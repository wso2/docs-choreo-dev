# Develop an Event Handler Integration

An Event Handler integration executes predefined actions in response to specific events. Choreo simplifies the process of creating and deploying such integrations.

This guide walks you through the steps to create and deploy an Event Handler integration using WSO2 MI and Choreo. 

In this guide, you will build a simple Event Handler integration that listen on RabbitMQ and show the messages when new messages are available on RabbitMQ.

## Prerequisites

Before you try out the steps in this guide, complete the following:

  - If you are signing in to the Choreo Console for the first time, create an organization as follows:
    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.
       This creates the organization and opens the **Project Home** page of the default project created for you.

  - Configure RabbitMQ

  - Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples), which contains the sample integration for this guide.
## Step 1: Create an Event Handler integration component

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page. 
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click **Event Handler**.
4. Enter a unique name and a description for the component. You can use the name and description given below:

    | **Field**       | **Value**              |
    |-----------------|------------------------|
    | **Component Name**        | `SalesOrderListener`   |
    | **Description** | `RabbitMQ integration` |

5. Click **Next**.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**.
7. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials, and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:<br/><br/>- Read and write access to code and pull requests.<br/><br/>- Read access to issues and metadata.<br/><br/>You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only used to send pull requests to a user repository. Choreo will not directly push any changes to a repository.


8. In the **Connect Repository** pane, enter the following information:

    | **Field**             | **Description**                                                                |
    |-----------------------|--------------------------------------------------------------------------------|
    | **Organization**    | Your account                                                                   |
    | **Repository** | The repository you created by following the steps in the prerequisites section |
    | **Branch**            | **`main`**                                                                     |

9. Select **WSO2 MI** as Buildpack.

    !!! tip
    	    - **Buildpack** specifies the type of build to run depending on the implementation of the component. It converts the integration code into a Docker image that can run on Choreo cloud. If an integration is developed using [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/), select **Micro Integrator** as the buildpack. If an integration is developed using the [Ballerina language](https://ballerina.io), select **Ballerina** as the buildpack. 

          - **Project Path** specifies the location of the project to build the component.

10. Select **mi-rabbitmq-listener** as the **WSO2 MI Project Directory**. 
11. click **Create**.

## Step 2: Deploy the integration

To deploy the integration, follow the steps given below:

1. In the left navigation menu, click **Deploy**.
2. In the **Build Area** card, click **Configure & Deploy**.
3. In the **Configure & Deploy** pane, click **Add** on **Environment Variables**
4. Specify values as follows for the environment variables:

   | **Field**     | **Value**                                        |
       | ------------- |--------------------------------------------------|
   | **HOSTNAME**    | Hostname of your RabbitMQ server                 |
   | **VHOST**  | Virtual hostname of your RabbitMQ server         |
   | **USERNAME** | Username for connecting to RabbitMQ.             |
   | **PASSWORD**     | Password associated with the RabbitMQ username.|

5. Click **Deploy**. This deploys the Event Handler integration to the development environment and indicates the **Deployment Status** as **Active** in the **Development** card.


## Step 3: Test the integration

To test the integration, follow the step given below:

- Send a Sales Order Message
    - Send a sample sales order message to the **SalesOrderQueue** on RabbitMQ server.
        - Example JSON:
      ```json
      {
          "order_id": "12345",
          "customer_name": "John Doe",
          "product": "Widget",
          "quantity": 10,
          "total_amount": 100.00
      }
      ```
    - Observe the logs:
        - In the left navigation menu, click **Logs** and **Runtime Logs**.
        - The order message should appear in the logs.
  
Now you have gained hands-on experience in creating, configuring, and deploying an Event Handler integration.
 
