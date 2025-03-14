# Expose a Ballerina GraphQL Endpoint via a Service

Choreo offers the flexibility to develop and deploy applications in a language of your preference. This guide shows you how to deploy a service component that exposes a GraphQL API using the Ballerina language in Choreo. No prior knowledge of the Ballerina language is required to follow this guide. 

GraphQL API is a query language and runtime that provides a single endpoint for retrieving flexible and efficient data in a strongly-typed and self-documenting way. By following this guide, you will build a service component in Ballerina and deploy it on Choreo for any GraphQL client application to utilize it.

This guide shows how to build a simple reading list service and deploy it in Choreo using Ballerina. The GraphQL endpoint you create in this guide has two operations: **Query** and **Mutation**. The Query type operations read the data in the data source, and the Mutation operations update the data in the data source. The reading list service has two queries and three mutations as follows:

### Queries

**Retrieve the reading list**

This resource accepts an optional filter, `status`, which filters the reading list by reading status. Accepted values for status are `reading`, `read`, and `to_read`.

**Sample request**:
```
$ curl -X POST -H "Content-Type: application/json" -d '{"query": "query {allBooks (status: \"reading\") { id title author status }}"}' http://localhost:8090
```

**Sample response**
```
$ {
  "data": {
    "allBooks": [
      {
        "id": 1,
        "title": "Sample Book",
        "author": "Test Author",
        "status": "to_read"
      }
    ]
  }
}
```

**Retrieve a book item from the reading list**

This resource accepts a filter, `id`, which will select the book item from the reading list by book id.  The id is an Integer value. 

**Sample request**
```
$ curl -X POST -H "Content-Type: application/json" -d '{"query": "query {book (id: 1) { id title author status }}"}' http://localhost:8090
```

**Sample response**
```
$ {
  "data": {
    "book": {
      "id": 1,
      "title": "Sample Book",
      "author": "Test Author",
      "status": "to_read"
    }
  }
}
```

### Mutations

**Add a book item to the reading list**

This remote function accepts a book record as the input and consists of the title and the author. When you add a new book to the reading list, the method updates the reading status of the newly added book to `to_read`. This method returns the added book item upon successful execution.


**Sample request**

```
$ curl -X POST -H "Content-type: application/json" -d '{ "query": "mutation { addBook(book: {title: \"Sample Book\", author: \"Test Author\"}) { id title author status } }" }' 'http://localhost:8090'
```

**Sample response**

```
$ {
  "data": {
    "addBook": {
      "id": 1,
      "title": "Sample Book",
      "author": "Test Author",
      "status": "to_read"
    }
  }
}
```

**Update the reading status of a book**

This remote function requires `id` and `status` as inputs to update the reading status of the selected book. The `id` refers to the id of the book. It is an integer.  The `status` refers to the reading status that needs to be updated. This method returns the updated book item upon successful execution.

**Sample request**

```
$ curl -X POST -H "Content-type: application/json" -d '{ "query": "mutation { setStatus(id: 1, status: \"reading\") { id title author status } }" }' 'http://localhost:8090'
```

**Sample response**

```
$ {
  "data": {
    "setStatus": {
      "id": 1,
      "title": "Sample Book",
      "author": "Test Author",
      "status": "reading"
    }
  }
}
```

**Delete a book item from the reading list**

This remote function requires the `id` as the input to delete the book item from the reading list. This method returns the removed book item upon successful execution.

**Sample request**

```
$ curl -X POST -H "Content-type: application/json" -d '{ "query": "mutation { deleteBook(id: 1) { id title author status } }" }' 'http://localhost:8090'
```

**Sample response**
```
$ {
  "data": {
    "deleteBook": {
      "id": 1,
      "title": "Sample Book",
      "author": "Test Author",
      "status": "reading"
    }
  }
}
```

Our next step is to set up the resources that you will require to follow the guide, including the sample reading list application and the Choreo GitHub app.

## Prerequisites

- If you are signing in to the Choreo Console for the first time, create an organization as follows:

    1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
    2. Enter a unique organization name. For example, `Stark Industries`.
    3. Read and accept the privacy policy and terms of use.
    4. Click **Create**.

    This creates the organization and opens the **Project Home** page of the default project created for you.

- Fork the [Choreo samples repository](https://github.com/wso2/choreo-samples/), which contains the [sample GraphQL service](https://github.com/wso2/choreo-samples/tree/main/reading-list-graphql) implementation for this guide.

Let's get started!

## Learn the repository file structure

It is important to understand the purpose of the key files in the sample service. The following table provides a brief overview of each file in the reading list service.

!!! note
    The following file paths are relative to the path `<sample-repository-dir>/reading-list-graphql`.

|Filepath                 | Description                                                     |
|-------------------------|-----------------------------------------------------------------|
| `service.bal`           | The reading list service code written in the Ballerina language.|
| `Ballerina.toml`        | The Ballerina configuration file.                               |
| `.choreo/component.yaml`| The configuration file with endpoint details.                   |

## Step 1: Create a service component 

To create a Ballerina service component, follow these steps:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/) and sign in. This opens the project home page.
2. If you already have one or more components in your project, click **+ Create**. Otherwise, proceed to the next step.
3. Click the **Service** card.
4. To allow Choreo to connect to your GitHub account, click **Authorize with GitHub**. If you have not already connected your GitHub repository to Choreo, enter your GitHub credentials and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    Alternatively, select the **Use Public GitHub Repository** option and paste the [Choreo samples repository](https://github.com/wso2/choreo-samples) URL in the **Provide Repository URL** field to connect to it without requiring authorization from the [Choreo Apps](https://github.com/marketplace/choreo-apps) GitHub application. However, authorizing the repository with the [Choreo GitHub App](https://github.com/marketplace/choreo-apps) is necessary if you want to enable [**Auto Deploy**](https://wso2.com/choreo/docs/choreo-concepts/ci-cd/#deploy) for the component.

    !!! note
           The **Choreo GitHub App** requires the following permissions:

           - Read and write access to code and pull requests.
           - Read access to issues and metadata.
             
           You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, write access is exclusively utilized for sending pull requests to a user repository. Choreo will not directly push any changes to a repository.

5. Under **Connect a Git Repository**, enter the following information:

    | **Field**               | **Value**               |
    |-------------------------|-------------------------|
    | **Organization**        | Your GitHub account     |
    | **GitHub Repository**   | **`choreo-samples`**    |
    | **Branch**              | **`main`**              |
    | **Component Directory**| `reading-list-graphql`    |.

6. Select **Ballerina** as the buildpack.

7. Enter a display name, a unique name, and a description for the service component. You can enter the values given below:
    
    !!! info
         In the **Component Name** field, you must specify a name to uniquely identify the component in various contexts. The value is editable only at the time you create the component. You cannot change the name after you create the component.

    | **Field**                 | **Value**               |
    |---------------------------|-------------------------|
    | **Component Display Name**| `Ballerina Reading List`|
    | **Component Name**        | `ballerina-reading-list`|
    | **Description**           | Manage a reading list   |

8. Click **Create**. This creates the component and takes you to the **Overview** page of the component.

You have successfully created a service component that exposes a GraphQL API written in the Ballerina language. Next, let's build and deploy the service.

## Step 2: Build and deploy

Now that we have connected the source repository, it's time to build and deploy the reading list service.

### Step 2.1: Build

To build the service, follow these steps:

1. In the left navigation menu, click **Build**.
2. On the **Build** page, click **Build Latest**.

    !!! note
        Building the service component may take a while. You can track the progress via the logs in the **Build Details** pane. Once the build process is complete, the build status changes to **Success**.

### Step 2.2: Deploy

To deploy the service, follow these steps: 

1. In the left navigation menu, click **Deploy**.
2. On the **Set Up** card, click **Configure &  Deploy**.
3. In the **Configurations** pane that opens, click **Next** to skip the configuration.
4. Review the **Endpoint Details** and click **Deploy**.

    !!! note
        Deploying the service component may take a while. Once deployed, the **Development** environment card indicates the **Deployment Status** as **Active**.
To build and deploy the service, follow the steps below:

Once you have successfully deployed your service, you can test, manage, and observe it like any other component type in Choreo.

For detailed instructions, see the following sections:

- [Step 3: Test](https://wso2.com/choreo/docs/testing/test-graphql-endpoints-via-the-graphql-console/)
- [Step 4: Manage](https://wso2.com/choreo/docs/manage/api-management/)
