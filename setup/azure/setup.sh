#!/usr/bin/env bash

helpFunction()
{
    echo "Usage: $0 -d azure-deploy.properties"; \
    echo "   -d=azuredfile  - azure deployment properties file"; \
    echo; \
    echo "   e.g. $0 -d=azure-deploy.properties"; \
    exit 1;
}

############## Loop through arguments and process them
for arg in "$@"
do
    case $arg in
        -d=*|--azuredfile=*)
        azuredfile="${arg#*=}"
        shift
        ;;
        *)
        OTHER_ARGUMENTS+=("$1")
        shift # Remove generic argument from processing
        ;;
    esac
done

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

############## Install Reloader
echo "--- Installing Reloader..."
if [[ -f "../reloader.yaml" ]]; then
    kubectl apply -n kube-system -f ../reloader.yaml
else
    kubectl apply -n kube-system -f reloader.yaml
fi

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
  --version v1.2.0 \
  -n cert-manager \
  --set installCRDs=true

echo "-- Creating secrets for DNS-01 challenge"
kubectl create secret generic choreo-secret-azuredns-config clientsecret="${DNS01_CHALLENGE_CLIENT_SECRET}" -n cert-manager

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

echo "--- Installing linkerd viz extension... "
helm install linkerd-viz linkerd/linkerd-viz

################ Install emberstack refrector ########
helm repo add emberstack https://emberstack.github.io/helm-charts
helm repo update
helm upgrade --install reflector emberstack/reflector --namespace kube-system --version 5.4.17

echo "--- Creating AKS view cluster role binding to AAD"
kubectl apply -f conf/view-cluster-role-binding.yaml

echo "--- Add OMS Agent Config"
kubectl apply -f oms/container-azm-ms-agentconfig.yaml

bash configure-csi-secret-store.sh
bash install-nginx-ingress.sh

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

successful="true"
# shellcheck disable=SC2154
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


