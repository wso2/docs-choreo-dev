# Create Your First Event-Triggered Integration

Event-triggered integrations can help automate business processes and reduce manual work. Choreo allows you to effortlessly create and manage event-triggered integrations, eliminating the need for a complicated infrastructure setup.

Consider a scenario where a developer has already created an integration using [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/) and wants to deploy it on Choreo to create an event-triggered integration. This tutorial walks you through the steps to accomplish this requirement. 

In this tutorial, you will do the following:

- Create an event-triggered integration component in Choreo by connecting a GitHub repository with a sample application developed using [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/). 

	!!! info
    	    The sample application listens to a [RabbitMQ](https://www.rabbitmq.com/) topic and logs incoming messages.

- Deploy the integration component to the development environment in Choreo.
- Test the integration component.


!!! tip "Before you begin"
    - Fork the [sample application](https://github.com/wso2/choreo-examples) GitHub repository. For this tutorial, you need the [simple-rabbitmq-listener](https://github.com/wso2/choreo-examples/tree/main/ipaas/micro-integrator/simple-rabbitmq-listener) in the **choreo-sample** repository.  
 
    - Use an existing RabbitMQ instance or start a new [RabbitMQ](https://www.rabbitmq.com/download.html) instance on a server that can be accessed via the internet. Obtain the `username`, `hostname`, `password`, and `vhost` from the RabbitMQ instance to use later as environment variables. 

!!! tip "RabbitMQ instance"
    You can use a service such as [CloudAMQP](https://www.cloudamqp.com/) to easily set up a RabbitMQ instance.

## Step 1: Create the integration component

Let's create the integration component by following the steps given below:

1. Go to [https://console.choreo.dev/?profile=ipaas](https://console.choreo.dev/?profile=ipaas) and sign in to the Choreo Console.

2. On the **Home** page, click **+Create Project**.

3. Enter a unique name and a description for the project, and select a region.

4. Click **Create**.  

5. Go to the **Event-Triggered Integration** card, click **Create**, and enter the following information:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Name**        | `MQTT listener`           |
    | **Description** | `My sample listener`    |

6. In the **Access Mode** field, leave the default selection (i.e., **External: API is publicly accessible**) unchanged.
    
7. Click **Next**.

8. To allow Choreo to access your GitHub account, click **Authorize with GitHub**. 

	!!! tip
    	    If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials, and select the [sample application](https://github.com/wso2/choreo-examples) GitHub repository to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

9. In the **Connect Repository** dialog box, enter the following information:

    | **Field**             | **Description**                                   |
    |-----------------------|---------------------------------------------------|
    | **GitHub Account**    | Your account                                      |
    | **GitHub Repository** | `choreo-examples`                                 |
    | **Branch**            | `main`                                            |
    | **Build Preset**      | Click **Micro Integrator**                        |
    | **Path**              | `ipaas/micro-integrator/simple-rabbitmq-listener` | 

	!!! tip
    	    - **Build Preset** specifies the type of build to run depending on the implementation of the component. It converts the integration code into a Docker image that can run on Choreo cloud. If an integration is developed using [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/), select **Micro Integrator** as the build preset. If an integration is developed using the [Ballerina language](https://ballerina.io), select **Ballerina** as the build preset. 

            - **Path** specifies the location of the project to build the component. 

10.  Click **Create**. This creates the component and takes you to the **Deploy** view, where you can proceed to deploy the component.


## Step 2: Deploy

Before you deploy the component, you must follow the steps given below to assign appropriate RabbitMQ credentials to the environment variables:

1. In the **Deploy** view, go to the **Build Area** card, and then click **Configure & Deploy**.
2. In the **Configure & Deploy** pane, add the environment variables as key-value pairs. You must use the values you obtain from your RabbitMQ instance as the values for the following keys: 

    | **Key**    |
    |------------|
    | HOSTNAME   | 
    | USERNAME   |
    | PASSWORD   | 
    | VHOST      | 

3. Click **Deploy**. 

4. Alternatively, you can go to the **Development** card and click **Manage Configs and Secrets**. This takes you to the [Configurations and secrets](../../deploy/devops/configs-and-secrets.md) page, where you can apply environment-specific configuration files, environment variables, and other secret resources to containers.
    1. Click **Create**.
    2. In **Step 1** page, select the type **Environment Variables** as the configuration type and click **Next**.
    3. Select **Create New** and enter a **Config Name**. Here, let's specify `environmentconf` as the **Config Name**.
    4. Click **Next**.
    5. Click **Finish**. Now you are ready to deploy the component to the developer environment. 
    6. Go to the **iPaaS** profile and click the `MQTT listener` component you created.
    7. Click **Deploy** on the left navigation, go to the **Build Area** card, and then click **Configure & Deploy**.
    8. Click **Deploy**. 

    !!! info
        You have to deploy the component manually the first time. Subsequent deployments will occur automatically because automatic deployment is enabled by default.

After Choreo deploys the component, you can proceed to test it.

## Step 3: Test

To test the component, you must publish messages to a designated topic in your RabbitMQ server.

1. Go to the RabbitMQ Management Console and publish the following message to the **SalesOrderQueue** topic.

    ```
    {
      "Orders": {
        "Order": {
          "Id": 1,
          "Price": 150
        }
      }
    }
    ```
   Now you are ready to observe the logs and see the message received by the integration component.

2. Go to the **iPaaS** profile and click the `MQTT listener` component you created.

3. Click **Observe** on the left navigation, go to the **Logs** section, and observe the message received by the integration component from the RabbitMQ server. You will see a message similar to the following:

    !!! info
            It can take a few minutes for messages to appear in the **Logs** section. To view the latest logs, click **Load Latest Logs**.

      ![Message entry in logs](../../assets/img/tutorials/event-triggered-integration/rabbitmq-message-in-logs.png){.cInlineImage-full}

      This confirms that the event-triggered integration is functioning as expected.

    !!! tip
            The `Observe` view allows you to observe statistics related to your Choreo component. For more information on observing a Choreo component, see the [Observability overview](../../observe-and-analyze/observe/observability-overview.md).


Congratulations! You have successfully created an event-triggered integration in Choreo with an existing integration, deployed the integration component, triggered an event, and tested the component.
