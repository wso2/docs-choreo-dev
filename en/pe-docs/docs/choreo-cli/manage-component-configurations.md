# Manage Component Configurations with the {{ product_name }} CLI

Component configurations are the deployment-time configurations that {{ product_name }} applies to a component in a specific environment. They include environment variables, file mounts, and configuration group mappings, which are the same configurations you set on the **Deploy** page of the {{ product_name }} Console.

With the {{ product_name }} CLI, you declare component configurations in a file and apply them with a single command. Because the file is declarative and the command is safe to run repeatedly, you can use it in scripts and CI/CD pipelines, and store files that contain no sensitive values in version control.

!!! warning "Keep sensitive values out of version control"
    The CLI reads the values in a component configuration file as plain text. Setting `secret: true` encrypts a value when {{ product_name }} stores it, but it doesn't encrypt the file on your machine. The CLI also doesn't expand environment variables in the file, so a value such as `${DB_PASSWORD}` is applied as that literal text.

    Don't commit a file that contains sensitive values. Instead, use one of the following approaches:

    - Store the sensitive values as sensitive keys in a [configuration group](./manage-configuration-groups.md), and map the configuration group in the file with the `configGroups` section or a `fromConfigGroup` block. The file then contains only the name of the configuration group and its keys.
    - Generate the file at run time from a secret store, such as the secrets of your CI/CD system, apply it, and then delete it.

This guide walks you through the following tasks:

- Create a component configuration file
- Preview and apply component configurations
- View component configurations
- Redeploy a component to apply configuration changes
- Remove component configurations

This guide uses the to-do app (`my-web-app` in `web-app-project`) from [Get Started with the {{ product_name }} CLI](./get-started-with-the-choreo-cli.md), and two environments: Development and Production.

!!! note
    {{ product_name }} applies component configurations to a running component only on its next deployment. After you change component configurations, redeploy the component. For details, see [Step 5: Redeploy the component to apply the changes](#step-5-redeploy-the-component-to-apply-the-changes).

!!! info "Component configurations and DevOps configurations"
    The {{ product_name }} CLI manages two separate types of configurations:

    - **Component configurations**: Environment variables, file mounts, and configuration group mappings. You manage them with `{{ cli_root_name }} set component-config` and `{{ cli_root_name }} unset component-config`, as described in this guide.
    - **DevOps configurations**: Config maps and secrets. You manage them with `{{ cli_root_name }} create config`, `{{ cli_root_name }} list configs`, `{{ cli_root_name }} describe config`, and `{{ cli_root_name }} delete config`.

## Prerequisites

- Install the {{ product_name }} CLI and log in to {{ product_name }}. For instructions, see [Get Started with the {{ product_name }} CLI](./get-started-with-the-choreo-cli.md).
- Create a component and deploy it to the environment that you want to configure.

## Step 1: Create a component configuration file

A component configuration file declares the configurations to apply to one component in one environment. The CLI accepts YAML and JSON files.

1. To generate an annotated template file to start from, run the following command:

    ``` sh
    {{ cli_root_name }} set component-config --sample > component-config.yaml
    ```

2. Edit `component-config.yaml` to declare your configurations. The following example adds an environment variable, a file mount, and a configuration group mapping:

    ``` yaml
    schemaVersion: 1
    component: my-web-app
    project: web-app-project
    environment: Development

    configs:
      env:
        - name: LOG_LEVEL
          value: debug
          secret: false

      files:
        - mountPath: /etc/certs/ca.pem
          content: |
            -----BEGIN CERTIFICATE-----
            ...
            -----END CERTIFICATE-----
          secret: false

      configGroups:
        - name: app-shared-config
    ```

The component configuration file supports the following fields:

| Field | Required | Description |
| ----- | -------- | ----------- |
| `schemaVersion` | Yes | The version of the file format. It must be `1`. |
| `component` | Yes | The component to configure. You can use the `--component` flag instead. |
| `project` | Yes | The project of the component. You can use the `--project` flag instead. |
| `environment` | Yes | The environment to configure. You can use the `--env` flag instead. |
| `deploymentTrack` | No | The deployment track to configure. If you omit it, the CLI uses the default deployment track of the component. |
| `configs` | Yes | The configurations to apply. It must contain at least one of the `env`, `files`, or `configGroups` sections. |
| `configs.env` | No | Environment variables. Each entry requires a `name` and a `value`. Set `secret` to `true` to store the value encrypted. |
| `configs.files` | No | File mounts. Each entry requires a `mountPath`, which is the absolute path of the file in the container, including the file name, and the file `content`. Set `secret` to `true` to store the content encrypted. |
| `configs.configGroups` | No | Configuration group mappings. For details, see [Map configuration groups](#map-configuration-groups). |

Each mounted name, which is an environment variable name or a file mount path, must be unique across the file.

### Map configuration groups

A configuration group mapping mounts the keys of a [configuration group](./manage-configuration-groups.md) into the component. Depending on the fields you specify, the CLI mounts the keys as follows:

| Fields | How the keys mount |
| ------ | ------------------ |
| `name` | Every key in the configuration group mounts as an environment variable with the same name as the key. |
| `name` and `keys` | Only the listed keys mount, as environment variables. For each key, specify the `key` and the `envVarName` to mount it as. |
| `name` and `mountPath` | Every key in the configuration group mounts as a file inside the `mountPath` directory. The `mountPath` must be an absolute path. |
| `name`, `mountPath`, and `keys` | Only the listed keys mount, as files inside the `mountPath` directory. For each key, specify the `key` and the `fileName` to mount it as. |

The following example shows each type of mapping:

``` yaml
configGroups:
  - name: app-shared-config

  - name: db-credentials
    keys:
      - key: DB_PASSWORD
        envVarName: APP_DB_PASSWORD

  - name: app-settings
    mountPath: /etc/app

  - name: tls-config
    mountPath: /etc/tls
    keys:
      - key: TLS_CERT
        fileName: cert.pem
```

### Step 1.1: Configure a Ballerina component

Ballerina components use the `configs.configurables` section instead of the `env`, `files`, and `configGroups` sections, because their configurations come from a schema that the Ballerina compiler generates. You can't combine the `configurables` section with the other sections in the same file.

1. To generate a file that contains the configurable keys of your Ballerina component, run the following command with the `--component` and `--env` flags:

    ``` sh
    {{ cli_root_name }} set component-config --sample --component="my-bal-service" --env="Development" > bal-config.yaml
    ```

    The CLI reads the keys from the live schema of the component, so the component must have at least one build.

2. Edit `bal-config.yaml` to set a value for each configurable. To use a value from a configuration group instead of a literal value, specify `fromConfigGroup`:

    ``` yaml
    configs:
      configurables:
        - key: dbPassword
          fromConfigGroup:
            name: db-credentials
            key: DB_PASSWORD
        - key: logLevel
          value: debug
          secret: false
    ```

Each configurable requires a `key` that matches a key in the schema of the component, and either a `value` or a `fromConfigGroup` block.

## Step 2: Preview the changes

To validate the file and view the planned changes without applying them, run the following command with the `--dry-run` flag:

``` sh
{{ cli_root_name }} set component-config -f component-config.yaml --dry-run
```

## Step 3: Apply the component configurations

To apply the component configurations, run the following command:

``` sh
{{ cli_root_name }} set component-config -f component-config.yaml
```

The `set` command creates and updates configurations as follows:

- If an entry in the file doesn't exist on the component, the CLI creates it.
- If an entry with the same mounted name already exists, the CLI updates it.
- If an existing entry isn't in the file, the CLI leaves it unchanged.

Because the CLI doesn't delete entries that you leave out, you can safely run the same file more than once.

### Step 3.1: Reuse the file for another environment

The `--project`, `--component`, `--env`, and `--deployment-track` flags override the corresponding fields in the file, so you can reuse one file across environments and components. To apply the same configurations to the Production environment, run the following command:

``` sh
{{ cli_root_name }} set component-config -f component-config.yaml --env="Production"
```

To apply the same file to many components, run the command in a loop. For example, the following Bash script uses `jq` to apply `shared-config.yaml` to every component in a project:

``` sh
set -euo pipefail

components=$({{ cli_root_name }} list components --project="web-app-project" -o json | jq -r '.[].component')

if [ -z "$components" ]; then
  echo "No components found in the project." >&2
  exit 1
fi

for component in $components; do
  {{ cli_root_name }} set component-config -f shared-config.yaml --project="web-app-project" --component="$component" --env="Development"
done
```

The script stops with a non-zero exit code if the `list` command or `jq` fails, if the project has no components, or if the `set` command fails for any component.

## Step 4: View the component configurations

To view the configurations of a component in an environment, run the following command:

``` sh
{{ cli_root_name }} describe component-config --project="web-app-project" --component="my-web-app" --env="Development"
```

The output shows both the component configurations and the DevOps configurations of the component. For a Ballerina component, the output shows its configurables, including fields nested inside a record.

The output lists file-type DevOps configurations by name only. To view the content of a DevOps configuration, run the `describe config` command with the `--name` flag. The CLI redacts secret values in the output.

``` sh
{{ cli_root_name }} describe config --project="web-app-project" --component="my-web-app" --env="Development" --name=<config-name>
```

## Step 5: Redeploy the component to apply the changes

To apply the changed configurations to the running component, run the following command:

``` sh
{{ cli_root_name }} redeploy component "my-web-app" --project="web-app-project" --env="Development"
```

The `redeploy` command redeploys the release that is currently deployed in the environment. Because it uses the same build, only the configurations change. If the redeployment fails, the command exits with a non-zero exit code, so you can use it in scripts.

To deploy a newer build instead, use the `{{ cli_root_name }} create deployment` command. For details, see [Get Started with the {{ product_name }} CLI](./get-started-with-the-choreo-cli.md#step-7-deploy-to-the-development-environment).

### Step 5.1: Verify the deployment

To verify the status of the deployment, run the following command:

``` sh
{{ cli_root_name }} describe deployment --project="web-app-project" --component="my-web-app" --env="Development" -o json
```

The JSON output includes the following fields, which you can use to confirm the status of the deployment and the build and commit that are running:

| Field | Description |
| ----- | ----------- |
| `deploymentStatus` | The status of the deployment, for example, `Active`. |
| `lastDeployed` | How long ago the component was deployed, for example, `3 minutes ago`. |
| `buildId` | The ID of the deployed build. |
| `buildRunId` | The ID of the build run. |
| `commit` | The deployed commit, with its `sha`, `message`, `createdBy`, and `committed` details. |

!!! note
    The `redeploy` command doesn't wait for the deployment to complete. To wait in a script, run the `describe deployment` command repeatedly until the `deploymentStatus` changes.

## Step 6: Remove component configurations

To remove component configurations, run the `unset` command with a file that lists the entries to remove:

``` sh
{{ cli_root_name }} unset component-config -f component-config.yaml
```

The `unset` command uses the same file format as the `set` command, so you can reuse the file that you used to set the configurations. The CLI removes entries as follows:

- The CLI matches entries by mounted name, which is the environment variable name, the `envVarName` of a configuration group key, or the file mount path. It ignores the values and contents in the file.
- If a `configGroups` entry has no `keys` list, the CLI removes every mapping of that configuration group.
- If an entry in the file isn't set on the component, the CLI shows a warning instead of an error, so you can safely run the same file more than once.

To preview the removals without applying them, add the `--dry-run` flag. To apply the removals to the running component, redeploy the component as described in [Step 5](#step-5-redeploy-the-component-to-apply-the-changes).

## View all component configuration commands

To view the available flags and examples for a component configuration command, run the command with the `--help` flag. For example:

``` sh
{{ cli_root_name }} set component-config --help
```
