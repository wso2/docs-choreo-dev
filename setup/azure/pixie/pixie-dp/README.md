### How to setup pixie agent (in data-planes)

1. Log into pixie admin console `px-cloud.{ENV}.choreo.dev` and create a deploy-key

2. Create `pl` namespace

    kubectl create ns pl

3. Create `pl-deploy-secrets` secret

   kubectl create secret generic -n pl pl-deploy-secrets --from-literal=deploy-key="<deploy-key generated from pixie console>"

4. Apply kustomize overlay
