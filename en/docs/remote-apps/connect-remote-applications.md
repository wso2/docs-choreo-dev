# Connect Remote Applications

If you need to observe an external application that you have written in the [Ballerina programming language](https://ballerina.io/), you can connect it to Choreo.

## Prerequisites

Create an application in the Ballerina programming language. For instructions, see [Ballerina Documentation - Writing Your First Ballerina Program](https://ballerina.io/learn/user-guide/getting-started/writing-your-first-ballerina-program/).

!!! Tip
    This guide uses the same Ballerina application that is created in the guide linked above as an example.

## Step 1: Generate and copy the Ballerina configuration

In this step, you will generate the Ballerina configuration that includes an application secret to link the remote application to Choreo.

To get this configuration, follow this procedure:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev/).

2. Go to the **Remote Apps** card, and click **Connect App**.

3. Click **Connect**.

4. In the **Connect existing application** card, enter a name for the remote application (e.g., `HelloService`).

5. Click **Next**. The Ballerina configuration with the application secret generated for the remote application is displayed.

6. Copy the Ballerina configuration.

Now you have the application secret that you need to set up the Ballerina project of your remote application.

## Step 2: Set up the Ballerina project

In this step, you will set up your Ballerina project with the configurations that connect the Ballerina application to Choreo. Here, the Ballerina project is the directory that contains the definition of your Ballerina application in a `.bal` file and all the configuration files required to run it.

To set up the Ballerina project:

1. Create a directory in your machine (e.g., named `HelloWorld`) and move your file with the remote Ballerina application (in this example the `hello_service.bal` file) to it.

    !!! info
        This directory will be referred to as `<BALLERINA_PROJECT>`from here onward.

2. To link the Ballerina application to Choreo via the application secret, open a new file in a text editor and add the Ballerina configurations you copied in Step 1. Then save it as `Config.toml` in the `<BALLERINA_PROJECT>`.

3. To enable the Ballerina application to publish observability statistics, save a file named `Ballerina.toml` with the following content in the `<BALLERINA_PROJECT>`.

    ```ballerina
    [build-options]
    observabilityIncluded = true
    ```
   
4. For the Ballerina application to import the `ballerinax/choreo` module, open the `.bal` file (in this example, `hello_service.bal`) and add the following line at the top.

    ```ballerina
    import ballerinax/choreo as _;
    ```
   
Now you have set up your Ballerina project to connect to Choreo once you build and run it.

## Step 3: Build the Ballerina project

In this step, you will build and run your Ballerina project. 

1. To build the Ballerina project, open your terminal, navigate to `<BALLERINA_PROJECT>`, and issue the following command.

    ```bash
    bal build
    ```
   
    This creates a `JAR` in the `<BALLERINA_PROJECT>/target/bin` subdirectory.
   
2. To run the Ballerina application, issue the following command from the `<BALLERINA_PROJECT>`.

    ```bash
    bal run target/bin/<JAR_NAME>.jar
    ```
   
    e.g., `bal run target/bin/HelloService.jar`
    
    Logs similar to the following appear in the terminal.
    
    ```bash
    ballerina: started publishing metrics to Choreo
    [ballerina/http] started HTTP/WS listener 0.0.0.0:9090
    ballerina: started publishing traces to Choreo
    ```
   
    These logs indicate that your Ballerina application is connected to Choreo and that it is publishing data to it. Now you can invoke it and generate some observability data.
   
## Step 4: Invoke the Ballerina application

To invoke your Ballerina application, send a few requests.

In this example, you can invoke the `HelloService` application via the following cURL command.

```bash
curl http://localhost:9090/hello/sayHello
```

After sending a few requests, you can proceed to the next step to view the observability statistics generated for your Ballerina application.

## Step 5: View observability statistics

To view the observability statistics generated for your remote Ballerina application, click the link provided for observability data in the startup logs of your remote application. As shown in the startup logs generated in [Step 3](step-3-build-the-ballerina-project), the link in this example is `http://console.choreo.dev/observe/app/91e9750f-e2e1-11eb-90ce-de6d84510939/dd9780a8-e2e2-11eb-90ce-de6d84510939`.

Observability data is displayed as shown in the example below.

![View observability data](../assets/img/remote-apps/remote-service-observability.png){.cInlineImage-full}

!!! Tip
    Alternatively, you can navigate to the same page in the Choreo Console as follows:<br/><br/>
    1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev/).<br/>
    2. Go to the **Remote Apps** card, and click **Connect App**.<br/>
    3. Click on your remote Ballerina application to open it.
   
Congratulations! You have successfully connected a remote application to Choreo and viewed its observability statistics in the Choreo Console.
