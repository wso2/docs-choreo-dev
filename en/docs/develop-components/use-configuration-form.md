Choreo allows you to inject values for your configurations through a rich user interface.

!!! info
    - This feature is currently only available for Go, Python, Java, .NET, NodeJs, Ruby and PHP buildpacks.
    - The configuration form is supported starting from component.yaml v1.2.

You can configure configurations in the [component.yaml](./manage-component-source-configurations.md). Based on this configurations, a form is displayed in the Choreo console. User can define environment variables and file keys under the `configurations` section in the component.yaml.

| Configuration                 | Required     | Description                                                                       |
|-------------------------------|--------------|-----------------------------------------------------------------------------------|
| **env**                       | Optional     | An array of env variable configurations.                                          |
| **file**                      | Optional     | An array of file configurations.                                                  |
| **name**                      | Required     | A unique name for the environment variable, starting with a letter or an underscore, and containing only letters, numbers, or underscores. |
| **mountPath**                 | Required     | Path that file to be mounted in the container                                     |
| **valueFrom**                 | Required     | The source of the configuration form.                                             |
| **values**                    | Required     | Required under file section. File key-values definition                           |
| **configForm**                | Required     | Configuration form value source definition                                        |
| **displayName**               | Optional     | The display name of the defined key in configuration form                         |
| **type**                      | Optional     | The type of value. Supported types are string, number, boolean, secret, object, and array. Default type is string when not specified. If this is under file array section. Supported types are yaml, json and toml and type is required |
| **required**                  | Optional     | Define whether the value is required or not for the key. Default to true when not specified |
| **properties**                | Optional     | Required if type is an object. Definition for defining the sub-properties of the object                      |
| **items**                     | Optional     | Required if type is an array. Definition for defining the array                  |

Refer following examples for more details.

### Define environment variables

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
                # Allowed types - number, boolean, secret
                type: string
```

!!! notes
    - Only string, boolean, number, and secret are allowed as environment variable types.

### Define File keys

```yaml
configurations:
  # +optional List of files to be injected into the component from config form
  file:
    # +required Name of the file
    - name: application.yaml
      # +required path that file to be mounted
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

!!! notes
    - It is allowed to define nested configurations under the types `object` and `array`.
    - `properties` is required if the type is `object` and define sub-object keys under it.
    - `items` is required if the type is `array` and define array properties under it.

Once user clicks the `Configure and Deploy` button, defined configurtions shown and user can inject values through the form.
