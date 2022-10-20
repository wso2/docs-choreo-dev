### How to setup pixie agent (in data-planes)

1. Log into pixie admin console `px-cloud.{preview-dv/st/NA}.choreo.dev` and create a deploy-key

2. Create `pl` namespace

   ```
   kubectl create ns pl
   ```

5. Create `pl-deploy-secrets` and `pl-cluster-secrets` secrets

   ```
   kubectl create secret generic -n pl pl-deploy-secrets --from-literal=deploy-key="{deploy-key generated from pixie console}"

   kubectl create secret generic -n pl pl-cluster-secrets --from-literal=sentry-dsn=""
   ```

6. Apply kustomize overlay

#### Setup Data Retention Script

1. Go to `px-cloud.{preview-dv/st/NA}.choreo.dev/admin/plugins`

2. Enable OpenTelemetry Plugin and put `opentelemetry-agent.{dev/stage/prod}-choreo-obs.svc.cluster.local:4317` as the export URL.

3. Make sure "Secure connections with TLS" is off since pixie will be talking to collector running within the data plane and click "Save".

    ![Plugin Settings for Pixie](plugin-settings.png)

4. Then press "Edit Scripts" and Turn off all plugins under "Presets from OpenTelemetry".

5. Click "Create script" and copy the content of `choreo-control-plane/setup/azure/pixie/pixie-dp/http_request_latency.pxl` to the PxL Script section.

6. Give the `adx-exporter` as the script name and choose OpenTelemetry as the plugin.

7. Change the summary window to `10 seconds` to press create.

    ![Script Config UI](script-config.png)

8. Once everything is configured Data exporter view should look like this.

    ![Data Exporters UI](data-exporters.png)

### How to re-deploy pixie agent (in case of an unrecoverable error)

1. Log into pixie admin console `px-cloud.{preview-dv/st/NA}.choreo.dev` and create a deploy-key

2. Stop choreo deployment pipeline

3. Delete `pl` namespace

4. Create `pl` namespace

5. Create `pl-deploy-secrets` and `pl-cluster-secrets` secrets

   ```
   kubectl create secret generic -n pl pl-deploy-secrets --from-literal=deploy-key="{deploy-key generated from pixie console}"

   kubectl create secret generic -n pl pl-cluster-secrets --from-literal=sentry-dsn=""
   ```

6. Enable and trigger choreo deployment pipeline

7. Continue with "Setup Data Retention Script"
