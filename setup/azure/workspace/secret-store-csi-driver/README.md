## Secret Store CSI Driver configuration

### Description
Configure the Secret Store CSI Driver in workspace cluster

### Prerequisites
1) Helm 3 installed in execution environment 

### Usage
1) Initially ssh into relevant bastion and set the cluster context to workspace cluster

2) Export following environmental variables
    ```bash
    export CLUSTER_NAME="xxxxxxxxxxxxxxxxx"
    export SYSTEM_CSI_KEY_VAULT_CLIENT_ID="xxxxxxxxxxxxxxxxx"
    export DP_SYSTEM_NAMESPACE="xxxxxxxxxxxxxxxxx" #ex:- <env>-choreodp-system
    ```


3) Execute `configure-csi-secret-store.sh` script
    ```bash
    bash configure-csi-secret-store.sh
    ```
   