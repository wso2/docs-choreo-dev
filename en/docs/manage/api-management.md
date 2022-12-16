# Manage

Choreo's API Management capabilities allow you to manage your APIs through the Choreo Console. You can manage the lifecycle, consumers, and documents, secure your backends by applying rate-limiting policies through usage plans, define permissions using scopes, configure security, rate-limiting, and visibility settings, and define any properties. 

# Overview

The overview of the **Manage** page gives you a bird's eye view of how you currently manage your API component. You can view the lifecycle status of your component, the number of consumers, the security scheme configured, and the usage plan applied to the API. The **Overview** pane also lists the environment(s) of the API.

# Lifecycle

Choreo allows you to manage the lifecycle of the API through the Choreo Console. A state represents the stage of an API in the process from creation to retirement. You can apply any of the six API lifecycles states (i.e., created, pre-released, published, blocked, deprecated, and retired) while adhering to their respective lifecycle flows.

When you publish an API, you can also publish a connector for that API to make it available on the Choreo Marketplace.

# Consumers

The **Consumers** pane shows us the applications that have subscribed to your API. You can manage access to your API by blocking or unblocking these applications through the Choreo Console. The **Consumers** pane lists the applications, and for each application, it shows the usage tier, owner, and status: blocked or unblocked. 

# Documents

API documentation is vital in helping API consumers understand the functionality of your API and is beneficial to marketing the API. The Choreo Console allows you to add a document on the fly by entering a name and the API contents. You can also preview the document and verify if it is suitable for publishing. 

# Usage Plans

You can secure your API backends by applying rate-limiting policies through the **Usage Plans** pane, typically in cases such as the following: 

- To protect your APIs from common types of security attacks, such as certain types of denial of service (DOS) attacks
- To regulate traffic according to infrastructure availability
- To make an API, application, or resource available to a consumer at different levels of service, usually for monetization purpose

Choreo allows you to apply a usage plan on the API that is not bound to an environment or build. Choreo supports the following usage plans by default:

- Bronze - 1000 requests per minute.
- Silver - 2000 requests per minute.
- Gold - 5000 requests per minute.
- Unlimited - unlimited requests per minute.

# Permissions

Permissions allow you to attach scopes that enable fine-grained access control to your API resources. You can create a scope entry by giving a scope name. You can then select and apply these scopes to the individual resources in your API. On the identity provider, a scope will have a role mapped to it. A role specifies the permissions associated with it. When you invoke the API resource with a specific scope, the access to your API resource is controlled based on the permissions associated with the role. 

# Settings

The Choreo Console allows you to define security, rate-limiting, and visibility settings for an API. You can configure the following settings: 

- **Access mode** - Configure API access as internal or external. 
- **API Visibility** - Configure API visibility as private or public.
- **CORS configuration** - Enables CORS configurations and provides necessary CORS headers.
- **Application level security** - This determines the type of security used to safeguard this API.  You can secure your APIs with OAuth2, any other available security scheme, or both. If the OAuth2 option is selected, that API will need a valid OAuth2 token for successful invocation.
- **Operations Configuration** - Configure rate limiting to your API in Choreo per API operation.

# Properties

You can view and edit the API properties (i.e., the business owner's name and email and the technical owner's name and email) from the Choreo Console."