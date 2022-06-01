## APIM Nginx Ingress Controller configuration

### Description
Configure the Nginx Ingress Controller used by Routing cluster

### Usage
1) Initially ssh into relevant bastion and set the cluster context to Routing cluster

2) Export following environmental variables
    ```bash
    export APIM_NAMESPACE="xxxxxxxxxxxxxxxxx" #ex:- <env>-choreo-apim
    export ROUTING_LOADBALANCER_IP="xxxxxxxxxxxxxxxxx" 
    export LOADBALANCER_SUBNET="xxxxxxxxxxxxxxxxx"
    export LOADBALANCER_IP_RG="xxxxxxxxxxxxxxxxx"
    ```

3) Execute `configure-ingress-controller.sh` script
    ```bash
    bash configure-ingress-controller.sh
    ```