#!/bin/bash
# -------------------------------------------------------------------------------------
#
# Copyright (c) 2024, WSO2 LLC (http://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 LLC and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content.
#
# --------------------------------------------------------------------------------------

source credentials.sh


key=$(az storage account keys list --account-name "$storage" --resource-group "$resourceGroup" --subscription "$subscriptionId" -o json --query [0].value | tr -d '"')


echo "Downloading the db-names-${currentDate}.txt file to get the availale db names for the import process"

az storage blob download -c dbnames -n "db-names-${currentDate}.txt" --account-name "$storage" --account-key "$key" > "db-names-${currentDate}-check.txt"

failed_exports=()

# Read the file line by line and add each line to the array
echo "Reading the file data and adding the database names to an array"

while IFS= read -r line; do
  databases+=("$line")
done < db-names-"${currentDate}"-check.txt

for element in "${databases[@]}"
do
    json_content=$(jq . < "${element}".json)
    parameter_value=$(jq -r '.status' <<< "$json_content")
    if [[ "$parameter_value" == "Completed" ]]; then
        echo "DB ${element} exported correctly"
    else
        failed_exports+=("$element")
        echo "DB ${element} does not exported correctly"
    fi

done


echo "Following are the databases that failed to export......"
echo "................................."
echo "................................."

for element in "${failed_exports[@]}"
do
    echo "${element} failed to export"
done
