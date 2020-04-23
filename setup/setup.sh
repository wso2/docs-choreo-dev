#!/usr/bin/env bash
echo "----------------------------------------------"
echo "| Choreo Control Plane setup on Kubernetes   |"
echo "----------------------------------------------"

propfile="choreo-secrets.properties"
create_ingress="true"
declare -a environments=("dev")
[[ $# -eq 0 ]] &&
{
    echo "Usage: $0 -d=secretdir [-e=environments] [-i=true/false]"; \
    echo "   -d=secretdir    - directory containing secret properties files";
    echo "   -e=environments - comma separated environment list";
    echo "   -i=true/false   - create ingress";
    echo;
    echo "   e.g. $0 -d=secret -e=prod,stage,dev -i=false";
    exit 1;
}

for arg in "$@"
do
    case $arg in
        -d=*|--secretdir=*)
        secretdir="${arg#*=}"
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

outdir=out
mkdir -p $outdir

############## Initialize Kubernetes Cluster
source common/k8s-cluster-init.sh

if [[ "$create_ingress" == "true" ]]; then
    ############## Install nginx ingress (Optional)
    echo "--- Installing nginx ingress..."
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/nginx-0.30.0/deploy/static/mandatory.yaml
    read -p "Are you using Docker Desktop? [y/N] " response
    echo    # (optional) move to a new line
    if [[ ${response} =~ ^[Yy]$ ]]; then
      echo "--- Installing nginx ingress for Docker Desktop..."
      kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/nginx-0.30.0/deploy/static/provider/cloud-generic.yaml
    else
        read -p "Are you using Minikube? [y/N] " response
        echo    # (optional) move to a new line
        if [[ ${response} =~ ^[Yy]$ ]]; then
          echo "--- Enabling nginx ingress addon for Minikube..."
          minikube addons enable ingress
        fi
    fi

    ## TODO: If self signed, do the following, else use specified tls.crt and tls.key
    for env in "${environments[@]}"; do
        ./certsecretgen.sh -n=${env}-choreo-system --self-signed=true  --secret-name="ingress-cert" \
                            -o="../kustomize/$env/secret/"
        echo "Sealed secret ingress cert generated and copied to "$env"/secret"
    done
fi

########### Create choreo sealed secrets for all environments
for env in "${environments[@]}"; do
    echo "--- Creating Choreo sealed secrets for for ${env} environment..."
    mkdir -p ${outdir}/${env}
    ./secretgen.sh -d=${secretdir} -n=${env}-choreo-system -o=../kustomize/${env}/secret
done

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

