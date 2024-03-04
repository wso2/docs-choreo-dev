# Manage Environments

You can view the environments of a given project on the **Environments** page of the project.

By default, all projects in the cloud data plane (irrespective of the data plane region) are provisioned with two environments (i.e., development and production).
The environments are listed in the order of deployment and promotion. The initial build takes place in the first environment and you can proceed to promote a component to subsequent environments.

## Create a new environment

!!! info "Note"

       - The capability to create a new environment is only available in private data plane organizations.
       - You must have the `ENVIRONMENT-MANAGEMENT` permission to create a new environment in a private data plane organization. The `ENVIRONMENT-MANAGEMENT` permission is granted to Admin and Choreo DevOps roles by default.

To create a new environment, follow the steps given below:

1. Sign in to the [Choreo Console](https://console.choreo.dev/) and go to the project for which you want to create a new environment.
2. In the left navigation menu, click **DevOps** and then click **Environments**. 
3. On the **Environments** page, click **Create** and specify the following details to create a new environment:
   
    - **Name**: A display name for the new environment.
    - **Description**: A brief description of the new environment.
    - **Data Plane** - The data plane to create the new environment.

        !!!tip
            - The **Data Plane** list displays all the private data planes registered under your organization. 
            - Different projects can share private data planes to create multiple, isolated environments.

    - **DNS Prefix**: A DNS prefix to identify the exposed APIs in the environment. Here, the base domain depends on the custom domain attached to the API gateways provisioned on the selected data plane.
    - **Mark environment as a Production environment**: Select if you want this environment to be a production environment.
  
        !!!tip

              In Choreo, a project can have multiple non-production and production environments. To work in a production environment, you must have permission to access and deploy to production environments. Depending on your requirement, you can decide how you want to configure a new environment within a project.

## Change the order of promotion

!!! info "Note"
     The capability to change the order of promotion is only available in private data plane organizations.

The order in which environments are listed on the **Environments** page of a project is the same order in which promotion takes place.

To change the order of promotion across environments in a project, follow the steps given below:

1. Sign in to the [Choreo Console](https://console.choreo.dev/) and go to the project for which you want to change the order of promotion.
2. In the left navigation menu, click **DevOps** and then click **Environments**. 
3. On the **Environments** page, click and drag environment list items to rearrange the order of listed environments.

    !!! note
        Although changes to the order of promotion for environments are applied immediately, the change does not affect the components already running in environments. Only the new builds and promotions follow the new order.

To see the changes, go to the **Deploy** page of a component in the project.
