# Manage Configuration Groups with the {{ product_name }} CLI

A configuration group in {{ product_name }} is a reusable, organization-scoped set of key-value configurations that you can link to multiple components. Instead of repeating the same configurations in each component, you define them once in a configuration group.

Each key in a configuration group holds a separate value per environment. For example, a `DATABASE_URL` key can point to your development database in the Development environment and your production database in the Production environment, while every component that consumes the group refers to the same key.

This guide walks you through the following tasks:

- Create a configuration group
- List configuration groups
- View configuration group details
- Link a configuration group to a component
- Find where configuration groups are used
- Update a configuration group
- Delete a configuration group

This guide uses a configuration group named `app-shared-config` for the to-do app (`my-web-app` in `web-app-project`) from [Get Started with the {{ product_name }} CLI](./get-started-with-the-choreo-cli.md), and two environments: Development and Production.

!!! note
    - Configuration groups hold configurations that are shared across components. To manage the environment variables and file mounts of a single component, see [Manage Component Configurations with the {{ product_name }} CLI](./manage-component-configurations.md).
    - To manage configuration groups from the {{ product_name }} Console, see [Manage Configuration Groups](../devops-and-ci-cd/manage-configuration-groups.md).

## Prerequisites

- Install the {{ product_name }} CLI and log in to {{ product_name }}. For instructions, see [Get Started with the {{ product_name }} CLI](./get-started-with-the-choreo-cli.md).

All configuration group commands act on your current organization. To target a different organization, add the `--org` flag to the command.

## Step 1: Create a configuration group

You can define a configuration group in a file or pass the definition inline. The `--file` and `--inline` flags are mutually exclusive, so use only one of them.

### Step 1.1: Create a configuration group from a file

1. Create a file named `config-group.yaml` with the following content. The CLI accepts `.yaml`, `.yml`, and `.json` files.

    ``` yaml
    name: app-shared-config
    description: "Shared configurations for the to-do application"
    configurations:
      - key: DATABASE_URL
        isSensitive: false
        isFile: false
        values:
          Development: "postgres://dev-host/db"
          Production: "postgres://prod-host/db"
      - key: API_KEY
        isSensitive: true
        isFile: false
        values:
          Development: "dev-key"
          Production: "prod-key"
    ```

2. To create the configuration group, run the following command. You can also use the `-f` shorthand for the `--file` flag.

    ``` sh
    {{ cli_root_name }} create config-group --file=config-group.yaml
    ```

    The CLI validates the definition before it sends the request, so it reports errors such as an unknown environment name or a missing file immediately. On success, the CLI prints the created configuration group and its keys.

The configuration group definition supports the following fields:

| Field | Required | Description |
| ----- | -------- | ----------- |
| `name` | Yes | The name of the configuration group. It must be unique within the organization. |
| `description` | No | A short description of what the configuration group contains. |
| `configurations` | Yes | The list of configuration keys. It must contain at least one key. |
| `key` | Yes | The name of the configuration key. Each key name must appear only once. |
| `isSensitive` | Yes | Set to `true` to store the value as a secret. Sensitive values are masked in command output. |
| `isFile` | Yes | Set to `true` if the value is a file. For details, see [Step 1.3: Add file configurations](#step-13-add-file-configurations). |
| `values` | Yes | A map of environment names to values. Environment names must match the environments in your organization. |

!!! note
    You can omit an environment from the `values` map. The CLI still creates the key for that environment with an empty value, which you can set later. To see the environments of a project, run `{{ cli_root_name }} list environments --project=<project-name>`.

### Step 1.2: Create a configuration group from an inline definition

To create a configuration group without a file, pass the definition as a JSON or YAML string to the `--inline` flag or its `-i` shorthand. This is useful in scripts and CI/CD pipelines.

``` sh
{{ cli_root_name }} create config-group --inline='{"name":"app-shared-config","configurations":[{"key":"DATABASE_URL","isSensitive":false,"isFile":false,"values":{"Development":"postgres://dev-host/db"}}]}'
```

### Step 1.3: Add file configurations

To store the contents of a file, such as a certificate or a keystore, set `isFile` to `true` and set the value for each environment to the path of a local file:

``` yaml
name: app-shared-config
description: "Shared configurations for the to-do application"
configurations:
  - key: TLS_CERT
    isSensitive: true
    isFile: true
    values:
      Development: "./certs/dev-cert.pem"
      Production: "./certs/prod-cert.pem"
```

The CLI reads each file and encodes its content before it sends the request. Relative paths resolve against the directory where you run the command.

!!! note
    A file configuration can be up to approximately 19 KB, because its encoded content must stay within the 25 KB limit for a single configuration value.

## Step 2: List configuration groups

To list the configuration groups in your organization, run the following command:

``` sh
{{ cli_root_name }} list config-groups
```

To get the output in JSON format, add the `--output` flag or its `-o` shorthand. For details, see [Configure command output](./get-started-with-the-choreo-cli.md#configure-command-output).

``` sh
{{ cli_root_name }} list config-groups -o json > config-groups.json
```

## Step 3: View configuration group details

To view the keys in a configuration group along with their per-environment values, run the following command:

``` sh
{{ cli_root_name }} describe config-group "app-shared-config"
```

You can also pass the name with the `--name` flag, for example, `--name="app-shared-config"`.

The command prints the configuration group details followed by a table of keys, with one column per environment. Sensitive values appear as `*****` and file values appear as `<file>`, so you can safely share the output. If an environment has no value set for a key, the table shows `-`. The command also prints a hint with the command to find the components that consume the configuration group.

### Step 3.1: View a configuration value

To view the value of a single key in a specific environment, run the following command with the `--key` and `--env` flags:

``` sh
{{ cli_root_name }} describe config-group "app-shared-config" --key=DATABASE_URL --env=Development
```

If the key is a file configuration, the CLI decodes the content before it prints it. To write the value to a file instead of the terminal, add the `--out` flag:

``` sh
{{ cli_root_name }} describe config-group "app-shared-config" --key=TLS_CERT --env=Production --out=./tls-cert.pem
```

## Step 4: Link a configuration group to a component

To use a configuration group in a component, link it to the component in each environment where the component needs it. You link a configuration group by adding it to the `configGroups` section of a component configuration file.

1. Create a file named `link-config-group.yaml` with the following content:

    ``` yaml
    schemaVersion: 1
    component: my-web-app
    project: web-app-project
    environment: Development

    configs:
      configGroups:
        - name: app-shared-config
    ```

    With only the `name` field, every key in the configuration group mounts as an environment variable with the same name as the key. To mount selected keys, rename them, or mount them as files, see [Map configuration groups](./manage-component-configurations.md#map-configuration-groups).

2. To link the configuration group, run the following command:

    ``` sh
    {{ cli_root_name }} set component-config -f link-config-group.yaml
    ```

3. To apply the link to the running component, redeploy the component:

    ``` sh
    {{ cli_root_name }} redeploy component "my-web-app" --project="web-app-project" --env="Development"
    ```

To link the configuration group in the Production environment, run the `set component-config` command again with the `--env="Production"` flag, and then redeploy the component in the Production environment. For details on component configuration files, see [Manage Component Configurations with the {{ product_name }} CLI](./manage-component-configurations.md).

## Step 5: Find where configuration groups are used

A configuration group can be linked to multiple components, and a component can consume multiple configuration groups. You can look up this relationship in both directions:

- **From a component:** Find the configuration groups that a component consumes.
- **From a configuration group:** Find the components that consume a configuration group.

### Step 5.1: Find the configuration groups a component consumes

To list the configuration groups that a component consumes, run the following command with the `--project` and `--component` flags:

``` sh
{{ cli_root_name }} list config-groups --project="web-app-project" --component="my-web-app"
```

The output shows each configuration group with the environments in which the component references it. To view a specific deployment track of the component, add the `--deployment-track` flag. If you omit it, the CLI uses the latest deployment track.

### Step 5.2: Find the components that consume a configuration group

To list the components that consume a configuration group, run the following command with the `--config-group` flag:

``` sh
{{ cli_root_name }} list components --config-group="app-shared-config"
```

The `--config-group` flag accepts the name, ID, or UUID of the configuration group. The lookup covers your entire organization in a single call, so you don't need to specify a project. The output shows each component that consumes the configuration group, the project it belongs to, and the environments and deployment tracks in which it uses the configuration group.

In JSON output, each entry has the `component`, `project`, `environments`, and `deploymentTracks` fields:

``` sh
{{ cli_root_name }} list components --config-group="app-shared-config" -o json
```

!!! tip
    Run this command before you update or delete a configuration group to see which components the change affects.

## Step 6: Update a configuration group

!!! warning
    The `configurations` list you provide replaces the existing list, so the CLI deletes every key that you leave out. Always start from the current definition of the configuration group when you update it.

To update a configuration group, follow the steps below:

1. To get the current definition of the configuration group, run the following command:

    ``` sh
    {{ cli_root_name }} describe config-group "app-shared-config" -o json > config-group.json
    ```

2. Edit the definition in `config-group.json` as required. The `name` field identifies the configuration group to update, so you can't change it. You can update the display name, the description, and the configuration keys.

3. To update the configuration group, run the following command. The `update` command accepts the same `--file` and `--inline` flags as the `create` command.

    ``` sh
    {{ cli_root_name }} update config-group --file=config-group.json
    ```

4. To apply the updated values, redeploy each component that consumes the configuration group. To find these components, see [Step 5.2: Find the components that consume a configuration group](#step-52-find-the-components-that-consume-a-configuration-group).

    ``` sh
    {{ cli_root_name }} redeploy component "my-web-app" --project="web-app-project" --env="Development"
    ```

The `configurations` list you provide is the complete set of keys that the configuration group must have, not a set of changes to merge. The CLI applies it as follows:

- If a key already exists, the CLI updates it with the values you provide.
- If a key is new, the CLI adds it.
- If an existing key isn't in the list, the CLI deletes it.

A key name identifies the key, so you can't rename a key. To replace a key with a key of a different name, add the new key and remove the old key from the list. The CLI deletes the old key and its values, and adds the new key.

Values within a key apply per environment. The CLI inserts or updates each value you provide, and deletes each value you omit.

The CLI stores the updated values immediately, but each component that consumes the configuration group uses the new values only after its next deployment.

### Step 6.1: Keep a sensitive value unchanged

To keep an existing sensitive value unchanged, list the key and leave its value blank:

``` yaml
name: app-shared-config
displayName: App Shared Config
description: "Updated description"
configurations:
  - key: DATABASE_URL
    isSensitive: false
    isFile: false
    values:
      Development: "postgres://dev-host/db"
      Production: "postgres://prod-host/db"
  - key: API_KEY
    isSensitive: true
    isFile: false
    values:
      Development: ""
      Production: ""
```

## Step 7: Delete a configuration group

You can't delete a configuration group that a component consumes, because doing so would break the deployments of that component. Before you delete a configuration group, unlink it from every component that consumes it.

1. To find the components that consume the configuration group, see [Step 5.2: Find the components that consume a configuration group](#step-52-find-the-components-that-consume-a-configuration-group).

2. To unlink the configuration group from each component, run the `unset component-config` command with the file that you used to link it. The file specifies a single environment, so run the command once for each environment in which the component uses the configuration group, and use the `--env` flag to select the environment:

    ``` sh
    {{ cli_root_name }} unset component-config -f link-config-group.yaml --env="Development"
    {{ cli_root_name }} unset component-config -f link-config-group.yaml --env="Production"
    ```

    For details, see [Remove component configurations](./manage-component-configurations.md#step-6-remove-component-configurations).

3. To delete the configuration group, run the following command:

    ``` sh
    {{ cli_root_name }} delete config-group "app-shared-config"
    ```

Before it deletes the configuration group, the CLI checks whether any component consumes it. If one or more components consume it, the CLI lists those components and stops without deleting the configuration group. You can't bypass this check.

## View all configuration group commands

To view the available flags and examples for a configuration group command, run the command with the `--help` flag. For example:

``` sh
{{ cli_root_name }} create config-group --help
```
