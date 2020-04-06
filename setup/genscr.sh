#!/bin/bash

sudo openssl genrsa -out server.key 2048
sudo openssl req -new -out server.csr -key server.key -config openssl.cnf
sudo openssl x509 -req -days 3650 -in server.csr -signkey server.key -out server.crt -extensions v3_req -extfile openssl.cnf
sudo keytool -import  -alias choreoingress_local -keystore $JAVA_HOME/jre/lib/security/cacerts -file server.crt
