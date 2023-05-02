# Environments

In a given data plane, Choreo offers one or more environments for developers to run their applications. The cloud data plane of Choreo provides two environments by default. One for development and one for production use. Each project in Choreo is bound to one or more environments available for the organization. For example, project A can choose to use environments dev, staging and production while project B can choose to use only development and production environments.

a components in a project can be promoted across the environments that are available in the project. Upon promotion, configuration values of a component can be overridden with values that are specific to the environment to which the component is being promoted.

The following diagram illustrates how a component is promoted across environments.

![Choreo environments](../assets/img/choreo-concepts/choreo-environments.png){.cInlineImage-threeQuarter}
