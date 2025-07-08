When configuring a pipeline, you can use built-in pipeline templates provided by Choreo. These templates include pre-defined functionality that can simplify and accelerate pipeline creation.

[Choreo’s pipeline specification](./specification.md) supports referencing these templates using the following syntax:

```
steps:
  - name: Build
    template: choreo/<template name>
```

## Available Built-in Templates

!!! info "Note"
    The list of built-in templates is continuously growing.

??? "choreo/buildpack-build@v1"
    Builds applications using supported buildpacks within Choreo. This is used by Choreo’s default build pipeline.

??? "choreo/dockerfile-scan@v1"
    Scans Dockerfiles for security vulnerabilities and best practices.

??? "choreo/docker-build@v1"
    Builds Docker images using a provided Dockerfile.

??? "choreo/trivy-scan@v1"
    Performs vulnerability scanning on container images using Trivy.

??? "choreo/webapp-build@v1"
    Builds web applications using supported frameworks.

??? "choreo/ballerina-build@v1"
    Builds Ballerina projects.

??? "choreo/mi-trivy-scan@v1"
    Performs Trivy-based vulnerability scanning for Micro Integrator images.

??? "choreo/integration-project-build@v1"
    Builds integration projects.

??? "choreo/test-runner-build@v1"
    Runs tests using the built-in test runner.

??? "choreo/prism-build@v1"
    Builds Prism projects.
