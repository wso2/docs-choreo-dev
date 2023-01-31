# Create Your First Event Triggered Integration

Consider a scenario where a developer has created an integration in [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/) and wants to use it as Event-Triggered Integration by deploying it in Choreo. In this tutorial, you will learn how to do the following actions to address this requirement:

- Create an Event-Triggered Integration in Choreo by connecting the GitHub repository which has integration developed in [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/).
- Deploy the integration component to the development environment.
- Test the integration component.

For this tutorial, let's use a basic sample application which listens to [RabbitMQ](https://www.rabbitmq.com/) topic and logs incoming messages.

!!! tip "Before you begin!"
    To try this tutorial, you can use a sample integration designed via [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/).<br/><br/>To do this, fork the [choreo-examples GitHub repository](https://github.com/wso2/choreo-examples).

In this tutorial, we going to use [simple-rabbitmq-listener](https://github.com/wso2/choreo-examples/tree/main/ipaas/wso2-synapse/simple-rabbitmq-listener) project in the **choreo-sample** repo. 

## Step 0: Prepare for the tutorial
To complete this tutorial you need to complete the following steps.

1. You need to start a RabbitMQ server on the cloud. once it's started grab the `username`, `hostname`, `password` and `vhost` from the RabbitMQ instance. 

!!! tip RabbitMQ Server
    To try this tutorial, you can use [CloudAMQP](https://www.cloudamqp.com/) to configure and start a RabbitMQ server with a few steps.

2. Then in your fork go to `/ipaas/wso2-synapse/simple-rabbitmq-listener/sales-rabbitmq-inboundConfigs/src/main/synapse-config/inbound-endpoints/SalesOrderInbooundEP.xml` file.

3. Update parameters (`username`, `hostname`, `password` and `vhost`) in this file with the credentials you obtained from your RabbitMQ server. 
4. Commit Changes.

## Step 1: Create

Let's create the integration component by following the steps given below:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev/).

2. On the **Home** page, click on the project in which you want to create the integration. Alternatively, you can click **+ Create Project** and add a new project.

3. If your project has one or more components, click **+ Create**. If not, proceed to the next step.

4. On the **Event-Triggered Integration** card, click **Create** to start creating a  event-triggered integration component.

5. Enter a name and a description for the component. For example, you can enter `RabbitMQ listener` as the name and `My first MI RabbitMQ listener` as the description.

6. leave the selection in the **Access Mode** list unchanged for now.

7. Click **Next**.

8. Authorize Choreo to connect to your GitHub account by clicking **Authorize with GitHub**.

9. If you have not already authorized Choreo applications, click **Authorize Choreo Apps** when prompted.

10. Enter information related to the GitHub repository you want to connect as follows:

     | **Field**             | **Value**                                                                                             |
     |-----------------------|-------------------------------------------------------------------------------------------------------|
     | **GitHub Account**    | Select your GitHub account.                                                                           |
     | **GitHub Repository** | Select your fork of the [choreo-examples GitHub repository](https://github.com/wso2/choreo-examples). |
     | **Branch**            | `main`                                                                                                |

11. Under **Build Preset**, click **Micro Integrator**.

     !!! info
         The build preset specifies the type of build that Choreo needs to run for the component (for example, Choreo needs to run a micro integrator build for components developed via the [WSO2 Integration Studio]((https://wso2.com/integration/integration-studio/), a Ballerina build for a component added via a Ballerina project, etc.,).

12. Enter information as follows.

    !!! info
         When Choreo builds the component this is the path it looking for the project in the repository.

     | **Field**             | **Value**                                      |
     |-----------------------|------------------------------------------------|
     | **Path**              | `ipaas/wso2-synapse/simple-rabbitmq-listener`              |
 
13. Click **Create**.

After the creation process is completed, the new component opens on a separate page.

## Step 2: Deploy

Let's deploy the component you created to the developer environment by following the steps given below:

1. In the left navigation menu, click **Deploy**.

2. Click **Deploy Manually**.

    !!! info
        Automatic deployment is enabled for the component by default. You are required to carry out only the first deployment manually.

Once Choreo has deployed the component, you can proceed to the next step to test it.

## Step 3: Test

Once you have deployed the component you can test it. To test it, you have to publish messages from your RabbitMQ server to specific a topic.  
 
1. Go to your RabbitMQ server and publish the following message to **`SalesOrderQueue`** topic.

```
<Orders>
   <Order>
       <Id>2</Id>
       <Price>120.0</Price>
   </Order>
</Orders>
```

2. Then you need to view the logs. for that, you can use `Observe` view. To open the Observe view, click the Observe icon in the left panel.
3. Your will be able to see the message we publish in the RabbitMQ server is logged. 

If you can see the message in the logs, it indicates that your Event Triggered Integration is working as expected.

## Step 4: Observe

You can use the `Observe` view to observe statistics for your component. For more information about observing components, see [Observability Overview](../../observe-and-analyze/observe/observability-overview.md).


Congratulations! You have successfully deployed an integration designed in the [WSO2 Integration Studio](https://wso2.com/integration/integration-studio/) as an Event-Triggered Integration in Choreo and triggered it!

