# Develop Components Using VS Code

The [{{ product_name }} VS Code extension](https://marketplace.visualstudio.com/items?itemName=WSO2.choreo) provides comprehensive component management capabilities to streamline local development within {{ product_name }}.

## Key Features

- **Manage Projects**: Work with {{ product_name }} projects directly in your VS Code workspace.
- **Create Components**: Generate components linked to your application source.
- **Trigger Builds**: Initiate builds in the cloud and view detailed build logs.
- **Deploy Builds**: Deploy builds to your preferred {{ product_name }} environments.
- **Test Services**: Verify functionality of publicly exposed service endpoints.
- **Monitor Components**: Access runtime logs and monitor your deployed components.
- **Connect Locally to Dependencies**: Link your app to dependent connections while developing. See [guide](../../develop-components/connect-to-remote-dependencies-while-developing/#using-vs-code).

## Prerequisites

To ensure a smooth development experience with the {{ product_name }} extension, make sure you have the following:

1. [Visual Studio Code](https://code.visualstudio.com/download) installed with the [{{ product_name }} extension](https://marketplace.visualstudio.com/items?itemName=WSO2.choreo) version **2.0.0** or later.

2. A locally cloned GitHub repository to create new components or link to existing {{ product_name }} components.

3. [Git](https://git-scm.com) version 2.0.0 or later.

## Get started

To use the capabilities of the {{ product_name }} extension in the VS Code editor, you need an active [{{ product_name }} account](https://wso2.com/choreo/pricing/). If you already have an account, follow these steps to set up the extension:

1. Install the [{{ product_name }} VS Code extension](https://marketplace.visualstudio.com/items?itemName=WSO2.choreo) and wait for activation. On successful activation, the {{ product_name }} extension opens in the VS Code editor. 
2. Sign in to {{ product_name }} using one of the following methods:
    - In the {{ product_name }} activity pane, click **Sign In**.
       ![Sign in](../assets/img/develop-components/develop-using-vs-code/sign-in.png)
    - Use the `Sign In` command provided by the {{ product_name }} extension.

    This redirects you to an external URI to complete the authentication process. On successful sign-in, the {{ product_name }} activity pane displays your account details along with any components detected within the VS Code workspace.

## Create a new component

1. Open the source code directory where you want to build, deploy, and manage components using {{ product_name }}.
2. Create a new component using one of the following methods:
    - In the {{ product_name }} activity pane, click **Create Component**.
       ![Create Component Button](../assets/img/develop-components/develop-using-vs-code/create-component-btn.png)
    - Use the `Create New Component` command provided by the {{ product_name }} extension.

3. If the {{ product_name }} extension cannot determine the project context of the opened workspace, it prompts you to select the organization and the project to which the new component belongs.
4. Specify component details such as the name, type, build preset, etc.

    ![Component Form](../assets/img/develop-components/develop-using-vs-code/component-form.png)
            
    On successful creation, the component details view opens, and the {{ product_name }} activity pane displays the new component.

    ![Component Details](../assets/img/develop-components/develop-using-vs-code/component-details-view.png)
     
    !!! tip
        Once the component is created, a `[Understand the project context](#understand-the-project-context)` file is generated in the root of the Git repository. For more details, see .choreo/context.yaml.

The component details view allows you to manage your component by performing various actions such as the following:

 - Triggering builds for selected commits.
 - Viewing lists of builds and statuses.
 - Diagnosing build failures with build logs.
 - Deploying builds in available environments.
 - Accessing runtime logs and deployed component URLs.
 - Invoking deployed service endpoints.

## Understand the project context

Context files contain metadata related to the project, allowing the extension to establish an association between local directories and {{ product_name }} projects. These files, such as the `context.yaml`file, resides in the `/.{{ product_name }}` directory within the root of the Git repository.

The {{ product_name }} extension scans the root of the opened Git repository to find the `context.yaml` file and lists the components of the associated project. This allows you to easily open and manage the components they are developing within the VS Code workspace.

A `context.yaml` file can contain multiple projects, whereas, a workspace opened via VS Code can have multiple `context.yaml` files with different project associations. In such cases, VS Code allows you to switch between these projects, add new project associations, or remove existing ones, allowing you as a developer to focus on components of a particular project at a time.

You can decide whether to commit the `context.yaml` file to the Git repository. Committing this file enables other team members working on the same repository to have a seamless developer experience with {{ product_name }}.

If the `context.yaml` file for a particular project is not committed to the Git repository or is unavailable for other reasons, you can easily regenerate it using one of the following methods:

 - In the {{ product_name }} activity pane, click **Link Directory**.
    ![Link Directory](../assets/img/develop-components/develop-using-vs-code/link-dir-btn.png)
 - Use the `Link Directory` command provided by the {{ product_name }} extension.

## Discover additional features

To access a range of functionalities provided by the {{ product_name }} extension, open the VS Code command palette and type `{{ product_name }}`.

## Troubleshoot issues

To troubleshoot {{ product_name }} extension issues, follow these steps:

1. To open the **OUTPUT** pane, go to the VS Code editor main menu, click **View**, and then click **Output**.

2. Select **{{ product_name }}** from the drop-down menu on the right-hand side to view the {{ product_name }} output for troubleshooting.

## Get help

For assistance with the {{ product_name }} VS Code extension, create [GitHub issues](https://github.com/wso2/choreo-vscode/issues).
