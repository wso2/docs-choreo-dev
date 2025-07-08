# Choreo Pipelines Specification

Choreo provides a yaml based syntax to define automation pipelines. This documentation provides a guide on the syntax with examples.

## Concepts

Following are the high level concepts in the Choreo automation pipeline syntax:

- Steps
- Templates
- Container Templates
- Volume Claim Templates
- Arguments

### Steps

Steps are the individual tasks that make up the pipeline. Each step is defined by a template which is a reusable unit of work. Steps are executed in the order they are defined in the pipeline.yaml file.

{% raw %}
```yaml
steps:
  - name: Build
    template: choreo/buildpack-build@v1
  - name: Static Code Analysis
    template: sonar-qube
```
{% endraw %}

### Templates

Templates are the reusable units of work that make up the pipeline. Templates are executed in the order they are defined in the pipeline.yaml file. There are two types of templates:

- Choreo Templates
- Argo Templates - Currently we only support sequential [script based templates](https://argo-workflows.readthedocs.io/en/latest/fields/#scripttemplate) and [container set templates](https://argo-workflows.readthedocs.io/en/latest/fields/#containersettemplate).

**Important Note: Argo Template Support**

- Currently, only script templates and container set templates are supported in the Choreo pipeline syntax.
- Each template supports a single level of parallelism.
- DAG templates, HTTP templates, and other advanced Argo template types are not supported at this time.
- Conditional execution within templates is also not yet supported.

{% raw %}
```yaml
templates:
  - name: sonar-qube
    inlineScript: |
      #!/bin/bash
      echo "Starting SonarQube scan..."
```
{% endraw %}

### Container Templates

Container templates are used to define the container that will be used to execute the step.

{% raw %}
```yaml
name: go-test
inlineScript: |
    #!/bin/bash
    echo "Starting Go test..."
    go test -v ./...
    if [ $? -eq 0 ]; then
    echo "Go test completed successfully."
    exit 0
    fi
image: "choreocontrolplane.azurecr.io/choreoipaas/choreo-go-test:v1"
```
{% endraw %}

### Volume Claim Templates

Volume claim templates are used to define the volume that will be used to execute the step.

{% raw %}
```yaml
name: workflows-volume
storageClassName: "choreo-workflows-storage"
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: "4Gi"
```
{% endraw %}

### Volumes

Volumes are used to store the data that is required by the templates.

{% raw %}
```yaml
volumes:
  - name: workflows-volume
    emptyDir: {}
```
{% endraw %}

### Arguments

Arguments are used to pass parameters to the templates. Common use case is to pass common environment variables to the templates.

{% raw %}
```yaml
arguments:
  parameters:
    - name: sonar_project_key
      value: "{{VARIABLES.SONAR_PROJECT_KEY}}"
    - name: sonar_host_url
      value: "{{VARIABLES.SONAR_HOST_URL}}"
```
{% endraw %}


## Specification

Following are the sections that can be added to the pipeline.yaml file:

### Steps

Steps will have set of steps which contains the following fields:

| Field | Required | Description |
|-------|----------|-------------|
| name | Required | The name of the step - This is the name of the step which will be displayed in the Choreo console which should be Alphanumeric and unique. |
| template | Optional | The template that will be used to execute the step |
| containerSet | Optional | The container set that will be used to execute the step |
| inlineScript | Optional | The inline script that will be used to execute the step |
| env | Optional | The environment variables that will be used in the step |
| volumeMounts | Optional | The volume mounts that will be used in the step |
| resources | Optional | The resources that will be used in the step |
| retryStrategy | Optional | The retry strategy that will be used in the step |
| timeout | Optional | The timeout that will be used in the step |

#### Basic Steps

{% raw %}
```yaml
steps:
  - name: Security Scan
    template: choreo/checkov-scan@v1
  - name: Build Container
    template: choreo/buildpack-build@v1
```
{% endraw %}

#### Steps with Inline Script

{% raw %}
```yaml
steps:
  - name: Security Scan
    inlineScript: |
      #!/bin/bash
      echo "Starting Security Scan..."
      checkov -d . -o json > checkov.json
      echo "Security Scan completed successfully."
    env:
      - name: CHECKOV_DIR
        value: <checkov-dir>
```
{% endraw %}

#### Steps with Choreo Template

{% raw %}
```yaml
steps:
  - name: Security Scan
    template: checkov-scan

templates:
  - name: checkov-scan
    inlineScript: |
      #!/bin/bash
      echo "Starting Security Scan..."
      checkov -d . -o json > checkov.json
      echo "Security Scan completed successfully."
```
{% endraw %}

#### Steps with Container Set

{% raw %}
```yaml
steps:
  - name: Security Scan
    containerSet:
        containers:
            - name: Security Scan
              containerTemplate: choreo/checkov-scan@v1
```
{% endraw %}

#### Steps with Container Set and Inline Script

{% raw %}
```yaml
steps:
  - name: Security Scan
    containerSet:
      containers:
      - name: Security Scan
        inlineScript: |
          #!/bin/bash
          echo "Starting Security Scan..."
          checkov -d . -o json > checkov.json
          echo "Security Scan completed successfully."
```
{% endraw %}

#### Parallel Steps

In the following example, the Go Test and Go Lint will be executed in parallel.

{% raw %}
```yaml
steps:
  - name: Security Scan
    template: choreo/checkov-scan@v1
  - - name: Go Test
      template: choreo/go-test@v1
    - name: Go Lint
      template: choreo/go-lint@v1
```
{% endraw %}

#### Steps with Retry Strategy

{% raw %}
```yaml
steps:
  - name: Security Scan
    template: choreo/checkov-scan@v1
    retryStrategy:
      limit: 3
      backoff:
        duration: 1m
        maxDuration: 10m
    timeout: 10m
```
{% endraw %}

### Templates

Choreo automation pipeline syntax provides two types of templates:

- Choreo Templates
- Argo Templates

#### Choreo Templates

Choreo templates provide a way to define a templates easier compare to Argo templates.

| Field | Required | Description |
|-------|----------|-------------|
| name | Required | The name of the template |
| inlineScript | Required | The inline script that will be used to execute the template |
| image | Optional | The image that will be used to execute the template |
| env | Optional | The environment variables that will be used in the template |
| volumeMounts | Optional | The volume mounts that will be used in the template |
| resources | Optional | The resources that will be used in the template |

{% raw %}
```yaml
#+required: name is the name of the template.
name: sonar-qube
#+required: inlineScript is the script that will be executed in the template. This has to be a valid bash script.
inlineScript: |
    #!/bin/bash
    echo "Starting SonarQube scan..."
    sonar-scanner \
    -Dsonar.projectKey="$SONAR_PROJECT_KEY" \
    -Dsonar.sources="$SOURCE_DIR" \
    -Dsonar.host.url="$SONAR_HOST_URL" \
    -Dsonar.login="$SONAR_TOKEN"
    if [ $? -eq 0 ]; then
    echo "SonarQube scan completed successfully."
    exit 0
    else
    echo "SonarQube scan failed. Check the logs for details."
    exit 1
    fi
#+optional: image is used to specify the image that will be used to execute the step.
# by default, the image will be a Choreo base image
image: "choreocontrolplane.azurecr.io/choreoipaas/choreo-sonar-qube-scan:v1"
#+optional: env is used to specify the environment variables that will be used in the template. There are three types of environment variables:
# 1. VARIABLES: These are the variables which are added though the Choreo console as variables
# Variables can be reference as `{{VARIABLES.VARIABLE_NAME}}` or `{{VARIABLES.COMPONENT.VARIABLE_NAME}}`
# 2. SECRETS: These are the secrets which are added though the Choreo console as secrets
# Secrets can be reference as `{{SECRETS.SECRET_NAME}}` or `{{SECRETS.COMPONENT.SECRET_NAME}}`
# 3. INLINE: These are the inline variables that are added in the pipeline.yaml file
env:
    - name: SONAR_PROJECT_KEY
      value: "{{VARIABLES.SONAR_PROJECT_KEY}}"
    - name: SOURCE_DIR
      value: <source-dir>
    - name: SONAR_HOST_URL
      value: <source-host-url>
    - name: SONAR_TOKEN
      value: "{{SECRETS.SONAR_TOKEN}}"
#+optional: volumeMounts is used to specify the volume mounts that will be used in the template. volumeClaimTemplates is used to specify the volume claim templates that will be used in the template.
volumeMounts:
    - name: "workflows-volume"
      mountPath: "/tmp"
#+optional: resources is used to specify the resources that will be used in the template.
resources:
    #+optional: requests is used to specify the requests that will be used in the template.
    requests:
        #+optional: memory is used to specify the memory that will be used in the template.
        memory: 128Mi
        #+optional: cpu is used to specify the cpu that will be used in the template.
        cpu: 100m
    #+optional: limits is used to specify the limits that will be used in the template.
    limits:
        #+optional: memory is used to specify the memory that will be used in the template.
        memory: 256Mi
        #+optional: cpu is used to specify the cpu that will be used in the template.
        cpu: 200m
```
{% endraw %}

#### Argo Templates

Argo templates provide a way to define a templates in a more flexible way compare to Choreo templates. Currently we only support sequential [script based templates](https://argo-workflows.readthedocs.io/en/latest/fields/#scripttemplate) and [container set templates](https://argo-workflows.readthedocs.io/en/latest/fields/#containersettemplate).

| Field | Required | Description |
|-------|----------|-------------|
| name | Required | The name of the template |
| script | Optional | The script that will be executed in the template |
| containerSet | Optional | The container set that will be used to execute the template |

Script based templates will have the following fields:

| Field | Required | Description |
|-------|----------|-------------|
| image | Required | The image that will be used to execute the template |
| command | Optional | The command that will be used to execute the template |
| source | Optional | The source that will be used to execute the template |
| env | Optional | The environment variables that will be used in the template |
| volumeMounts | Optional | The volume mounts that will be used in the template |
| resources | Optional | The resources that will be used in the template |

Container set templates will have the following fields:

| Field | Required | Description |
|-------|----------|-------------|
| containers | Required | The containers that will be used to execute the template |
| volumeMounts | Optional | The volume mounts that will be used in the template |
| resources | Optional | The resources that will be used in the template |

**Script based templates**

{% raw %}
```yaml
name: sonar-qube-v2
#+required: script is the script that will be executed in the template.
script:
  #+required: image is the image that will be used to execute the step.
  image: "choreocontrolplane.azurecr.io/choreoipaas/choreo-sonar-qube-scan:v1"
  #+required: command is the command that will be used to execute the step.
  command: ["sh"]
  #+required: source is the source that will be used to execute the step.
  source: |
    #!/bin/bash
    echo "Starting SonarQube scan..."
    sonar-scanner \
    -Dsonar.projectKey="$SONAR_PROJECT_KEY" \
    -Dsonar.sources="$SOURCE_DIR" \
    -Dsonar.host.url="$SONAR_HOST_URL" \
    -Dsonar.login="$SONAR_TOKEN"
    if [ $? -eq 0 ]; then
      echo "SonarQube scan completed successfully."
      exit 0
    else
      echo "SonarQube scan failed. Check the logs for details."
      exit 1
    fi
  #+optional: env is used to specify the environment variables that will be used in the template. There are three types of environment variables:
  # 1. VARIABLES: These are the variables which are added though the Choreo console as variables
  # Variables can be reference as `{{VARIABLES.VARIABLE_NAME}}` or `{{VARIABLES.COMPONENT.VARIABLE_NAME}}`
  # 2. SECRETS: These are the secrets which are added though the Choreo console as secrets
  # Secrets can be reference as `{{SECRETS.SECRET_NAME}}` or `{{SECRETS.COMPONENT.SECRET_NAME}}`
  # 3. INLINE: These are the inline variables that are added in the pipeline.yaml file
  env:
    - name: SONAR_PROJECT_KEY
      value: "{{VARIABLES.SONAR_PROJECT_KEY}}"
    - name: SOURCE_DIR
      value: <source-dir>
    - name: SONAR_HOST_URL
      value: "{{VARIABLES.SONAR_HOST_URL}}"
  #+optional: volumeMounts is used to specify the volume mounts that will be used in the template. volumeClaimTemplates is used to specify the volume claim templates that will be used in the template.
  volumeMounts:
    - name: "workflows-volume"
      mountPath: "/tmp"
  #+optional: resources is used to specify the resources that will be used in the template.
  resources:
    #+optional: requests is used to specify the requests that will be used in the template.
    requests:
      #+optional: memory is used to specify the memory that will be used in the template.
      memory: 128Mi
      #+optional: cpu is used to specify the cpu that will be used in the template.
      cpu: 100m
    #+optional: limits is used to specify the limits that will be used in the template.
    limits:
      #+optional: memory is used to specify the memory that will be used in the template.
      memory: 256Mi
      #+optional: cpu is used to specify the cpu that will be used in the template.
      cpu: 200m
```
{% endraw %}

**Container set based templates**

{% raw %}
```yaml
name: sonar-qube-v2
containerSet:
  #+required: containers is the containers that will be used to execute the template.
  containers:
    #+required: name is the name of the container.
    - name: security-scan
      #+required: image is the image that will be used to execute the container.
      image: "choreocontrolplane.azurecr.io/choreoipaas/choreo-sonar-qube-scan:v1"
      #+required: command is the command that will be used to execute the container.
      command: ["sh"]
      #+required: args is the arguments that will be used to execute the container.
      args: ["-c", "sonar-scanner -Dsonar.projectKey=$SONAR_PROJECT_KEY -Dsonar.sources=$SOURCE_DIR -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.login=$SONAR_TOKEN"]
      #+optional: env is the environment variables that will be used in the container. There are three types of environment variables:
      # 1. VARIABLES: These are the variables which are added though the Choreo console as variables
      # Variables can be reference as `{{VARIABLES.VARIABLE_NAME}}` or `{{VARIABLES.COMPONENT.VARIABLE_NAME}}`
      # 2. SECRETS: These are the secrets which are added though the Choreo console as secrets
      # Secrets can be reference as `{{SECRETS.SECRET_NAME}}` or `{{SECRETS.COMPONENT.SECRET_NAME}}`
      # 3. INLINE: These are the inline variables that are added in the pipeline.yaml file
      env:
        - name: SONAR_PROJECT_KEY
          value: "{{VARIABLES.SONAR_PROJECT_KEY}}"
        - name: SOURCE_DIR
          value: <source-dir>
      #+optional: resources is the resources that will be used in the container.
      resources:
        #+optional: requests is the requests that will be used in the container.
        requests:
          #+optional: memory is the memory that will be used in the container.
          memory: 128Mi
          #+optional: cpu is the cpu that will be used in the container.
          cpu: 100m
        #+optional: limits is the limits that will be used in the container.
        limits:
          #+optional: memory is the memory that will be used in the container.
          memory: 256Mi
          #+optional: cpu is the cpu that will be used in the container.
          cpu: 200m
    - name: security-scan-v2
      #+required: image is the image that will be used to execute the container.
      image: "choreocontrolplane.azurecr.io/choreoipaas/choreo-sonar-qube-scan:v1"
      #+required: command is the command that will be used to execute the container.
      command: ["sh"]
      #+required: args is the arguments that will be used to execute the container.
      args: ["-c", "sonar-scanner -Dsonar.projectKey=$SONAR_PROJECT_KEY -Dsonar.sources=$SOURCE_DIR -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.login=$SONAR_TOKEN"]
      #+optional: env is the environment variables that will be used in the container. There are three types of environment variables:
      # 1. VARIABLES: These are the variables which are added though the Choreo console as variables
      # Variables can be reference as `{{VARIABLES.VARIABLE_NAME}}` or `{{VARIABLES.COMPONENT.VARIABLE_NAME}}`
      # 2. SECRETS: These are the secrets which are added though the Choreo console as secrets
      # Secrets can be reference as `{{SECRETS.SECRET_NAME}}` or `{{SECRETS.COMPONENT.SECRET_NAME}}`
      # 3. INLINE: These are the inline variables that are added in the pipeline.yaml file
      env:
        - name: SONAR_PROJECT_KEY
          value: "{{VARIABLES.SONAR_PROJECT_KEY}}"
        - name: SOURCE_DIR
          value: <source-dir>
      #+optional: resources is the resources that will be used in the container.
      resources:
        #+optional: requests is the requests that will be used in the container.
        requests:
          #+optional: memory is the memory that will be used in the container.
          memory: 128Mi
          #+optional: cpu is the cpu that will be used in the container.
          cpu: 100m
        #+optional: limits is the limits that will be used in the container.
        limits:
          #+optional: memory is the memory that will be used in the container.
          memory: 256Mi
          #+optional: cpu is the cpu that will be used in the container.
          cpu: 200m
      #+optional: dependencies is the dependencies that will be used in the container.
      dependencies:
        #+optional: name is the name of the dependency.
        - security-scan
```
{% endraw %}

### Container Templates

Container templates are used to define a container that will be used in the pipeline.

| Field | Required | Description |
|-------|----------|-------------|
| name | Required | The name of the container |
| image | Optional | The image that will be used to execute the container |
| command | Optional | The command that will be used to execute the container |
| source | Optional | The source that will be used to execute the container |
| env | Optional | The environment variables that will be used in the container |
| volumeMounts | Optional | The volume mounts that will be used in the container |
| resources | Optional | The resources that will be used in the container |

{% raw %}
```yaml
name: sonar-qube-v2
image: "choreocontrolplane.azurecr.io/choreoipaas/choreo-sonar-qube-scan:v1"
command: ["sh"]
source: |
  #!/bin/bash
  echo "Starting SonarQube scan..."
  sonar-scanner \
  -Dsonar.projectKey="$SONAR_PROJECT_KEY" \
  -Dsonar.sources="$SOURCE_DIR" \
  -Dsonar.host.url="$SONAR_HOST_URL" \
  -Dsonar.login="$SONAR_TOKEN"
```
{% endraw %}

### Volume Claim Templates

Volume claim templates are used to define a volume that will be used in the pipeline.

| Field | Required | Description |
|-------|----------|-------------|
| metadata | Required | The metadata of the volume claim template |
| spec | Required | The spec of the volume claim template |

**Spec**

| Field | Required | Description |
|-------|----------|-------------|
| accessModes | Required | The access modes of the volume claim template |
| resources | Required | The resources of the volume claim template |

{% raw %}
```yaml
volumeClaimTemplates:
  - metadata:
      name: "workflows-volume"
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: "4Gi"
```
{% endraw %}

### Environment Variables

Environment variables are used to define the environment variables that will be used in the pipeline.

{% raw %}
```yaml
env:
  - name: SONAR_PROJECT_KEY
    value: "{{VARIABLES.SONAR_PROJECT_KEY}}"
  - name: SONAR_HOST_URL
    value: "{{VARIABLES.SONAR_HOST_URL}}"
  - name: SONAR_TOKEN
    value: "{{SECRETS.SONAR_TOKEN}}"
```
{% endraw %}

Users have three types of environment variables:

1. **VARIABLES**: These are the variables which are added though the Choreo console as variables
2. **SECRETS**: These are the secrets which are added though the Choreo console as secrets
3. **INLINE**: These are the inline variables that are added in the pipeline.yaml file

#### VARIABLES

These are the variables which are added though the Choreo console as variables. Variables can be referenced as:
- <code>&#123;&#123;VARIABLES.VARIABLE_NAME&#125;&#125;</code> - for component-level variables
- <code>&#123;&#123;VARIABLES.COMPONENT.VARIABLE_NAME&#125;&#125;</code> - for specific component variables

**Examples:**
{% raw %}
```yaml
env:
  - name: DB_HOST
    value: "{{VARIABLES.DB_HOST}}"
  - name: API_URL
    value: "{{VARIABLES.API_URL}}"
  - name: NODE_VERSION
    value: "{{VARIABLES.NODE_VERSION}}"
```
{% endraw %}

#### SECRETS

These are the secrets which are added though the Choreo console as secrets. Secrets can be referenced as:
- <code>&#123;&#123;SECRETS.SECRET_NAME&#125;&#125;</code> - for component-level secrets
- <code>&#123;&#123;SECRETS.COMPONENT.SECRET_NAME&#125;&#125;</code> - for specific component secrets

**Examples:**
{% raw %}
```yaml
env:
  - name: API_TOKEN
    value: "{{SECRETS.API_TOKEN}}"
  - name: DB_PASSWORD
    value: "{{SECRETS.DB_PASSWORD}}"
  - name: REGISTRY_PASSWORD
    value: "{{SECRETS.REGISTRY_PASSWORD}}"
```
{% endraw %}

**Legacy Syntax Support:**

For backward compatibility, the following legacy syntax is also supported:

- Variables: <code>&#123;&#123;VAR.VARIABLE_NAME&#125;&#125;</code> (legacy) → <code>&#123;&#123;VARIABLES.VARIABLE_NAME&#125;&#125;</code> (current)
- Secrets: <code>&#123;&#123;SECRET.SECRET_NAME&#125;&#125;</code> (legacy) → <code>&#123;&#123;SECRETS.SECRET_NAME&#125;&#125;</code> (current)

**Note:** It is recommended to use the current <code>&#123;&#123;VARIABLES.*&#125;&#125;</code> and <code>&#123;&#123;SECRETS.*&#125;&#125;</code> syntax for all new pipelines.


#### INLINE

INLINE variables are hardcoded values directly specified in the pipeline.yaml file.

**Examples:**
{% raw %}
```yaml
env:
  - name: NODE_ENV
    value: "production"
  - name: BUILD_TYPE
    value: "release"
  - name: LOG_LEVEL
    value: "info"
```
{% endraw %}

### Template Expressions

The automation pipelines syntax supports various template expressions for dynamic values:

#### Workflow Context References

Access workflow-level information:
{% raw %}
```yaml
# Workflow unique identifier
"{{workflow.uid}}"

# Current workflow status
"{{workflow.status}}"

# Workflow output parameters
"{{workflow.outputs.parameters.environment}}"
```
{% endraw %}

#### Step Input/Output References
Pass data between steps:
{% raw %}
```yaml
# Reference step outputs in subsequent steps
"{{steps.setup-environment.outputs.parameters.build_id}}"
"{{steps.build.outputs.parameters.image_tag}}"

# Use inputs within a template
"{{inputs.parameters.build-number}}"
"{{inputs.parameters.version}}"
```
{% endraw %}

#### Global Arguments References
Access workflow-level arguments:
{% raw %}
```yaml
# Global parameters defined in arguments section
"{{arguments.parameters.node_version}}"
"{{arguments.parameters.coverage_threshold}}"
```
{% endraw %}

#### System Variables
Built-in system variables available in all steps:
```yaml
# Repository directory where code is checked out
$REPOSITORY_DIR

# Current branch name
$CI_BRANCH

# Pipeline execution ID
$CI_PIPELINE_ID
```

### Inputs and Outputs

Templates can optionally define both inputs and outputs. Outputs from earlier template executions can be passed as inputs to subsequent steps. See the examples below.

#### Define inputs

Define template inputs at the same level as the script section in the YAML structure
{% raw %}
```yaml
  inputs:
    parameters:
      - name: sonar_project_key
      - name: config
      - name: sonar_host_url
```
{% endraw %}
#### Define outputs

Define template outputs at the same level as the script section in the YAML structure. Internally, Argo workflow always writes output to a given file.
{% raw %}
```yaml
outputs:
  parameters:
    - name: scan_summary_url
      valueFrom:
        path: /tmp/security-scan.json
```
{% endraw %}

#### Pass input parameters

To pass inputs required by a template, define them under the step where the template is referenced, as shown below. Inputs can be a static value, a JSON object, or an output from a previous step referenced as an expression.

{% raw %}
```yaml
steps:
  - name: Static Code Analysis
    template: sonar-qube
    arguments:
      parameters:
        - name: sonar_project_key
          value: "{{steps.setup.outputs.parameters.projectKey}}"
        - name: config
          yamlObject:
            severity: "HIGH,CRITICAL"
            exclude_paths:
              - "node_modules/"
              - "tests/"
        - name: sonar_host_url
          value: "https://sonarqube.example.com"
```
{% endraw %}

## Examples

### Pass output parameters from one step to the next

Following example shows how to output variables from one step and pass them onto the next steps in a Choreo workflow.

{% raw %}
```yaml
steps:
  - name: step1
    template: generate-variable
  - name: step2
    template: consume-variable
    arguments:
      parameters:
        - name: input-var1
          value: "{{steps.step1.outputs.parameters.result1}}"
        - name: input-var2
          value: "{{steps.step1.outputs.parameters.result2}}"
templates:
  - name: generate-variable
    inlineScript: |
      #!/bin/bash
      # Generate two random numbers between 1 and 100
      RANDOM_NUMBER1=$((RANDOM % 100 + 1))
      RANDOM_NUMBER2=$((RANDOM % 100 + 1))
      echo "Random number 1: $RANDOM_NUMBER1"
      echo "Random number 2: $RANDOM_NUMBER2"

      # Output the results as JSON
      echo "$RANDOM_NUMBER1" > /tmp/output1.json
      echo "$RANDOM_NUMBER2" > /tmp/output2.json
    outputs:
      parameters:
        - name: result1
          valueFrom:
            path: /tmp/output1.json
        - name: result2
          valueFrom:
            path: /tmp/output2.json
  - name: consume-variable
    inlineScript: |
      #!/bin/bash
        # Evaluate and sum the input variables
        VAR1="{{inputs.parameters.input-var1}}"
        VAR2="{{inputs.parameters.input-var2}}"
        eval "SUM=\$(( $VAR1 + $VAR2 ))"

        # Print the received variables and their sum
        echo "Received variable 1: $VAR1"
        echo "Received variable 2: $VAR2"
        echo "Sum of the two variables: $SUM"
    inputs:
      parameters:
        - name: input-var1
        - name: input-var2
```
{% endraw %}


## Examples

**Go Component Build With Templates**

{% raw %}
```yaml
steps:
  - name: Dockerfile Scan
    template: choreo/dockerfile-scan@v1
  - - name: Go Test
      template: go-test
    - name: Go Lint
      template: go-lint
  - name: Docker Build
    template: choreo/docker-build@v1
  - name: Vulnerability Scan
    template: choreo/trivy-scan@v1
  - name: Slack Notification
    template: slack-notification
templates:
  - name: go-test
    inlineScript: |
      #!/bin/bash
      echo "Installing Go..."
      apk add --no-cache go

      echo "Changing directory to repository directory..."
      cd $REPOSITORY_DIR
      echo "Running Go test..."
      go test
    env:
      - name: GO_VERSION
        value: "1.20"
  - name: go-lint
    inlineScript: |
      #!/bin/bash
      echo "Installing Go..."
      apk update && apk add --no-cache go golangci-lint

      echo "Changing directory to repository directory..."
      cd $REPOSITORY_DIR
      echo "Running Go lint..."
      golangci-lint run
    env:
      - name: GO_VERSION
        value: "1.20"
  - name: slack-notification
    inlineScript: |
      #!/bin/bash
      echo "Sending Slack notification..."
      MESSAGE=":white_check_mark: Pipeline succeeded"
      curl -X POST \
        -H 'Content-type: application/json' \
        --data "{\"text\":\"$MESSAGE\"}" \
        $SLACK_WEBHOOK_URL
    env:
      - name: SLACK_WEBHOOK_URL
        value: "{{SECRETS.SLACK_WEBHOOK_URL}}"
```
{% endraw %}


**Go Component Build With Container Templates**

{% raw %}
```yaml
steps:
  - name: Dockerfile Scan
    containerSet:
      containers:
        - name: Dockerfile Scan
          containerTemplate: choreo/dockerfile-scan@v1
        - - name: Go Test
            containerTemplate: go-test
          - name: Go Lint
            containerTemplate: go-lint
        - name: Docker Build
          containerTemplate: choreo/docker-build@v1
        - name: Vulnerability Scan
          containerTemplate: choreo/trivy-scan@v1
        - name: Slack Notification
          containerTemplate: slack-notification

containerTemplates:
  - name: go-test
    inlineScript: |
      #!/bin/bash
      echo "Installing Go..."
      apk add --no-cache go

      echo "Changing directory to repository directory..."
      cd $REPOSITORY_DIR
      echo "Running Go test..."
      go test
    env:
      - name: GO_VERSION
        value: "1.20"
  - name: go-lint
    inlineScript: |
      #!/bin/bash
      echo "Installing Go..."
      apk update && apk add --no-cache go golangci-lint
      echo "Changing directory to repository directory..."
      cd $REPOSITORY_DIR
      echo "Running Go lint..."
      golangci-lint run
    env:
      - name: GO_VERSION
        value: "1.20"
  - name: slack-notification
    inlineScript: |
      #!/bin/bash
      echo "Sending Slack notification..."
      MESSAGE=":white_check_mark: Pipeline succeeded"
      curl -X POST \
        -H 'Content-type: application/json' \
        --data "{\"text\":\"$MESSAGE\"}" \
        $SLACK_WEBHOOK_URL
    env:
      - name: SLACK_WEBHOOK_URL
        value: "{{SECRETS.SLACK_WEBHOOK_URL}}"
```
{% endraw %}
