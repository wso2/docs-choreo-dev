#!/bin/bash
# -------------------------------------------------------------------------------------
#
# Copyright (c) 2025, WSO2 LLC (http://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 LLC and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content.
#
# --------------------------------------------------------------------------------------

set -e

# volume mounts
config_volume=${WORKING_DIRECTORY}/wso2-config-volume
artifact_volume=${WORKING_DIRECTORY}/wso2-artifact-volume

# keystores are mounted here from Azure Key Vault
key_store_volume=${WORKING_DIRECTORY}/wso2-keystores

# check if the WSO2 non-root user home exists
test ! -d "${WORKING_DIRECTORY}" && echo "WSO2 Docker non-root user home does not exist" && exit 1

# check if the WSO2 product home exists
test ! -d "${WSO2_SERVER_HOME}" && echo "WSO2 Docker product home does not exist" && exit 1

# copy the product distribution to ${WSO2_SERVER_HOME}
if [[ -n "${WSO2_DIST_DIRECTORY}" ]]
then
    cp -R "${WSO2_DIST_DIRECTORY}"/* "${WSO2_SERVER_HOME}"/

    test ! -d "${WSO2_SERVER_HOME}"/bin && echo "Product bin does not exist" && exit 1
fi

# optimize WSO2 Carbon Server, if the profile name is defined as an environment variable
if [[ -n "${PROFILE_NAME}" ]]
then
  echo "Optimizing WSO2 Carbon Server" >&2
  sh "${WSO2_SERVER_HOME}"/bin/profileSetup.sh -Dprofile="${PROFILE_NAME}"
fi

# copy any configuration changes mounted to config_volume
test -d "${config_volume}" && [[ "$(ls -A "${config_volume}")" ]] && cp -RL "${config_volume}"/* "${WSO2_SERVER_HOME}"/

# copy any artifact changes mounted to artifact_volume
test -d "${artifact_volume}" && [[ "$(ls -A "${artifact_volume}")" ]] && cp -RL "${artifact_volume}"/* "${WSO2_SERVER_HOME}"/

# Azure Key Valut mounts keystores without passwords, hence we need to add them back
echo "Managing KeyStores" >&2
pushd "${key_store_volume}"

# Remove any existing keystores inside the server
echo "Removing any existing KeyStores" >&2
test -f "${WSO2_SERVER_HOME}"/repository/resources/security/primary-keystore.pfx && rm "${WSO2_SERVER_HOME}"/repository/resources/security/primary-keystore.pfx
test -f "${WSO2_SERVER_HOME}"/repository/resources/security/internal-keystore.pfx && rm "${WSO2_SERVER_HOME}"/repository/resources/security/internal-keystore.pfx
test -f "${WSO2_SERVER_HOME}"/repository/resources/security/tls-keystore.pfx && rm "${WSO2_SERVER_HOME}"/repository/resources/security/tls-keystore.pfx

keytool -importkeystore \
        -srckeystore primary-keystore.pfx \
        -srcstoretype PKCS12 \
        -srcstorepass "" \
        -srcalias wso2carbon \
        -srckeypass "" \
        -destkeystore "${WSO2_SERVER_HOME}"/repository/resources/security/primary-keystore.pfx \
        -deststoretype PKCS12 \
        -deststorepass  "${PRIMARY_KEYSTORE_PSWD}" \
        -destalias wso2carbon \
        -destkeypass "${PRIMARY_KEYSTORE_KEY_PSWD}" >&2

keytool -importkeystore \
        -srckeystore internal-keystore.pfx \
        -srcstoretype PKCS12 \
        -srcstorepass "" \
        -srcalias wso2carbon \
        -srckeypass "" \
        -destkeystore "${WSO2_SERVER_HOME}"/repository/resources/security/internal-keystore.pfx \
        -deststoretype PKCS12 \
        -deststorepass "${INTERNAL_KEYSTORE_PSWD}" \
        -destalias wso2carbon \
        -destkeypass "${INTERNAL_KEYSTORE_KEY_PSWD}" >&2

keytool -importkeystore \
        -srckeystore tls-keystore.pfx \
        -srcstoretype PKCS12 \
        -srcstorepass "" \
        -srcalias wso2carbon \
        -srckeypass "" \
        -destkeystore "${WSO2_SERVER_HOME}"/repository/resources/security/tls-keystore.pfx \
        -deststoretype PKCS12 \
        -deststorepass "${TLS_KEYSTORE_PSWD}" \
        -destalias wso2carbon \
        -destkeypass "${TLS_KEYSTORE_KEY_PSWD}" >&2

popd

# start WSO2 Carbon server
echo "Start WSO2 Carbon server" >&2
if [[ -z "${PROFILE_NAME}" ]]
then
  # start the server with the provided startup arguments
  sh "${WSO2_SERVER_HOME}"/bin/api-manager.sh "$@" -Dlog4j2.formatMsgNoLookups=true
else
  # start the server with the specified profile and provided startup arguments
  sh "${WSO2_SERVER_HOME}"/bin/api-manager.sh -Dprofile="${PROFILE_NAME}" "$@" -Dlog4j2.formatMsgNoLookups=true
fi
