#!/usr/bin/env bash
echo "----------------------------------------------"
echo "| Choreo Control Plane setup on Kubernetes   |"
echo "----------------------------------------------"

propfile=$1
[[ $# -eq 0 ]] &&
{ echo "Usage: $0 propfile"; \
echo "   -p=propfile - secrets properties file"; exit 1; }

for arg in "$@"
do
    case $arg in
        -p=*|--propfile=*)
        propfile="${arg#*=}"
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

############## Install nginx ingress
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

############## Install Linkerd
echo "--- Installing Linkerd..."
linkerd_installed="false"
command -v linkerd >/dev/null 2>&1 || {
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
kubeseal_installed="false"
command -v kubeseal >/dev/null 2>&1 || {
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

############## Install Kustomize
echo "--- Installing Kustomize..."
kustomize_installed="false"
command -v kustomize >/dev/null 2>&1 || {
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

################ create the ingress certificate
echo "--- Generating ingress TLS key & certificate..."
sudo openssl genrsa -out $outdir/tls.key 2048
sudo openssl req -new -out $outdir/tls.csr -key $outdir/tls.key -config openssl.cnf
sudo openssl x509 -req -days 3650 -in $outdir/tls.csr -signkey $outdir/tls.key -out $outdir/tls.crt \
     -extensions v3_req -extfile openssl.cnf

############### import the cert into JRE CA trusted certs to make Java clients work
echo "--- Import ingress certificate to JRE trust store..."
echo "Default keystore password = changeit"
keystore=$JAVA_HOME/jre/lib/security/cacerts
sudo keytool -delete -alias choreoingress_local -keystore $keystore
sudo keytool -import  -alias choreoingress_local -keystore $keystore -file $outdir/tls.crt -noprompt

############### create ingress TLS cert sealed secrets for all environments
echo "--- Creating ingress TLS cert sealed secrets for all environments..."
sudo chown $USER $outdir/tls.key
sudo chown $USER $outdir/tls.crt

for env in "dev" "stage" "prod"; do
    mkdir -p $outdir/$env
    kubectl create -n $env-choreo-system secret tls ingress-cert --key $outdir/tls.key --cert $outdir/tls.crt \
            --dry-run=client -o yaml > $outdir/$env/ingress-cert.yaml
    kubeseal --scope strict < $outdir/$env/ingress-cert.yaml -o yaml  > ../kustomize/$env/sealed-ingress-cert.yaml
    echo "Sealed secret ingress cert generated and copied to "$env
done

########### create choreo sealed secrets for all environments
echo "--- Creating Choreo sealed secrets for all environments..."
for env in "dev" "stage" "prod"; do
    mkdir -p ${outdir}/${env}
    ./secretgen.sh -p=${propfile} -n=${env}-choreo-system -o=${outdir}/${env}
    cp ${outdir}/${env}/sealed-secret.yaml ../kustomize/${env}/
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

