# Deploy Applications using Choreo CD Pipeline

Using Choreo, you can easily deploy applications written in different language frameworks (such as Java, Go, NodeJS, Python, etc.) on shared or private data planes.

After building your application, you can deploy it from the **CD Pipelines** page. On this page, you can access the CD pipeline of the selected component.

## Trigger a deployment

Follow below steps to deploy a component,

1.  Sign in to the [Choreo Console](https://console.choreo.dev/).
2.  In the Choreo Console top navigation menu, select the **Organization**, then the **Project** and finally the relevant **Component**.
3.  From the left navigation, Click **DevOps** and then click **CD Pipelines**
4.  The setup card will contain the latest built image

    !!! Tip -
        Click on the image in the **Setup** card to select a different image instead of the latest build.

5.  To deploy your application, you can use one of the following options :

    1. **Configure & Deploy**: This option allows you to set up specific configurations for your component before deploying it. In the subsequent steps after clicking **Configure & Deploy**, you can set environment variables, configure file mounts, and adjust endpoint settings.

        - Environment variables : Apply environment-specific variables via the **Environment Configurations** pane
        - File mounts : Apply a file mount via the **File Mount** pane
        - Endpoint settings: Configure endpoint settings via the **Endpoint Details** pane.

    2. **Deploy**: This option initiates the deployment process using the existing configurations without making any changes.

## Auto Deploy on Build

When **Auto Deploy on Build** is enabled, it triggers an automatic deployment of your application to the first environment upon the completion of each successful build.

!!! Note -
    The **Auto Deploy on Build** feature, is disabled by default. To enable this feature, toggle on the Auto Deploy on Build option in the **Setup** card.

## Promoting a component to a higher environment

You can go to the CD Pipelines page of a component and manually promote it across environments.

Follow below steps to promote a component to a higher level environment:

1.  Sign in to the [Choreo Console](https://console.choreo.dev/).
2.  In the Choreo Console top navigation menu, select the **Organization**, then the **Project** and finally the relevant **Component**.
3.  From the left navigation, Click **DevOps** and then click **CD Pipelines**
4.  Click the **Promote** button on the required environment card from which the promotion is initiated.
5.  In the **Configuration Types** pane, you can either provide new values for the configurations or use the **Development configuration**.

!!! Note -
    If you have configured any default values for the configurable variables, selecting Use default configuration values allows you to proceed with those values. If not, specify values for the configurable variables.

6. When promoting the component, you can modify environment-specific configurations (environment variables, file mount etc) in the subsequent steps of the promotion flow.

7. Click **Promote**.

!!! Tip  -
       Choreo supports workflow approvals, allowing organizations to enforce approval processes before a component is promoted to a higher environment. For more details, see [Configure Approvals for Choreo Workflows](../../governance/workflows/configure-approvals-for-choreo-workflows.md)

## Configurations

### Environment-Independent Configurations

These configurations apply to all environments. To change environment-independent configurations, make the necessary configuration changes via the **Setup** card, and then trigger a new deployment to the initial environment. Once deployed, you can promote the component to higher environments. **Endpoint configurations** can be managed from the **Setup** card.

### Environment-specific configurations
These configurations apply to a particular environment. To change environment-specific configurations, make the necessary configuration changes via the specific environment card, and trigger a new deployment. From the environment card, you can manage **Configs & Secret** and **Scaling** options.
