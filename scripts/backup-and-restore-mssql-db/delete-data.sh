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

echo "Generating the key to access the storage account"
key=$(az storage account keys list --account-name "$storage" --resource-group "$resourceGroup" --subscription "$subscriptionId" -o json --query [0].value | tr -d '"')

echo "Downloading the db-names.txt file to get the availale db names for the import process"
az storage blob download -c dbnames -n db-names-${data_delete_date}.txt --account-name "$storage" --account-key "$key" > db-names-${data_delete_date}-delete.txt

echo "Completed downloading the file"

databases=()

# Read the file line by line and add each line to the array
echo "Reading the file data and adding the database names to an array"
while IFS= read -r line; do
  databases+=("$line")
done < db-names-${data_delete_date}-delete.txt

# Deleting the storage containers

for element in "${databases[@]}"
do
    output_container=$(echo "$element" | sed 's/-//g' | sed 's/_//g')
    echo "Deleting ${data_delete_date} data from ${output_container}container on $storage for $element database"
    az storage blob delete  --account-key "$key" --account-name "$storage" --blob-url "https://$storage.blob.core.windows.net/${output_container}container/${dateToRestore}-backup.bacpac" --subscription "$subscriptionId"
    sleep 5
done

echo "Deleted all the storage containers"

