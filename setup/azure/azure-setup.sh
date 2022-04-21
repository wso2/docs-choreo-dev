#!/usr/bin/env bash

[[ $# -eq 0 ]] &&
{
    echo "Usage: $0 -d azure-deploy.properties [-n namespace]"; \
    echo "   -d=azuredfile  - azure deployment properties file"; \
    echo "   -n=namespace  - namespace for which the deployment is done"; \
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

helm install \
  cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --version v1.8.0 \
  -n cert-manager \
  --set installCRDs=true \
  --set replicaCount=2 \
  --set webhook.replicaCount=2 \
  --set cainjector.replicaCount=2

############### Install Linkerd2 using Helm 3
echo "-- Creating namespace linkerd"
kubectl create namespace linkerd

echo "-- Creating secrets for linkerd"

step certificate create root.linkerd.cluster.local /tmp/ca.crt /tmp/ca.key \
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
  --set-file identityTrustAnchorsPEM=/tmp/ca.crt \
  linkerd/linkerd2 \
  -f linkerd2/values.yaml -f linkerd2/ha-values.yaml \
  --set identity.issuer.scheme=kubernetes.io/tls \
  --set installNamespace=false --set linkerdVersion=stable-2.10.0 \
  -n linkerd --version 2.10.0

## TODO: Migrate to Buoyant Cloud for Linkerd Monitoring
echo "--- Installing linkerd viz extension... "
helm install linkerd-viz linkerd/linkerd-viz

############### Install Nginx Ingress Controller using Helm 3
echo "--- Creating namespace ${namespace}-nginx-ingress..."
kubectl create namespace "${namespace}-nginx-ingress" --dry-run=client -o yaml | kubectl apply -f -

# Add label to Nginx ingress namespace
kubectl label namespace "${namespace}-nginx-ingress" purpose="${namespace}-ingress-traffic"

# Annotate Nginx ingress namespace for linker mTLS
kubectl annotate namespace "${namespace}-nginx-ingress" linkerd.io/inject=enabled
kubectl annotate namespace "${namespace}-nginx-ingress" config.linkerd.io/skip-inbound-ports=443

kubectl apply -f ./netpol/"${namespace}-nginx-ingress-ns.yaml"

helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx

helm repo update

echo "--- Installing nginx ingress using Helm 3..."
# shellcheck disable=SC2140
helm upgrade --install prod-choreo-system ingress-nginx/ingress-nginx \
  --namespace "${namespace}-nginx-ingress" \
  --version 3.8.0 \
  --set controller.replicaCount=2 \
  --set controller.service.loadBalancerIP="${LOADBALANCER_IP}"\
  --set rbac.create=true \
  --set controller.service.externalTrafficPolicy=Local \
  --set controller.resources.requests."memory"=500Mi \
  --set controller.resources.requests."cpu"=500m \
  --set controller.resources.limits."cpu"=1000m \
  --set controller.ingressClass="${namespace}-nginx" \
  --set controller.image.repository="choreocontrolplane.azurecr.io/kubernetes-ingress-controller/nginx-ingress-controller" \
  --set controller.image.tag="v0.41.2" \
  --set controller.image.digest=null \
  --set-string controller.config.server-tokens=false \
  --set controller.admissionWebhooks.enabled=false \
  --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group"="${LOADBALANCER_IP_RG}"


################ Install emberstack refrector ########
helm repo add emberstack https://emberstack.github.io/helm-charts
helm repo update
helm upgrade --install reflector emberstack/reflector --namespace kube-system --version 5.4.17

################ Install CSI Secret Store Driver ########
echo "--- Creating namespace csi-secret-store-driver..."
kubectl create namespace csi-secret-store-driver --dry-run=client -o yaml | kubectl apply -f -

helm repo add csi-secrets-store-provider-azure https://raw.githubusercontent.com/Azure/secrets-store-csi-driver-provider-azure/master/charts
helm repo update
helm upgrade --install csi-secrets-store-provider-azure csi-secrets-store-provider-azure/csi-secrets-store-provider-azure --namespace csi-secret-store-driver --version 0.0.16 --set secrets-store-csi-driver.linux.driver.resources.limits.memory=400Mi --set secrets-store-csi-driver.linux.driver.resources.requests.memory=200Mi

################ Install CSI Secret Store Class Secret ########
# This secret has to be created in other namespaces as well if CSI driver is going to be used
kubectl create secret generic csi-secret-store-azure --from-literal clientid="${CSI_KEY_VAULT_CLIENT_ID}" --from-literal clientsecret="${CSI_KEY_VAULT_CLIENT_SECRET}" -n "${NAMESPACE}"

echo "--- Creating AKS view cluster role binding to AAD"
kubectl apply -f conf/view-cluster-role-binding.yaml

echo "--- Add OMS Agent Config"
kubectl apply -f oms/container-azm-ms-agentconfig.yaml

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
