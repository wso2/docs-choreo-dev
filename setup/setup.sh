#!/usr/bin/env bash

outdir=out
mkdir $outdir

############## Install nginx ingress
echo "Installing nginx ingress..."
# nginx-ingress
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/nginx-0.30.0/deploy/static/mandatory.yaml
# for docker for mac
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/nginx-0.30.0/deploy/static/provider/cloud-generic.yaml

############## Install Linkerd
echo "Installing Linkerd..."
brew install linkerd
linkerd install | kubectl apply -f -

############## Install Sealed secret support
echo "Installing kubeseal & Bitnami sealed secrets..."
brew install kubeseal
kubectl apply -f https://github.com/bitnami-labs/sealed-secrets/releases/download/v0.12.1/controller.yaml

############## Install Kustomize
echo "Installing Kustomize..."
brew install kustomize

################ create the ingress certificate
echo "Generating ingress TLS key & certificate..."
sudo openssl genrsa -out $outdir/tls.key 2048
sudo openssl req -new -out $outdir/tls.csr -key $outdir/tls.key -config openssl.cnf
sudo openssl x509 -req -days 3650 -in $outdir/tls.csr -signkey $outdir/tls.key -out $outdir/tls.crt \
     -extensions v3_req -extfile openssl.cnf

############### import the cert into JRE CA trusted certs to make Java clients work
echo "Import ingress certificate to JRE trust store..."
keystore=$JAVA_HOME/jre/lib/security/cacerts
sudo keytool -delete -alias choreoingress_local -keystore $keystore
sudo keytool -import  -alias choreoingress_local -keystore $keystore -file $outdir/tls.crt

############### create ingress TLS cert secrets for all environments
echo "Creating ingress TLS certs for all environments..."
sudo chown $USER $outdir/tls.key
sudo chown $USER $outdir/tls.crt

for env in "dev" "staging" "prod"
do
    mkdir -p out/$env
    kubectl create -n $env-choreo-system secret tls ingress-cert --key $outdir/tls.key --cert $outdir/tls.crt \
            --dry-run=client -o yaml > out/$env/ingress-cert.yaml
    kubeseal --scope strict < out/$env/ingress-cert.yaml -o yaml  > out/$env/sealed-ingress-cert.yaml
    cp out/$env/sealed-ingress-cert.yaml ../kustomize/$env/
    echo "Sealed secret ingress cert generated and copied to "$env
done

rm -rf $outdir
