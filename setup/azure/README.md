## Sample azure-deploy.properties files for each cluster

### Control Plane Cluster

```bash
CLUSTER_NAME=xxxxxxxxxxxxxxxxx
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
APIM_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
IDP_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
AKS_READONLY_AD_GROUP_ID=xxxxxxxxxxxxxxxxx
ENV=xxxxx
LINKERD_VIZ_LOADBALANCER_IP=xxxxxxxxxxxxxxxxx
LINKERD_VIZ_INGRESS_CLASS=xxxxxxxxxxxxxxxxx
LOADBALANCER_IP_RG=xxxxxxxxxxxxxxxxx
LOADBALANCER_SUBNET=xxxxxxxxxxxxxxxxx
LINKERD_VIZ_DASHBOARD_AUTH_UNAME_PWD=xxxxxxxxxxxxxxxxx
BUOYANT_CLOUD_AGENT_ID=xxxxxxxxxxxxxxxxx
BUOYANT_CLOUD_AGENT_KEY=xxxxxxxxxxxxxxxxx
BUOYANT_CLOUD_AGENT_DOWNLOAD_KEY=xxxxxxxxxxxxxxxxx
BUOYANT_CLOUD_NAME=xxxxxxxxxxxxxxxxx
EOF
```
#### Creating TLS Secrets
For executing the TLS Secret generation script mentioned in `controlplane/create-ingress-tls-secrets.sh`
Provide the following properties in addition to the above-mentioned properties.

``````
SUBSCRIPTION_INGRESS_TLS_CERT_FILE_PATH=xxxxxxxxxxxxxxxxx
SUBSCRIPTION_INGRESS_TLS_KEY_FILE_PATH=xxxxxxxxxxxxxxxxx
CONTROLPLANE_SYSTEM_INTERNAL_INGRESS_TLS_CERT_FILE_PATH=xxxxxxxxxxxxxxxxx
CONTROLPLANE_SYSTEM_INTERNAL_INGRESS_TLS_KEY_FILE_PATH=xxxxxxxxxxxxxxxxx
CONTROLPLANE_APIM_INTERNAL_INGRESS_TLS_CERT_FILE_PATH=xxxxxxxxxxxxxxxxx
CONTROLPLANE_APIM_INTERNAL_INGRESS_TLS_KEY_FILE_PATH=xxxxxxxxxxxxxxxxx
``````
Follow the following chart to obtain the required CERT and KEY files, for first time execution. 
Proceed to store the file values in the CSI Key Vault for further reference

| Cert/Key File Pair                      | First Execution          | CSI Key Vault Secret for reference                                                                      |
|-----------------------------------------|--------------------------|---------------------------------------------------------------------------------------------------------|
| Subscription Ingress                    | Obtain from DigiOps team | `subscription-ingress-TLS-KEY` <br/> `subscription-ingress-TLS-CERTIFICATE`                             |
| ControlPlane System Internal Ingress    | Obtain from SecOps team  | `controlplane-system-internal-ingress-TLS-KEY` <br/> `controlplane-system-internal-ingress-TLS-CERTIFICATE` |
| ControlPlane APIM Internal Ingress      | Obtain from SecOps team  | `controlplane-apim-internal-ingress-TLS-KEY` <br/> `controlplane-apim-internal-ingress-TLS-CERTIFICATE` |
| BCentral APIM Internal Ingress          | Obtain from SecOps team  | `bcentral-apim-internal-ingress-TLS-KEY` <br/> `bcentral-apim-internal-ingress-TLS-CERTIFICATE`         |
| BCentral GlobalAdapter Internal Ingress | Obtain from SecOps team  | `bcentral-ga-internal-ingress-TLS-KEY` <br/> `bcentral-ga-internal-ingress-TLS-CERTIFICATE`       |

### Buoyant Cloud Setup
> NOTE: Buoyant Cloud setup is required for Stage and Prod environments only

To integrate Buoyant Cloud with Choreo, the following  secrets are used

```
BUOYANT_CLOUD_AGENT_ID=xxxxxxxxxxxxxxxxx
BUOYANT_CLOUD_AGENT_KEY=xxxxxxxxxxxxxxxxx
BUOYANT_CLOUD_AGENT_DOWNLOAD_KEY=xxxxxxxxxxxxxxxxx
BUOYANT_CLOUD_NAME=xxxxxxxxxxxxxxxxx
```

In the initial setup these secrets can be obtained by adding a new Cluster to the `wso2-choreo` project within Buoyant Cloud Console, as specified here https://docs.buoyant.cloud/article/29-adding-a-cluster

Obtain the value for the above-mentioned secrets by inspecting the yaml file that is provided in this setup. For subsequent reruns refer the secrets via Key Vault Secrets
### Routing Cluster

```bash
CLUSTER_NAME=xxxxxxxxxxxxxxxxx
APIM_NAMESPACE=xxxxxxxxxxxxxxxxx
ROUTING_LOADBALANCER_IP=xxxxxxxxxxxxxxxxx
LOADBALANCER_IP_RG=xxxxxxxxxxxxxxxxx
LOADBALANCER_SUBNET_NAME=xxxxxxxxxxxxxxxxx
DP_SYSTEM_NAMESPACE=xxxxxxxxxxxxxxxxx
SYSTEM_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
APIM_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
DNS01_CHALLENGE_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
AKS_READONLY_AD_GROUP_ID=xxxxxxxxxxxxxxxxx
ENV=xxxxx
EOF
````

### Data Plane Cluster

```bash
CLUSTER_NAME=xxxxxxxxxxxxxxxxx
APIM_NAMESPACE=xxxxxxxxxxxxxxxxx
DP_SYSTEM_NAMESPACE=xxxxxxxxxxxxxxxxx
SYSTEM_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
APIM_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
AKS_READONLY_AD_GROUP_ID=xxxxxxxxxxxxxxxxx
ENV=xxxxx
EOF
```

### Private Data Plane Cluster

```bash
CLUSTER_NAME=xxxxxxxxxxxxxxxxx
APIM_NAMESPACE=xxxxxxxxxxxxxxxxx
DP_SYSTEM_NAMESPACE=xxxxxxxxxxxxxxxxx
ROUTING_LOADBALANCER_IP=xxxxxxxxxxxxxxxxx
LOADBALANCER_IP_RG=xxxxxxxxxxxxxxxxx
LOADBALANCER_SUBNET_NAME=xxxxxxxxxxxxxxxxx
USERAPPS_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
DNS01_CHALLENGE_CLIENT_ID=xxxxxxxxxxxxxxxxx
AKS_READONLY_AD_GROUP_ID=xxxxxxxxxxxxxxxxx
DNS_NAME_1=xxxxxxxxxxxxxxxxx
ENV=xxxxx
EOF
```

### Workspace Cluster

```bash
CLUSTER_NAME=xxxxxxxxxxxxxxxxx
DP_SYSTEM_NAMESPACE=xxxxxxxxxxxxxxxxx
WORKSPACE_INGRESS_NAMESPACE=xxxxxxxxxxxxxxxxx
WORKSPACE_INGRESS_LOADBALANCER_IP=xxxxxxxxxxxxxxxxx
LOADBALANCER_IP_RG=xxxxxxxxxxxxxxxxx
LOADBALANCER_SUBNET_NAME=xxxxxxxxxxxxxxxxx
SYSTEM_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
AKS_READONLY_AD_GROUP_ID=xxxxxxxxxxxxxxxxx
NGINX_OIDC_AUTH_ENDPOINT=xxxxxxxxxxxxxxxxx
NGINX_OIDC_TOKEN_ENDPOINT=xxxxxxxxxxxxxxxxx
NGINX_OIDC_JWKS_ENDPOINT=xxxxxxxxxxxxxxxxx
NGINX_OIDC_CLIENT_ID=xxxxxxxxxxxxxxxxx
NGINX_OIDC_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
NGINX_OIDC_HMAC=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
NGINX_OIDC_REDIRECT_ENDPOINT=xxxxxxxxxxxxxxxxx
NGINX_OIDC_SCOPES=xxxxxxxxxxxxxxxxx
ENV=xxxxx
EOF
```

Run the script by adding the cluster-prefix cp | rt | dp | ws
```bash
 bash <cluster-prefix>-setup.sh -d=azure-deploy.properties
```

To Obtain Let's Encrypt Certs [follow](Lets-encrypt-certs)
