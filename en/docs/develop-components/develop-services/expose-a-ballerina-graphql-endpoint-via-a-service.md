# Develop a Ballerina GraphQL API

Choreo offers the flexibility to develop and deploy applications in a language of your preference. This guide shows you how to deploy a service component that exposes a GraphQL API using the Ballerina language in Choreo. No prior knowledge of the Ballerina language is required to follow this guide. 

GraphQL API is a query language and runtime that provides a single endpoint for retrieving flexible and efficient data in a strongly-typed and self-documenting way. By following this guide, you will build a service component in Ballerina and deploy it on Choreo for any GraphQL client application to utilize it.

This guide shows how to build a simple reading list service and deploy it in Choreo using Ballerina. The GraphQL endpoint you create in this guide has two operations: **Query** and **Mutation**. The Query type operations read the data in the data source, and the Mutation operations update the data in the data source. The reading list service has two queries and three mutations as follows:

### Queries

**Retrieve the reading list**

This resource accepts an optional filter, `status`, which filters the reading list by reading status. Accepted values for status are `reading`, `read`, and `to_read`.

**Sample Request**:
```
$ curl -X POST -H "Content-Type: application/json" -d '{"query": "query {allBooks (status: \"reading\") { id title author status }}"}' http://localhost:8090
```

**Sample Response**
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

**Sample Request**
```
$ curl -X POST -H "Content-Type: application/json" -d '{"query": "query {book (id: 1) { id title author status }}"}' http://localhost:8090
```

**Sample Response**
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


**Sample Request**

```
$ curl -X POST -H "Content-type: application/json" -d '{ "query": "mutation { addBook(book: {title: \"Sample Book\", author: \"Test Author\"}) { id title author status } }" }' 'http://localhost:8090'
```

**Sample Response**

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

**Update reading status of a book**

This remote function requires `id` and `status` as inputs to update the reading status of the selected book. The `id` refers to the id of the book. It is an integer.  The `status` refers to the reading status that needs to be updated. This method returns the updated book item upon successful execution.

**Sample Request**

```
$ curl -X POST -H "Content-type: application/json" -d '{ "query": "mutation { setStatus(id: 1, status: \"reading\") { id title author status } }" }' 'http://localhost:8090'
```

**Sample Response**

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

**Sample Request**

```
$ curl -X POST -H "Content-type: application/json" -d '{ "query": "mutation { deleteBook(id: 1) { id title author status } }" }' 'http://localhost:8090'
```

**Sample Response**
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

1. To deploy a service component with a GraphQL endpoint, you will need a GitHub account with a repository that contains an `endpoints.yaml`. Fork the [Choreo sample apps repository](https://github.com/wso2/choreo-sample-apps/), which contains the sample for this guide.
2. The Choreo GitHub App requires the following permissions:
- Read access to issues and metadata
- Read and write access to code, pull requests, and repository hooks

Let's get started!

## Repository file structure

Before we begin, it's important to familiarize yourself with the key files in the sample reading list application. The below table gives a brief overview of the important files in the reading list service.

!!! note
    The following file paths are relative to the path `<sample-repository-dir>/ballerina/reading-list`.

|Filepath                 | Description                                                                              |
|-------------------------|------------------------------------------------------------------------------------------|
| service.bal             | Reading list service code written in the Ballerina language.                                 |
| Ballerina.toml          | Ballerina configuration file.                                                             |
| .choreo/endpoints.yaml  | Endpoint details configuration file.                                                    |

## Create a service component 

Let's create a Ballerina service component by following these steps:

1. Sign in to the Choreo Console at [https://console.choreo.dev](https://console.choreo.dev).
2. Create a project to add the service component. 
3. On the **Components** page, click on the **Service** card.
4. Enter a unique name and a description of the service. For this guide, let's enter the following values:

   | Field      | Value                      |
   |------------|----------------------------|
   | Name       | Ballerina Reading List     |
   | Description| Manage a reading list      |

5. Select **GitHub** tab.
6. If you have not already connected your GitHub repository to Choreo, to allow Choreo to connect to your GitHub account, click **Authorize with GitHub** and enter your GitHub credentials, and select the repository you created in the prerequisites section to install the [Choreo GitHub App](https://github.com/marketplace/choreo-apps).

    !!! info
         The **Choreo GitHub App** requires the following permissions:<br/><br/>- Read and write access to code and pull requests.<br/><br/>- Read access to issues and metadata.<br/><br/>You can [revoke access](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/reviewing-your-authorized-integrations#reviewing-your-authorized-github-apps) if you do not want Choreo to have access to your GitHub account. However, Choreo requires write access only to send pull requests to a user repository. Choreo will not directly push any changes to a repository.

7. Enter the following information:

    | **Field**             | **Description**        |
    |-----------------------|------------------------|
    | **GitHub Account**    | Your account           |
    | **GitHub Repository** | choreo-sample-apps     |
    | **Branch**            | **`main`**             |
    | **Buildpack**      | Ballerina              |
    | **Project Path**      | ballerina/reading-list |

8. Click **Create**. Once the component creation is complete, you will see the component overview page.

You have successfully created a Service component that exposes a GraphQL API written in the Ballerina language. Next, let's build and deploy the service.

## Build and deploy

Now that we have connected the source repository, it's time to build and deploy the reading list service.

To build and deploy the service, follow these steps:

1. In the left navigation click **Deploy** and navigate to the **Deploy** page.
2. In the **Deploy** page, click **Configure &  Deploy**. The endpoint added to the `.choreo/endpoints.yaml` is listed in the **Configure & Deploy** pane on the right. 

    !!! tip
        - Optionally, you can click the arrow icon on the endpoint in the **Configure & Deploy** pane to view the endpoint details. The endpoint details added to the `.choreo/endpoints.yaml` is visible here.
        - Optionally, to change the network visibility, you can click the edit icon on the endpoint on the **Configure & Deploy** pane to change the network visibility. Click **Update** to persist your changes.

    !!! note
        To test the service over the web you need to expose the service to the public. This is done securely by setting the network visibility to `public` in `.choreo/endpoints.yaml`. 

3. In the **Endpoint Details** pane, click **Deploy** to deploy the service.

    !!! note
        Deploying the service component may take a while. You can track the progress by observing the logs. Once the deployment is complete, the deployment status changes to `Active` in the corresponding environment card.


4. Check the deployment progress by observing the console logs on the right of the page.
    You can access the following scans under **Build**. 

    - **The Dockerfile scan**: Choreo performs a scan to check if a non-root user ID is assigned to the Docker container to ensure security. If no non-root user is specified, the build will fail.
    - **Container (Trivy) vulnerability scan**: This detects vulnerabilities in the final docker image. 
    - **Container (Trivy) vulnerability scan**: The details of the vulnerabilities open in a separate pane. If this scan detects critical vulnerabilities, the build will fail.

    !!! info
        If you have Choreo environments on a private data plane, you can ignore these vulnerabilities and proceed with the deployment.

The DevOps configurations related to scaling, health checks, configuration, and secret management are available to all Web Application components regardless of how you created them, similar to other Choreo components.  

For detailed instructions, see the following sections:

- [Step 3: Test](https://wso2.com/choreo/docs/testing/test-graphql-endpoints-via-the-graphql-console/)
- [Step 4: Manage](https://wso2.com/choreo/docs/manage/api-management/)

## Manage the deployment¶

If you want to view Kubernetes-level insights to perform a more detailed diagnosis of this Ballerina REST API, see Choreo's [DevOps capabilities](https://wso2.com/choreo/docs/devops-and-ci-cd/builds-and-deployments/).