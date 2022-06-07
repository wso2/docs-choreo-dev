#!/usr/bin/env bash

################ Install emberstack reflector ########
kubectl create ns cert-manager

helm repo add emberstack https://emberstack.github.io/helm-charts
helm repo update
helm upgrade --install reflector emberstack/reflector --namespace cert-manager --version 5.4.17
