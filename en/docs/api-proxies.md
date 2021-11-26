# REST API Proxy

Choreo allows you to expose an unmanaged API via a REST API proxy. An unmanaged API is usually not straightforward to adopt, poorly documented, improperly authenticated, and lacks access control. You can overcome these shortcomings by fronting your API with a REST API proxy through Choreo. Fronting your unmanaged API by a REST API proxy gives your application the flexibility to make changes to the backend API without affecting the applications that consume them.

Choreo allows you to create a REST API proxy in two ways:
- From scratch by using an existing endpoint
- By importing an OpenAPI specification

### What happens when you create a REST API proxy Component?

When you create a REST API proxy component, similar to all other components, Choreo will create a private GitHub repo for your user account. You can then use the UI to develop and publish your REST API proxy. 


## Develop

Designing your REST API proxy involves deciding the resources and the methods the end-user applications will access the API. Choreo provides you with a visual representation, a Swagger editor to help you design your REST API proxy. Furthermore, you can also generate your OpenAPI specification through this editor.

By default, Choreo maintains two gateway environments, production, and sandbox. 

## Deploy

Once you have designed your REST API proxy, it is ready to be deployed. Deploying your REST API proxy makes it invokable. 
Choreo allows you to create revisions of a particular version of an API. These revisions can be deployed independently on configured Gateway environments. 

### Configuring Gateway Environment

#####TO-DO

### Revisions
API proxy Revisions allows you to have more control over the API deployments. Revisions allow you to restore changes done to the API or redeploy the API Proxy in a different environment. API revisions are similar to a checkpoint in time, which means that when you create a revision, it captures the current state of the API proxy. This gives you the ability to restore a previous revision of an API at any point. In addition, even if you want to test out a new run time configuration, this concept provides the ability to test it in an independent Gateway before applying the change to all the Gateway.

!!! info
    At any given time, Choreo maintains a maximum of 5 revisions per API.


## Test
Testing your REST API Proxy assures your API proxy works as expected. Choreo gives you multiple options to test your REST API proxy in your configured gateway environments. The interactive Swagger UI makes it easier to provide the required inputs and run a quick test for your API Proxy. 
Choreo uses OAuth2 as the default authentication for API invocation. Hence, you will need an OAuth2 access token. Choreo allows you to create an access token for testing purposes within the Swagger UI. 
Apart from the integrated Swagger UI, Choreo provides you with multiple options to test your REST API proxy. You can either generate a cURL command or integrate Postman based on your preference.

## Manage

Choreo allows you to expose your unmanaged API through a well-managed REST API proxy. 
Managing your API involves managing the lifecycle of the API, adding documentation, applying security, and applying rate limits.  

!!! note
    This section is the same as REST API. I feel like it’s good to point to that content, or we can re-use the content and visually duplicate the content on both pages. 

