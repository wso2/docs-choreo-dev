# Manual Triggers

Learn how to use manual triggers to execute triggers manually.

## What is a manual trigger?

A manual trigger is a mechanism that allows a user or developer to initiate a specific action or process manually. Manual triggers are often used to provide more control and oversight over the deployment process, as well as to ensure that specific quality assurance or compliance checks are met before a deployment is executed.

## Develop a manual trigger

Developing a manual trigger refers to configuring an action to be executed.

e.g., If you need to get a health check log for an application, you may need to configure the following:

- How the Manual Trigger component gets the heartbeat of the application that it is monitoring

- The conditions based on which the Manual Trigger component can determine whether the application is healthy or not

- A log that indicates whether the application is healthy or not to the end-user

You can develop a manual trigger with such configurations in two possible ways as shown in the image below:

![Create manual trigger](../../assets/img/manual-triggers/create-manual-trigger.png){.cInlineImage-half}

### Ballerina

If you have a manual trigger written in the [Ballerina Programming Language](https://ballerina.io), you can deploy it in Choreo. To do this, you must save it in a GitHub repository and connect that repository to Choreo.

#### Start with a sample

Choreo gives the developers the flexibility to start with a sample manual trigger. If your connected repository does not have a manual trigger written in the [Ballerina Programming Language](https://ballerina.io), and you select the Ballerina build preset, you can proceed by either adding a code later to the repository or by starting with a sample as shown in the image below:

![Start with a sample manual trigger](../../assets/img/manual-triggers/start-with-a-sample-manual-trigger.png){.cInlineImage-full}

If you select the option to start with a sample, a pull request will be created in your repository with the sample manual trigger implementation. You can proceed by merging the pull request.

### Dockerfile

If you have an existing manual trigger written in any programming language, you can deploy it in Choreo. To do this, you must generate a Docker image for the manual trigger and save the Dockerfile in a GitHub repository. When you connect this GitHub repository to Choreo, you can build the Dockerfile to pull the manual trigger implementation from the Docker image.

## Deploy a manual trigger

The following diagram illustrates the procedure to deploy a manual trigger in Choreo to the default development environment.

![Deploy API](../../assets/img/rest-apis/deploy-api.png){.cInlineImage-small}

Choreo runs a professional, enterprise-grade CI/CD process to deploy APIs to its runtime(data plane) clusters. Under the hood, the data plane of Choreo runs on a Kubernetes stack, benefitting from all its features such as auto-scaling, auto-healing, secret management, liveness, readiness checks, etc.

When you deploy the manual trigger, Choreo checks out your latest code, builds it, creates a Docker image, and starts it in a Kubernetes cluster managed by Choreo.

Once you deploy the manual trigger to the development environment, you can run the manual trigger by clicking **Run Once** button. When you are ready to take the code to production, you can promote the deployment and enable the functionality for your consumers.

Choreo allows you to view the deployment logs through the **Deploy** view. Therefore you can troubleshoot and view the status of the builds and deployment.

## Choreo environments

Choreo provides a development environment and a production environment in two separate Kubernetes clusters by default. Once you have deployed the manual trigger to the development environment and verified whether it functions as expected, you can promote it to the production environment.

![Promote manual trigger](../../assets/img/manual-triggers/promote-manual-trigger-to-production.png){.cInlineImage-full}

## Observe a manual trigger

You can assess the performance of your manual trigger by observing the success and failure rates of the requests sent to it. The observability view allows you to visualize the performance statistics via interactive graphs.

To learn more about how you can observe your manual trigger, see [Observability Overview](../../observe-and-analyze/observe/observability-overview.md).
