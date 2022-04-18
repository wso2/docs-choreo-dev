#!/usr/bin/env bash

############### Install Workspace Nginx Plus Ingress Controller using Helm 3
echo "--- Setting up Workspace Nginx Ingress Controller.."
echo "--- Creating namespace ${WORKSPACE_INGRESS_NAMESPACE}-nginx-plus..."
kubectl create namespace "${WORKSPACE_INGRESS_NAMESPACE}-nginx-plus" --dry-run=client -o yaml | kubectl apply -f -

# Add label to Nginx ingress namespace
kubectl label namespace "${WORKSPACE_INGRESS_NAMESPACE}-nginx-plus" purpose="${WORKSPACE_INGRESS_NAMESPACE}-ingress-traffic"

#kubectl annotate namespace "${WORKSPACE_INGRESS_NAMESPACE}" linkerd.io/inject=enabled
#kubectl annotate namespace "${WORKSPACE_INGRESS_NAMESPACE}" config.linkerd.io/skip-inbound-ports=443

helm repo add nginx-stable https://helm.nginx.com/stable
helm repo update

echo "--- Installing Workspace Nginx Ingress using Helm 3..."
# shellcheck disable=SC2140

openid_connect_configuration_path="workspace/nginx-openid-connect-resources/openid_connect_configuration.conf"
openid_connect_path="workspace/nginx-openid-connect-resources/openid_connect.js"
openid_connect_server_path="workspace/nginx-openid-connect-resources/openid_connect.server_conf"
zone_sync_path="workspace/nginx-openid-connect-resources/zone_sync.conf"

echo "--- Updating OpenID configuration files..."

sed -i "s/NGINX_OIDC_AUTH_ENDPOINT/${NGINX_OIDC_AUTH_ENDPOINT}/g" $openid_connect_configuration_path
sed -i "s/NGINX_OIDC_TOKEN_ENDPOINT/${NGINX_OIDC_TOKEN_ENDPOINT}/g" $openid_connect_configuration_path
sed -i "s/NGINX_OIDC_JWKS_ENDPOINT/${NGINX_OIDC_JWKS_ENDPOINT}/g" $openid_connect_configuration_path
sed -i "s/NGINX_OIDC_CLIENT_ID/${NGINX_OIDC_CLIENT_ID}/g" $openid_connect_configuration_path
sed -i "s/NGINX_OIDC_CLIENT_SECRET/${NGINX_OIDC_CLIENT_SECRET}/g" $openid_connect_configuration_path
sed -i "s/NGINX_OIDC_HMAC/${NGINX_OIDC_HMAC}/g" $openid_connect_configuration_path
sed -i "s/NGINX_OIDC_REDIRECT_ENDPOINT/${NGINX_OIDC_REDIRECT_ENDPOINT}/g" $openid_connect_configuration_path
sed -i "s/NGINX_OIDC_SCOPES/${NGINX_OIDC_SCOPES}/g" $openid_connect_configuration_path

sed -i "s/NGINX_OIDC_HEADLESS_SERVICE_NAMESPACE/${WORKSPACE_INGRESS_NAMESPACE}-nginx-plus/g" $zone_sync_path

echo "--- Creating OpenID ConfigMap..."

kubectl create configmap openid-connect-configmap --from-file=$openid_connect_configuration_path --from-file=$openid_connect_path --from-file=$openid_connect_server_path -n "${WORKSPACE_INGRESS_NAMESPACE}-nginx-plus"

echo "--- Creating Zone-Sync ConfigMap..."

kubectl create configmap zone-sync-configmap --from-file=$zone_sync_path -n "${WORKSPACE_INGRESS_NAMESPACE}-nginx-plus"

helm upgrade --install "${WORKSPACE_INGRESS_NAMESPACE}" nginx-stable/nginx-ingress \
  --version 0.11.3 \
  --namespace "${WORKSPACE_INGRESS_NAMESPACE}-nginx-plus" \
  --set controller.image.repository="choreocontrolplane.azurecr.io/nginx-plus-ingress-openid-connect" \
  --set controller.replicaCount=2 \
  --set controller.image.tag="2" \
  --set controller.nginxplus=true \
  --set controller.service.loadBalancerIP="${WORKSPACE_INGRESS_LOADBALANCER_IP}" \
  --set rbac.create=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.volumeMounts[0].name="openid-connect-configuration-volume-mount" \
  --set controller.volumeMounts[1].name="openid-connect-configuration-volume-mount" \
  --set controller.volumeMounts[2].name="openid-connect-configuration-volume-mount" \
  --set controller.volumeMounts[0].mountPath="/etc/nginx/conf.d/openid_connect_configuration.conf" \
  --set controller.volumeMounts[1].mountPath="/etc/nginx/conf.d/openid_connect.js" \
  --set controller.volumeMounts[2].mountPath="/etc/nginx/conf.d/openid_connect.server_conf" \
  --set controller.volumeMounts[0].subPath="openid_connect_configuration.conf" \
  --set controller.volumeMounts[1].subPath="openid_connect.js" \
  --set controller.volumeMounts[2].subPath="openid_connect.server_conf" \
  --set controller.volumes[0].name="openid-connect-configuration-volume-mount" \
  --set controller.volumes[0].configMap.name="openid-connect-configmap" \
  --set controller.volumeMounts[3].name="zone-sync-volume-mount" \
  --set controller.volumeMounts[3].mountPath="/etc/nginx/stream-conf.d/zone_sync.conf" \
  --set controller.volumeMounts[3].subPath="zone_sync.conf" \
  --set controller.volumes[1].name="zone-sync-volume-mount" \
  --set controller.volumes[1].configMap.name="zone-sync-configmap" \
  --set controller.config.entries.main-snippets="load_module modules/ngx_http_js_module.so;" \
  --set controller.config.name="nginx-config" \
  --set controller.resources.requests."memory"=500Mi \
  --set controller.resources.requests."cpu"=500m \
  --set controller.resources.limits."cpu"=1000m \
  --set controller.resources.limits."memory"=1Gi \
  --set controller.ingressClass="choreo-workspace-ingress-nginx-plus" \
  --set controller.enableSnippets=true \
  --set controller.wildcardTLS.secret="cert-manager/${WORKSPACE_INGRESS_NAMESPACE}-wildcard-tls" \
  --set-string controller.config.server-tokens=false \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal=\"true\"" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group=${LOADBALANCER_IP_RG}" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal-subnet=${LOADBALANCER_SUBNET_NAME}"

echo "--- Creating Nginx Ingress Headless Service..."

kubectl expose deployment "${WORKSPACE_INGRESS_NAMESPACE}-nginx-ingress" -n "${WORKSPACE_INGRESS_NAMESPACE}-nginx-plus" --port=10113 --target-port=10113 --name=nginx-ingress-headless --cluster-ip=None