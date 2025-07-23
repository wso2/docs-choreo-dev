Choreo allows you to inject values for your configurations through a rich user interface.

!!! info
    - This feature is currently only available for Go, Python, Java, .NET, NodeJs, Ruby and PHP buildpacks (excluding Web Applications).
    - The configuration form is supported starting from component.yaml v1.2.

You can configure configurations in the [component.yaml](./manage-component-source-configurations.md). Based on these configurations, a form is displayed in the Choreo console. Users can define environment variables and file keys under the `configurations` section in the component.yaml.

Refer to the following examples for more details.

| Configuration                 |    Type      | Required     | Description                                                                       |
|-------------------------------|--------------|--------------|-----------------------------------------------------------------------------------|
| **env**                       | [Object[ ]](#environment-variables) | Optional     | An array of environment variable configurations.                                  |
| **file**                      | [Object[ ]](#file-mount) | Optional     | An array of file configurations.                                                  |

## Environment Variables

| Configuration                 |    Type      | Required     | Description                                                                       |
|-------------------------------|--------------|--------------|-----------------------------------------------------------------------------------|
| **name**                      | String       | Required     | A unique name for the environment variable, starting with a letter or an underscore, and containing only letters, numbers, or underscores. |
| **valueFrom**                 | [Object](#valuefrom-object)      | Required     | The source of the configuration form.                                             |


```yaml
configurations:
    # +optional List of environment variables to be injected into the component.
    env:
        - name: DB_USER
        # +required value source
        # Allowed value sources: connectionRef, configForm
        valueFrom:
            # +required config form value source
            configForm:
                # +optional display name inside the config form, name will be shown in config form if not specified
                displayName: DB User
                # +optional default value is true if not specified
                required: false
                # +optional default value is string if not specified
                # Allowed types - string, number, boolean, secret
                type: string
```

!!! notes
    Only string, boolean, number, and secret are allowed as environment variable types.

## File Mount

| Configuration                 |    Type      | Required     | Description                                                                       |
|-------------------------------|--------------|--------------|-----------------------------------------------------------------------------------|
| **mountPath**                 | String       | Required     | Path that file to be mounted in the container                                     |
| **name**                      | String       | Required     | A unique name for the environment variable, starting with a letter or an underscore, and containing only letters, numbers, or underscores. |
| **type**                      | String       | Required     | File extension type. Supported types are yaml, json, and toml, and type is required |
| **values**                    | [Object[ ]](#values-object)        | Required     | Required under file section. File key-values definition                           |

```yaml
configurations:
  # +optional List of files to be injected into the component from config form
  file:
    # +required name of the file
    - name: application.yaml
      # +required path to mount the file at
      mountPath: /src/resources
      # +required file type
      # Supported types - yaml, json and toml
      type: yaml
      # +required define keys of the file
      values:
        # keys of the file
        # +required at least one key
        - name: version
          # +required value source
          # Allowed value sources: connectionRef, configForm
          valueFrom:
              # +required config form value source
              configForm:
                # +optional display name inside the config form, name will be shown in config form if not specified
                displayName: Version
                # +optional default value is string if not specified
                # Allowed types - string, number, boolean, secret, object, array
                type: number
                # +required if type is object or array
                # define the properties of the object
                properties:
                  # +required sub key of the object
                  - name: logger_name
                    # +optional display name inside the config form, name will be shown in config form if not specified
                    displayName: Logger Name
                    # +required sub key of the object
                  - name: level
                    # +optional display name inside the config form, name will be shown in config form if not specified
                    displayName: Level
                    # +optional define enum list for value selection
                    values:
                      - info
                      - debug
        # keys of the file
        - name: users
          # +required value source
          # Allowed value sources: connectionRef, configForm
          valueFrom:
              # +required config form value source
              configForm:
                # +optional display name inside the config form, name will be shown in config form if not specified
                displayName: Users
                # +optional default value is string if not specified
                # Allowed types - string, number, boolean, secret, object, array
                type: array
                # +required if type is array
                items:
                    # +optional default value is string if not specified
                    type: object
                    # +required if type is object or array
                    # define the properties of the object
                    properties:
                        - name: name
                        - name: description
                          required: false
                        - name: age
                          type: number
                        - name: address
                          type: object
                          properties:
                          - name: street
                          - name: city
```

## General Configuration Form Definitions

### Values object
| Configuration                 |    Type      | Required     | Description                                                                       |
|-------------------------------|--------------|--------------|-----------------------------------------------------------------------------------|
| **name**                      | String       | Required     | The name of the file sub key                                                      |
| **valueFrom**                 | [Object](#valuefrom-object)       | Required     | Configuration form value source definition                   |

### ValueFrom object

| Configuration                 |    Type      | Required     | Description                                                                       |
|-------------------------------|--------------|--------------|-----------------------------------------------------------------------------------|
| **configForm**               | [Object](#configform-object)      | Required     | Configuration form value source definition                   |

### ConfigForm object

| Configuration                 |    Type      | Required     | Description                                                                       |
|-------------------------------|--------------|--------------|-----------------------------------------------------------------------------------|
| **displayName**               | String       | Optional     | The display name of the defined key in the configuration form                     |
| **type**                      | String       | Optional     | The type of value. Supported types are string, number, boolean, secret, object,   |
| **required**                  | Boolean      | Optional     | Define whether the value is required or not for the key. Defaults to true when not specified. |
| **properties**                | Object       | Optional     | Required if type is an object. Definition for defining the sub-properties of the object                      |
| **items**                     | [Object](#items-object)       | Optional     | Required if type is an array. Definition for defining the array                                   |

### Properties object

| Configuration                 |    Type      | Required     | Description                                                                       |
|-------------------------------|--------------|--------------|-----------------------------------------------------------------------------------|
| **name**                      | String       | Required     | The name of the file sub key                                                      |
| **displayName**               | String       | Optional     | The display name of the defined key in the configuration form                     |
| **type**                      | String       | Optional     | The type of value. Supported types are string, number, boolean, secret, object,   |
| **required**                  | Boolean      | Optional     | Define whether the value is required or not for the key. Defaults to true when not specified. |

### Items object
| Configuration                 |    Type      | Required     | Description                                                                       |
|-------------------------------|--------------|--------------|-----------------------------------------------------------------------------------|
| **type**                      | String       | Optional     | The type of value. Supported types are string, number, boolean, secret, object,   |
| **properties**                | [Object[ ]](#properties-object)        | Optional     | Required if type is an object. Definition for defining the sub-properties of the object                      |


!!! notes
    - It is allowed to define nested configurations under the types `object` and `array`.
    - `properties` is required if the type is `object` and define sub-object keys under it.
    - `items` is required if the type is `array` and define array properties under it.

Once user clicks the `Configure and Deploy` button, defined configurtions are shown and user can inject values through the form.
