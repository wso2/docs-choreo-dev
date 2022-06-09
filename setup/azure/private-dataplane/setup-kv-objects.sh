#!/usr/bin/env bash

echo "--- Generating Certificate and Private Key for use in Enforcer, Adapter and Router ---"

PRIVATE_DP_BASE_PATH=private-dataplane
TEMPLATE_CERT_PATH="${PRIVATE_DP_BASE_PATH}"/template-cert-req.conf

cp "${TEMPLATE_CERT_PATH}" cert-req-enforcer-router-adapter.conf
/bin/echo -e "DNS.1 = enforcer\nDNS.2 = adapter\nDNS.3 = router\nDNS.4 = localhost" >> cert-req-enforcer-router-adapter.conf

openssl req -x509 -sha256 -nodes -days 10950 -newkey rsa:2048 -keyout mg.key -out mg.pem -config cert-req-enforcer-router-adapter.conf -extensions 'v3_req'

echo "--- Generating Certificate and Private Key for use in Global Adapter ---"

GA_PWD=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c8)

cp "${TEMPLATE_CERT_PATH}" cert-req-global-adapter.conf
/bin/echo -e "DNS.1 = global-adapter" >> cert-req-global-adapter.conf

openssl req -x509 -sha256 -nodes -days 10950 -newkey rsa:2048 -keyout global-adapter.key -out global-adapter.pem -config cert-req-global-adapter.conf -extensions 'v3_req'

echo "--- Generating Certificate and Private Key for use in Traffic Manager ---"

cp "${TEMPLATE_CERT_PATH}" cert-req-wso2carbon.conf
/bin/echo -e "DNS.1 = choreo-eventhub-service\nDNS.2 = choreo-eventhub-1-service\nDNS.3 = choreo-eventhub-3-service\nDNS.4 = choreo-tm-1-service\nDNS.5 = choreo-tm-2-service" >> cert-req-wso2carbon.conf

openssl req -x509 -sha256 -nodes -days 10950 -newkey rsa:2048 -keyout wso2carbon.key -out wso2carbon.pem -config cert-req-wso2carbon.conf -extensions 'v3_req'

cp "${TEMPLATE_CERT_PATH}" cert-req-internal-keystore.conf
/bin/echo -e "DNS.1 = localhost" >> cert-req-internal-keystore.conf

openssl req -x509 -sha256 -nodes -days 10950 -newkey rsa:2048 -keyout internal.key -out internal.pem -config cert-req-internal-keystore.conf -extensions 'v3_req'

cp "${TEMPLATE_CERT_PATH}" cert-req-primary-keystore.conf
/bin/echo -e "DNS.1 = localhost" >> cert-req-primary-keystore.conf

openssl req -x509 -sha256 -nodes -days 10950 -newkey rsa:2048 -keyout primary.key -out primary.pem -config cert-req-primary-keystore.conf -extensions 'v3_req'

echo "--- Generating TLS, Internal and Primary Keystore PFX files ---"

TLS_KEYSTORE_PWD=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c8)
openssl pkcs12 -export -out tls-keystore.pfx -inkey wso2carbon.key -in wso2carbon.pem -name wso2carbon -password pass:"${TLS_KEYSTORE_PWD}"

INTERNAL_KEYSTORE_PWD=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c8)
openssl pkcs12 -export -out internal-keystore.pfx -inkey internal.key -in internal.pem -name internal -password pass:"${INTERNAL_KEYSTORE_PWD}"

PRIMARY_KEYSTORE_PWD=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c8)
openssl pkcs12 -export -out primary-keystore.pfx -inkey primary.key -in primary.pem -name primary -password pass:"${PRIMARY_KEYSTORE_PWD}"

TM_PWD=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c8)

echo "--- Creating Client Truststore JKS ---"
APIM_TRUSTSTORE_PSWD=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c8)
keytool -import -file wso2carbon.pem -alias wso2carbon -keystore client-truststore.jks -storepass "${APIM_TRUSTSTORE_PSWD}" -noprompt
keytool -import -file global-adapter.pem -alias global-adapter -keystore client-truststore.jks -storepass "${APIM_TRUSTSTORE_PSWD}" -noprompt

echo "--- Uploading secrets to Key Vault ---"
CUSTOMER_NAME_CAPS=$(echo "${CUSTOMER_NAME}" | tr "[:lower:]" "[:upper:]")

SECRET_FILE_PATH="csi-secrets/choreo-private-dp-secrets.properties"
CERT_FILE_PATH="csi-secrets/choreo-private-dp-certs.properties"
PEM_FILE_PATH="csi-secrets/choreo-private-dp-pems.properties"
#CERT_BASE_PATH="iprivate-dataplane"

cp ${SECRET_FILE_PATH} ${SECRET_FILE_PATH}.bak
cp ${CERT_FILE_PATH} ${CERT_FILE_PATH}.bak
cp ${PEM_FILE_PATH} ${PEM_FILE_PATH}.bak

sed -i "s/apim_PRIMARY_KEYSTORE_PSWD/${PRIMARY_KEYSTORE_PWD}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_PRIMARY_KEYSTORE_KEY_PSWD/${PRIMARY_KEYSTORE_PWD}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_TLS_KEYSTORE_PSWD/${TLS_KEYSTORE_PWD}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_TLS_KEYSTORE_KEY_PSWD/${TLS_KEYSTORE_PWD}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_INTERNAL_KEYSTORE_PSWD/${INTERNAL_KEYSTORE_PWD}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_INTERNAL_KEYSTORE_KEY_PSWD/${INTERNAL_KEYSTORE_PWD}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_TRUSTSTORE_PSWD/${APIM_TRUSTSTORE_PSWD}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_H2_SHARED_DB_PSWD/wso2carbon/g" ${SECRET_FILE_PATH}
sed -i "s/asb_CONNECTION_STRING/${ASB_CONNECTION_STRING}/g" ${SECRET_FILE_PATH}
sed -i "s/apim_ANALYTICS_AUTH_TOKEN/${APIM_ANALYTICS_AUTH_TOKEN}/g" ${SECRET_FILE_PATH}
sed -i "s/CUSTOMER_NAME/${CUSTOMER_NAME_CAPS}/g" ${SECRET_FILE_PATH}
sed -i "s/ga_PASSWORD/${GA_PWD}/g" ${SECRET_FILE_PATH}
sed -i "s/tm_PASSWORD/${TM_PWD}/g" ${SECRET_FILE_PATH}

TLS_KEYSTORE_PATH="tls-keystore.pfx"
INTERNAL_KEYSTORE_PATH="internal-keystore.pfx"
PRIMARY_KEYSTORE_PATH="primary-keystore.pfx"

sed -i -e "s|TLS_KEYSTORE_PATH|${TLS_KEYSTORE_PATH}|g" ${CERT_FILE_PATH}
sed -i -e "s|INTERNAL_KEYSTORE_PATH|${INTERNAL_KEYSTORE_PATH}|g" ${CERT_FILE_PATH}
sed -i -e "s|PRIMARY_KEYSTORE_PATH|${PRIMARY_KEYSTORE_PATH}|g" ${CERT_FILE_PATH}

sed -i -e "s|TLS_KEYSTORE_PSWD|${TLS_KEYSTORE_PWD}|g" ${CERT_FILE_PATH}
sed -i -e "s|INTERNAL_KEYSTORE_PSWD|${INTERNAL_KEYSTORE_PWD}|g" ${CERT_FILE_PATH}
sed -i -e "s|PRIMARY_KEYSTORE_PSWD|${PRIMARY_KEYSTORE_PWD}|g" ${CERT_FILE_PATH}

ADAPTER_KEYSTORE_PATH="mg.key"
ENFORCER_KEYSTORE_PATH="mg.key"
ROUTER_KEYSTORE_PATH="mg.key"

sed -i "s|ADAPTER_KEYSTORE_PATH|${ADAPTER_KEYSTORE_PATH}|g" ${PEM_FILE_PATH}
sed -i "s|ENFORCER_KEYSTORE_PATH|${ENFORCER_KEYSTORE_PATH}|g" ${PEM_FILE_PATH}
sed -i "s|ROUTER_KEYSTORE_PATH|${ROUTER_KEYSTORE_PATH}|g" ${PEM_FILE_PATH}

KEYVAULT_NAME=$(az keyvault list --resource-group choreo-"${CUSTOMER_NAME}"-key-vault-rg --query "[?contains(name, '${CUSTOMER_NAME}-userapps-${ENV}')].name" --output tsv)

bash csi-secrets/kv-secret-uploader.sh -v "${KEYVAULT_NAME}" -i "${SECRET_FILE_PATH}" -t secret
bash csi-secrets/kv-secret-uploader.sh -v "${KEYVAULT_NAME}" -i "${PEM_FILE_PATH}" -t pem
bash csi-secrets/kv-secret-uploader.sh -v "${KEYVAULT_NAME}" -i "${CERT_FILE_PATH}" -t securecert 

echo "--- Cleaning up configuration files ---"
#rm client-truststore.jks mg.* tls-keystore.pfx wso2carbon.* global-adapter.* internal* cert-req-* primary*

mkdir cert-files

mv client-truststore.jks mg.* tls-keystore.pfx wso2carbon.* global-adapter.* internal* cert-req-* primary* cert-files/

mv ${SECRET_FILE_PATH}.bak ${SECRET_FILE_PATH}
mv ${CERT_FILE_PATH}.bak ${CERT_FILE_PATH}
mv ${PEM_FILE_PATH}.bak ${PEM_FILE_PATH}

echo "TLS Keystore Password is: ${TLS_KEYSTORE_PWD}"

echo "Internal Keystore Password is: ${INTERNAL_KEYSTORE_PWD}"

echo "Primary Keystore Password is: ${PRIMARY_KEYSTORE_PWD}"

echo "Private Global Adapter Password is: ${GA_PWD}"

echo "Traffic Manager Password is: ${TM_PWD}"

