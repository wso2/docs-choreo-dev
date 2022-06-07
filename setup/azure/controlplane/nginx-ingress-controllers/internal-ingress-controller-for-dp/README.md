## Controlplane - Dataplane internal conn. Nginx Ingress Controller configuration

### Description
Configure the Nginx Ingress Controller used for internal communication between Choreo controlplane and Choreo dataplane

### Usage
1) Initially ssh into relevant bastion and set the cluster context to Controlplane cluster

2) Export following environmental variables
    ```bash
    export INTERNAL_INGRESS_NAMESPACE="xxxxxxxxxxxxxxxxx" #ex:- <env>-choreo-internal-nginx-ingress
    export INTERNAL_INGRESS_LOADBALANCER_IP="xxxxxxxxxxxxxxxxx" 
    export LOADBALANCER_SUBNET="xxxxxxxxxxxxxxxxx"
    export LOADBALANCER_IP_RG="xxxxxxxxxxxxxxxxx"
    ```

3) Execute `configure-ingress-controller.sh` script
    ```bash
    bash configure-ingress-controller.sh
    ```