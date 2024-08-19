# Manage Environments

By default, all projects created in the cloud data planes (irrespective of the data plane region) are provisioned with two environments ("Development" and "Production" environments)

The environments are listed in the order of deployment and promotion. The initial deployment takes place in the first environment and you can proceed to promote a component to subsequent environments.

!!! info "Prerequisites"

       - You must have a Choreo subscription or a private data plane to create additional environments.
       - You must have the `ENVIRONMENT-MANAGEMENT` permission to create a new environment in a private data plane organization. The `ENVIRONMENT-MANAGEMENT` permission is granted to Admin and Choreo DevOps roles by default.


## Create a new environment

To create a new environment, follow the steps given below:

1. Sign in to the [Choreo Console](https://console.choreo.dev/) and switch to the organization you want to create a new project in. 
2. In the left navigation menu, click **DevOps** and then click **Environments** (note that this is the Environments page under your organization, not your projects).
3. On the **Environments** page, click **Create** and specify the following details to create a new environment:
   
    - **Name**: A display name for the new environment.
    - **Description**: A brief description of the new environment.
    - **Data Plane** - The data plane to create the new environment.

        !!!tip
            - The **Data Plane** list displays all the private data planes registered under your organization. 

    - **DNS Prefix**: A DNS prefix to identify the exposed APIs in the environment. Here, the base domain depends on the custom domain attached to the API gateways provisioned on the selected data plane.
    - **Mark environment as a Production environment**: Select if you want this environment to be a production environment.
  
        !!!tip

              In Choreo, you can have multiple non-production and production environments. To work in a production environment, you must have priviledged permissions to access and deploy to production environments. 


## Change the order of promotion

The order in which environments are listed on the **Environments** page is the same order in which promotion takes place.

To change the order of promotion across environments in an organization, follow the steps given below:

1. Sign in to the [Choreo Console](https://console.choreo.dev/) and switch to the organization for which you want to change the order of promotion.
2. In the left navigation menu, click **DevOps** and then click **Environments**. 
3. On the **Environments** page, click and drag environment list items to rearrange the order of listed environments.

    !!! note
        Although changes to the order of promotion for environments are applied immediately, the change does not affect the components already running in environments. Only subsequent builds and promotions will follow the new order.

To see the changes, go to the **Deploy** page of a component (in any project).

## Delete an environment

1. Sign in to the [Choreo Console](https://console.choreo.dev/) and switch to your organization.
2. In the left navigation menu, click **DevOps** and then click **Environments**. 
3. Click on the red trash icon button next to the environment you want to delete.
4. This will prompt you with a confirmation dialog and a report containing the scope of impact of the deletion for your review.
5. Once you have reviewed and confirmed the details, you can proceed to delete the environment. Note that environment deletion is a permament, non-reversible operation.

