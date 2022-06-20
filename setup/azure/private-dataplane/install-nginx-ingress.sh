#!/usr/bin/env bash

echo "--- Setting up Routing Nginx Ingress Controller.."
echo "--- Creating namespace ${ENV}-choreo-apim-nginx-ingress..."
kubectl create namespace "${ENV}-choreo-apim-nginx-ingress" --dry-run=client -o yaml | kubectl apply -f -

# Add label to Nginx ingress namespace
kubectl label namespace "${ENV}-choreo-apim-nginx-ingress" purpose="${ENV}-choreo-apim-ingress-traffic"

# Annotate Nginx ingress namespace for linker mTLS
kubectl annotate namespace "${ENV}-choreo-apim-nginx-ingress" linkerd.io/inject=enabled
kubectl annotate namespace "${ENV}-choreo-apim-nginx-ingress" config.linkerd.io/skip-inbound-ports=443

kubectl apply -f netpol/"${ENV}-choreo-apim-nginx-ingress-ns.yaml"

LOADBALANCER_IP_RG=$(az group show --name choreo-"${CUSTOMER_NAME}"-dataplane-"${ENV}"-network-rg --query "name" --output tsv)

echo "LB IP RG is: ${LOADBALANCER_IP_RG}"

LOADBALANCER_VNET_NAME=$(az network vnet show --name choreo-"${CUSTOMER_NAME}"-dataplane-"${ENV}"-virtual-network --resource-group "${LOADBALANCER_IP_RG}" --query "name" --output tsv)

echo "LB VNET NAME is: ${LOADBALANCER_VNET_NAME}"

LOADBALANCER_SUBNET_NAME=$(az network vnet subnet show --resource-group "${LOADBALANCER_IP_RG}" --vnet-name "${LOADBALANCER_VNET_NAME}" --name choreo-"${CUSTOMER_NAME}"-dp-"${ENV}"-loadbalancer-subnet --query "name" --output tsv)

echo "LB SUBNET NAME is: ${LOADBALANCER_SUBNET_NAME}"

az config set extension.use_dynamic_install=yes_without_prompt

if [[ "${CHOREO_ENV}" == "dev" ]]; then
	    CUSTOMDNS_LOADBALANCER_IP=$(az network firewall policy rule-collection-group collection list --policy-name choreo-dev-hub-fw-policy --resource-group choreo-dev-network-rg --rcg-name 'private-dp-dnat-collection-group' --query "[1].rules[?contains(name, 'public-ip-customdns-${ENV}-http')].translatedAddress" --output tsv)
	
    elif [[ "${CHOREO_ENV}" == "stage" ]]; then
	    #todo; change this to same as dev once the Firewall has been upgraded to premium in Stage
	    CUSTOMDNS_LOADBALANCER_IP=$(az network firewall nat-rule collection show --firewall-name choreo-stg-dp-fw --resource-group choreo-stg-hub-network-rg --collection-name choreo-stg-dnat-rule-collection-http --query "rules[?contains(name, 'public-ip-customdns-${ENV}-http')].translatedAddress" --output tsv)
    else
	    CUSTOMDNS_LOADBALANCER_IP=$(az network firewall nat-rule collection show --firewall-name choreo-"${CUSTOMER_NAME}"-dp-fw --resource-group choreo-"${CUSTOMER_NAME}"-hub-network-rg --collection-name choreo-"${CUSTOMER_NAME}"-dnat-rule-collection-http --query "rules[?contains(name, 'public-ip-customdns-${ENV}-http')].translatedAddress" --output tsv)
fi

echo "CUSTOMDNS LB IP is: ${CUSTOMDNS_LOADBALANCER_IP}"

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update

helm upgrade --install "${ENV}-choreo-apim" ingress-nginx/ingress-nginx \
  --namespace "${ENV}-choreo-apim-nginx-ingress" \
  --set controller.replicaCount=1 \
  --set controller.service.loadBalancerIP="${CUSTOMDNS_LOADBALANCER_IP}"\
  --set rbac.create=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.resources.requests."memory"=500Mi \
  --set controller.resources.requests."cpu"=500m \
  --set controller.resources.limits."cpu"=1000m \
  --set controller.resources.limits."memory"=1Gi \
  --set controller.ingressClass="${ENV}-choreo-apim-nginx" \
  --set controller.image.digest=null \
  --set-string controller.config.server-tokens=false \
  --set controller.admissionWebhooks.enabled=false \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group=${LOADBALANCER_IP_RG}" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal=true" \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-internal-subnet=${LOADBALANCER_SUBNET_NAME}"

