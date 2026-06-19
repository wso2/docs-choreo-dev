# Manage customizable CI pipelines

A customizable CI pipeline lives at the organization level. Authoring and maintaining the pipeline YAML is a platform-engineer-only task. Making a pipeline take effect on a build is a two-step task that can be done by either a platform engineer or a developer: first attach the pipeline to the project (this makes it available in the project), then select it on the specific deployment track of a component that should use it. Defining the variables and secrets the pipeline expects at any scope, and reviewing build validation errors, can also be done by either role.

!!! info
    Customizable CI is available only on private data plane organizations and must be enabled by WSO2 on request.

| Task | Who |
|---|---|
| Create a pipeline | Platform engineer |
| Edit the pipeline YAML | Platform engineer |
| Attach a pipeline to a project | Platform engineer or developer |
| Attach a pipeline to a deployment track | Platform engineer or developer |
| Add variables and secrets at any scope (organization, project, or deployment track) | Platform engineer or developer |
| View pipeline validation errors after a build | Platform engineer or developer |

## Create a pipeline

You author a customizable CI pipeline by writing its YAML in the Console editor.

1. In the {{ product_name }} Console, switch to the **Platform** view.
2. In the top navigation menu, select the relevant **Organization**.
3. In the left navigation menu, click **DevOps** and then click **CI Pipelines**.
4. Click **Create a CI Pipeline**.
5. Enter a **Name** and **Description** for the pipeline.
6. Select the **Build Preset** that matches the components you will attach this pipeline to.
7. Paste your pipeline content into the YAML editor. See [Author pipeline.yaml](author-pipeline-yaml.md) for the syntax.
8. Click **Create**.

The Console validates the YAML structure and required mandatory steps when you click **Create**. If the pipeline.yaml has structural problems or is missing the build step required for the selected Build Preset, the validation dialog opens with the specific issues. Fix them in the editor and click **Create** again.

## Attach a pipeline

For a deployment track to use a customizable CI pipeline, two steps are required, in order:

1. **Attach the pipeline to the project** that contains the deployment track's component. This makes the pipeline visible in the project so it can be selected on individual deployment tracks. Attaching to the project alone does not activate the pipeline on any build.
2. **Select the pipeline on each deployment track** that should use it, from the deployment track's Build Configurations page. Each deployment track of a component is configured individually — there is no component-wide attach. Repeat this for every deployment track that should use the customizable pipeline.

### Step 1: Attach the pipeline to the project

1. Switch to the **Platform** view in the {{ product_name }} Console.
2. In the top navigation menu, select the **Organization** and then the **Project**.
3. In the left navigation menu, click **DevOps** and then click **CI Pipelines**.
4. Click **Add to Project**, select the organization-level pipeline you created, and confirm. The pipeline is now available to the project's components.

### Step 2: Select the pipeline on a deployment track

1. Switch to the **Development** view.
2. Open the **Component** that contains the deployment track you want to configure.
3. At the top of the page, switch to the **Deployment Track** you want to configure.
4. In the left navigation menu, click **Build** and then click **Build Configurations**.
5. Under **CI Pipeline**, select the pipeline from the dropdown.
6. Click **Save**.

The next build of that deployment track uses the customizable pipeline.

## Add variables and secrets

If your pipeline references variables or secrets — for example, `{% raw %}{{ORG.SECRETS.SNYK_TOKEN}}{% endraw %}` or `{% raw %}{{DT.VARIABLES.GO_VERSION}}{% endraw %}` — you need to define the values before the first build runs.

Define values at the scope that matches the reference in your YAML. An explicit reference such as `{% raw %}{{ORG.SECRETS.X}}{% endraw %}` or `{% raw %}{{DT.VARIABLES.X}}{% endraw %}` resolves at exactly the named scope. The legacy unqualified `{% raw %}{{VARIABLES.X}}{% endraw %}` and `{% raw %}{{SECRETS.X}}{% endraw %}` form resolves by walking **DT → Project → Org** and using the first match.

### Add organization-level variables and secrets

1. Switch to the **Platform** view.
2. In the top navigation menu, select the **Organization**.
3. In the left navigation menu, click **DevOps** and then click **CI Pipelines**.
4. Open the pipeline you created.
5. Click the **Variables** tab to add variables, or the **Secrets** tab to add secrets.
6. Click **+ Add Variable** (or **+ Add Secret**) and provide the name and value. Add one entry per name your YAML references.
7. Click **Save**.

### Add project or deployment-track scope

Variables and secrets at the project and deployment-track scopes are managed from the same Build Configurations page used to attach the pipeline. Open the component, navigate to **Build** > **Build Configurations**, and use the **Variables** and **Secrets** sections to define values at the scope you need.

## Edit the pipeline YAML

1. Switch to the **Platform** view.
2. In the top navigation menu, select the **Organization**.
3. In the left navigation menu, click **DevOps** and then click **CI Pipelines**.
4. Open the pipeline.
5. Click **Edit Pipeline YAML**, make your changes, and click **Save**.

The same validation that runs at create time runs again when you save — structural errors and missing mandatory steps are surfaced in the editor before the change is persisted.

## View pipeline validation errors

When a build runs against a pipeline that has a problem the validator could not catch at save time (for example, an unresolved variable reference, a missing required parameter, or an incompatible step input), the build fails fast and the **Build Details** panel shows the specific problem — for example, which variables or secrets are missing at which scope, or which mandatory step is missing. Fix the YAML or define the missing values, then trigger a new build.

For build log details, see [CI Pipeline logs](../ci-pipeline-logs.md).

## What's next

- [Author pipeline.yaml](author-pipeline-yaml.md) — pipeline YAML syntax, steps, templates, variables, secrets, and system variables.
- [Built-in steps](built-in-steps.md) — the `choreo/*` steps you can call from your pipeline.
