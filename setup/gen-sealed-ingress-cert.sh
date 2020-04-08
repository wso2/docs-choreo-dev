#!/usr/bin/env bash
mkdir out
kubectl create -n kube-system secret tls ingress-cert --key tls.key --cert tls.crt --dry-run -o yaml > out/ingress-cert.yaml
kubeseal --scope strict --cert sealed-secret.crt < out/ingress-cert.yaml -o yaml  > out/sealed-ingress-cert.yaml

kubectl apply -f out/sealed-ingress-cert.yaml
kubectl apply -f nginx-ingress-controller.yaml
