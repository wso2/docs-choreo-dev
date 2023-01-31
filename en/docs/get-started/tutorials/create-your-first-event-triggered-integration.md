# Create Your First Event-Triggered Integration with Integration Studio

Consider a scenario where a developer has created an integration using [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/) and wants to use it as an event-triggered integration by deploying it in Choreo. In this tutorial, you will learn how to do the following actions to address this requirement:

- Create an event-triggered integration in Choreo by connecting the GitHub repository which has a integration developed in [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/).
- Deploy the integration component to the development environment in Choreo.
- Test the integration component.

For this tutorial, let's use a basic sample application which listens to a [RabbitMQ](https://www.rabbitmq.com/) topic and logs incoming messages.

!!! tip "Before you begin!"
    To follow this tutorial, you can utilize a sample integration created with [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/). Simply fork the [choreo-examples](https://github.com/wso2/choreo-examples) repository on GitHub.

In this tutorial, we will be using the [simple-rabbitmq-listener](https://github.com/wso2/choreo-examples/tree/main/ipaas/wso2-synapse/simple-rabbitmq-listener) project in the **choreo-sample** repo. 

## Step 0: Get Ready for the Tutorial
To complete this tutorial you need to complete the following steps.

1. You need to start a RabbitMQ server on the cloud. once it's started grab the `username`, `hostname`, `password` and `vhost` from the RabbitMQ instance. 

!!! tip RabbitMQ Server
    To try this tutorial, you can use [CloudAMQP](https://www.cloudamqp.com/) to configure and start a RabbitMQ server with a few steps.

2. Then in your fork go to `/ipaas/wso2-synapse/simple-rabbitmq-listener/sales-rabbitmq-inboundConfigs/src/main/synapse-config/inbound-endpoints/SalesOrderInbooundEP.xml` file.

3. Update parameters (`username`, `hostname`, `password` and `vhost`) in this file with the credentials you obtained from your RabbitMQ server. 
4. Commit Changes.

## Step 1: Create the integration component

Let's create the integration component by following the steps given below:

1. Go to [https://console.choreo.dev/?profile=ipaas](https://console.choreo.dev/?profile=ipaas) and sign in to the Choreo Console.

2. On the **Home** page, select a project or create a new one by clicking **+Create Project**.

3. If the project has components, click **+Create**. If not, skip to the next step.

4. On the **Event-Triggered Integration** card, click **Create** to start creating an event-triggered integration component

5. Give the component a **name** and **description** (e.g. `MQTT listener` and `My first MI MQTT listener`).

6. Click **Next**.

7. Authorize Choreo to access your GitHub account by clicking **Authorize with GitHub**.

8. If you have not already authorized Choreo applications, click **Authorize Choreo Apps** when prompted.

9.  Enter information related to the GitHub repository you want to connect as follows:

     | **Field**             | **Value**                                                                                             |
     |-----------------------|-------------------------------------------------------------------------------------------------------|
     | **GitHub Account**    | Select your GitHub account.                                                                           |
     | **GitHub Repository** | Select your fork of the [choreo-examples GitHub repository](https://github.com/wso2/choreo-examples). |
     | **Branch**            | `main`                                                                                                |

10. Under **Build Preset**, click **Micro Integrator**.

     !!! info
        Build Preset specifies the type of build that must be executed, based on the implementation of the component. For instance, if the component was developed using [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/), the Micro Integrator build preset should be chosen. Meanwhile, if the Ballerina language was used to develop the component, the Ballerina build preset should be selected. These presets convert the integration code into a Docker image that can run on Choreo Cloud.


11. Enter information as follows.

    !!! info
         When Choreo builds the component this is the path it looking for the project in the repository.

     | **Field**             | **Value**                                      |
     |-----------------------|------------------------------------------------|
     | **Path**              | `ipaas/wso2-synapse/simple-rabbitmq-listener`              |
 
12. Click **Create**.

After the creation process is completed, the new component opens on a separate page.

## Step 2: Deploy

Let's deploy the component you created to the developer environment by following the steps given below:

1. Go to **Deploy** In the left navigation menu.

2. Click **Deploy Manually**.

    !!! info
        Automatic deployment is enabled for the component by default. You are required to carry out only the first deployment manually.

After Choreo deploys the component, proceed to the next step to test it.

## Step 3: Test

Test the component by publishing messages to a designated topic in your RabbitMQ server.
 
1. Publish following message to the **SalesOrderQueue** topic in RabbitMQ server.

```
<Orders>
   <Order>
       <Id>2</Id>
       <Price>120.0</Price>
   </Order>
</Orders>
```

2. To view logs, use the `Observe` view by clicking the **Observe** icon in the left panel.

3. Check the logs to see the message received by the integration component from the RabbitMQ server. 

If the message appears in the logs, your Event-Triggered Integration is functioning as expected.

!!! tip "Observe"
    You can use the `Observe` view to observe statistics for your component. For more information about observing components, see [Observability Overview](../../observe-and-analyze/observe/observability-overview.md).


Congratulations! You have successfully deployed an integration designed in the [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/) as an Event-Triggered Integration in Choreo and triggered it!

