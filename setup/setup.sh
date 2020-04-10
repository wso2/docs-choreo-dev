#!/usr/bin/env bash
echo "----------------------------------------------"
echo "| Choreo Control Plane setup on Kubernetes   |"
echo "----------------------------------------------"

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
command -v linkerd >/dev/null 2>&1 || {brew install linkerd}
linkerd install | kubectl apply -f -

############## Install Sealed secret support
echo "--- Installing kubeseal & Bitnami sealed secrets..."
command -v kubeseal >/dev/null 2>&1 || {brew install kubeseal}
kubectl apply -f https://github.com/bitnami-labs/sealed-secrets/releases/download/v0.12.1/controller.yaml

############## Install Kustomize
echo "--- Installing Kustomize..."
command -v kustomize >/dev/null 2>&1 || {brew install kustomize}

################ create the ingress certificate
echo "--- Generating ingress TLS key & certificate..."
sudo openssl genrsa -out $outdir/tls.key 2048
sudo openssl req -new -out $outdir/tls.csr -key $outdir/tls.key -config openssl.cnf
sudo openssl x509 -req -days 3650 -in $outdir/tls.csr -signkey $outdir/tls.key -out $outdir/tls.crt \
     -extensions v3_req -extfile openssl.cnf

############### import the cert into JRE CA trusted certs to make Java clients work
echo "--- Import ingress certificate to JRE trust store..."
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
echo "--- Creating ingress TLS cert sealed secrets for all environments..."

from_lit_str=""
for k in "db_password" "eh_shared_access_sig_key" "tsi_client_id" "tsi_client_secret" "tsi_tenant_id" "tsi_env_fqdn"; do
    read -p "${k}: " v
    from_lit_str=${from_lit_str}" --from-literal "$k"="$v" "
done

for env in "dev" "stage" "prod"; do
    mkdir -p ${outdir}/${env}
    kubectl create secret generic choreo-secret -n ${env}-choreo-system ${from_lit_str} \
             --dry-run=client -o yaml > ${outdir}/${env}/choreo-secret.yaml
    kubeseal --scope strict < ${outdir}/${env}/choreo-secret.yaml -o yaml  > ../kustomize/${env}/sealed-secret.yaml
    echo "Choreo Sealed secret generated and copied to "$env
done

########### Cleanup
rm -rf $outdir

