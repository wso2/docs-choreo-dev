## Configuring Nginx Ingress Controllers

### Description
Configure the following Nginx Ingress Controllers used in Choreo routing cluster
1. NIC used by Choreo APIM

### Prerequisites
1) Helm 3 installed in execution environment 

### Usage
1) Initially ssh into relevant bastion and set the cluster context to routing cluster

2) Export following environmental variables
    ```bash
    export APIM_NAMESPACE="xxxxxxxxxxxxxxxxx" #ex:- <env>-choreo-apim
    export ROUTING_LOADBALANCER_IP="xxxxxxxxxxxxxxxxx"
    export LOADBALANCER_SUBNET="xxxxxxxxxxxxxxxxx"
    export LOADBALANCER_IP_RG="xxxxxxxxxxxxxxxxx"
    ```

3) Execute `configure-ingress-controllers.sh` script
    ```bash
    bash configure-ingress-controllers.sh
    ```