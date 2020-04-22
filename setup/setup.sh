#!/usr/bin/env bash
echo "----------------------------------------------"
echo "| Choreo Control Plane setup on Kubernetes   |"
echo "----------------------------------------------"

## TODO: pass environments

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
mkdir $outdir

############## Install Reloader
echo "--- Installing Reloader..."
kubectl apply -n kube-system -f reloader.yaml

############## Install Linkerd
echo "--- Installing Linkerd..."
linkerd_installed="true"
command -v linkerd >/dev/null 2>&1 || {
    linkerd_installed="false"
    if [[ "$OSTYPE" == "linux-gnu" ]]; then
        curl -sL https://run.linkerd.io/install | sh
        linkerd_installed="true"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        brew install linkerd
        linkerd_installed="true"
    else
        echo "Could not install linkerd. Unsupported operating system. Please manually install it.."
    fi
}
linkerd install | kubectl apply -f -

############## Install Sealed secret support
echo "--- Installing kubeseal & Bitnami sealed secrets..."
kubeseal_installed="true"
command -v kubeseal >/dev/null 2>&1 || {
    kubeseal_installed="false"
    if [[ "$OSTYPE" == "linux-gnu" ]]; then
        wget https://github.com/bitnami-labs/sealed-secrets/releases/download/v0.12.1/kubeseal-linux-amd64 -O kubeseal
        sudo install -m 755 kubeseal /usr/local/bin/kubeseal
        kubeseal_installed="true"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        brew install kubeseal
        kubeseal_installed="true"
    else
        echo "Could not install kubeseal. Unsupported operating system. Please manually install it."
    fi
}
kubectl apply -f https://github.com/bitnami-labs/sealed-secrets/releases/download/v0.12.1/controller.yaml
sleep 15

############## Install Kustomize
echo "--- Installing Kustomize..."
kustomize_installed="true"
command -v kustomize >/dev/null 2>&1 || {
    kustomize_installed="false"
    if [[ "$OSTYPE" == "linux-gnu" ]]; then
        curl -s "https://raw.githubusercontent.com/kubernetes-sigs/kustomize/master/hack/install_kustomize.sh"  | bash
        kustomize_installed="true"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        brew install kustomize
        kustomize_installed="true"
    else
        echo "Could not install kustomize. Unsupported operating system. Please manually install it."
    fi
}

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

    ################ Create the ingress certificate (optional)
    echo "--- Generating ingress TLS key & certificate..."
    sudo openssl genrsa -out $outdir/tls.key 2048
    sudo openssl req -new -out $outdir/tls.csr -key $outdir/tls.key -config openssl.cnf
    sudo openssl x509 -req -days 3650 -in $outdir/tls.csr -signkey $outdir/tls.key -out $outdir/tls.crt \
         -extensions v3_req -extfile openssl.cnf

    ############### Import the cert into JRE CA trusted certs to make Java clients work (Optional)
    echo "--- Import ingress certificate to JRE trust store..."
    echo "Default keystore password = changeit"
    keystore=$JAVA_HOME/jre/lib/security/cacerts
    sudo keytool -delete -alias choreoingress_local -keystore $keystore
    sudo keytool -import  -alias choreoingress_local -keystore $keystore -file $outdir/tls.crt -noprompt

    ############### Create ingress TLS cert sealed secrets for all environments (Optional)
    sudo chown $USER $outdir/tls.key
    sudo chown $USER $outdir/tls.crt

    for env in "${environments[@]}"; do
        echo "--- Creating ingress TLS cert sealed secrets for ${env} environment..."
        mkdir -p $outdir/$env
        kubectl create -n $env-choreo-system secret tls ingress-cert --key $outdir/tls.key --cert $outdir/tls.crt \
                --dry-run -o yaml > $outdir/$env/ingress-cert.yaml
        kubeseal --scope strict < $outdir/$env/ingress-cert.yaml -o yaml  > ../kustomize/$env/secret/sealed-ingress-cert.yaml
        echo "Sealed secret ingress cert generated and copied to "$env"/secret"
    done
fi

### TODO: generate for all secret prop files
########### Create choreo sealed secrets for all environments
for env in "${environments[@]}"; do
    echo "--- Creating Choreo sealed secrets for for ${env} environment..."
    mkdir -p ${outdir}/${env}
    ./secretgen.sh -d=${secretdir} -n=${env}-choreo-system -o=../kustomize/${env}/secret
done

########### Cleanup
rm -rf ${outdir}
successful="true"
if [[ "${linkerd_installed}" == "false" ]]; then
    echo "[FAILED] linkerd installation. See https://linkerd.io/2/getting-started/"
    successful=false
fi
if [[ "${kubeseal_installed}" == "false" ]]; then
    echo "[FAILED] kubeseal installation. See https://github.com/bitnami-labs/sealed-secrets/releases"
    successful=false
fi
if [[ "${kustomize_installed}" == "false" ]]; then
    echo "[FAILED] kustomize installation. See https://github.com/kubernetes-sigs/kustomize/blob/master/docs/INSTALL.md"
    successful=false
fi
if [[ "${successful}" == "true" ]]; then
    echo "Choreo control plane successfully installed"
fi

