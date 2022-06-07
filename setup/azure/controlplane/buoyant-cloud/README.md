## Cert Manager configuration

### Description
Configure the Buoyant Cloud in control plane cluster

### Usage
1) Initially ssh into relevant bastion and set the cluster context to Controlplane cluster

2) Export following environmental variables
    ```bash
    export BUOYANT_CLOUD_AGENT_ID="xxxxxxxxxxxxxxxxx"
    export BUOYANT_CLOUD_AGENT_KEY="xxxxxxxxxxxxxxxxx"
    export BUOYANT_CLOUD_AGENT_DOWNLOAD_KEY="xxxxxxxxxxxxxxxxx"
    export BUOYANT_CLOUD_NAME="xxxxxxxxxxxxxxxxx"
    ```
3) Execute `configure-buoyant-cloud.sh` script
    ```bash
    bash configure-buoyant-cloud.sh
    ```