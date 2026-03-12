# {{ product_name }} Command-Line Interface (CLI) Overview

The {{ product_name }} command-line interface (CLI) is a command-line tool that helps you easily work with {{ product_name }} using commands. By utilizing commands, it significantly improves the development experience for {{ product_name }} users. This versatile tool simplifies different stages of the development process, making interactions more efficient and user-friendly.

{{ product_name }} serves as a comprehensive internal platform-as-a-service. The {{ product_name }} CLI serves as a pivotal tool aimed at enhancing its capabilities. With the {{ product_name }} CLI, you can leverage the following benefits: 

- **Streamline Deployment Processes**: {{ product_name }} CLI simplifies the entire process from creating a component in {{ product_name }}, building it, to deploying, testing, and monitoring independent of the language and framework used to implement the component.

- **Versatile workflow across frameworks**: Regardless of your chosen framework, {{ product_name }} CLI offers a uniform end-to-end process. {{ product_name }} seamlessly integrates with different web application types (SPA, SSR, SSG, or simple static files), services (REST, GraphQL, gRPC), scheduled jobs, manual triggers, API proxies and more. This versatility enables you to orchestrate a wide range of cloud-native components seamlessly.

## Key features of the {{ product_name }} CLI

- **Create and Manage Resources**: Simplify project and component management. You can easily initiate and organize projects in {{ product_name }} through simple commands.

   - **Create Builds and Deployments**: Simplifies the process of creating builds and deploying components. You can build and promote components to environments easily with simple commands, ensuring a seamless transition from development to deployment.

    !!! info "Note"

          The {{ product_name }} CLI currently supports the following component types:

          - Service
          - Web Application
          - Webhook
          - Scheduled Task
          - Manual Task
          - API Proxy

- **Monitor with Logs** : The integrated log functionality in the {{ product_name }} CLI allows you to effectively monitor your components. You can access valuable insights into your components behavior and performance directly from the command line.

- **Develop with Remote Dependencies** : Use the {{ product_name }} CLI or VS Code extension to securely connect your local setup to {{ product_name }} environment for integrated testing and validation — see the [guide](../../develop-components/connect-to-remote-dependencies-while-developing/#using-cli) for details.

For troubleshooting tips and answers to frequently asked questions, see the [{{ product_name }} CLI FAQ](../references/faq.md#{{ product_name }}-cli).
