# Deploy an Application with Build Presets

Using {{ product_name }}, you can easily deploy applications written in different language frameworks (such as Java, Go, NodeJS, Python, Ruby, PHP, etc.) on shared or private data planes.

{{ product_name }} supports deploying applications with build presets for the following component types:

- Service
- Web Application
- Scheduled Task
- Manual Task
- Webhook
- Event Handler
- Test Runner

## Build Presets
Build presets are a fundamental building block in modern application development. They convert your source code into a secure, efficient, production-ready container image without a Dockerfile. With {{ product_name }}, developers can take advantage of this powerful tool to effortlessly deploy their applications without the hassle of manual configuration.

{{ product_name }} uses [Google Buildpacks](https://cloud.google.com/docs/buildpacks/overview) as default build presets for Java, Go, NodeJS, Python, PHP, and Ruby. {{ product_name }} uses its own build presets for Ballerina and WSO2 MI.

## Develop a component

To develop a service component that exposes a Service in Go, you can follow the [Develop a Service ](develop-services/develop-a-service.md) guide.

Follow the guidelines below based on your language:

=== "Python"
    Supported Versions - 3.10.x, 3.11.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service| [Reading Books List Service](https://github.com/wso2/choreo-samples/tree/main/reading-books-list-service-python)|
    | Manual Task | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-python-task)|

    For more examples, see [{{ product_name }} samples](https://github.com/wso2/choreo-samples).
    
    !!! info 
        In **Python** projects, it is mandatory to provide a `Run Command` when creating a component.

    Here are some example run commands for an application:<br>
    e.g.,

    - python main.py
    - gunicorn --bind :8080 --workers 1 --threads 8 --timeout 0 main:app
    - flask run --host=0.0.0.0

=== "Ballerina"

    Supported Versions - 2201.3.5, 2201.4.1, 2201.5.0, 2201.5.1, 201.6.0,  2201.7.0

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service| [Echo Service](https://github.com/wso2/choreo-samples/tree/main/echo-service)|
    | Manual Task | [Covid19 Statistics To Email](https://github.com/wso2/choreo-samples/tree/main//covid19-statistics-to-email)|
    | Webhook | [Salesforce New Case To Google Sheet](https://github.com/wso2/choreo-samples/tree/main/sfdc-new-case-to-gsheet)|
    | Scheduled Task | [Shopify New Customers to HubSpot Create/Update Contact](https://github.com/wso2/choreo-samples/tree/main/shopify-new-customers-to-hubspot-contact)|

    For more examples, see [{{ product_name }} samples](https://github.com/wso2/choreo-samples).

=== "Go"

    Supported Versions - 1.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service| [Greeting Service](https://github.com/wso2/choreo-samples/tree/main/greeting-service-go)|
    | Manual Task | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-go-task)|
    | Web Application | [Hello World Web Application](https://github.com/wso2/choreo-samples/tree/main/hello-world-go-webapp)|

    For more examples, see [{{ product_name }} samples](https://github.com/wso2/choreo-samples).

=== ".NET"
    Supported Versions - 6.x, 7.x, 8.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service | [Greeting Service](https://github.com/wso2/choreo-samples/tree/main/dotnet-greeter)|

    For more examples, see [{{ product_name }} samples](https://github.com/wso2/choreo-samples).

=== "Java"
    Supported Versions
        - 8, 11, 17, 18 (OpenJDK Runtime Environment Temurin)

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service| [Product Management Service](https://github.com/wso2/choreo-samples/tree/main/product-management-service)|
    | Manual Task | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-java-task)|

    !!! info

         When working on Java projects:

           - The `Main` class should be defined in the manifest file.
           - If Maven files such as `mvn.cmd` exist in the project without the `.mvn` directory, the build will fail. To ensure a successful build, you must either commit the `.mvn` directory along with any Maven files or not include any Maven files in the project if you choose not to commit the `.mvn` directory.

    For more examples, see [{{ product_name }} samples](https://github.com/wso2/choreo-samples).

=== "NodeJS"
    Supported Versions - 12.x.x, 14.x.x, 16.x.x, 18.x.x, 20.x.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service| [Reading Books List Service](https://github.com/wso2/choreo-samples/tree/main/reading-books-list-service-nodejs)|
    | Manual Task | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-nodejs-task)|

    For more examples, see [{{ product_name }} samples](https://github.com/wso2/choreo-samples).

=== "PHP"
    Supported Versions - 8.1.x, 8.2.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service| [Hello World Service](https://github.com/wso2/choreo-samples/tree/main/hello-world-php-service)|
    | Manual Task | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-php-task)|
    | Web Application | [Hello World Web Application](https://github.com/wso2/choreo-samples/tree/main/hello-world-php-webapp)|

    For more examples, see [{{ product_name }} samples](https://github.com/wso2/choreo-samples).

=== "Ruby"
    Supported Versions - 3.1.x, 3.2.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service | [Hello World Service](https://github.com/wso2/choreo-samples/tree/main/hello-world-ruby-service)|
    | Manual Task | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-ruby-task)|
    | Web Application | [Hello World Web Application](https://github.com/wso2/choreo-samples/tree/main/hello-world-ruby-webapp)|

    For more examples, see [{{ product_name }} samples](https://github.com/wso2/choreo-samples).

=== "WSO2 MI"
    Supported Versions - 4.1.0.x, 4.2.0.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service |[Hello World Service](https://github.com/wso2/choreo-samples/tree/main/hello-world-mi)|

    For more examples, see [{{ product_name }} samples](https://github.com/wso2/choreo-samples).

=== "Spring Boot"
    Supported Java Versions
        - 8, 11, 17, 18 (OpenJDK Runtime Environment Temurin)

    !!! info
         You can use this build preset only with web applications. For other component types, use the **Java** build preset. Additionally, if the generated artifact is a `WAR` file, you must include the run command in the **Build Configurations** editor on the component's **Build** page. <br> e.g.,
            `java -jar target/sample.war` <br>

### Configure build-time environment variables

You can configure the environment variables necessary to build the component using the **Build Configurations Editor** on the component **Build** page. 

For Buildpack components:
During the build process, the build-time environment variables and their values are passed to the build preset. Therefore, you can configure both build preset-specific environment variables and those required for the component build.

For example, if you want to override the Maven command of the **Java** build presets, you can use `GOOGLE_MAVEN_BUILD_ARGS` as the environment key and `clean install` as the value.

For more examples, see [Google Cloud's buildpacks documentation](https://cloud.google.com/docs/buildpacks/service-specific-configs)..

For BYOC (Bring Your Own Container) components:
You can configure build-time environment variables that are passed as build arguments (ARG) during the Docker image build process. These variables are available during the build stage of your Dockerfile and can be used to customize the build behavior.

For example, you can set environment variables to configure build-specific settings such as build modes, feature flags, or dependency options that your Dockerfile references via ARG directives.

### Customize the Default Run Command

You can configure the run command required to start the component via the **Build Configurations** editor on the component's **Build** page. This overrides the default run command provided by the build preset. After configuring the run command, you must rebuild the component for the changes to take effect.<br>

e.g., <br>

- java -jar target/sample.jar
- node app.js
- php -S 0.0.0.0:8000 index.php


## Troubleshooting Go build issues

If you encounter errors similar to:

```bash
package meal-planner-backend/prisma/db is not found
```

ensure that:

- all generated Prisma client files are committed to the repository
- the correct Go module path is configured
- generated directories are included before deployment

This issue can occur during Buildpack-based deployments in Choreo when generated Prisma files are excluded from the Git repository.