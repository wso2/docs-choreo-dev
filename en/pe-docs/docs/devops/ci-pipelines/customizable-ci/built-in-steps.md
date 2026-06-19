# Built-in steps

{{ product_name }} ships a set of pre-built CI steps you can call from a `pipeline.yaml` without writing your own container or script. Built-in steps handle the common build pipeline tasks — building the component, scanning for vulnerabilities, running unit tests, and so on — and they understand the conventions the rest of the build flow expects (where the source lives, how the image is named, how status is reported back to the Console).

Call a built-in step from inside a `containerSet` by setting `containerTemplate` to the step's identifier:

```yaml
steps:
  - name: Build Steps
    containerSet:
      containers:
        - name: Build Component
          containerTemplate: choreo/buildpack-build@v1
```

## Build steps

These steps produce the component image. Each customizable CI pipeline must include the build step that matches the component's build preset (see [Mandatory steps](author-pipeline-yaml.md#mandatory-steps)).

| Step | Build preset | What it does |
|---|---|---|
| `choreo/buildpack-build@v1` | Buildpack | Runs Cloud Native Buildpacks (`pack build`) at `$SOURCE_ROOT` and produces the component image. |
| `choreo/docker-build@v1` | BYOC (Docker) | Builds the image from the component's `Dockerfile`. |
| `choreo/ballerina-build@v1` | Ballerina Buildpack | Runs the Ballerina build and produces the component image. |
| `choreo/webapp-build@v1` | Web App | Builds the web app (React, Angular, Vue, or static files) and produces the deployable image. |
| `choreo/prism-build@v1` | Prism Mock | Builds a Prism mock service from the OpenAPI specification. |
| `choreo/test-runner-build@v1` | Test Runner | Packages a Postman test collection runner. |
| `choreo/build-preparation@v1` | WSO2 MI | Prepares the WSO2 MI build workspace (downloads dependencies, validates the project). Pair with `choreo/integration-project-build@v1`. |
| `choreo/integration-project-build@v1` | WSO2 MI | Builds the WSO2 MI integration project. |
| `choreo/mi-unit-test-preparation@v1` | WSO2 MI | Optional MI unit test preparation step. Pair with `choreo/mi-unit-test@v1`. |
| `choreo/mi-unit-test@v1` | WSO2 MI | Optional MI unit test execution step. |
| `choreo/buildpack-unit-test@v1` | Buildpack | Optional buildpack unit test step. See [Integrate Unit Tests into the CI Pipeline](../integrate-unit-tests-into-the-build-pipeline.md). |
| `choreo/ballerina-unit-test@v1` | Ballerina | Optional Ballerina unit test step. |

All build steps write the resulting image to a shared workspace location so subsequent steps (vulnerability scan, image push) can consume it directly.

## Security and scanning steps

| Step | What it does |
|---|---|
| `choreo/trivy-scan@v1` | Scans the image produced by the build step for vulnerabilities using Trivy. Honors a `.trivyignore` file at the component source root if present. |
| `choreo/dockerfile-scan@v1` | Static analysis of the component's `Dockerfile` using Checkov. Used by BYOC components. |

!!! info
    `choreo/trivy-scan@v1` runs Trivy, `choreo/dockerfile-scan@v1` runs Checkov. They are not interchangeable. Buildpack flows that do not produce a `Dockerfile` cannot use `choreo/dockerfile-scan@v1`.

## Step naming convention

Built-in step identifiers follow the format `choreo/<step-name>@v<major>`. The version suffix is required. The current versions are listed above; existing pipelines continue to work when new versions are added.

You can mix built-in steps with your own templates and container templates in the same pipeline — for example, a Snyk source scan you author before `choreo/buildpack-build@v1`, and a Slack notification after `choreo/trivy-scan@v1`.

## What's next

- [Author pipeline.yaml](author-pipeline-yaml.md) — full YAML syntax for steps, templates, and container templates.
- [Manage customizable CI pipelines](manage-customizable-ci-pipeline.md) — create, attach, and update pipelines from the {{ product_name }} Console.
