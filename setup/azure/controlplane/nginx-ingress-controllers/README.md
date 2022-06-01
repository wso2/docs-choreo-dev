## Configuring Nginx Ingress Controllers

### Description
Configure the following Nginx Ingress Controllers used in Choreo controlplane
1. NIC used by Choreo IDP
2. NIC used for internal communication between Choreo controlplane and Choreo analytics
3. NIC used for internal communication between Choreo controlplane and Ballerina Central
4. NIC used for internal communication between Choreo controlplane and Choreo dataplane
5. NIC used by Choreo System/APIM

### Prerequisites
1) Helm 3 installed in execution environment 

### Usage
1) Initially ssh into relevant bastion and set the cluster context to Controlplane cluster

2) Export following environmental variables
    ```bash
    export IDP_NAMESPACE="xxxxxxxxxxxxxxxxx" #ex:- <env>-choreo-idp
    export IDP_INGRESS_LOADBALANCER_IP="xxxxxxxxxxxxxxxxx" 
    export INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE="xxxxxxxxxxxxxxxxx" #ex:- <env>-choreo-cp-internal-nginx-ingress
    export INTERNAL_CHOREO_CONTROLPLANE_INGRESS_LOADBALANCER_IP="xxxxxxxxxxxxxxxxx" 
    export BCENTRAL_INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE="xxxxxxxxxxxxxxxxx"
    export BCENTRAL_INTERNAL_CHOREO_CONTROLPLANE_INGRESS_LOADBALANCER_IP="xxxxxxxxxxxxxxxxx"
    export INTERNAL_INGRESS_NAMESPACE="xxxxxxxxxxxxxxxxx" #ex:- <env>-choreo-internal-nginx-ingress
    export INTERNAL_INGRESS_LOADBALANCER_IP="xxxxxxxxxxxxxxxxx"
    export SYSTEM_NAMESPACE="xxxxxxxxxxxxxxxxx" #ex:- <env>-choreo-system
    export SYSTEM_INGRESS_LOADBALANCER_IP="xxxxxxxxxxxxxxxxx"
    export LOADBALANCER_SUBNET="xxxxxxxxxxxxxxxxx"
    export LOADBALANCER_IP_RG="xxxxxxxxxxxxxxxxx"
    ```

3) Execute `configure-ingress-controllers.sh` script
    ```bash
    bash configure-ingress-controllers.sh
    ```