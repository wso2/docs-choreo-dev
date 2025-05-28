# Deploy a MCP Server

Choreo allows you to create and deploy MCP Server applications in Python and Node.js. 

In this guide, you will:

- Deploy an stdio-based [github-mcp-server](https://github.com/github/github-mcp-server) in Choreo  over SSE (Server-Sent Events) using a `NodeJs` buildpack. 

## Prerequisites

1. If you're signing in to the Choreo Console for the first time, create an organization:
    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

2. Create a [GitHub Personal Access Token (PAT)](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-fine-grained-personal-access-token) for your account.

## Step 1: Create a MCP Server component

To create a mcp server component, follow these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click **View All Component Types** link button.
4. Click the **MCP Server** card.
5. Under **MCP Server Metadata**, enter the following details:

    | **Field**              | **Value**          |
    |------------------------|--------------------|
    | **Server Package**       | @modelcontextprotocol/server-github |
    | **Run Command**  | npx -y @modelcontextprotocol/server-github    |

6. Select **NodeJs** as the buildpack.
7. Provide component display name, name and description:

    !!! info
        The **Component Name** field must be unique and cannot be changed after creation.

    | **Field**                 | **Value**          |
    |---------------------------|--------------------|
    | **Component Display Name**| GitHub MCP Server         |
    | **Component Name**        | github-mcp-server         |
    | **Description**           | Mcp server for GitHub     |

8. Click **Create**. This creates the component and lists it under **Component Listing** on the project home page.

You have successfully created the server. The initial build has been triggered. Please wait until it completes.
If you need to rebuild the component later, follow the steps below.

## Step 2: Build

1. On the project home page, click on the `GitHub Mcp Server` component you created. This takes you to the component overview page.
2. In the left navigation menu, click **Build**.
3. On the **Build** page, click **Build Latest**.

    !!! note
        The build process may take some time. You can track progress in the **Build Details** pane. Once complete, the build status changes to **Success**.

Now that you have built the component, it's time to deploy the mcp server.

## Step 3: Deploy

1. In the left navigation menu, click **Deploy**.
2. On the **Set Up** card, click **Configure & Deploy**.
3. In the **Environment Configurations** pane, click **Add a Configuration** and add following environment variables in the table. Then tick **Mark as a Secret** and save the configuration by clicking **Next** button.

    | **Name**                 | **Value**          |
    |---------------------------|--------------------|
    | **GITHUB_PERSONAL_ACCESS_TOKEN**| YOUR GITHUB PAT         |

4. In the **File Mount** pane, click **Next** to skip this step.
5. Review the **Endpoint Details** and click **Deploy**.

    !!! note
        - In Choreo, by default endpoint authentication is enabled. You can disable endpoint authentication if you don't require it.
        - Deploying the service component may take some time. Once deployed, the **Development** environment card indicates the **Deployment Status** as **Active**.

Once you have successfully deployed the service, you can test your server. You can view the invoke URL details from the **Overview** tab in left navigation menu.

!!! info
    The path for SSE subscription is **/sse**, and the path for messages is **/messages**.

## Step 4: Test

You can test the deployed MCP Server with an external client.

As security is enabled for the server, a token must be generated to connect the external client to the server. Follow below steps to generate the token. Refer [develop an external consumer](../develop-an-external-consumer.md) for more details.

1. Create an external consumer component from the component creation wizard
2. Go to the **Connections** tab in left navigation menu and select **Service** type
3. Create a connection by selecting the already deployed Mcp Server component
4. Go to the component **Settings** page and click on **Generate Keys** button
5. Use those credentials to generate the authorization token for the deployed MCP server. You can get the **Token Endpoint** from **Identity Provider Connection Data** section. To generate the token, use the following curl command.

```
curl -k -X POST <token-endpoint> -d "grant_type=client_credentials"
-H "Authorization: Basic Base64(consumer-key:consumer-secret)"
```

### Test the MCP Server with Postman

**Prerequisites** <br>
Download and install [postman](https://www.postman.com/downloads/) in your machine.

1. Create a new request with MCP type
2. Paste the MCP server URL copied from [step 3](#step-3-deploy)
3. Select server's communication method as HTTP
4. In the **Authorization** section, select **Bearer Token** as auth type and paste the generated token
5. Click **Connect** button

### Test the MCP Server with Inspector

**Prerequisites** <br>
Install [Inspector](https://github.com/modelcontextprotocol/inspector) in your machine.

1. Select **SSE** as transport type
2. Paste the MCP server URL copied from [step 3](#step-3-deploy) in the **URL** input field
3. Paste the generated token in the **Bearer Token** input field
4. Click **Connect** button

### Test the MCP Server with Vscode
1. Create mcp.json files in .vscode directory to add an MCP server to your directory. Refer [Use MCP servers in VS Code](https://code.visualstudio.com/docs/copilot/chat/mcp-servers) for more details.
2. Add the MCP server URL details in **servers** section in the mcp.json file. 
3. Paste the generated token as a header. For example,

```
{
    "servers": {
        "github-mcp-server": {
            "type": "sse",
            "url": "<sse endpoint of deployed MCP server>",
            "headers": { "Authorization": "Bearer  <token>" }
        }
    }
}
```
4. Start the MCP server.
5. Use chat space in Agent mode.

Currently, Cursor does not support authenticating MCP servers. Hence, you have to disable endpoint authentication as mentioned in [step 3](#step-3-deploy)

### Test the MCP Server with Cursor
1. Open **Settings** page in Cursor
2. Click **MCP** tab in left navigation menu
3. Click **Add new global MCP server** button
4. Add the MCP server URL details in **mcpServers** section in mcp.json file. For example,

```
{
  "mcpServers": {
    "github-mcp-server": {
      "url": "<sse endpoint of deployed MCP server>"
    }
  }
}
```
