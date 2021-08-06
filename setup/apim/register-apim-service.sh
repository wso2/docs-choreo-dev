#!/bin/sh

#### Set necessary values ####
ADMIN_USERNAME="admin"
ADMIN_PASSWORD="admin"
DOMAIN="localhost:9443"
##############################

TOKEN_ENDPOINT="https://${DOMAIN}/oauth2/token"
CLIENT_REGISTRATION_ENDPOINT="https://${DOMAIN}/client-registration/v0.17/register"
PUBLISHER_REST_ENDPOINT="https://${DOMAIN}/api/am/publisher"

PAYLOAD='{"callbackUrl":"http://localhost:9090","clientName":"choreo_apim_service","owner":"--APP_OWNER--","grantType":"client_credentials","saasApp":true}'

## Registering Application
ENCODED_CREDENTIALS=$(echo "$ADMIN_USERNAME:$ADMIN_PASSWORD" | tr -d '\040\011\012\015' | base64);
CLIENT_APP_REGISTRATION_PAYLOAD=$(echo ${PAYLOAD} | sed "s/--APP_OWNER--/${ADMIN_USERNAME}/g" > temp_client_app_create_payload.json)
CLIENT_APP_REGISTRATION_RESPONSE=$(curl -s -w "HTTPSTATUS:%{http_code}" -k -X POST -H "Authorization: Basic $ENCODED_CREDENTIALS" -H "Content-Type: application/json" -d @temp_client_app_create_payload.json ${CLIENT_REGISTRATION_ENDPOINT});
CLIENT_APP_REGISTRATION_BODY=$(echo ${CLIENT_APP_REGISTRATION_RESPONSE} | sed -e 's/HTTPSTATUS\:.*//g')
CLIENT_APP_REGISTRATION_STATUS=$(echo ${CLIENT_APP_REGISTRATION_RESPONSE} | sed -e 's/.*HTTPSTATUS://')
if [ "${CLIENT_APP_REGISTRATION_STATUS}" != "200" ]; then
  echo "Client Application Registration failed with status code : ${CLIENT_APP_REGISTRATION_STATUS}"
  echo "Response : ${CLIENT_APP_REGISTRATION_BODY}"
  exit 1
fi
echo "Client Application registered.";

rm temp_client_app_create_payload.json

CLIENT_ID=$(echo ${CLIENT_APP_REGISTRATION_BODY} | jq '.clientId')
CLIENT_ID=$(echo ${CLIENT_ID} | sed "s/\"//g");
CLIENT_SECRET=$(echo ${CLIENT_APP_REGISTRATION_BODY} | jq '.clientSecret')
CLIENT_SECRET=$(echo ${CLIENT_SECRET} | sed "s/\"//g");

## Test generating Access Token
ENCODED_CK_CS=$(echo "$CLIENT_ID:$CLIENT_SECRET" | tr -d '\040\011\012\015' | base64);
ACCESS_TOKEN_RESPONSE=$(curl -s -w "HTTPSTATUS:%{http_code}" -k -H "Authorization: Basic ${ENCODED_CK_CS}" -H "Content-Type: application/x-www-form-urlencoded" -d 'grant_type=client_credentials&scope=apim:api_view' ${TOKEN_ENDPOINT});
ACCESS_TOKEN_BODY=$(echo ${ACCESS_TOKEN_RESPONSE} | sed -e 's/HTTPSTATUS\:.*//g')
ACCESS_TOKEN_STATUS=$(echo ${ACCESS_TOKEN_RESPONSE} | sed -e 's/.*HTTPSTATUS://')
if [ "${ACCESS_TOKEN_STATUS}" != "200" ]; then
  echo "Access Token generation failed with status code : ${ACCESS_TOKEN_STATUS}"
  echo "Response : ${ACCESS_TOKEN_BODY}"
  echo "Something wrong with the above generated application or the system"
  exit 1
fi

echo "Client ID: ${CLIENT_ID}"
echo "Client Secret: ${CLIENT_SECRET}"
echo "Configure the above two values in apim-service configs"
