## Configuring Nginx Ingress Controllers

### Description
Configure the following Nginx Ingress Controllers used in Choreo workspace cluster
1. NIC used by codeservers

### Prerequisites
1) Helm 3 installed in execution environment 

### Usage
1) Initially ssh into relevant bastion and set the cluster context to workspace cluster

2) Export following environmental variables
    ```bash
    export WORKSPACE_INGRESS_NAMESPACE="xxxxxxxxxxxxxxxxx" #ex:- <env>-choreo-workspace-nginx-plus-ingress
    export WORKSPACE_INGRESS_LOADBALANCER_IP="xxxxxxxxxxxxxxxxx"
    export LOADBALANCER_SUBNET="xxxxxxxxxxxxxxxxx"
    export LOADBALANCER_IP_RG="xxxxxxxxxxxxxxxxx"
    export NGINX_OIDC_AUTH_ENDPOINT="xxxxxxxxxxxxxxxxx"
    export NGINX_OIDC_TOKEN_ENDPOINT="xxxxxxxxxxxxxxxxx"
    export NGINX_OIDC_JWKS_ENDPOINT="xxxxxxxxxxxxxxxxx"
    export NGINX_OIDC_CLIENT_ID="xxxxxxxxxxxxxxxxx"
    export NGINX_OIDC_CLIENT_SECRET="xxxxxxxxxxxxxxxxx"
    export NGINX_OIDC_HMAC="xxxxxxxxxxxxxxxxx"
    export NGINX_OIDC_REDIRECT_ENDPOINT="xxxxxxxxxxxxxxxxx"
    export NGINX_OIDC_SCOPES="xxxxxxxxxxxxxxxxx"
    ```

3) Execute `configure-ingress-controllers.sh` script
    ```bash
    bash configure-ingress-controllers.sh
    ```