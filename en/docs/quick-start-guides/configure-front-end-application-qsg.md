
In this step, you are adding the configurations needed for the web app to successfully invoke the **Reading List Service** REST API. These configurations need to be updated for each environment you deploy the web app. Here you will be updating the configurations for the development environment.

!!! note
        The web application is reading the environment-specific configurations from the `window` object at runtime. This is done via the `config.js` file in the root of the web application. In this section, we are mounting the `config.js` file for the development environment. You will need to do the same for other environments as well when you deploy your web application to multiple environments.

To configure the front-end application, follow the steps given below:

1. While on the web application component page, click **DevOps** in the left navigation menu and then click **Configs and Secrets**.
2. Click **+ Create**.
3. Select the mount configuration options as follows and click **Next**:

    | **Field**             | **Description**                               |
    |-----------------------|-----------------------------------------------|
    | **Config Type**       | **Config Map**                                |
    | **Mount Type**        | **File Mount**                                |

4. Specify values as follows for the mount configuration:

    | **Field**             | **Description**                               |
    |-----------------------|-----------------------------------------------|
    | **Config Name**       | **Web App Config**                            |
    | **Mount Path**        | **/usr/share/nginx/html/config.js**. Every config that needs to be exposed through the web server should be placed inside `/usr/share/nginx/html/`                          |

5. Copy the config details as a JSON file as shown below into the text area. Fill the placeholders with the values you copied from the previous steps as mentioned in the table below.

    ```javascript
    window.config = {
        redirectUrl: "<web-app-url>",
        asgardeoClientId: "<asgardeo-client-id>",
        asgardeoBaseUrl: "https://api.asgardeo.io/t/<your-org-name>",
        choreoApiUrl: "<reading-list-service-url>"
    };
    ```

    | **Field**             | **Description**                               |
    |-----------------------|-----------------------------------------------|
    | **redirectUrl**       | The web app URL you copied earlier. |
    | **asgardeoClientId**  | The **Client ID** of your OAuth application. In Asgardeo, you can find it on the **Protocol** tab of the **readingListApp** application  |
    | **asgardeoBaseUrl**   | Specify the IdP API URL (For example, Asgardeo API URL) with your organization name. i.e., `https://api.asgardeo.io/t/<ORG_NAME>`.      |
    | **choreoApiUrl**      | The reading list service URL. Copy the Public URL of the **Reading List Service** component from the endpoint table in the overview page for the relevant environment |

6. Click **Create**.
