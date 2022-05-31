## Cert Manager configuration

### Description
Configure the Cert Manager in control plane cluster

### Prerequisites
1) Helm 3 installed in execution environment 

### Usage
1) Initially ssh into relevant bastion and set the cluster context to Controlplane cluster

2) Execute `configure-cert-manager.sh` script
    ```bash
    bash configure-cert-manager.sh
    ```