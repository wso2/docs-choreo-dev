# What is Customizable CI

{{ product_name }} runs every component build through a built-in CI pipeline that prepares the source, builds the image, scans it for vulnerabilities, and pushes it to your registry. For most components, this default pipeline is enough.

**Customizable CI** lets you replace that default pipeline with one you author yourself in YAML. You decide which steps run, in what order, and what tools they use — for example, running a Snyk source scan and a SonarCloud quality gate before the build, running unit tests or other custom containers alongside the build step, scanning the built image with a third-party scanner like Snyk Container or Aqua in addition to (or instead of) the default Trivy scan, and posting a Slack notification on completion.

!!! info
    Customizable CI is available only on private data plane organizations and must be enabled by WSO2 on request.

## When to use Customizable CI

Use customizable CI when you need to:

- **Run pre-build checks on the source** — vulnerability scans, license checks, code quality gates, secret detection, and similar.
- **Run unit tests, linters, or other custom containers alongside the build step**.
- **Scan the built image** with a third-party vulnerability scanner (Snyk Container, Aqua, Anchore, or any other OCI-compliant scanner) in addition to or instead of the default Trivy scan.
- **Run post-build actions** like external notifications, approvals, or artifact publishing from inside the pipeline.
- **Standardize a CI workflow** across components in a way the default pipeline doesn't allow.

!!! note
    Every customizable CI pipeline must still include the built-in build step for its Build Preset (for example, `choreo/buildpack-build@v1` for Buildpack components, `choreo/docker-build@v1` for BYOC). You can add your own steps around and alongside it, but the built-in build step itself can't be replaced today. See [Mandatory steps](author-pipeline-yaml.md#mandatory-steps) for the full list.

If you only need to build and deploy a component with reasonable defaults, the [default CI pipeline](../configure-ci-pipeline.md) is simpler and requires no YAML.

## How it works

A customizable CI pipeline is a YAML document (`pipeline.yaml`) that defines the steps for a build. {{ product_name }} stores the pipeline at the **organization** level. Authoring and maintaining the pipeline YAML is a platform-engineer task. Once the pipeline exists, either a platform engineer or a developer can attach it to a project (which makes it available to components in that project) and then select it on the specific deployment track of a component that should use it, define the variables and secrets it expects at any scope, and review build validation errors when something fails.

When a build triggers, {{ product_name }} converts the YAML into an Argo Workflow and runs it in the data plane build cluster.

Inside each step you can:

- Call a built-in step from {{ product_name }} (for example, `choreo/buildpack-build@v1`, `choreo/trivy-scan@v1`).
- Run any container image you choose with an inline script.
- Reference variables and secrets you've defined at the organization, project, or deployment-track scope.
- Read system variables that {{ product_name }} injects (the component source path, the branch being built, the build run ID, and so on).

The same `choreo/*` built-in steps the default pipeline uses are also available to you, so you can keep the parts you want and replace only the parts you want to change.

## Quick start

The smallest pipeline you can save and run for a Buildpack component is six lines — it just calls the built-in build step. Paste this into the YAML editor when you create a pipeline with **Build Preset: Buildpack** to confirm the feature works end to end:

```yaml
steps:
  - name: Build
    containerSet:
      containers:
        - name: Build Component
          containerTemplate: choreo/buildpack-build@v1
```

The next pipeline adds a "Hello World" step that runs an arbitrary container with your own script before the build — the part you can replace with anything you want (a Snyk scan, a custom linter, a notification, etc.). The build step that follows is the mandatory one every Buildpack pipeline needs.

```yaml
steps:
  - name: Hello
    image: alpine:latest
    inlineScript: |
      #!/bin/sh
      echo "Hello, World! Building $CI_BRANCH (build $CI_PIPELINE_ID)"
  - name: Build
    containerSet:
      containers:
        - name: Build Component
          containerTemplate: choreo/buildpack-build@v1
```

A pipeline with a vulnerability scan after the build:

```yaml
steps:
  - name: Build Steps
    containerSet:
      containers:
        - name: Build Component
          containerTemplate: choreo/buildpack-build@v1
        - name: Vulnerability Scan
          containerTemplate: choreo/trivy-scan@v1
```

From here you can add your own steps — for example, a Snyk source scan before the build, or a Slack notification after the scan. See [Author pipeline.yaml](author-pipeline-yaml.md) for the full syntax and a complete worked example, or [Built-in steps](built-in-steps.md) for the equivalent identifiers for other Build Presets (BYOC, Web App, Ballerina, MI, Prism Mock, Test Runner).

## What's next

- [Author pipeline.yaml](author-pipeline-yaml.md) — pipeline YAML syntax, steps, templates, variables, secrets, and system variables.
- [Built-in steps](built-in-steps.md) — the `choreo/*` steps you can call from your pipeline.
- [Manage customizable CI pipelines](manage-customizable-ci-pipeline.md) — create, attach, and update pipelines from the {{ product_name }} Console.
