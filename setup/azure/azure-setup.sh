#!/usr/bin/env bash

[[ $# -eq 0 ]] &&
{ echo "Usage: $0 -d azure-deploy.properties [-n namespace]"; \
echo "   -d=azuredfile  - azure deployment properties file"; \
echo "   -n=namespace  - namespace for which sealed secrets are generated"; \
echo; \
echo "   e.g. $0 -d=azure-deploy.properties -n=dev-choreo-system "; \
exit 1; }

azuredfile=$1
namespace=$2

# Loop through arguments and process them
for arg in "$@"
do
    case $arg in
        -d=*|--azuredfile=*)
        azuredfile="${arg#*=}"
        shift
        ;;
        -n=*|--namespace=*)
        namespace="${arg#*=}"
        shift
        ;;
        *)
        OTHER_ARGUMENTS+=("$1")
        shift # Remove generic argument from processing
        ;;
    esac
done
[[ -z "${namespace}" ]] && { namespace="default"; }

echo "--- Setting Properties values as environmental variables"
if [[ -r ${azuredfile} ]]
then
    while IFS= read -r line
    do
         k=$(cut -d "=" -f1 <<< $line)
         v=$(cut -d "=" -f2 <<< "$line")
         export_command="export $k=$v"
         eval ${export_command}
    done < "${azuredfile}"
else
    echo "File "${azuredfile}" not found"; exit 1
fi
export NAMESPACE=${namespace}

############### Install Helm 3
echo "--- Installing Helm 3..."
helm3_installed="true"
command -v helm >/dev/null 2>&1 || {
    helm3_installed="false"
    if [[ "$OSTYPE" == "linux-gnu" ]]; then
        curl https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 | bash
        helm3_installed="true"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        brew install helm
        helm3_installed="true"
    else
        echo "Could not install helm3. Unsupported operating system. Please manually install it.."
    fi
}

############### Install Nginx Ingress Controller using Helm 3
echo "--- Creating namespace ${namespace}-nginx-ingress..."
kubectl create namespace "${namespace}-nginx-ingress" --dry-run -o yaml | kubectl apply -f -

helm repo add stable https://kubernetes-charts.storage.googleapis.com/

echo "--- Installing nginx ingress using Helm 3..."
helm upgrade --install nginx-ingress-controller stable/nginx-ingress \
    --namespace "${namespace}-nginx-ingress" \
    --set controller.replicaCount=2 \
    --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-dns-label-name"="${namespace}-nginx-ingress" \
    --set controller.service.loadBalancerIP="${LOADBALANCER_IP}" \
    --set rbac.create=true \
    --set controller.service.externalTrafficPolicy=Local \
    --set controller.resources.requests."memory"=500Mi \
    --set controller.resources.requests."cpu"=500m \
    --set controller.resources.limits."memory"=1000Mi \
    --set controller.resources.limits."cpu"=1000m \
    --set controller.ingressClass="${namespace}-nginx"

############### Install Certmanager CRDS
echo "--- Installing Certmanager CRDS"
kubectl apply -f https://github.com/jetstack/cert-manager/releases/download/v0.10.0/cert-manager.yaml

############## Install Cluster Issuer
## Create azure dns contributor client secret
echo "--- Creating azure dns contributor client secret"
kubectl create secret generic "${namespace}-secret-azuredns-config" --from-literal=client-secret="$SERVICE_PRINCIPLE_CLIENT_SECRET" -n cert-manager --dry-run -oyaml | kubectl apply -f -

## Install Cluster Issuer
envsubst < conf/cluster-issuer.yaml  | kubectl apply -n cert-manager  -f -

## Create Namespace
kubectl create namespace ${namespace} --dry-run -oyaml | kubectl apply -f -
#
echo "--- Requesting wildcard cert for ${WILDCARD_DOMAIN}"
envsubst < conf/wildcard-cert.yaml | kubectl apply -n ${namespace} -f -

echo "--- Creating AKS view cluster role binding to AAD"
kubectl apply -f conf/view-cluster-role-binding.yaml

############ Cleanup
echo "--- Unsetting Properties values set as environmental variables"
if [[ -r ${azuredfile} ]]
then
    while IFS= read -r line
    do
         k=$(cut -d "=" -f1 <<< $line)
         unset_command="unset $k"
         eval ${unset_command}
    done < "${azuredfile}"
else
    echo "File "${azuredfile}" not found"; exit 1
fi
unset NAMESPACE
