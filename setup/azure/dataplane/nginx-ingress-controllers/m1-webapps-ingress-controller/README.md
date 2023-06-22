## IDP Nginx Ingress Controller configuration

### Description
Configure the Nginx Ingress Controller used by Choreo IDP

### Usage
1) Run `az login` to Login to the AZ CLI

2) Initially ssh into relevant bastion and set the cluster context to Controlplane cluster

3) Export following environmental variables, This must be done for all Dataplane clusters
    ```bash
    export WEBAPPS_NAMESPACE="xxxxxxxxxxxxxxxxx"                   
    export WEBAPPS_INGRESS_LOADBALANCER_IP="xxxxxxxxxxxxxxxxx" 
    export LOADBALANCER_SUBNET="xxxxxxxxxxxxxxxxx"
    export LOADBALANCER_IP_RG="xxxxxxxxxxxxxxxxx"
    ```
   Find the values as defined below
   * `WEBAPPS_NAMESPACE` - `<env>-choreo-webapps` where env=dev,stage,prod
   * `WEBAPPS_INGRESS_LOADBALANCER_IP`, `LOADBALANCER_SUBNET`, `LOADBALANCER_IP_RG` - Refer to the following table

   | Dataplane region | Subscription                       | Virtual Network Name                         | `LOADBALANCER_SUBNET`                                                                                          | `WEBAPPS_INGRESS_LOADBALANCER_IP`                                                                                                                  | `LOADBALANCER_IP_RG`                              |
   |------------------|------------------------------------|----------------------------------------------|----------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------|
   | US East 2        | `choreo-<env>-dataplane-e1-001`    | `choreo-<env>-dataplane-e1-virtual-network`  | Navigate to the Virtual Network, Proceed to subnets and find the Subnet that contains the word `LoadBalancer`  | Any IP from the mentioned subnet range, verify that it is not allocated by searching it via the `Connected devices` tab within the Virtual Network | `mc-choreo-<env>-dataplane-e1-aks-cluster-rg`     |
   | Central India    | `choreo-<env>-in-dataplane-e1-001` | `choreo-<env>-cin-dataplane-virtual-network` | Navigate to the Virtual Network, Proceed to subnets and find the Subnet that contains the word `LoadBalancer`  | Any IP from the mentioned subnet range, verify that it is not allocated by searching it via the `Connected devices` tab within the Virtual Network | `mc-choreo-<env>-cin-dataplane-e1-aks-cluster-rg` |
   | North Europe     | `choreo-<env>-ne-dataplane-e1-001` | `choreo-<env>-ne-dataplane-virtual-network`  | Navigate to the Virtual Network, Proceed to subnets and find the Subnet that contains the word `LoadBalancer`  | Any IP from the mentioned subnet range, verify that it is not allocated by searching it via the `Connected devices` tab within the Virtual Network | `mc-choreo-<env>-ne-dataplane-e1-aks-cluster-rg`  |

4) Execute `configure-ingress-controller.sh` script
    ```bash
    bash configure-ingress-controller.sh
    ```
   