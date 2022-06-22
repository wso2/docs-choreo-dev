## Code Server Nginx Ingress Controller configuration

### Description
Configure the Nginx Ingress Controllers used by codeservers

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

3) Execute `configure-ingress-controller.sh` script
    ```bash
    bash configure-ingress-controller.sh
    ```