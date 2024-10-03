# Managing Third Party Services

The registered third party services will be listed in the **Dependencies > Third Party Services** page.

To view the details of a third party service, click on the service from the list.

## Viewing Service Details

The service details page will have the following information:

- General Details: Service name, summary, overview , and labels.

- Service Definition: View/ edit the service definition. This will show the api definition of the third party service. The swagger ui will be displayed for REST APIs and the text content will be displayed for other service types. To edit the definition, click the **Upload** button and select the new definition file.

- Endpoints: Add/ modify or delete endpoints.


## Add the service to the Internal Marketplace

Click on the **Add to Marketplace** button to add the service to the Internal Marketplace.

!!! info
    The service can be added to the Internal Marketplace only if all the required parameters are provided for at least one endpoint.

## Remove the service from the Internal Marketplace

If the service should be removed from the Internal Marketplace, click on the **Remove from Marketplace** button.

This will remove the service from the Internal Marketplace and will not be discoverable or consumable via a Connection.
The existing connections will continue to work as before.
