# Developing Integrations with Integration Studio

Choreo iPaaS offers a platform for developing and deploying integrations with ease. With the Integration Studio,
developers can create integrations using WSO2 Synapse and deploy them in a Micro Intgrator (MI) runtime. Choreo iPaaS
provides a centralized management system for integrations, making deployment and maintenance simple.

## Integration Types

Choreo supports following types of MI Integrations

- __Integration as an API__
- __Event-Triggered Integration__
- __Scheduled Integration__
- __Manually-Triggered Integration__
- __Composite Integration (Coming soon)__

## Develop with Integration Studio

[Integration Studio](https://wso2.com/integration/integration-studio/) is a graphical development environment for
building and deploying integration artifacts in WSO2 Micro Integrator. It is an Eclipse-based IDE that provides a
comprehensive set of tools for designing and testing integration flows and building integration artifacts. With WSO2
Integration Studio, developers can create integration projects and develop integration artifacts in a visual,
drag-and-drop manner, reducing the time and effort required to build and deploy integration flows. Additionally, the
Studio provides features such as debugging, testing, and version control to help ensure the quality and reliability of
integration flows.

Creating an Integration project and Composite Exporter project in WSO2 Integration Studio allows developers to export
their integration projects as a single deployment artifact, called a composite application. This composite application
contains all the necessary components and configurations required to deploy the integration project to WSO2 Micro
Integrator. The Composite Exporter project helps to simplify the deployment process by allowing developers to package
their integration projects as a single, self-contained artifact that can be easily deployed and managed. This can help
improve the consistency and reliability of integration projects, as well as simplify the overall deployment process.

To get started with Integration Studio, please refer to
the [document](https://apim.docs.wso2.com/en/latest/integrate/develop/intro-integration-development/).

## Integration Project Directory Structure

Integration Project is a multi module maven project. It can contain multiple modules with WSO2 Synpase configurations
and composite application module to export all the configurations as a single deployable artifact.
Addition to the above OpenAPI definition file and libraries can be added optionally. OpenAPI definition can be added to
the any location in the project and should be define it’s path when creating a component. Java libraries can be added to
the directory created in the project root location with name libs.

## OpenAPI support

An [OpenAPI definition](https://spec.openapis.org/oas/v3.1.0#openapi-specification) can be developed to define your REST
API. When a REST API is developed with integration studio and deployed on Choreo, it allows you to give the OpenAPI
definition to attach to your Integration as an API component. Attaching an OpenAPI definition allows developers and
consumers to understand the API.

When a REST API is developed in WSO2 Integration Studio, developers can add an OpenAPI definition to the project. During
component creation, the path to the OpenAPI Specification (OAS) file can be specified.

![Open API File](/assets/img/ipaas/integration-studio/openapi_file.png){.cInlineImage-half}

## Adding third party libraries

Adding third-party jars to WSO2 Micro Integrator can bring several benefits to the development and deployment of
integration solutions. Third-party jars can provide additional functionality that is not available in the core Micro
Integrator distribution, allowing for greater flexibility and customization of integration solutions. They can also be
used to integrate Micro Integrator with existing systems, such as databases or other APIs, improving the overall
integration capabilities. Third-party jars can also provide optimized implementations of common functions, improving the
performance of Micro Integrator. By using third-party jars, developers can avoid having to re-implement commonly used
functions, reducing development time and increasing efficiency.

To include such third-party libraries, a new folder can be created with the name __libs__. During the component
deployment, all the __jars__ in the __libs__ directory in the project path will be included in the MI runtime.

![Libs Dir](/assets/img/ipaas/integration-studio/libs_dir.png){.cInlineImage-half}

## Support Environment variables

Environment variables are used in WSO2 Micro Integrator to improve the configuration management, security, portability,
and manageability of the installation. By using environment variables, organizations can simplify the management and
maintenance of their MI installations, and ensure that they can be quickly and easily updated as needed.

The Choreo DevOps portal allows configuring the __Configuration and secrets__ of your deployment. For more about how to
configure Configuration, secrets, and Environment variables please refer to
the [Configurations and secrets](/devops/devops-portal/#configurations-and-secrets) documentation.

To know about supported wso2 synapse parameters from environment variable, please refer to the
Integrator [documentation](https://apim.docs.wso2.com/en/latest/integrate/develop/injecting-parameters/#supported-parameters)
.

## Using Connectors in an Integration project

WSO2 Micro Integrator (MI) Connectors are pre-built connectors that provide integration between WSO2 MI and various
other systems. They allow you to easily connect to different systems, such as databases, message brokers, or REST APIs,
and perform actions such as sending messages, executing queries, or retrieving data. The connectors are designed to be
simple to use and can be easily integrated into your WSO2 MI integration flows. They can be used to implement a wide
range of integration scenarios, including data integration, service-oriented architecture (SOA) integration, and
event-driven architecture (EDA) integration.

WSO2 Integration Studio is a development environment that can be used to develop integration flows using the WSO2 MI
Connectors. With Integration Studio, you can design and implement integration flows that connect various systems and
perform various actions, such as sending messages, executing queries, or retrieving data. The integration flows can be
built using the pre-built connectors provided by WSO2 MI, or custom connectors that you develop using the Connector
Development Toolkit. Integration Studio provides a graphical user interface that makes it easy to build and test
integration flows, and it also provides a set of tools for managing and deploying the integration flows to a WSO2 MI
runtime environment.

Please refer to
the [Adding Connectors](https://apim.docs.wso2.com/en/latest/integrate/develop/creating-artifacts/adding-connectors/)
documentation on adding a connector to your Integration project. Also refer to the [Connectors
Overview](https://apim.docs.wso2.com/en/latest/reference/connectors/connectors-overview/) document for know more about
connectors.
