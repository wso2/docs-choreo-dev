# Deploy an Application with Buildpacks

Using Choreo, you can easily deploy applications written in different language frameworks (such as Java, Go, NodeJS, Python, Ruby, PHP, etc.) on shared or private data planes.

Choreo supports deploying applications with buildpacks for the following component types:

- Service
- Web Application
- Scheduled Task
- Manual Task
- Webhook
- Event Handler
- Test Runner

## Buildpacks
Buildpacks are a fundamental building block in modern application development. They convert your source code into a secure, efficient, production-ready container image without a Dockerfile. With Choreo, developers can take advantage of this powerful tool to effortlessly deploy their applications without the hassle of manual configuration.

Choreo uses [Google Buildpacks](https://cloud.google.com/docs/buildpacks/overview) as default buildpacks for Java, Go, NodeJS, Python, PHP, and Ruby. Choreo uses its own buildpacks for Ballerina and WSO2 MI.

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

    ### Procfile
    A `Procfile` is a configuration file used to declare the commands that are run by your application's containers. 
    
    !!! info 
        In **Python** projects, it is mandatory to have a `Procfile` with the `web` process type in the project root directory.

    Here's an example `Procfile` for an application:

    ```
    web: <command to start your application>
    ```

    e.g.,

    `web: python main.py` <br>
    `web: gunicorn --bind :8080 --workers 1 --threads 8 --timeout 0 main:app` <br>
    `web: flask run --host=0.0.0.0`

    For more examples, see [Choreo samples](https://github.com/wso2/choreo-samples).

=== "Ballerina"

    Supported Versions - 2201.3.5, 2201.4.1, 2201.5.0, 2201.5.1, 201.6.0,  2201.7.0

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service| [Echo Service](https://github.com/wso2/choreo-samples/tree/main/echo-service)|
    | Manual Task | [Covid19 Statistics To Email](https://github.com/wso2/choreo-samples/tree/main//covid19-statistics-to-email)|
    | Webhook | [Salesforce New Case To Google Sheet](https://github.com/wso2/choreo-samples/tree/main/sfdc-new-case-to-gsheet)|
    | Scheduled Task | [Shopify New Customers to HubSpot Create/Update Contact](https://github.com/wso2/choreo-samples/tree/main/shopify-new-customers-to-hubspot-contact)|

    For more examples, see [Choreo samples](https://github.com/wso2/choreo-samples).

=== "Go"

    Supported Versions - 1.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service| [Greeting Service](https://github.com/wso2/choreo-samples/tree/main/greeting-service-go)|
    | Manual Task | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-go-task)|
    | Web Application | [Hello World Web Application](https://github.com/wso2/choreo-samples/tree/main/hello-world-go-webapp)|

    For more examples, see [Choreo samples](https://github.com/wso2/choreo-samples).

=== ".NET"
    Supported Versions - 6.x, 7.x, 8.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service | [Greeting Service](https://github.com/wso2/choreo-samples/tree/main/dotnet-greeter)|

    For more examples, see [Choreo samples](https://github.com/wso2/choreo-samples).

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
    
    #### Procfile 

    A `Procfile` is a configuration file used to declare the commands that are run by your application's containers. 
    If you want to customize the default entry point of the container, you can include a `Procfile` with the `web` process type in the project root directory. 
    In **Java** projects, it is optional to include a `Procfile`.

    Here's an example `Procfile` for an application:

    ```
    web: <command to start your application>
    ```

    e.g.,

    `web: java -jar target/sample.jar` <br>

    For more examples, see [Choreo samples](https://github.com/wso2/choreo-samples).

=== "NodeJS"
    Supported Versions - 12.x.x, 14.x.x, 16.x.x, 18.x.x, 20.x.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service| [Reading Books List Service](https://github.com/wso2/choreo-samples/tree/main/reading-books-list-service-nodejs)|
    | Manual Task | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-nodejs-task)|

    #### Procfile 

    A `Procfile` is a configuration file used to declare the commands that are run by your application's containers. 
    If you want to customize the default entry point of the container, you can include a `Procfile` with the `web` process type in the project root directory. 
    In **NodeJS** projects, it is optional to include a `Procfile`.

    Here's an example `Procfile` for an application:

    ```
    web: <command to start your application>
    ```

    e.g.,

    `web: node app.js` <br>

    For more examples, see [Choreo samples](https://github.com/wso2/choreo-samples).

=== "PHP"
    Supported Versions - 8.1.x, 8.2.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service| [Hello World Service](https://github.com/wso2/choreo-samples/tree/main/hello-world-php-service)|
    | Manual Task | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-php-task)|
    | Web Application | [Hello World Web Application](https://github.com/wso2/choreo-samples/tree/main/hello-world-php-webapp)|

    #### Procfile 

    A `Procfile` is a configuration file used to declare the commands that are run by your application's containers. 
    If you want to customize the default entry point of the container, you can include a `Procfile` with the `web` process type in the project root directory. 
    In **PHP** projects, it is optional to include a `Procfile`.

    Here's an example `Procfile` for an application:

    ```
    web: <command to start your application>
    ```

    e.g.,

    `web:php -S 0.0.0.0:8000 index.php` <br>

    For more examples, see [Choreo samples](https://github.com/wso2/choreo-samples).

=== "Ruby"
    Supported Versions - 3.1.x, 3.2.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service | [Hello World Service](https://github.com/wso2/choreo-samples/tree/main/hello-world-ruby-service)|
    | Manual Task | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-ruby-task)|
    | Web Application | [Hello World Web Application](https://github.com/wso2/choreo-samples/tree/main/hello-world-ruby-webapp)|

    #### Procfile 

    A `Procfile` is a configuration file used to declare the commands that are run by your application's containers. 
    If you want to customize the default entry point of the container, you can include a `Procfile` with the `web` process type in the project root directory. 
    In **Ruby** projects, it is optional to include a `Procfile`.

    Here's an example `Procfile` for an application:

    ```
    web: <command to start your application>
    ```

    e.g.,

    `web: ruby app.rb` <br>
    `web:bundle exec ruby app.rb -p 8080` <br>

    For more examples, see [Choreo samples](https://github.com/wso2/choreo-samples).

=== "WSO2 MI"
    Supported Versions - 4.1.0.x, 4.2.0.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service |[Hello World Service](https://github.com/wso2/choreo-samples/tree/main/hello-world-mi)|

    For more examples, see [Choreo samples](https://github.com/wso2/choreo-samples).

=== "Spring Boot"
    Supported Java Versions
        - 8, 11, 17, 18 (OpenJDK Runtime Environment Temurin)

    !!! info
         You can use this buildpack only with web applications. For other component types, use the **Java** buildpack. Additionally, if the generated artifact is a `WAR` file, it is necessary to include a **Procfile**.

    #### Procfile 

    A `Procfile` is a configuration file used to declare the commands that are run by your application's containers. 
    If you want to customize the default entry point of the container, you can include a `Procfile` with the `web` process type in the project root directory. 
    In **Java** projects, it is optional to include a `Procfile`.

    Here's an example `Procfile` for an application:

    ```
    web: <command to start your application>
    ```

    e.g.,

    `web: java -jar target/sample.war` <br>

### Configure build-time environment variables

You can configure the environment variables necessary to build the component using the **Build Configurations Editor** on the component **Build** page. 

!!! info
    The capability to configure build-time environment variables is not available for components created using **Ballerina** or **WSO2 MI** buildpacks.

During the build process, the build-time environment variables and their values are passed to the buildpack. Therefore, you can configure both buildpack-specific environment variables and those required for the component build.

For example, if you want to override the Maven command of the **Java** buildpack, you can use `GOOGLE_MAVEN_BUILD_ARGS` as the environment key and `clean install` as the value. 

For more examples, see [Google Cloud's buildpacks documentation](https://cloud.google.com/docs/buildpacks/service-specific-configs).
