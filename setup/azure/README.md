sample azure-deploy.properties file

```bash
LOADBALANCER_SUBNET=xxxxxxxxxxxxxxxxx
LOADBALANCER_IP_RG=xxxxxxxxxxxxxxxxx
AKS_READONLY_AD_GROUP_ID=xxxxxxxxxxxxxxxxx
SYSTEM_NAMESPACE=xxxxxxxxxxxxxxxxx
APIM_NAMESPACE=xxxxxxxxxxxxxxxxx
IDP_NAMESPACE=xxxxxxxxxxxxxxxxx
USERAPPS_NAMESPACE=xxxxxxxxxxxxxxxxx
SYSTEM_LOADBALANCER_IP=xxxxxxxxxxxxxxxxx
USERAPPS_LOADBALANCER_IP=xxxxxxxxxxxxxxxxx
IDP_LOADBALANCER_IP=xxxxxxxxxxxxxxxxx
SYSTEM_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
SYSTEM_CSI_KEY_VAULT_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
APIM_CSI_KEY_VAULT_CLIENT_ID=xxxxxxxxxxxxxxxxx
APIM_CSI_KEY_VAULT_CLIENT_SECRET=xxxxxxxxxxxxxxxxx
EOF
```

Running the script
```bash
 bash setup.sh -d=azure-deploy.properties
```

To Obtain Let's Encrypt Certs [follow](Lets-encrypt-certs)
