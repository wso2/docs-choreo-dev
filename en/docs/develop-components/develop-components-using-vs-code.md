# Develop Components Using VS Code

The [Choreo VS Code extension](https://marketplace.visualstudio.com/items?itemName=WSO2.choreo) offers comprehensive component management capabilities to streamline local development within Choreo.

## Prerequisites

To ensure a smooth development experience with the Choreo extension, make sure you have the following:

1. [Visual Studio Code](https://code.visualstudio.com/download) installed with the [Choreo extension](https://marketplace.visualstudio.com/items?itemName=WSO2.choreo) version **2.0.0** or later.

2. Locally cloned GitHub repository (to create a new component or link to an existing Choreo component).

3. [Git](https://git-scm.com) version 2.0.0 or later.

## Getting Started

To utilize the capabilities of the Choreo extension in the VS Code editor, you need an active Choreo account. If you already have one, follow these steps to set up the extension:

1. Install the [Choreo VS Code extension](https://marketplace.visualstudio.com/items?itemName=WSO2.choreo) and wait for activation.
2. Sign in to Choreo using one of the following methods:

   - Click the `Sign In` button within the Choreo activity panel.
   - Use the `Sign In` command provided by the Choreo extension.

   ![Sign in](../assets/img/develop-components/develop-using-vs-code/sign-in.png)

   This redirects you to an external URI to complete the authentication process. Upon successful sign-in, the Choreo activity pane will display your account details along with any components detected within the VS Code workspace.

## Create a New Component

1. Open the source code directory that you want to build, deploy, and manage using Choreo.
2. Initiate the creation of a new component using one of these methods:

   - Click the `Create Component` button in the Choreo activity panel.
   - Utilize the `Create New Component` command from the Choreo extension.

   ![Create Component Button](../assets/img/develop-components/develop-using-vs-code/create-component-btn.png)

3. If the Choreo extension is unable to determine the project context of the opened workspace, it will prompt you to select the organization and the project that the new component will belong to.
4. Fill out the component details such as name, type, buildpack, etc., in the provided form.

   ![Component Form](../assets/img/develop-components/develop-using-vs-code/component-form.png)

5. After creation, the component details view will open, and the Choreo activity panel will update with the new component.

   ![Component Details](../assets/img/develop-components/develop-using-vs-code/component-details-view.png)

!!! tip
Once the component is created, a `.choreo/context.yaml` file will be generated within the root of the Git repository. Refer to the [Project Context](#project-context) section for more details.

6. The component details view allows you to manage your component by performing various actions such as:
   - Triggering builds for selected commits
   - Viewing lists of builds and statuses
   - Diagnosing build failures with build logs
   - Deploying builds in available environments
   - Accessing runtime logs and deployed component URLs
   - Invoking deployed service endpoints

## Project Context

Context files contain metadata related to the project, enabling the extension to establish an association between local directories and Choreo projects. These files, named `context.yaml`, reside in the `/.choreo` folder within the root of the Git repository.

The Choreo extension scans the root of the opened Git repository to find the `context.yaml` file and lists the components of the associated project. This allows users to easily open and manage the components they are developing within the VS Code workspace.

A `context.yaml` file could contain multiple projects, and a workspace opened via VS Code could have multiple `context.yaml` files with different project associations. In such cases, VS Code will allow the user to switch between these projects, add new project associations, or remove existing ones. This enables developers to focus on components of a particular project at a time.

It's up to the user to decide whether to commit this file to their Git repository. Committing this file enables other team members working on the same repository to have a seamless developer experience with Choreo.

If the `context.yaml` file for a particular project has not been committed to the Git repository or is unavailable for other reasons, users can easily regenerate it using one of the following options:

- Click the `Link Directory` button in the Choreo activity panel.
- Use the `Link Directory` command from the Choreo extension.

![Link Directory](../assets/img/develop-components/develop-using-vs-code/link-dir-btn.png)

## Discover Additional Features

Access a range of functionalities provided by the Choreo extension by opening the VS Code command palette and typing `Choreo`.

## Troubleshooting

To troubleshoot Choreo extension issues:

1. Open the **OUTPUT** pane by clicking **View** and then **OUTPUT** from the main menu.

2. Select **Choreo** from the drop-down menu on the right-hand side to view Choreo output for troubleshooting.

## Get Help

Feel free to create [GitHub issues](https://github.com/wso2/choreo-vscode/issues) to reach out to us.
