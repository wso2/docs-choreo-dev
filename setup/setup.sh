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

if [[ (( -z "${tlskey}" ) && ( ! -z "${tlscert}" )) || (( ! -z "${tlskey}" ) && ( -z "${tlscert}" )) ]]; then
    printusage
fi

outdir=out
mkdir -p $outdir

############## Initialize Kubernetes Cluster
source common/k8s-cluster-init.sh

if [[ "$create_ingress" == "true" ]]; then
    ############## Install nginx ingress (Optional)
    echo "--- Installing nginx ingress..."
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/nginx-0.30.0/deploy/static/mandatory.yaml
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/nginx-0.30.0/deploy/static/provider/cloud-generic.yaml
    read -p "Are you using Minikube? [y/N] " response
    echo    # (optional) move to a new line
    if [[ ${response} =~ ^[Yy]$ ]]; then
      echo "--- Enabling nginx ingress addon for Minikube..."
      minikube addons enable ingress
    fi
fi

########## Create sealed ingress TLS secret
for env in "${environments[@]}"; do
    echo "--- Creating sealed ingress TLS secret..."
    ./certsecretgen.sh -n=${env}-choreo-system --tls-key=${tlskey} --tls-cert=${tlscert} --secret-name="ingress-cert" \
                        -o="../kustomize/$env/secret/"
    echo "Sealed secret ingress cert generated and copied to "${env}"/secret"
done

########### Create choreo sealed secrets for all environments
for env in "${environments[@]}"; do
    echo "--- Creating Choreo sealed secrets for for ${env} environment..."
    mkdir -p ${outdir}/${env}
    ./secretgen.sh -d=${secretdir} -n=${env}-choreo-system -o=../kustomize/${env}/secret
done

########## Azure docker registry loging
echo "--- Logging in to Azure Container Registry..."
#az login #TODO: This has to be done before running the setup
az acr login --subscription "wso2choreo-payasyouGo" --name choreoctrlplane

########### Cleanup
rm -rf ${outdir}
successful="true"
if [[ "${k8s_install_successful}" == "false" ]]; then
    echo "[FAILED] Kubernetes initialization."
    successful=false
fi
if [[ "${successful}" == "true" ]]; then
    echo "Choreo control plane successfully installed"
fi