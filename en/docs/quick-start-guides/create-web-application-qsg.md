To host the front-end application in Choreo, you must create a web application component. To create a web application component, follow the steps given below.

1. In the Choreo console, select the project of the reading list application that you created in the previous steps, from the project list located on the header.
2. Click **Create** under the **Component Listing** section to create a new component.
3. On the **Web Application** card, click **Create**.
4. Enter a unique name and a description for the web application. You can enter the name and description given below:

    | **Field**       | **Value**               |
    |-----------------|-------------------------|
    | **Name**        | `Reading List Web App`  |
    | **Description** | `Frontend application for the reading list service` |

5. Click **Next**.
6. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**.
7. In the **Connect Repository** pane, enter the following information:

    | **Field**             | **Description**                               |
    |-----------------------|-----------------------------------------------|
    | **GitHub Account**    | Your account                                  |
    | **GitHub Repository** | **`choreo-samples`** |
    | **Branch**            | **`main`**                               |
    | **Buildpack**      | Click **React** since the frontend is a React application built with Vite|
    | **Build Context Path**              | **`reading-list-app/reading-list-front-end`** |
    | **Build Command**     | **`npm install && npm run build`**             |
    | **Build Output**      | **`dist`**                                    |
    | **Node Version**      | **`18`**                                      |

9. Click **Create**. This initializes the service with the implementation from your GitHub repository and takes you to the **Overview** page of the component.

Let's consume the service through the web app. Choreo services are by default secured. To consume a service in Choreo you need an access token. Let's configure the web application to connect to an IdP (For this guide, let's use Asgardeo) to generate an access token for a user.