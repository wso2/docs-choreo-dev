#!/usr/bin/env bash

[[ $# -eq 0 ]] &&
{
    echo "Usage: $0 -d azure-deploy.properties [-n namespace]"; \
    echo "   -d=azuredfile  - azure deployment properties file"; \
    echo "   -n=namespace  - namespace for which sealed secrets are generated"; \
    echo; \
    echo "   e.g. $0 -d=azure-deploy.properties -n=dev-choreo-system "; \
    exit 1;
}

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
         k=$(cut -d "=" -f1 <<< "$line")
         v=$(cut -d "=" -f2 <<< "$line")
         export_command="export $k=$v"
         eval "${export_command}"
    done < "${azuredfile}"
else
    echo "File ${azuredfile} not found"; exit 1
fi
export NAMESPACE=${namespace}

## Initialize Kubernetes Cluster
# shellcheck disable=SC1091
source ../common/k8s-cluster-init.sh

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

############### Install Step Cli
echo "--- Installing Step to generate Keys"
step_installed="true"
command -v helm >/dev/null 2>&1 || {
    step_installed="false"
    if [[ "$OSTYPE" == "linux-gnu" ]]; then
        wget https://github.com/smallstep/cli/releases/download/v0.14.6/step-cli_0.14.6_amd64.deb -O /tmp/step-cli_0.14.6_amd64.deb
        sudo dpkg -i /tmp/step-cli_0.14.6_amd64.deb
        step_installed="true"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        brew install step
        step_installed="true"
    else
        echo "Could not install helm3. Unsupported operating system. Please manually install it.."
    fi
}

############### Install Certmanager
echo "--- Installing Certmanager"
kubectl create ns cert-manager
kubectl label namespace cert-manager cert-manager.io/disable-validation=true

## Install CRDs
kubectl apply -f https://raw.githubusercontent.com/jetstack/cert-manager/release-0.14/deploy/manifests/00-crds.yaml

## Install certmanager deployment
helm repo add jetstack https://charts.jetstack.io
helm repo update
helm upgrade --install cert-manager --namespace cert-manager --wait jetstack/cert-manager --version v0.14.0

############### Install Linkerd2 using Helm 3
echo "-- Creating namespace linkerd"
kubectl create namespace linkerd
kubectl annotate namespace linkerd config.linkerd.io/admission-webhooks=disabled

echo "-- Creating secrets for linkerd"
step certificate create identity.linkerd.cluster.local /tmp/ca.crt /tmp/ca.key \
  --profile root-ca --no-password --insecure

echo "-- Creating k8s TLS secrets to Automatically rotate control plane TLS using certmanager"
#Automatically Rotating Control Plane TLS Credentials https://linkerd.io/2/tasks/automatically-rotating-control-plane-tls-credentials/
kubectl create secret tls linkerd-trust-anchor --cert=/tmp/ca.crt --key=/tmp/ca.key --namespace=linkerd

kubectl apply -n linkerd -f linkerd2/certmanager/issuer.yaml
kubectl apply -n linkerd -f linkerd2/certmanager/certificate.yaml

echo "--- Installing linkerd2... "
helm repo add linkerd https://helm.linkerd.io/stable
helm repo update
helm upgrade --install linkerd2 --wait \
     --set-file global.identityTrustAnchorsPEM=/tmp/ca.crt \
     linkerd/linkerd2 \
     -f linkerd2/values.yaml -f linkerd2/ha-values.yaml \
     -n linkerd --version 2.8.1

############### Install Nginx Ingress Controller using Helm 3
echo "--- Creating namespace ${namespace}-nginx-ingress..."
kubectl create namespace "${namespace}-nginx-ingress" --dry-run=client -o yaml | kubectl apply -f -

# Annotate Nginx ingress namespace for linker mTLS
kubectl annotate namespace "${namespace}-nginx-ingress" linkerd.io/inject=enabled

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx

helm repo update

echo "--- Installing nginx ingress using Helm 3..."
# shellcheck disable=SC2140
helm upgrade --install nginx-ingress-controller ingress-nginx/ingress-nginx \
    --namespace "${namespace}-nginx-ingress" \
    --version 2.11.3 \
    --set controller.replicaCount=2 \
    --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-dns-label-name"="${namespace}-nginx-ingress" \
    --set controller.service.loadBalancerIP="${LOADBALANCER_IP}" \
    --set rbac.create=true \
    --set controller.service.externalTrafficPolicy=Local \
    --set controller.resources.requests."memory"=500Mi \
    --set controller.resources.requests."cpu"=500m \
    --set controller.resources.limits."memory"=1000Mi \
    --set controller.resources.limits."cpu"=1000m \
    --set controller.ingressClass="${namespace}-nginx" \
    --set controller.image.repository="choreoctrlplane.azurecr.io/kubernetes-ingress-controller/nginx-ingress-controller" \
    --set controller.image.tag="v0.34.1" \
    --set controller.image.digest=null \
    --set-string controller.config.server-tokens=false \
    --set controller.admissionWebhooks.enabled=false

################ Install emberstack refrector ########
helm repo add emberstack https://emberstack.github.io/helm-charts
helm repo update
helm upgrade --install reflector emberstack/reflector --namespace kube-system --version 5.2.11


echo "--- Creating AKS view cluster role binding to AAD"
kubectl apply -f conf/view-cluster-role-binding.yaml

## Install kured for AKS linux node update and restart https://docs.microsoft.com/en-us/azure/aks/node-updates-kured
# Add the stable Helm repository
helm repo add stable https://kubernetes-charts.storage.googleapis.com/

# Update your local Helm chart repository cache
helm repo update

# Create a dedicated namespace where you would like to deploy kured into
kubectl create namespace kured

# Install kured in that namespace with Helm 3 (only on Linux nodes, kured is not working on Windows nodes)
helm upgrade --install kured stable/kured --namespace kured \
    --set nodeSelector."beta\.kubernetes\.io/os"=linux \
    --set extraArgs.start-time=4am \
    --set extraArgs.end-time=10am \
    --set extraArgs.reboot-days="tue" \
    --set extraArgs.slack-hook-url="https://hooks.slack.com/services/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" \
    --set extraArgs.slack-username="kured" \
    --set image.repository="choreoctrlplane.azurecr.io/weaveworks/kured" \
    --set image.tag="1.3.0"

############ Cleanup
echo "--- Unsetting Properties values set as environmental variables"
if [[ -r ${azuredfile} ]]
then
    while IFS= read -r line
    do
         k=$(cut -d "=" -f1 <<< "$line")
         unset_command="unset $k"
         eval "${unset_command}"
    done < "${azuredfile}"
else
    echo "File ${azuredfile} not found"; exit 1
fi
unset NAMESPACE

successful="true"
# shellcheck disable=SC2154
if [[ "${k8s_install_successful}" == "false" ]]; then
    echo "[FAILED] Kubernetes initialization."
    successful=false
fi
if [[ "${successful}" == "true" ]]; then
    echo "Choreo control plane successfully installed"
fi
if [[ "${helm3_installed}" == "false" ]]; then
    echo "[FAILED] helm3 installation. See https://helm.sh/docs/intro/install/"
    helm3_installed=false
fi
if [[ "${step_installed}" == "false" ]]; then
    echo "[FAILED] step cli installation. See https://smallstep.com/docs/getting-started/#1-installing-step-and-step-ca"
    step_installed=false
fi
