# Create Automation Pipelines

{{ product_name }} allows to define automation pipelines at organization level. Follow these steps to create an automation pipeline.

1. Sign in to the [{{ product_name }} Console](https://console.choreo.dev/).
2. Open **Platform Operations** perspective.
3. In the left navigation menu, click **DevOps** and then click **Automation Pipelines**.
4. Click **+ Create**.
5. You can define an inline yaml or import from GitHub

## Define Pipeline within {{ product_name }}

This option is useful when you are testing a pipeline or if you like to let {{ product_name }} to keep your pipeline definitions.

1. Under `Define your Pipeline YAML`, click **Use YAML Editor**
2. Provide a name by which you can identify the pipeline.
3. Optionally provide a description
4. Click **Create**

This pipeline definition is managed within {{ product_name }}. You can update it after defining as well.

## Import Pipeline from Git

If your organization manages pipelines in Git (GitHub, Bitbucket or GitLab), you can authorize and let {{ product_name }} to refer it. It could be a monolithic repository having multiple pipelines.

1. Under `Import Pipeline from Git` Click on the Git provider to authorize, and proceed with steps to grant access.
2. You can select **Repository**, **Branch** and Specific **Pipeline YAML** file within a directory in the repository.
3. Provide a name by which you can identify the pipeline.
4. Optionally provide a description
5. Click **Create**

Created pipelines will be listed in a tabular view.


# Edit Automation Pipelines

Editing is only possible for pipelines defined within {{ product_name }}. Click **Edit Pipeline YAML** and in the right side panel that opens up you can edit the pipeline definition. You can view the pipeline definition in full-screen view as well by maximizing the panel.

!!! info "Note"
    Once a pipeline is edited, you cannot roll back to a previous version.

When a pipeline is defined in Git, it always uses the latest version from the repository at runtime.
To update the pipeline, modify it in Git and trigger a new run.

# Delete Automation Pipelines

In the pipeline listing, under the **Actions** column, click the **Delete** icon. A confirmation pop-up will appear. Upon confirmation, the pipeline and all its associated runs will be permanently removed from {{ product_name }}.
