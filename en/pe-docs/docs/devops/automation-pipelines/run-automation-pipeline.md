After a pipeline is defined, you can execute it. The pipeline may need some inputs for it to run. Ensure you configure them correctly before triggering the run.

# Add Pipeline Variables and Secrets

The pipelines are defined at the organization level. Thus the variables defined for them are injected at the same level. Learn more on how to define a input variable in your pipeline at [Variables and Secrets - {{ product_name }} Pipelines Specification](https://github.com/wso2/choreo-pipeline-specification/blob/36f292dcb34bdcf2292f5c8537efc57ef5f5e5a4/docs/specification/environment-variables.md). Follow these steps to define inputs and their values for a given pipeline.

1. Click on **Pipeline Variables & Secrets** at upper right corner of the pipeline page.
2. A panel will appear on the right side. To add a variable, under **Variables** section, add a new one by providing the Name and Value. The variable name must exactly match the name expected by the pipeline.
3. If you have a secret to be added as a pipeline input, under **Secrets** section, add a new one by providing the Name and Value. The secret name must exactly match the name expected by the pipeline.

!!! info "Note"
    Once a secret is added, it is securely uploaded to the environment’s configured vault and injected into the pods running the workflow. You will not be able to view the secret value again after it is added.

## Manage Existing Pipeline Variables and Secrets

To update or remove a secret, click the three vertical dots in the upper-right corner of the variable box in the {{ product_name }} UI. From the menu, choose the appropriate action.

## Pipeline Parameters

Parameters are another type of variables referred by automation pipelines. Speciality of these variables is, before running the pipeline, user needs to choose a value for the parameters out of specified.

In pipeline definition, specify parameters like below.

```yaml
parameters:
  - name: environment
    default: "Development"
    enum: ["Development", "Production"]
    displayName: "Environment"
```

# Run Pipeline

Once the pipeline inputs are configured, click **Start New Run** to trigger a workflow run. The run will use the latest version of the pipeline definition along with the configured inputs.

Under the Runs table, a new row will appear displaying a unique **Run ID** for the triggered run. The following details are shown for each pipeline run:

1. **Version** – The version of the pipeline definition. This version is automatically incremented each time the pipeline definition is updated. It helps determine whether the run was triggered after a definition update.
2. **Status** – The current status of the run:
    - **Successful**: All steps in the pipeline completed successfully.
    - **Failed**: One or more steps in the pipeline failed.
    - **Stopped**: The pipeline was manually stopped.
3. **Start Time** – The timestamp when the pipeline run was triggered.
4. **Duration** – The total time taken to execute the pipeline. If the pipeline runs for more than a predefined threshold (default 60 minutes), it will be automatically force-stopped by {{ product_name }}.


## View Pipeline Logs

Click **View Logs** for a selected pipeline run in the Runs table. This opens a panel on the right, displaying logs for each step of the pipeline. You can close or maximize the log panel using the icons in the upper-right corner of the panel.

!!! info "Note"
    {{ product_name }} keeps pipeline logs maximum for 30 days.

## Stop a Pipeline Run

Once a pipeline run is triggered, click on **View Logs** and then in the log panel that opens to the right, click on **Stop Run**.
