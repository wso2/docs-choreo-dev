#!/usr/bin/env bash
sudo openssl genrsa -out tls.key 2048
sudo openssl req -new -out tls.csr -key tls.key -config openssl.cnf
sudo openssl x509 -req -days 3650 -in tls.csr -signkey tls.key -out tls.crt -extensions v3_req -extfile openssl.cnf

keystore=$JAVA_HOME/jre/lib/security/cacerts
sudo keytool -delete -alias choreoingress_local -keystore $keystore
sudo keytool -import  -alias choreoingress_local -keystore $keystore -file tls.crt

sudo chown $USER tls.key
sudo chown $USER tls.crt
kubectl create secret tls ingress-secret --key tls.key --cert tls.crt --dry-run -o yaml > ingress-secret.yaml
kubeseal < ingress-secret.yaml -o yaml > sealed-ingress-secret.yaml

