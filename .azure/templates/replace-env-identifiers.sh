#!/bin/bash

sed -i "s/${SRC_NS_IDENTIFIER}-choreo-system/${TRGT_NS_IDENTIFIER}-choreo-system/g" "$FILE_PATH"

sed -i "s/${SRC_NS_IDENTIFIER}-choreo-apim/${TRGT_NS_IDENTIFIER}-choreo-apim/g" "$FILE_PATH"

sed -i "s/${SRC_NS_IDENTIFIER}-choreo-authorization/${TRGT_NS_IDENTIFIER}-choreo-authorization/g" "$FILE_PATH"

sed -i "s/sts\.${SRC_DNS_IDENTIFIER}\.choreo\.dev/sts.${TRGT_DNS_IDENTIFIER}.choreo.dev/g" "$FILE_PATH"

sed -i "s/publish\.${SRC_DNS_IDENTIFIER}\.websubhub\.choreo\.dev/publish.${TRGT_DNS_IDENTIFIER}.websubhub.choreo.dev/g" "$FILE_PATH"

sed -i "s/console\.${SRC_DNS_IDENTIFIER}\.choreo\.dev/console.${TRGT_DNS_IDENTIFIER}.choreo.dev/g" "$FILE_PATH"

sed -i "s/apis\.${SRC_DNS_IDENTIFIER}\.choreo\.dev/apis.${TRGT_DNS_IDENTIFIER}.choreo.dev/g" "$FILE_PATH"

sed -i "s/app\.${SRC_DNS_IDENTIFIER}\.choreo\.dev/app.${TRGT_DNS_IDENTIFIER}.choreo.dev/g" "$FILE_PATH"

sed -i "s/${SRC_DNS_IDENTIFIER}\.api\.asgardeo\.io/${TRGT_DNS_IDENTIFIER}.api.asgardeo.io/g" "$FILE_PATH"

sed -i "s/${SRC_DNS_IDENTIFIER}\.choreoapps\.dev/${TRGT_DNS_IDENTIFIER}.choreoapps.dev/g" "$FILE_PATH"

sed -i "s/${SRC_DNS_IDENTIFIER}\.choreoapis\.dev/${TRGT_DNS_IDENTIFIER}.choreoapis.dev/g" "$FILE_PATH"

sed -i "s/${SRC_DNS_IDENTIFIER}\.choreo\.dev/${TRGT_DNS_IDENTIFIER}.choreo.dev/g" "$FILE_PATH"

sed -i "s/${SRC_DNS_IDENTIFIER}\.asgardeo\.io/${TRGT_DNS_IDENTIFIER}.asgardeo.io/g" "$FILE_PATH"

sed -i "s/choreo_${SRC_NS_IDENTIFIER}_apim_admin/choreo_${TRGT_NS_IDENTIFIER}_apim_admin/g" "$FILE_PATH"

sed -i "s/choreo_${SRC_NS_IDENTIFIER}_ga_admin/choreo_${TRGT_NS_IDENTIFIER}_ga_admin/g" "$FILE_PATH"

sed -i "s/subscribe\.${SRC_DNS_IDENTIFIER}\.websubhub\.choreo\.dev/subscribe.${TRGT_DNS_IDENTIFIER}.websubhub.choreo.dev/g" "$FILE_PATH"

sed -i "s/${SRC_DNS_IDENTIFIER}\.console\.asgardeo\.io/${TRGT_DNS_IDENTIFIER}.console.asgardeo.io/g" "$FILE_PATH"

sed -i "s/hub\.${SRC_DNS_IDENTIFIER}\.websubhub\.choreo\.dev/hub.${TRGT_DNS_IDENTIFIER}.websubhub.choreo.dev/g" "$FILE_PATH"

sed -i "s/hub\.${SRC_DNS_IDENTIFIER}\.websubhub\.choreo\.dev/hub.${TRGT_DNS_IDENTIFIER}.websubhub.choreo.dev/g" "$FILE_PATH"

sed -i "s/devportal.${SRC_DNS_IDENTIFIER}-controlplane\.internal/devportal.${TRGT_DNS_IDENTIFIER}-controlplane.internal/g" "$FILE_PATH"

sed -i "s/usagehandler\.${SRC_DNS_IDENTIFIER}\.choreo\.internal/usagehandler.${TRGT_DNS_IDENTIFIER}.choreo.internal/g" "$FILE_PATH"

sed -i "s/${SRC_SQL_HOST}/${TRGT_SQL_HOST}/g" "$FILE_PATH"

sed -i "s/${SRC_GA_REDIS_HOST}/${TRGT_GA_REDIS_HOST}/g" "$FILE_PATH"

sed -i "s/${SRC_RUNTIME_REDIS_HOST}/${TRGT_RUNTIME_REDIS_HOST}/g" "$FILE_PATH"

sed -i "s/${SRC_BCENTRAL_HOST}/${TRGT_BCENTRAL_HOST}/g" "$FILE_PATH"
