#!/bin/bash
if [[ -z "${RUDDER_HOST}" ]]; then
  echo "Rudder host undefined. Please set the env variable for RUDDER_HOST"
  exit 1
else
  RUDDER_LOCAL_HOST="${RUDDER_HOST}"
fi
[ -e output_file.txt ] && rm output_file.txt
[ -e failure_output.txt ] && rm failure_output.txt
output=output_file.txt
failure_output=failure_output.txt
redeployArtifacts(){
    xCoreHeaderUUID=$(uuidgen)
    echo "Enter $1 UUID:"
    read -r uuid
    if [[ $uuid =~ ^\{?[A-F0-9a-f]{8}-[A-F0-9a-f]{4}-[A-F0-9a-f]{4}-[A-F0-9a-f]{4}-[A-F0-9a-f]{12}\}?$ ]]; then
        echo -e "Started redeployment process for $1  $uuid" | tee  $output

        echo "Request-X-Correlation-Id: dp-rudder-$xCoreHeaderUUID" | tee -a $output
        resp=$(curl -X POST -H "X-Correlation-Id: dp-rudder-$xCoreHeaderUUID" -s --head -i "http://$RUDDER_LOCAL_HOST/internal/admin/redeploy-$1/${uuid}")
        status_code=$(echo "$resp" | head -n 1 | cut -d ' ' -f 2)
        xCoreId=$(echo "$resp" | grep "X-Correlation-Id")

        if [[ "$status_code" -ne 200 ]] ; then
            echo "Failed in redeployment of $1. Status Received as $status_code" | tee -a $output
            echo "Response-X-Correlation-Id:  $xCoreId" | tee -a $output
            echo "$uuid" | tee -a $failure_output
            (( F_COUNTER++ ))
        else
            echo "Successful Status Received as $status_code" | tee -a $output
            echo "Response-X-Correlation-Id:  $xCoreId" | tee -a $output
            (( S_COUNTER++ ))
        fi
        echo -e "Finished redeployment process for $1" "$uuid" | tee -a $output
        echo ""
    else
        echo "Invalid $1 Id. Please Try again"
        exit 0
    fi
}

reDeployAllOrg(){
  S_COUNTER=0
  F_COUNTER=0

# shellcheck disable=SC2207
orgArray=($(curl http://"$RUDDER_LOCAL_HOST"/internal/admin/deployment/orgs | jq -r '.data[]'))

for orgId in "${orgArray[@]}"; do
  echo -e "Redeploying Org" "$orgId" | tee -a $output

  xCoreHeaderUUID=$(uuidgen)
  echo "Request-X-Correlation-Id: dp-rudder-$xCoreHeaderUUID" | tee -a $output

  resp=$(curl -X POST -H "X-Correlation-Id: dp-rudder-$xCoreHeaderUUID" -s --head -i "http://$RUDDER_LOCAL_HOST/internal/admin/redeploy-org/${orgId}")
  status_code=$(echo "$resp" | head -n 1 | cut -d ' ' -f 2)
  xCoreId=$(echo "$resp" | grep "X-Correlation-Id")

  if [[ "$status_code" -ne 200 ]] ; then
   echo "Status Received as $status_code" | tee -a $output
   echo "Response-X-Correlation-Id:  $xCoreId" | tee -a $output
   echo "$orgId" | tee -a $failure_output
   (( F_COUNTER++ ))
  else
   echo "Successful Status Received as $status_code" | tee -a $output
   echo "Response-X-Correlation-Id:  $xCoreId" | tee -a $output
   (( S_COUNTER++ ))
  fi

  echo -e "Completed redeploying Org" "$orgId" | tee -a $output
  echo "" | tee -a $output
done
echo -e "Successful Org Redeployments: " "$S_COUNTER" | tee -a $output
echo -e "Failure Org Redeployments: " "$F_COUNTER" | tee -a $output
echo "" | tee -a $output
}

PS3='Please enter your choice: '
options=("Redeploy Component" "Redeploy Environment" "Redeploy Project" "Redeploy Org"  "Redeploy all Org" "Quit")
select opt in "${options[@]}"
do
    case $opt in
        "Redeploy Component")
            redeployArtifacts "component"
            break
            ;;
        "Redeploy Environment")
            redeployArtifacts "environment"
            break
            ;;
        "Redeploy Project")
            redeployArtifacts "project"
            break
            ;;
        "Redeploy Org")
            redeployArtifacts "org"
            break
            ;;
        "Redeploy all Org")
            echo "you chose choice $REPLY which is $opt"
            reDeployAllOrg
            break
            ;;
        "Quit")
            break
            ;;
        *) echo "invalid option $REPLY";;
    esac
done
