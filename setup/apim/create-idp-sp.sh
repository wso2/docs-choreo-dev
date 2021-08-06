#!/bin/bash

APIM_URL="https://localhost:9443"
APIM_ADMIN_USERNAME="admin"
APIM_ADMIN_PASSWORD="admin"

split_results(){
  export BODY=$(echo $HTTP_RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')
  export STATUS=$(echo $HTTP_RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
}

echo_results () {
  split_results
  if [ $STATUS -eq 200 ]; then
    tput setaf 2;
    echo $1
    tput sgr0;
  elif [[ $BODY == *"already exists"* ]]; then
    tput setaf 2;
    echo $3
  else
    tput setaf 1;
    echo "$2 , Status code : $STATUS"
    tput sgr0;
    echo "response from server: $BODY"
  fi
}

######################################## Choreo idp #########################################

HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" --header "Content-Type: text/xml;charset=UTF-8" --header "SOAPAction:urn:addIdp" -u ${APIM_ADMIN_USERNAME}:${APIM_ADMIN_PASSWORD} --data @idp/choreo-idp.xml ${APIM_URL}/services/IdentityProviderMgtService.IdentityProviderMgtServiceHttpsSoap11Endpoint -k)
echo_results "Choreo idp added successfully" "Error while adding Choreo idp" "Choreo idp already exists"


##################################### Choreo Console SP #####################################

# Create oauth app
HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" --header "Content-Type: text/xml;charset=UTF-8" --header "SOAPAction:urn:registerOAuthApplicationData" -u ${APIM_ADMIN_USERNAME}:${APIM_ADMIN_PASSWORD} --data @sp/create-oauth2-app-console.xml ${APIM_URL}/services/OAuthAdminService.OAuthAdminServiceHttpsSoap11Endpoint/ -k)
echo_results "Console OAuth2 application added successfully" "Error while adding Console OAuth2 application" "Console OAuth2 application already exists"


HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" --header "Content-Type: application/soap+xml;charset=UTF-8" --header "SOAPAction:urn:createApplication" -u ${APIM_ADMIN_USERNAME}:${APIM_ADMIN_PASSWORD} --data @sp/create-console-sp.xml ${APIM_URL}/services/IdentityApplicationManagementService.IdentityApplicationManagementServiceHttpsSoap12Endpoint/ -k)
echo_results "Console service provider created" "Error while creating Console service provider" "Console service provider already exists"

# Get the Id of created app

HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" --header "Content-Type: application/soap+xml;charset=UTF-8" --header "SOAPAction:urn:getApplication" -u ${APIM_ADMIN_USERNAME}:${APIM_ADMIN_PASSWORD} --data @sp/get-console-app-id.xml ${APIM_URL}/services/IdentityApplicationManagementService.IdentityApplicationManagementServiceHttpsSoap12Endpoint/ -k)
echo_results "Console SP id retrieved" "Error while getting Console SP app Id"

appId=$(echo "$BODY" | xmllint --format - | perl -ne 'if (/applicationID/){ s/.*?>//; s/<.*//;print;}')
update_portal_application=$(cat sp/update-console-sp.xml)
export update_portal_application=$(echo $update_portal_application | sed "s#{APP_ID}#${appId}#g")

# Update OAuth2 app

HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" --header "Content-Type: application/soap+xml;charset=UTF-8" --header "SOAPAction:urn:updateApplication" -u ${APIM_ADMIN_USERNAME}:${APIM_ADMIN_PASSWORD} --data "$update_portal_application" ${APIM_URL}/services/IdentityApplicationManagementService.IdentityApplicationManagementServiceHttpsSoap12Endpoint/ -k)
echo_results "Console service provider updated with OAuth2 app" "Error while updating Console service provider with OAuth2 app"


##################################### Choreo APIM Devportal SP #####################################

# Create oauth app
HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" --header "Content-Type: text/xml;charset=UTF-8" --header "SOAPAction:urn:registerOAuthApplicationData" -u ${APIM_ADMIN_USERNAME}:${APIM_ADMIN_PASSWORD} --data @sp/create-oauth2-app-devportal.xml ${APIM_URL}/services/OAuthAdminService.OAuthAdminServiceHttpsSoap11Endpoint/ -k)
echo_results "Devportal OAuth2 application added successfully" "Error while adding Devportal OAuth2 application" "Devportal OAuth2 application already exists"


HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" --header "Content-Type: application/soap+xml;charset=UTF-8" --header "SOAPAction:urn:createApplication" -u ${APIM_ADMIN_USERNAME}:${APIM_ADMIN_PASSWORD} --data @sp/create-devportal-sp.xml ${APIM_URL}/services/IdentityApplicationManagementService.IdentityApplicationManagementServiceHttpsSoap12Endpoint/ -k)
echo_results "Devportal service provider created" "Error while creating Devportal service provider" "Devportal service provider already exists"

# Get the Id of created app

HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" --header "Content-Type: application/soap+xml;charset=UTF-8" --header "SOAPAction:urn:getApplication" -u ${APIM_ADMIN_USERNAME}:${APIM_ADMIN_PASSWORD} --data @sp/get-devportal-app-id.xml ${APIM_URL}/services/IdentityApplicationManagementService.IdentityApplicationManagementServiceHttpsSoap12Endpoint/ -k)
echo_results "Devportal SP id retrieved" "Error while getting Devportal SP app Id"

appId=$(echo "$BODY" | xmllint --format - | perl -ne 'if (/applicationID/){ s/.*?>//; s/<.*//;print;}')
update_portal_application=$(cat sp/update-devportal-sp.xml)
export update_portal_application=$(echo $update_portal_application | sed "s#{APP_ID}#${appId}#g")

# Update OAuth2 app

HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" --header "Content-Type: application/soap+xml;charset=UTF-8" --header "SOAPAction:urn:updateApplication" -u ${APIM_ADMIN_USERNAME}:${APIM_ADMIN_PASSWORD} --data "$update_portal_application" ${APIM_URL}/services/IdentityApplicationManagementService.IdentityApplicationManagementServiceHttpsSoap12Endpoint/ -k)
echo_results "Devportal service provider updated with OAuth2 app" "Error while updating Devportal service provider with OAuth2 app"
