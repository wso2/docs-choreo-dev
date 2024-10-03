# Register a Third Party Service

### Prerequisites

Before registering a third party service, make sure that you have obtained the required parameters for the third party service. The required parameters may include the URL, authentication credentials, and other configurations required to access the service. These parameters are specific to each third party service and are typically provided by the service provider.

Follow the steps below to register a third party service:

1. Sign in to the Choreo Console at https://console.choreo.dev/.
2. From the Organization home page, click on the project which you want to register the third party service. (Skip this step if you want to register the service at the Organization level.)
3. From the left navigation menu, click **Dependencies** and then **Third Party Services**.
4. Click **Register** button in the Third Party Services page.
5. Provide the service name, version, and summary.
6. Upload the api definition file (.yaml) of the third party service.
7. Verify the service type. 
8. Click **Define Endpoints** button to move into the next step.


### Define Service Endpoints

1. In the Define New Endpoint section, provide a name for the endpoint.
2. Provide the URL of the third party service.
3. In the Additional Parameters section, define the other parameters that are required to access the third party service. e.g. API Key, Authorization Token, etc. If a parameter should be kept secret, check the **Secret** checkbox.

    !!! info
        The same parameter names will be shared will all the endpoints of the service.

4. Select the Allowed Environments for the endpoint. This will determine the choreo environments where the endpoint can be used.
5. Click **OK** button to add the endpoint.
6. If there are multiple endpoints, click **Add Endpoint** button to add more endpoints.
7. Click **Register** button to register the third party service.

!!! info
    When all the values for the additional parameters are provided at least for one endpoint, the service will be automatically added to the marketplace. Otherwise, you will have to manually add the service to the marketplace after providing the values for the endpoints.


For information on managing the third party services, see [Manage Third Party Services](../third-party-services/manage-third-party-services.md).

For information on consuming a third party service, see [Consume a Third Party Service](../develop-components/sharing-and-reusing/create-a-connection.md#create-a-connection-to-a-third-party-service).