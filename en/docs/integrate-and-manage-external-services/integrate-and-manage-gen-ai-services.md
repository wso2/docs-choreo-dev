# Integrate and Manage Generative AI Services

Generative AI (GenAI) services leverage advanced machine learning models to create original content such as text, images, music, or code, by identifying and learning patterns from existing data. Powered by deep neural networks and other machine learning models, these services can generate human-like outputs in various formats. This versatility makes GenAI ideal for tasks such as content creation, image generation, conversation automation, etc. 

Choreo enables seamless integration with GenAI services, allowing you to incorporate these capabilities within your applications.

## Register a GenAI service

To use a GenAI service in Choreo, you need to register it. Once registered, the service becomes available in the Internal Marketplace, allowing you to consume it via a Connection.

You can register a GenAI service at two levels:

  - Organization level: This makes the service accessible from any project within the organization.
  - Project level: This restricts the service to a specific project.

### Prerequisites

Before registering a GenAI service, obtain the following details from the service provider:
 - API key.
 - Service URL.
 - Other necessary parameters. For example, client credentials.

To register a GenAI service, follow these steps:

!!! note 
     Ensure that you register the service at the appropriate level depending on the usage. If the service must be shared among multiple projects, register it at the organization level. Otherwise, register it at the project level.

### Step 1: Select a service provider

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. Follow one of these steps depending on your requirements:
    - To register a GenAI service at the organization level, go to the Choreo Console header and select your organization from the **Organization** list. 
    - To register a GenAI service at the project level, go to the Choreo Console header and select your project from the **Project** list. 
3. In the left navigation menu, click **Dependencies** and then click **GenAI Services**.
4. Click **+ Register**.
5. Select a service provider.
6. Click **Next**.

### Step 2: Provide service details

1. Under **Register Service**, enter the following details for the service:
    - A **Name** and **Version**.
    - The **Service URL**.

2. Click **Next**.

### Step 3: Add configurations

1. Under **Add Configurations**, enter details for the service.
    !!! note 
         - The configuration details to enter vary depending on the selected service provider.
         - To successfully register a service, ensure you provide all the required configurations.

2. Click **Register**.

Once registered, the GenAI service is automatically listed in the Internal Marketplace.

If you want to remove the service from the Internal Marketplace, see [Remove a GenAI service from the Internal Marketplace](#remove-a-genai-service-from-the-internal-marketplace).

## Discover GenAI services

The GenAI services you register are discoverable via the Internal Marketplace for you to consume via a Connection.

For details on consuming a GenAI service via a connection, see [Create a Connection](../develop-components/sharing-and-reusing/create-a-connection.md).

## Manage GenAI services

When you create a GenAI service, it gets listed in the **GenAI Services** list. 

### View or update GenAI service details

To view or update a GenAI service, follow these steps.

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the left navigation menu, click **Dependencies** and then click **GenAI Services**. This lists all the GenAI services you have created.
3. Click on a service to view or update its details.
    - **General Details**: Displays service metadata. For example, the service name, overview, labels, etc. 
    - **Service Definition**: Displays the service definition. To update the definition, click **Upload** and select the new definition file.

### Add a GenAI service to the Internal Marketplace

!!! info
    To add a GenAI service to the Internal Marketplace, you must provide all the required configurations.

1. In the Choreo Console left navigation menu, click **Dependencies** and then click **GenAI Services**.
2. Click on the service you want to add to the Internal Marketplace.
3. Click **Add to Marketplace**.

### Remove a GenAI service from the Internal Marketplace

1. In the Choreo Console left navigation menu, click **Dependencies** and then click **GenAI Services**.
2. Click on the service you want to remove from the Internal Marketplace.
3. Click **Remove from Marketplace**.

This removes the service from the Internal Marketplace. Therefore, the service will not be available to consume via a Connection. However, the connections created before removal will continue to work as expected.
