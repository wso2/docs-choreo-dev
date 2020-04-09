#!/usr/bin/env bash

for env in "dev" "staging" "prod"
do
    mkdir -p out/$env
    kubectl create -n $env-choreo-system secret tls ingress-cert --key tls.key --cert tls.crt --dry-run=client -o yaml \
            > out/$env/ingress-cert.yaml
    kubeseal --scope strict < out/$env/ingress-cert.yaml -o yaml  > out/$env/sealed-ingress-cert.yaml
    cp out/$env/sealed-ingress-cert.yaml ../$env/
    echo "Sealed secret ingress cert generated and copied to "$env
done

rm -rf out


