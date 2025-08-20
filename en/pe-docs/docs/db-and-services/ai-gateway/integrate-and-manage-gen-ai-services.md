# Integrate and Manage Generative AI Services

Generative AI (GenAI) services leverage advanced machine learning models to create original content such as text, images, music, or code, by identifying and learning patterns from existing data. Powered by deep neural networks and other machine learning models, these services can generate human-like outputs in various formats. This versatility makes GenAI ideal for tasks such as content creation, image generation, conversation automation, etc. 

Choreo provides native support to integrate GenAI services into applications. Developers can register GenAI services, manage access, and expose them securely through the [Choreo AI Gateway](https://github.com/wso2/choreo-ai-gateway), ensuring enterprise-grade governance and control.

## Register a GenAI Service

When you register a GenAI service in Choreo, all invocations are automatically routed through the Choreo AI Gateway. The gateway enforces:
- Security – Authentication, authorization, and secure transport.
- Rate limiting – Request- and token-based throttling.
- Observability – Metrics, logging, and monitoring for all requests (coming soon)

This ensures consistent, secure, and cost-controlled access to the underlying GenAI provider.

### Scopes of Registration
GenAI services can be registered at two scopes:

- **Organization-level registration**: The service is available across all projects within the organization.
- **Project-level registration**: The service is restricted to the specific project where it is registered.

Once registered, the service is published to the Internal Marketplace, where developers can discover and consume it via Connections.

### Prerequisites
Before registering a GenAI service, obtain the following details from the provider:

- API key
- Service URL
- Any additional credentials (e.g., client secrets, subscription keys)

!!! note
    - Register services at the correct scope:
        - Use organization-level if the service will be shared across projects.
        - Use project-level if usage is limited to a single project.

### Step 1: Select a Service Provider

1. Sign in to the [Choreo Console](https://console.choreo.dev).
2. Select your Organization or Project depending on the required scope.
3. In the left navigation menu, go to **DB & Services → GenAI Services**.
4. Select a service provider:
    - **OpenAI**
    - **Azure OpenAI**
    - **Anthropic Claude**
    - **Mistral**
    - **AWS Bedrock**

5. Click **Next**.

### Step 2: Provide Service Details

1. Under **Register a GenAI Service**, enter the following details:

    - **Name**: The name of the service.
    - **Context path**: A unique identifier for the service API within the Choreo AI Gateway.
    - **Service URL**: The provider’s endpoint.
    - **Summary**: A short description of the service.

2. Click **Next**.

### Step 3: Add Environment Configurations

Under **Add Environment Configurations**, configure backend security and token-based rate limiting for your AI API.

!!! note
- Required configurations vary by provider.
- You must provide valid configurations for all environments to complete registration.

#### Configure Backend Security

1. In the AI API Security section:

    - Set the Authorization header.
    - Provide the API key or credentials from your provider (e.g., `Authorization: Bearer <token>`).
    - Key formats may differ by provider.

    ![Add environment configurations](../../assets/img/ai-gateway/add-configs.png)

#### Configure Token-Based Rate Limiting

Unlike traditional APIs, LLMs and GenAI services are billed based on token consumption rather than just request count. Token-based rate limiting allows you to:
- Control costs.
- Prevent excessive usage.
- Enforce fair quotas across consumers.

2. In the **Token-Based Rate Limiting** section, configure the following parameters:
    - **Time Unit**: Window for quota enforcement (per minute, per hour, per day).
    - **Max Prompt Token Count**: Limit for input tokens per request.
    - **Max Completion Token Count**: Limit for output tokens per request.
    - **Max Total Token Count**: Combined input + output tokens per request.

3. Click **Register**.

The service is now available in the Internal Marketplace.

### View or Update Service Details

1. Sign in to the [Choreo Console](https://console.choreo.dev).
2. Navigate to **DB & Services → GenAI Services**.
3. Select a service to view or edit its details:

    - **General Details**: Displays metadata such as name, description, and labels..
    - **Service Definition**: Displays the service definition.
    - **Environment Configurations**: Displays the security and rate-limiting settings for each environment, along with deployment status.

    ![View or update service details](../../assets/img/ai-gateway/view-configs.png)

### Add a GenAI Service to the Internal Marketplace

1. Sign in to the [Choreo Console](https://console.choreo.dev).
2. Navigate to **DB & Services → GenAI Services**.
3. Select the service.
4. Click **Add to Marketplace**.

### Remove a GenAI Service from the Internal Marketplace

1. Sign in to the [Choreo Console](https://console.choreo.dev).
2. Navigate to **DB & Services → GenAI Services**.
3. Select the service.
4. Click **Remove from Marketplace**.

The service is removed from the Internal Marketplace, making it unavailable for new Connections. However, existing Connections continue to function until manually removed.

## Manage GenAI Services

### View or Update Service Details

1. Sign in to the [Choreo Console](https://console.choreo.dev).
2. Navigate to **DB & Services → GenAI Services**.
3. Select a service to view or edit its details:

    - **General Details**: Displays service metadata. For example, the service name, overview, labels, etc.
    - **Service Definition**: Displays the service definition. To update the definition, click **Upload** and select the new definition file.
    - **Environment Configurations**: Displays the environment configurations for the service. To update the configurations, expand the environment and update the configurations. You can also view the deployment status of the AI API in the environment.

### Add a GenAI Service to the Internal Marketplace

1. Sign in to the [Choreo Console](https://console.choreo.dev).
2. Navigate to **DB & Services → GenAI Services**.
3. Select the service.
4. Click **Add to Marketplace**.

### Remove a GenAI Service from the Internal Marketplace

1. Sign in to the [Choreo Console](https://console.choreo.dev).
2. Navigate to **DB & Services → GenAI Services**.
3. Select the service.
4. Click **Remove from Marketplace**.

The service is removed from the Internal Marketplace, making it unavailable for new Connections. However, existing Connections continue to function until manually removed.
