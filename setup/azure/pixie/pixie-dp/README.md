### How to setup pixie agent (in data-planes)

1. Log into pixie admin console `px-cloud.{ENV}.choreo.dev` and create a deploy-key

2. Create `pl` namespace

    kubectl create ns pl

3. Create `pl-deploy-secrets` secret

   kubectl create secret generic -n pl pl-deploy-secrets --from-literal=deploy-key="<deploy-key generated from pixie console>"

4. Apply kustomize overlay

5. Go to `px-cloud.{ENV}.choreo.dev/configure-data-export`

6. Create script and copy the content of `choreo-control-plane/setup/azure/pixie/pixie-dp/http_request_latency.pxl` to the PxL Script section.

7. Give the `adx-exporter` as the script name and choose OpenTelemetry as the plugin.

8. Under the clusters select all available dataplanes and give `10 seconds` as summary window.

9. Set the export URL to `opentelemetry-agent.{ENV}-choreo-obs.svc.cluster.local:4317` and press Create.
