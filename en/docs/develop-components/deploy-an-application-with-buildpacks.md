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

## Language Specific Details

In Choreo, for a project to be detected as a given language project, at least one of the required files should be in the project directory.

| Language | Supported Versions    | Required Files             |
|----------|-----------------------|----------------------------|
| Ballerina | 2201.3.5, 2201.4.1, 2201.5.0, 2201.5.1, 2201.6.0, 2201.7.0 | `Ballerina.toml`|
| Java     | 8, 11, 17, 18 (OpenJDK Runtime Environment Temurin) | `pom.xml`, `*.java`, `build.gradle`, `build.gradle.kts` |
| Go       | 1.x | `go.mod`, `*.go` |
| NodeJS   | 12.x.x, 14.x.x, 16.x.x, 18.x.x, 20.x.x | `package.json`, `*.js` |
| Python   | 3.10.x, 3.11.x | `requirements.txt`, `*.py`   !!! info For Python, it is mandatory to have a [Procfile](#procfile) in the project root directory. |
| Ruby     | 3.1.x, 3.2.x | `Gemfile`, `*.rb` |
| PHP      | 8.1.x, 8.2.x | `composer.json`, `*.php` |
| WSO2 MI | 4.1.0.x, 4.2.0.x |`pom.xml`|

!!! info

    1. For Java, Main class should be defined in the Manifest.


## Develop a component

To develop a service component that exposes a REST API in Go. you can follow the [Develop a Go REST API ](develop-services/develop-a-go-rest-api.md) guide.

Refer below examples for other component types and buildpacks. Follow the readme.md inside the example.

| Component Type | Buildpack   | Example            |
|----------------|-------------|--------------------|
| Service        |  Java | [Product Management Service](https://github.com/wso2/choreo-samples/tree/main/product-management-service)|
| Service| Python | [Reading List Service](https://github.com/wso2/choreo-samples/tree/main/reading-books-list-service-python)|
| Service| NodeJS | [Reading List Service](https://github.com/wso2/choreo-samples/tree/main/reading-books-list-service-nodejs)|
| Service| Ballerina | [Echo Service](https://github.com/wso2/choreo-samples/tree/main/echo-service)|
| Service | WSO2 MI | [Hello World Service](https://github.com/wso2/choreo-samples/tree/main/hello-world-mi)|
| Service | Ruby | [Hello World Service](https://github.com/wso2/choreo-samples/tree/main/hello-world-ruby-service)|
| Web Application | PHP | [Hello World Web App](https://github.com/wso2/choreo-samples/tree/main/hello-world-php-webapp)|
| Manual Task | Go | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-go-task)|
| Manual Task | Ballerina | [Covid19 Statistics To Email](https://github.com/wso2/choreo-samples/tree/main//covid19-statistics-to-email)|
| Manual Task | Java | [Hello World Task](https://github.com/wso2/choreo-samples/tree/main/hello-world-java-task)|

Refer [choreo samples](https://github.com/wso2/choreo-samples) for more examples.

## Procfile

A `Procfile` is a configuration file used to declare what commands are run by your application's containers. It is mandatory to have a Procfile for `Python`. For other buildpacks(Java, PHP, NodeJS, Ruby and Go), you can add a Procfile if you want to override the default entrypoint of the container. Procfile should be located in project root directory.

Here's an example `Procfile` for an application:

```
web: <command to start your application>
```

Eg:-

Python - `web: python main.py` <br>
Go - `web: go run main.go` <br>
Java - `web: java -jar target/sample.jar` <br>
