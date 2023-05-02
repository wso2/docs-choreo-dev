# Test APIs using Postman

Choreo provides an integrated Postman console to test your APIs. To use this capability, you must integrate your Postman account with Choreo by generating a Postman API key. 

## Prerequisites

- An active [Postman](https://www.postman.com/) account.
- A Postman API key to integrate your Postman account with Choreo. To obtain a Postman API key, refer to the [Postman Documentation- Generate a Postman API key](https://learning.postman.com/docs/developer/postman-api/authentication/#generate-a-postman-api-key). Make a note of the generated key, which you will need to use when you integrate your Postman account with Choreo.

Follow the steps below to test your API via the integrated Postman console in Choreo:

## Step 1: Integrate your Postman account with Choreo

1. Sign in to the [Choreo Console](https://console.choreo.dev/). 
2. In the **Component Listing** pane, click on the component you want to test.
3. Click **Test** in the left navigation menu and then click **Postman** to open the integrated Postman console.
4. In the **Postman** pane, go to the third step and click the link to proceed.
5. In the **API Key** field, enter the Postman API key you obtained in the prerequisites section. Choreo validates the key and displays a success icon if it is valid.
6. Click **Save**. This takes you to the **Postman** pane where you can link a workspace.

## Step 2: Link a workspace

You can use workspaces to manage your Postman components for different tasks. For more information about workspaces, see [Postman Documentation - Using and managing workspaces](https://learning.postman.com/docs/collaborating-in-postman/using-workspaces/managing-workspaces/).

If you have an existing workspace, you can link it to Choreo. Otherwise, follow the steps given below to create a new workspace and link it:

1. In the **Postman** pane, click **+Create New** in the drop-down list.
2. Enter a name and a description for the new workspace.
3. Click **Create & Link**

Now you can add collections to the workspace.

## Step 3: Add a collection

A collection represents one request, and you can use the collection runner in Postman to run multiple requests in a specified sequence and log the results. For more information about collections, see [Postman Documentation - Collection Runner](https://learning.postman.com/docs/collections/running-collections/intro-to-collection-runs/).

To add a collection, follow the steps given below:

!!! tip
    If you linked an existing workspace with collections, you can skip this step.

1. In the **Collections** section, click **+Add**.
2. Enter a name and a description for the new collection.
3. Click **Create**.

Now that you have a collection, you can create a request to test your API. 

## Step 4: Create a request and test your API

To create a request and test your API, follow the steps given below:

1. Go to the collection you created and click the arrow icon.
2. In the **Requests** section, click **+Add**.
3. Specify a name and description for the request.
4. Select an appropriate HTTP method from the **Method** list.
5. Click the **Parameters** tab and add the necessary parameters for the API method.
6. Click the **Headers** tab and add the required header values.
7. Click the **Body** tab and select the message body type to invoke the API method.
   Choreo automatically populates the base URL and resource path with the URL to your actual service. 
8. Click **Save**. Now you can proceed to test the API via the Postman client.
9.  Sign in to your Postman account and go to the workspace you linked.
    You will see the collection and the request you created. 
10. Go to the request and click **Send** to test the API. 
