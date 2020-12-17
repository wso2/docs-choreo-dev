#!/usr/bin/env bash
echo "----------------------------------------------"
echo "| Choreo Control Plane setup on Kubernetes   |"
echo "----------------------------------------------"

function printusage {
    echo "Usage: $0 -d=secretdir [-e=environments] [-i=true/false] [--tls-key=key] [--tls-cert=cert]"; \
    echo "   -d=secretdir    - directory containing secret properties files";
    echo "   --tls-key=key   - TLS private key";
    echo "   --tls-cert=cert - TLS certificate";
    echo "   -e=environments - comma separated environment list";
    echo "   -i=true/false   - create ingress";
    echo;
    echo "   e.g. $0 -d=secret -e=prod,stage,dev -i=false";
    exit 1;
}

propfile="choreo-secrets.properties"
create_ingress="true"
declare -a environments=("dev")
[[ $# -eq 0 ]] &&
{
    printusage
}

for arg in "$@"
do
    case $arg in
        -d=*|--secretdir=*)
        secretdir="${arg#*=}"
        shift
        ;;
        -k=*|--tls-key=*)
        tlskey="${arg#*=}"
        shift
        ;;
        -c=*|--tls-cert=*)
        tlscert="${arg#*=}"
        shift
        ;;
        -p=*|--propfile=*)
	# shellcheck disable=SC2034
        propfile="${arg#*=}"
        shift
        ;;
        -e=*|--environments=*)
        IFS=',' read -r -a environments <<< "${arg#*=}"
        shift
        ;;
        -i=*|--ingress=*)
        create_ingress="${arg#*=}"
        shift
        ;;
        *)
        OTHER_ARGUMENTS+=("$1")
        shift
        ;;
    esac
done

if [[ (( -z "${tlskey}" ) && ( -n "${tlscert}" )) || (( -n "${tlskey}" ) && ( -z "${tlscert}" )) ]]; then
    printusage
fi

outdir=out
mkdir -p $outdir

############## Initialize Kubernetes Cluster
# shellcheck disable=SC1091
source common/k8s-cluster-init.sh

########### Install Linkerd #############
linkerd install | kubectl apply -f -

if [[ "$create_ingress" == "true" ]]; then
    ############## Install nginx ingress (Optional)
    echo "--- Installing nginx ingress..."
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v0.41.2/deploy/static/provider/cloud/deploy.yaml
    read -r -p "Are you using Minikube? [y/N] " response
    echo    # (optional) move to a new line
    if [[ ${response} =~ ^[Yy]$ ]]; then
      echo "--- Enabling nginx ingress addon for Minikube..."
      minikube addons enable ingress
    fi
    kubectl annotate namespace ingress-nginx linkerd.io/inject=enabled
    kubectl rollout restart deployment/ingress-nginx-controller -n ingress-nginx # to enable linkerd proxies for the ingress controller
fi

########## Create sealed ingress TLS secret
for env in "${environments[@]}"; do
    echo "--- Creating sealed ingress TLS secret..."
    ./certsecretgen.sh -n="${env}-choreo-system" --tls-key="${tlskey}" --tls-cert="${tlscert}" --secret-name="ingress-cert" \
                        -o="../kustomize/$env/choreo-system/secret/"
    echo "Sealed secret ingress cert generated and copied to ${env}/choreo-system/secret"
done

########### Create choreo sealed secrets for all environments
for env in "${environments[@]}"; do
    echo "--- Creating Choreo sealed secrets for for ${env} environment..."
    mkdir -p "${outdir}/${env}"
    ./secretgen.sh -d="${secretdir}" -n="${env}-choreo-system" -o="../kustomize/${env}/choreo-system/secret"
done

########### Cleanup
rm -rf ${outdir}
successful="true"
# shellcheck disable=SC2154
if [[ "${k8s_install_successful}" == "false" ]]; then
    echo "[FAILED] Kubernetes initialization."
    successful=false
fi
if [[ "${successful}" == "true" ]]; then
    echo "Choreo control plane successfully installed"
fi
