# Configuration Management

Choreo allows you to store, retrieve, and share configurations for your components across multiple components within an organization. A configuration is an input to the components and is a key-value pair that comes in the form of environment variables, files, and command-line arguments. Users can specify the configuration values per each environment in Choreo.

## Types of Configurations
Configuration values can be categorized into three types:

- **Secret**: Information that needs to be securely stored.
- **File**: File-based configurations.
- **Text Value**: Plain text configurations.

## Configuration Group

A Configuration Group represents a set of configuration keys and their associated values. These groups simplify configuration management by enabling the execution of CRUD operations (Create, Retrieve, Update, Delete) on the entire set of keys and values as a cohesive unit.

## Sharing Configurations

Choreo allows you to share your configurations as a configuration group among your components within an organization.

### Within a Project

Choreo allows you to share configuration groups among a selected set of components  or all components within the same project in the same organization, promoting reusability and consistency.

### Across the Organization

Configuration groups can be shared within organizational level, ensuring accessibility among a selected set of projects or all projects. During deployment, these shared groups and their corresponding keys are accessible to relevant components if they are mapped to a specific component configuration.