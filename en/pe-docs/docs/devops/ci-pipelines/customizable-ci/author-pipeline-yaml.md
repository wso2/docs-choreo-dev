# Author pipeline.yaml

A customizable CI pipeline is defined by a single `pipeline.yaml` document. This page covers the top-level structure, the step types you can use, the templates that back them, and the variables, secrets, and system variables available to your scripts.

## Top-level structure

A pipeline has three top-level sections, all optional except `steps`:

```yaml
steps:
  # Ordered list of steps that make up the build.

templates:
  # Reusable scripts referenced by step.template.

containerTemplates:
  # Reusable container definitions referenced by containerSet entries.
```

`steps` controls execution order. `templates` and `containerTemplates` are libraries of reusable definitions you reference by name.

## Steps

Each step has a `name` and one of three execution forms.

!!! note
    The examples in this section focus on the syntax being illustrated. A pipeline you save must also include the [mandatory build step](#mandatory-steps) for the Build Preset it will be attached to. See [A complete example](#a-complete-example) at the bottom of this page for a full working pipeline.

### Template step

Runs a single template defined under `templates`. Use this when a step does one self-contained task.

{% raw %}
```yaml
steps:
  - name: Slack Notification
    template: slack-notification

templates:
  - name: slack-notification
    image: curlimages/curl:latest
    inlineScript: |
      #!/bin/sh
      curl -X POST -H 'Content-type: application/json' \
        --data '{"text":"Build succeeded"}' "$SLACK_WEBHOOK_URL"
    env:
      - name: SLACK_WEBHOOK_URL
        value: '{{ORG.SECRETS.SLACK_WEBHOOK_URL}}'
```
{% endraw %}

### Container-set step

Runs several containers in a single pod, sharing the same workspace. Use this when multiple steps need to share files (for example, your tests run against the same source the build will use).

```yaml
steps:
  - name: Build Steps
    containerSet:
      containers:
        - name: Test
          containerTemplate: go-test
        - name: Build Component
          containerTemplate: choreo/buildpack-build@v1
        - name: Vulnerability Scan
          containerTemplate: choreo/trivy-scan@v1

containerTemplates:
  - name: go-test
    image: golang:latest
    inlineScript: |
      #!/bin/sh
      cd "$SOURCE_ROOT"
      go test ./...
```

Containers in a container set share the same workspace under `$WORKSPACE`. When you reference a built-in `choreo/*@v1` step from inside a container set, {{ product_name }} wires the order required between built-in steps automatically.

### Inline step

Runs an inline script directly without referencing a named template. Useful for short one-off steps.

{% raw %}
```yaml
steps:
  - name: Hello
    image: alpine:latest
    inlineScript: |
      #!/bin/sh
      echo "Hello from {{ORG.VARIABLES.GREETING_NAME}}"
```
{% endraw %}

## Templates

A `template` is a reusable step definition. Templates run as their own pod when referenced from a step.

```yaml
templates:
  - name: <template-name>
    image: <container image>
    inlineScript: |
      #!/bin/bash
      ...
    env:
      - name: VAR_NAME
        value: 'literal-or-variable-reference'
```

`image` is optional — if omitted, {{ product_name }} uses a default base image. `inlineScript` is a bash or sh script that runs inside the container. `env` is a list of environment variables; see [Variables and secrets](#variables-and-secrets) for the reference syntax.

## Container templates

A `containerTemplate` is the same shape as a `template`, but it is referenced from inside a `containerSet` rather than as a standalone step.

```yaml
containerTemplates:
  - name: <container-template-name>
    image: <container image>
    inlineScript: |
      #!/bin/sh
      ...
    env:
      - name: VAR_NAME
        value: 'literal-or-variable-reference'
```

Inside a `containerSet`, the `containerTemplate` field on each container entry references either one of these user-defined container templates by name, or one of {{ product_name }}'s [built-in steps](built-in-steps.md) by its `choreo/<name>@v1` identifier — for example, `containerTemplate: choreo/buildpack-build@v1`.

## Variables and secrets

Inside `env.value`, reference variables and secrets using the scoped syntax `{% raw %}{{SCOPE.TYPE.NAME}}{% endraw %}`, where:

| Field | Allowed values |
|---|---|
| `SCOPE` | `ORG` (organization), `PROJECT` (project), or `DT` (deployment track) |
| `TYPE` | `VARIABLES` or `SECRETS` |
| `NAME` | The variable or secret name you defined in the Console |

For example:

{% raw %}
```yaml
env:
  - name: SNYK_TOKEN
    value: '{{ORG.SECRETS.SNYK_TOKEN}}'
  - name: GO_VERSION
    value: '{{DT.VARIABLES.GO_VERSION}}'
```
{% endraw %}

### Scope precedence

An explicit reference such as `{% raw %}{{ORG.SECRETS.MY_TOKEN}}{% endraw %}` or `{% raw %}{{DT.VARIABLES.MY_VAR}}{% endraw %}` resolves at exactly the named scope — no walk.

The unqualified form `{% raw %}{{VARIABLES.X}}{% endraw %}` and `{% raw %}{{SECRETS.X}}{% endraw %}` is still accepted for backward compatibility and resolves by walking **DT → Project → Org** and using the first match. Prefer the explicit `{% raw %}{{SCOPE.TYPE.NAME}}{% endraw %}` form in new pipelines.

You add the actual variable and secret values from the {{ product_name }} Console — see [Manage customizable CI pipelines](manage-customizable-ci-pipeline.md#add-variables-and-secrets).

## System variables

{{ product_name }} injects the following environment variables into every step. They are always available and do not need to be declared under `env`.

| Variable | Description |
|---|---|
| `$WORKSPACE` | The shared workspace mount (`/mnt/vol`). Reset for each build. |
| `$REPOSITORY_DIR` | The clone root of the component's source repository. |
| `$SOURCE_ROOT` | The component's source directory. Equals `$REPOSITORY_DIR` for a root-level component, or `$REPOSITORY_DIR/<subpath>` in a monorepo. Use `cd "$SOURCE_ROOT"` to operate on the component without per-component conditionals. |
| `$COMPONENT_SUBPATH` | The component subpath relative to the clone root. Empty when the component sits at the repo root. |
| `$IMAGE_NAME` | The container image name {{ product_name }}'s built-in build steps produce (`choreo/app-image:latest`). |
| `$CI_BRANCH` | The branch being built. |
| `$CI_PIPELINE_ID` | The build run ID. |

## Mandatory steps

Every customizable CI pipeline must include the build step that produces the component image, otherwise {{ product_name }} rejects the YAML when you save it. The required step depends on the component's build preset:

| Build preset | Required step |
|---|---|
| Buildpack | `choreo/buildpack-build@v1` |
| BYOC (Docker) | `choreo/docker-build@v1` |
| Ballerina Buildpack | `choreo/ballerina-build@v1` |
| Web App | `choreo/webapp-build@v1` |
| WSO2 MI | `choreo/build-preparation@v1`, `choreo/integration-project-build@v1` |
| Prism Mock | `choreo/prism-build@v1` |
| Test Runner | `choreo/test-runner-build@v1` |

If a pipeline.yaml is missing the required step for the build preset it is attached to, the {{ product_name }} Console surfaces the missing step in the YAML editor and the build is not allowed to start. See [Built-in steps](built-in-steps.md) for the inputs and outputs each step expects.

## A complete example

The pipeline below is a working buildpack pipeline that runs unit tests, builds the component image, scans the image, and posts a Slack notification on completion. It uses every concept on this page — system variables, scoped variables and secrets, a user-defined container template, built-in steps inside a container set, and a stand-alone template step.

{% raw %}
```yaml
steps:
  - name: Build Steps
    containerSet:
      containers:
        - name: Test
          containerTemplate: go-test
        - name: Build Component
          containerTemplate: choreo/buildpack-build@v1
        - name: Vulnerability Scan
          containerTemplate: choreo/trivy-scan@v1

  - name: Notify
    template: slack-notification

containerTemplates:
  - name: go-test
    image: golang:latest
    inlineScript: |
      #!/bin/sh
      set -e
      echo "Testing $CI_PIPELINE_ID on branch $CI_BRANCH"
      cd "$SOURCE_ROOT"
      go test ./...
    env:
      - name: GO_VERSION
        value: '{{DT.VARIABLES.GO_VERSION}}'

templates:
  - name: slack-notification
    image: curlimages/curl:latest
    inlineScript: |
      #!/bin/sh
      MESSAGE=":white_check_mark: Build $CI_PIPELINE_ID for $CI_BRANCH succeeded"
      curl -X POST -H 'Content-type: application/json' \
        --data "$(printf '{"text":"%s"}' "$MESSAGE")" \
        "$SLACK_WEBHOOK_URL"
    env:
      - name: SLACK_WEBHOOK_URL
        value: '{{ORG.SECRETS.SLACK_WEBHOOK_URL}}'
```
{% endraw %}

Before this pipeline can build:

- Define `SLACK_WEBHOOK_URL` as an organization-level **Secret** on the pipeline.
- Define `GO_VERSION` (for example, `1.22.0`) as a **Variable** at the deployment-track scope for each component that uses this pipeline.

See [Manage customizable CI pipelines](manage-customizable-ci-pipeline.md#add-variables-and-secrets) for how to add these values from the Console.
