## Sample azure-deploy.properties files for each cluster

### Control Plane Cluster

```bash
SYSTEM_NAMESPACE=xxxxxxxxxxxxxxxxx
APIM_NAMESPACE=xxxxxxxxxxxxxxxxx
INTERNAL_INGRESS_NAMESPACE=xxxxxxxxxxxxxxxxx
IDP_NAMESPACE=xxxxxxxxxxxxxxxxx
USERAPPS_NAMESPACE=xxxxxxxxxxxxxxxxx
INTERNAL_CHOREO_CONTROLPLANE_INGRESS_NAMESPACE=xxxxxxxxxxxxxxxxx
SYSTEM_LOADBALANCER_IP=xxxxxxxxxxxxxxxxx
USERAPPS_LOADBALANCER_IP=xxxxxxxxxxxxxxxxx
INTERNAL_INGRESS_LOADBALANCER_IP=xxxxxxxxxxxxxxxxx
INTERNAL_CHOREO_CONTROLPLANE_INGRESS_LOADBALANCER_IP=xxxxxxxxxxxxxxxxx
IDP_LOADBALANCER_IP=xxxxxxxxxxxxxxxxx
LOADBALANCER_SUBNET=xxxxxxxxxxxxxxxxx
LOADBALANCER_IP_RG=xxxxxxxxxxxxxxxxx
SYSTEM_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
SYSTEM_CSI_KEY_VAULT_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
APIM_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
APIM_CSI_KEY_VAULT_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
IDP_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
IDP_CSI_KEY_VAULT_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
DNS01_CHALLENGE_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
AKS_READONLY_AD_GROUP_ID=xxxxxxxxxxxxxxxxx
ENV=xxxxx
LINKERD_VIZ_LOADBALANCER_IP=xxxxxxxxxxxxxxxxx
LINKERD_VIZ_INGRESS_CLASS=xxxxxxxxxxxxxxxxx
LOADBALANCER_IP_RG=xxxxxxxxxxxxxxxxx
LOADBALANCER_SUBNET=xxxxxxxxxxxxxxxxx
LINKERD_VIZ_DASHBOARD_AUTH_UNAME_PWD=xxxxxxxxxxxxxxxxx
EOF
```
#### Creating TLS Secrets
For executing the TLS Secret generation script mentioned in `controlplane/create-ingress-tls-secrets.sh`
Provide the following properties in addition to the above-mentioned properties.

``````
SUBSCRIPTION_INGRESS_TLS_CERT_FILE_PATH=xxxxxxxxxxxxxxxxx
SUBSCRIPTION_INGRESS_TLS_KEY_FILE_PATH=xxxxxxxxxxxxxxxxx
CONTROLPLANE_INTERNAL_INGRESS_TLS_CERT_FILE_PATH=xxxxxxxxxxxxxxxxx
CONTROLPLANE_INTERNAL_INGRESS_TLS_KEY_FILE_PATH=xxxxxxxxxxxxxxxxx
``````
Follow the following chart to obtain the required CERT and KEY files, for first time execution. 
Proceed to store in the file values in the CSI Key Vault for further reference

| Cert/Key File Pair            | First Execution          | CSI Key Vault Secret for reference                                                            |
|-------------------------------|--------------------------|-----------------------------------------------------------------------------------------------|
| Subscription Ingress          | Obtain from DigiOps team | `subscription-ingress-TLS-KEY` <br/> `subscription-ingress-TLS-CERTIFICATE`                   |
| ControlPlane Internal Ingress | Obtain from SecOps team  | `controlplane-internal-ingress-TLS-KEY` <br/> `controlplane-internal-ingress-TLS-CERTIFICATE` |

### Routing Cluster

```bash
APIM_NAMESPACE=xxxxxxxxxxxxxxxxx
ROUTING_LOADBALANCER_IP=xxxxxxxxxxxxxxxxx
LOADBALANCER_IP_RG=xxxxxxxxxxxxxxxxx
LOADBALANCER_SUBNET_NAME=xxxxxxxxxxxxxxxxx
DP_SYSTEM_NAMESPACE=xxxxxxxxxxxxxxxxx
SYSTEM_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
SYSTEM_CSI_KEY_VAULT_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
APIM_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
APIM_CSI_KEY_VAULT_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
DNS01_CHALLENGE_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
AKS_READONLY_AD_GROUP_ID=xxxxxxxxxxxxxxxxx
ENV=xxxxx
EOF
````

### Data Plane Cluster

```bash
APIM_NAMESPACE=xxxxxxxxxxxxxxxxx
DP_SYSTEM_NAMESPACE=xxxxxxxxxxxxxxxxx
SYSTEM_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
SYSTEM_CSI_KEY_VAULT_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
APIM_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
APIM_CSI_KEY_VAULT_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
DNS01_CHALLENGE_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
AKS_READONLY_AD_GROUP_ID=xxxxxxxxxxxxxxxxx
ENV=xxxxx
EOF
```

### Workspace Cluster

```bash
APIM_NAMESPACE=xxxxxxxxxxxxxxxxx
DP_SYSTEM_NAMESPACE=xxxxxxxxxxxxxxxxx
WORKSPACE_INGRESS_NAMESPACE=xxxxxxxxxxxxxxxxx
WORKSPACE_INGRESS_LOADBALANCER_IP=xxxxxxxxxxxxxxxxx
LOADBALANCER_IP_RG=xxxxxxxxxxxxxxxxx
LOADBALANCER_SUBNET_NAME=xxxxxxxxxxxxxxxxx
SYSTEM_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
SYSTEM_CSI_KEY_VAULT_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
APIM_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
APIM_CSI_KEY_VAULT_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
DNS01_CHALLENGE_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
AKS_READONLY_AD_GROUP_ID=xxxxxxxxxxxxxxxxx
ENV=xxxxx
EOF
```

Run the script by adding the cluster-prefix cp | rt | dp | ws
```bash
 bash <cluster-prefix>-setup.sh -d=azure-deploy.properties
```

To Obtain Let's Encrypt Certs [follow](Lets-encrypt-certs)
