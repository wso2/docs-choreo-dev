## IDP Nginx Ingress Controller configuration

### Description
Configure the Nginx Ingress Controller used by Choreo IDP

### Usage
1) Initially ssh into relevant bastion and set the cluster context to Controlplane cluster

2) Export following environmental variables
    ```bash
    export WEBAPPS_NAMESPACE="xxxxxxxxxxxxxxxxx"    #ex:- dev-choreo-webapps
    export WEBAPPS_INGRESS_LOADBALANCER_IP="xxxxxxxxxxxxxxxxx" 
    export LOADBALANCER_SUBNET="xxxxxxxxxxxxxxxxx"
    export LOADBALANCER_IP_RG="xxxxxxxxxxxxxxxxx"
    ```

3) Execute `configure-ingress-controller.sh` script
    ```bash
    bash configure-ingress-controller.sh
    ```
   