### Description
Create TLS secrets used by internal Ingresses in control plane cluster

### Usage
1) Initially ssh into relevant bastion and set the cluster context to Controlplane cluster

2) Export following environmental variables
    ```bash
    export ENV=xxxxx
    export SYSTEM_NAMESPACE=xxxxxxxxxxxxxxxxx #ex:- <env>-choreo-system
    export APIM_NAMESPACE=xxxxxxxxxxxxxxxxx #ex:- <env>-choreo-apim
    export SUBSCRIPTION_INGRESS_TLS_CERT_FILE_PATH=xxxxxxxxxxxxxxxxx
    export SUBSCRIPTION_INGRESS_TLS_KEY_FILE_PATH=xxxxxxxxxxxxxxxxx
    export CONTROLPLANE_SYSTEM_INTERNAL_INGRESS_TLS_CERT_FILE_PATH=xxxxxxxxxxxxxxxxx
    export CONTROLPLANE_SYSTEM_INTERNAL_INGRESS_TLS_KEY_FILE_PATH=xxxxxxxxxxxxxxxxx
    export CONTROLPLANE_APIM_INTERNAL_INGRESS_TLS_CERT_FILE_PATH=xxxxxxxxxxxxxxxxx
    export CONTROLPLANE_APIM_INTERNAL_INGRESS_TLS_KEY_FILE_PATH=xxxxxxxxxxxxxxxxx
    ```
   Follow the following chart to obtain the required CERT and KEY files, for first time execution. 
   Proceed to store the file values in the CSI Key Vault for further reference.
   
   | Cert/Key File Pair                      | First Execution          | CSI Key Vault Secret for reference                                                                    
   |-----------------------------------------|--------------------------|-------------------------------------------------------------------------------------------------------
   | Subscription Ingress                    | Obtain from DigiOps team | `subscription-ingress-TLS-KEY` <br/> `subscription-ingress-TLS-CERTIFICATE`                           
   | ControlPlane System Internal Ingress    | Obtain from SecOps team  | `controlplane-system-internal-ingress-TLS-KEY` <br/> `controlplane-system-internal-ingress-TLS-CERTIFI
   | ControlPlane APIM Internal Ingress      | Obtain from SecOps team  | `controlplane-apim-internal-ingress-TLS-KEY` <br/> `controlplane-apim-internal-ingress-TLS-CERTIFICATE
   | BCentral APIM Internal Ingress          | Obtain from SecOps team  | `bcentral-apim-internal-ingress-TLS-KEY` <br/> `bcentral-apim-internal-ingress-TLS-CERTIFICATE`       
   | BCentral GlobalAdapter Internal Ingress | Obtain from SecOps team  | `bcentral-ga-internal-ingress-TLS-KEY` <br/> `bcentral-ga-internal-ingress-TLS-CERTIFICATE`       |

3) Execute `create-ingress-tls-secrets.sh` script
    ```bash
    bash create-ingress-tls-secrets.sh
    ```