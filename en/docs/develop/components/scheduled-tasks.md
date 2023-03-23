# Scheduled Triggers

Scheduled triggers are a crucial component that enable developers to automate their workflows by executing specific actions at predetermined intervals.

## What is a scheduled trigger?

Scheduled triggers in Choreo work by invoking a predefined function or set of functions at a specified time, allowing developers to automate tasks such as data backups, system maintenance, or updates.

Scheduled triggers can be configured to run on a recurring basis, such as daily, weekly, or monthly, or they can be set to execute at a specific time and date. This flexibility makes them an essential tool for managing complex applications with multiple components that need to be synchronized.

Scheduled triggers automate repetitive tasks, which reduces workload and frees up time for more strategic projects. They also ensure timely and efficient execution of critical tasks and reduce the risk of human error, minimizing downtime in fast-paced business environments.

Following are sample scenarios where you can use scheduled triggers:

- Logging a session time-out message for a user at a specific time interval.

- Publishing a summary of GitHub issues (i.e., with details such as the number of GitHub issues in each status) as a message in a group chat window of a team every day at a specific time.

- Periodically checking an application's health.

## Develop a scheduled trigger

Developing a scheduled trigger refers to configuring an action to be executed.

!!! tip
    The development stage does not require you to specify the time interval at which the system needs to execute the action of the scheduled trigger. You need to provide it only when you deploy the Scheduled Trigger component.

e.g., If you need to schedule a health check log for an application, you may need to configure the following:

- How the Scheduled Trigger component gets the heartbeat of the application that it is monitoring

- The conditions based on which the Scheduled Trigger component can determine whether the application is healthy or not

- A log that indicates whether the application is healthy or not to the end-user

You can develop a scheduled trigger with such configurations in two possible ways as shown in the image below:

![Create scheduled task](../../assets/img/scheduled-tasks/create-scheduled-task.png){.cInlineImage-small}

### Ballerina

If you have a scheduled trigger written in the [Ballerina Programming Language](https://ballerina.io), you can deploy it in Choreo. To do this, you must save it in a GitHub repository and connect that repository to Choreo.

#### Start with a sample

Choreo gives the developers the flexibility to start with a sample scheduled trigger. If your connected repository does not have a scheduled trigger written in the [Ballerina Programming Language](https://ballerina.io), and you select the Ballerina build preset, you can proceed by either adding a code later to the repository or by starting with a sample as shown in the image below:

![Start with a sample scheduled task](../../assets/img/scheduled-tasks/start-with-a-sample-scheduled-task.png){.cInlineImage-full}

If you select the option to start with a sample, a pull request will be created in your repository with the sample scheduled trigger implementation. You can proceed by merging the pull request.

### Dockerfile

If you have an existing scheduled trigger written in any programming language, you can deploy it in Choreo. To do this, you must generate a Docker image for the scheduled trigger and save the Dockerfile in a GitHub repository. When you connect this GitHub repository to Choreo, you can build the Dockerfile to pull the scheduled trigger implementation from the Docker image.

## Deploy a scheduled trigger

At the deployment stage, Choreo prompts you to specify the time interval at which it should run the scheduled trigger to execute the configured action.

![Specify time interval](../../assets/img/scheduled-tasks/specify-time-interval.png){.cInlineImage-full}

Once you specify the time interval and proceed to deploy, Choreo starts the process of deploying an API to the default development environment as shown below:

![Deploy scheduled task](../../assets/img/scheduled-tasks/deploy-scheduled-task.png){.cInlineImage-small}

Choreo runs a professional, enterprise-grade CI/CD process to deploy APIs to its runtime(data plane) clusters. Under the hood, the data plane of Choreo runs on a Kubernetes stack, benefitting from all its features such as auto-scaling, auto-healing, secret management, liveness, readiness checks, etc.

Once you deploy the scheduled trigger, Choreo checks out your latest code, builds it, creates a Docker image, and starts it in a Kubernetes cluster managed by Choreo.

When you deploy the scheduled trigger to the development environment, it is active in the developer environment. When you are ready to take the code to production, you can promote the deployment and enable the functionality for your consumers.

Choreo allows you to view the deployment logs through the **Deploy** view. Therefore you can troubleshoot and view the status of the builds and deployment.

## Choreo environments

Choreo provides a development environment and a production environment in two separate Kubernetes clusters by default. Once you have deployed the scheduled trigger to the development environment and verified whether it functions as expected, you can promote it to the production environment.

![Promote scheduled trigger](../../assets/img/scheduled-tasks/promote-scheduled-task-to-production.png){.cInlineImage-threeQuarter}

If required, you can change the time interval of the scheduled trigger before you promote it to the production environment.

## Observe a scheduled trigger

You can assess the performance of your scheduled trigger by observing the success and failure rates of the requests sent to it. The observability view allows you to visualize the performance statistics via interactive graphs.

To learn more about how you can observe your scheduled trigger, see [Observability Overview](../../observe-and-analyze/observe/observability-overview.md).
