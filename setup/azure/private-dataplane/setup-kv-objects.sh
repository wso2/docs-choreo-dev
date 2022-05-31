#!/bin/bash

echo "--- Generating Certificate and Private Key for use in Enforcer, Adapter and Router ---"

cp template-cert-req.conf cert-req-enforcer-router-adapter.conf
/bin/echo -e "DNS.1 = enforcer\nDNS.2 = adapter\nDNS.3 = router\nDNS.4 = localhost" >> cert-req-enforcer-router-adapter.conf

openssl req -x509 -sha256 -nodes -days 10950 -newkey rsa:2048 -keyout mg.key -out mg.pem -config cert-req-enforcer-router-adapter.conf -extensions 'v3_req'

echo "--- Generating Certificate and Private Key for use in Global Adapter ---"

GA_PWD=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c8)

cp template-cert-req.conf cert-req-global-adapter.conf
/bin/echo -e "DNS.1 = global-adapter" >> cert-req-global-adapter.conf

openssl req -x509 -sha256 -nodes -days 10950 -newkey rsa:2048 -keyout global-adapter.key -out global-adapter.pem -config cert-req-global-adapter.conf -extensions 'v3_req'

echo "--- Generating Certificate and Private Key for use in Traffic Manager ---"

cp template-cert-req.conf cert-req-wso2carbon.conf
/bin/echo -e "DNS.1 = choreo-eventhub-service\nDNS.2 = choreo-eventhub-1-service\nDNS.3 = choreo-eventhub-3-service\nDNS.4 = choreo-tm-1-service\nDNS.5 = choreo-tm-2-service" >> cert-req-wso2carbon.conf

openssl req -x509 -sha256 -nodes -days 10950 -newkey rsa:2048 -keyout wso2carbon.key -out wso2carbon.pem -config cert-req-wso2carbon.conf -extensions 'v3_req'

cp template-cert-req.conf cert-req-internal-keystore.conf
/bin/echo -e "DNS.1 = localhost" >> cert-req-internal-keystore.conf

openssl req -x509 -sha256 -nodes -days 10950 -newkey rsa:2048 -keyout internal.key -out internal.pem -config cert-req-internal-keystore.conf -extensions 'v3_req'

cp template-cert-req.conf cert-req-primary-keystore.conf
/bin/echo -e "DNS.1 = localhost" >> cert-req-primary-keystore.conf

openssl req -x509 -sha256 -nodes -days 10950 -newkey rsa:2048 -keyout primary.key -out primary.pem -config cert-req-primary-keystore.conf -extensions 'v3_req'

echo "--- Generating TLS, Internal and Primary Keystore PFX files ---"

TLS_KEYSTORE_PWD=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c8)
openssl pkcs12 -export -out tls-keystore.pfx -inkey wso2carbon.key -in wso2carbon.pem -name wso2carbon -password pass:${TLS_KEYSTORE_PWD}

echo "TLS Keystore Password is: ${TLS_KEYSTORE_PWD}"

INTERNAL_KEYSTORE_PWD=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c8)
openssl pkcs12 -export -out internal-keystore.pfx -inkey internal.key -in internal.pem -name internal -password pass:${INTERNAL_KEYSTORE_PWD}

echo "Internal Keystore Password is: ${INTERNAL_KEYSTORE_PWD}"

PRIMARY_KEYSTORE_PWD=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c8)
openssl pkcs12 -export -out primary-keystore.pfx -inkey primary.key -in primary.pem -name primary -password pass:${PRIMARY_KEYSTORE_PWD}

echo "Primary Keystore Password is: ${PRIMARY_KEYSTORE_PWD}"

echo "Private Global Adapter Password is: ${GA_PWD}"

echo "--- Uploading secrets to Key Vault ---"

SECRET_FILE_PATH="../csi-secrets/choreo-private-dp-secrets.properties"
CERT_FILE_PATH="../csi-secrets/choreo-private-dp-certs.properties"
PEM_FILE_PATH="../csi-secrets/choreo-private-dp-pems.properties"
CERT_BASE_PATH="../private-dataplane"

cp ${SECRET_FILE_PATH} ${SECRET_FILE_PATH}.bak
cp ${CERT_FILE_PATH} ${CERT_FILE_PATH}.bak
cp ${PEM_FILE_PATH} ${PEM_FILE_PATH}.bak

sed -i "s/apim_PRIMARY_KEYSTORE_PSWD/${PRIMARY_KEYSTORE_PWD}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_PRIMARY_KEYSTORE_KEY_PSWD/${PRIMARY_KEYSTORE_PWD}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_TLS_KEYSTORE_PSWD/${TLS_KEYSTORE_PWD}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_TLS_KEYSTORE_KEY_PSWD/${TLS_KEYSTORE_PWD}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_INTERNAL_KEYSTORE_PSWD/${INTERNAL_KEYSTORE_PWD}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_INTERNAL_KEYSTORE_KEY_PSWD/${INTERNAL_KEYSTORE_PWD}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_TRUSTSTORE_PSWD/${apim_TRUSTSTORE_PSWD}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_H2_SHARED_DB_PSWD/${apim_H2_SHARED_DB_PSWD}/g" ${SECRET_FILE_PATH}
sed -i "s/asb_CONNECTION_STRING/${asb_CONNECTION_STRING}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_ANALYTICS_AUTH_TOKEN_PROD/${apim_ANALYTICS_AUTH_TOKEN_PROD}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_ANALYTICS_AUTH_TOKEN_DEV/${apim_ANALYTICS_AUTH_TOKEN_DEV}/g" ${SECRET_FILE_PATH}
sed -i "s/CUSTOMER_NAME/${CUSTOMER_NAME}/g" ${SECRET_FILE_PATH}
sed -i "s/ga_PASSWORD/${GA_PWD}/g" ${SECRET_FILE_PATH}

TLS_KEYSTORE_PATH="${CERT_BASE_PATH}/tls-keystore.pfx"
INTERNAL_KEYSTORE_PATH="${CERT_BASE_PATH}/internal-keystore.pfx"
PRIMARY_KEYSTORE_PATH="${CERT_BASE_PATH}/primary-keystore.pfx"
ROUTER_KEYSTORE_PATH="${CERT_BASE_PATH}/mg.key"

sed -i -e "s|TLS_KEYSTORE_PATH|${TLS_KEYSTORE_PATH}|g" ${CERT_FILE_PATH}
sed -i -e "s|INTERNAL_KEYSTORE_PATH|${INTERNAL_KEYSTORE_PATH}|g" ${CERT_FILE_PATH}
sed -i -e "s|PRIMARY_KEYSTORE_PATH|${PRIMARY_KEYSTORE_PATH}|g" ${CERT_FILE_PATH}
sed -i -e "s|ROUTER_KEYSTORE_PATH|${ROUTER_KEYSTORE_PATH}|g" ${CERT_FILE_PATH}

ADAPTER_KEYSTORE_PATH="${CERT_BASE_PATH}/mg.key"
ENFORCER_KEYSTORE_PATH="${CERT_BASE_PATH}/mg.key"

sed -i "s|ADAPTER_KEYSTORE_PATH|${ADAPTER_KEYSTORE_PATH}|g" ${PEM_FILE_PATH}
sed -i "s|ENFORCER_KEYSTORE_PATH|${ENFORCER_KEYSTORE_PATH}|g" ${PEM_FILE_PATH}

#az keyvault secret set --name mgw-ADAPTER-KEYSTORE-KEY --vault-name ${USERAPPS_VAULT_NAME} --file mg.key
#az keyvault secret set --name mgw-ENFORCER-KEYSTORE-KEY --vault-name ${USERAPPS_VAULT_NAME} --file mg.key
#az keyvault certificate import mgw-ROUTER-KEYSTORE --vault-name ${USERAPPS_VAULT_NAME} --file mg.key

#kv-secretuploader.sh

#echo "--- Cleaning up configuration files ---"
#rm cert-req*
#mv ${SECRET_FILE_PATH}.bak ${SECRET_FILE_PATH}
#mv ${CERT_FILE_PATH}.bak ${CERT_FILE_PATH}
#mv ${PEM_FILE_PATH}.bak ${PEM_FILE_PATH}
