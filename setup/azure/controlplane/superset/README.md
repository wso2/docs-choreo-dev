## Secret Store CSI Driver configuration

### Description
Configure Superset in control plane cluster

### Prerequisites
1) Helm 3 installed in execution environment 
2) Login to the Azure CLI via `az login`
3) Ensure you have connected to the correct subscription based on the environment
   * Dev - `choreo-dev-001`
   * Stage - `choreo-stg-controlplane-001`
   * Prod - `choreo-prod-controlplane-001`
4) Ensure that the proper key vault is referenced
   * Dev - `dev-csi-64`
   * Stage - `stg-csi-74`
   * Prod - `prod-csi-60`
   ```commandline
   export CSI-KEYVAULT-NAME=xxxxxxxxxxxx
   ```
### Usage
1) Initially ssh into relevant bastion and set the cluster context to Controlplane cluster

3) Execute `configure-csi-secret-store.sh` script
    ```bash
    bash configure-csi-secret-store.sh
    ```
   