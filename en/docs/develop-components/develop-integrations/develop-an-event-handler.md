# Develop an Event Handler

An Event Handler executes predefined actions in response to specific events. Choreo simplifies the process of creating and deploying such integrations.

This guide walks you through the steps to create and deploy an Event Handler using WSO2 MI and Choreo. 

In this guide, you will build a simple event handler that monitors RabbitMQ for new messages and displays them once they become available.

## Prerequisites

Before you try out the steps in this guide, complete the following:

  - If you are signing in to the Choreo Console for the first time, create an organization as follows:
    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.
       This creates the organization and opens the **Project Home** page of the default project created for you.

  - Use an existing RabbitMQ instance or start a new [RabbitMQ](https://www.rabbitmq.com/download.html) instance on a server that can be accessed via the internet. Obtain the `username`, `hostname`, `password`, and `vhost` from the RabbitMQ instance to use later as environment variables.

  - Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples), which contains the sample integration for this guide.

## Step 1: Create an event handler component

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page. 
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **Event Handler** card.
4. Enter a unique name and a description for the component. You can use the name and description given below:

    | **Field**          | **Value**              |
    |--------------------|------------------------|
    | **Component Name** | `SalesOrderListener`   |
    | **Description**    | `RabbitMQ integration` |

5. Click the **GitHub** tab
6. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials, and select the repository you created by forking [https://github.com/wso2/choreo-samples](https://github.com/wso2/choreo-samples) to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:<br/><br/>- Read and write access to code and pull requests.<br/><br/>- Read access to issues and metadata.<br/><br/>You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is only used to send pull requests to a user repository. Choreo will not directly push any changes to a repository.

7. Enter the following information:

    | **Field**             | **Description**              |
    |-----------------------|----------------------------- |
    | **Organization**      | Your GitHub account          |
    | **Repository**        | `choreo-samples`            |
    | **Branch**            | **`main`**                   |
    | **Buildpack**         | **WSO2 MI** because you are creating the REST API from a [WSO2 Integration Studio](https://wso2.com/micro-integrator/) project|
    | **Project Directory** | `mi-rabbitmq-listener` |                                                     |

    !!! tip
    	    - **Buildpack** specifies the type of build to run depending on the implementation of the component. It converts the integration code into a Docker image that can run on Choreo cloud. If an integration is developed using [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/), select **Micro Integrator** as the buildpack. If an integration is developed using the [Ballerina language](https://ballerina.io), select **Ballerina** as the buildpack. 

          - **Project Directory** specifies the location of the project to build the component.

8. Click **Create**.

## Step 2: Deploy the integration

To deploy the integration, follow the steps given below:

1. In the left navigation menu, click **Deploy**.
2. In the **Build Area** card, click **Configure & Deploy**.
3. In the **Configurations** pane, click **+ Add** corresponding to **Environment Variables** and add the following environment variables:
    
    !!! tip
    	    Use the values from your RabbitMQ instance as per the [Prerequisites](#prerequisites) section, for the environment variables.

    | **Name**     | **Value**                                         |
    | ------------- |--------------------------------------------------|
    | **HOSTNAME**  | Hostname of your RabbitMQ server                 |
    | **VHOST**     | Virtual hostname of your RabbitMQ server         |
    | **USERNAME**  | Username for connecting to RabbitMQ              |
    | **PASSWORD**  | Password associated with the RabbitMQ username   |

4. Click **Deploy**. This deploys the event handler to the development environment and indicates the **Deployment Status** as **Active** in the **Development** card.


## Step 3: Test the integration

To test the integration, follow the steps given below:

1. Send a sales order message to the **SalesOrderQueue** on the RabbitMQ server.
    - You can send a sample sales order message similar to the following:
      ```json
      {
          "order_id": "12345",
          "customer_name": "John Doe",
          "product": "Widget",
          "quantity": 10,
          "total_amount": 100.00
      }
      ```
2. Observe the logs:
    - In the left navigation menu, click **Logs** and then click **Runtime Logs**. You will see the order message in the logs.
  
Now you have gained hands-on experience in creating, configuring, and deploying an event handler.
 
