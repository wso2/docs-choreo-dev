# Deploy an Application with Buildpacks

Using Choreo, you can easily deploy applications written in different language frameworks (such as Java, Go, NodeJS, Python, Ruby, PHP etc.) on shared or private data planes.

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

Choreo uses [Google Buildpacks](https://cloud.google.com/docs/buildpacks/overview) as default buildpacks for Java, Go, NodeJS, Python, PHP and Ruby. Choreo uses its own buildpacks for Ballerina and WSO2 MI.


## Develop a component

To develop a service component that exposes a REST API in Go. you can follow the [Develop a Go REST API ](develop-services/develop-a-go-rest-api.md) guide.

Follow the guidelines below based on your language:

=== "Python"
    Supported Versions - 3.10.x, 3.11.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service| [Reading Books List Service](https://github.com/wso2/choreo-samples/tree/main/reading-books-list-service-python)|
    | Manual Task | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-python-task)|

    ### Procfile
    A `Procfile` is a configuration file used to declare what commands are run by your application's containers. 
    
    !!! info 
        For Python, it is mandatory to have a Procfile in the project root directory.

    Here's an example `Procfile` for an application:

    ```
    web: <command to start your application>
    ```

    Eg:-

    `web: python main.py` <br>
    `web: gunicorn --bind :8080 --workers 1 --threads 8 --timeout 0 main:app` <br>
    `web: flask run --host=0.0.0.0`

    Refer [choreo samples](https://github.com/wso2/choreo-samples) for more examples.

=== "Ballerina"

    Supported Versions - 2201.3.5, 2201.4.1, 2201.5.0, 2201.5.1, 201.6.0,  2201.7.0

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service| [Echo Service](https://github.com/wso2/choreo-samples/tree/main/echo-service)|
    | Manual Task | [Covid19 Statistics To Email](https://github.com/wso2/choreo-samples/tree/main//covid19-statistics-to-email)|
    | Webhook | [Salesforce New Case To Google Sheet](https://github.com/wso2/choreo-samples/tree/main/sfdc-new-case-to-gsheet)|
    | Scheduled Task | [Shopify New Customers to HubSpot Create/Update Contact](https://github.com/wso2/choreo-samples/tree/main/shopify-new-customers-to-hubspot-contact)|

    Refer [choreo samples](https://github.com/wso2/choreo-samples) for more examples.

=== "Go"

    Supported Versions - 1.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service| [Greeting Service](https://github.com/wso2/choreo-samples/tree/main/greeting-service-go)|
    | Manual Task | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-go-task)|
    | Web Application | [Hello World Web Application](https://github.com/wso2/choreo-samples/tree/main/hello-world-go-webapp)|

    #### Procfile 

    A `Procfile` is a configuration file used to declare what commands are run by your application's containers. 
    This is optional and you can add a Procfile if you want to override the default entrypoint of the container. Procfile should be located in project root directory.

    Here's an example `Procfile` for an application:

    ```
    web: <command to start your application>
    ```

    Eg:-

    `web: go run main.go` <br>

    Refer [choreo samples](https://github.com/wso2/choreo-samples) for more examples.

=== "Java"
    Supported Versions
        - 8, 11, 17, 18 (OpenJDK Runtime Environment Temurin)

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service| [Product Management Service](https://github.com/wso2/choreo-samples/tree/main/product-management-service)|
    | Manual Task | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-java-task)|

    !!! info

        1. For Java, Main class should be defined in the Manifest.
    
    #### Procfile 

    A `Procfile` is a configuration file used to declare what commands are run by your application's containers. 
    This is optional and you can add a Procfile if you want to override the default entrypoint of the container. Procfile should be located in project root directory.

    Here's an example `Procfile` for an application:

    ```
    web: <command to start your application>
    ```

    Eg:-

    `web: java -jar target/sample.jar` <br>

    Refer [choreo samples](https://github.com/wso2/choreo-samples) for more examples.

=== "NodeJS"
    Supported Versions - 12.x.x, 14.x.x, 16.x.x, 18.x.x, 20.x.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service| [Reading Books List Service](https://github.com/wso2/choreo-samples/tree/main/reading-books-list-service-nodejs)|
    | Manual Task | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-nodejs-task)|

    #### Procfile 

    A `Procfile` is a configuration file used to declare what commands are run by your application's containers. 
    This is optional and you can add a Procfile if you want to override the default entrypoint of the container. Procfile should be located in project root directory.

    Here's an example `Procfile` for an application:

    ```
    web: <command to start your application>
    ```

    Eg:-

    `web: node app.js` <br>

    Refer [choreo samples](https://github.com/wso2/choreo-samples) for more examples.

=== "PHP"
    Supported Versions - 8.1.x, 8.2.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service| [Hello World Service](https://github.com/wso2/choreo-samples/tree/main/hello-world-php-service)|
    | Manual Task | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-php-task)|
    | Web Application | [Hello World Web Application](https://github.com/wso2/choreo-samples/tree/main/hello-world-php-webapp)|

    #### Procfile 

    A `Procfile` is a configuration file used to declare what commands are run by your application's containers. 
    This is optional and you can add a Procfile if you want to override the default entrypoint of the container. Procfile should be located in project root directory.

    Here's an example `Procfile` for an application:

    ```
    web: <command to start your application>
    ```

    Eg:-

    `web:php -S 0.0.0.0:8000 index.php` <br>

    Refer [choreo samples](https://github.com/wso2/choreo-samples) for more examples.

=== "Ruby"
    Supported Versions - 3.1.x, 3.2.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service | [Hello World Service](https://github.com/wso2/choreo-samples/tree/main/hello-world-ruby-service)|
    | Manual Task | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-ruby-task)|
    | Web Application | [Hello World Web Application](https://github.com/wso2/choreo-samples/tree/main/hello-world-ruby-webapp)|

    #### Procfile 

    A `Procfile` is a configuration file used to declare what commands are run by your application's containers. 
    This is optional and you can add a Procfile if you want to override the default entrypoint of the container. Procfile should be located in project root directory.

    Here's an example `Procfile` for an application:

    ```
    web: <command to start your application>
    ```

    Eg:-

    `web: ruby app.rb` <br>
    `web:bundle exec ruby app.rb -p 8080` <br>

    Refer [choreo samples](https://github.com/wso2/choreo-samples) for more examples.

=== "WSO2 MI"
    Supported Versions - 4.1.0.x, 4.2.0.x

    Refer below examples for different component types. Follow the `readme.md` inside the example.

    | Component Type  | Example            |
    |---------------- |--------------------|
    | Service |[Hello World Service](https://github.com/wso2/choreo-samples/tree/main/hello-world-mi)|

    Refer [choreo samples](https://github.com/wso2/choreo-samples) for more examples.
