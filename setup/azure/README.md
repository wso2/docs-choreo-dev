sample azure-deploy.properties file
```bash
RESOURCE_GROUP_NAME=CHOREO-DNS-RG
SUBSCRIPTION_ID=xxxxxxxxxxxxxxxxxxxx
TENANT_ID=xxxxxxxxxxxxxxxxxx
SERVICE_PRINCIPLE_CLIENT_ID=xxxxxxxxxxxxxxxx
HOSTED_ZONE_NAME=choreo.dev
SERVICE_PRINCIPLE_CLIENT_SECRET=xxxxxxxxxxxxx
WILDCARD_DOMAIN=choreo.dev
LOADBALANCER_IP=xxx.xxx.xxx.xxx
AKS_READONLY_AD_GROUP_ID=xxxxxxxxxxxxxxxxx
EOF
```

Running the script 
```bash
 bash azure-setup.sh -d=azure-deploy.properties -n=prod-choreo-system 
```
