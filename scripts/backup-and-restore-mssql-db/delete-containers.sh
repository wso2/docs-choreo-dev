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
az storage blob download -c dbnames -n db-names.txt --account-name "$storage" --account-key "$key" > delete-db-storage-names.txt

echo "Completed downloading the file"

databases=()

# Read the file line by line and add each line to the array
echo "Reading the file data and adding the database names to an array"
while IFS= read -r line; do
  lines+=("$line")
done < delete-db-storage-names.txt


# Deleting the storage containers

for element in "${databases[@]}"
do
    output_container=$($element | sed 's/_//g')
    echo "Deleting ${output_container}container on $storage for $element database"
    az storage container delete --name "${output_container}container" --account-key "$key" --account-name "$storage" --subscription "$subscriptionId"
    sleep 5
done

# Deleting the storage account which contains the db-names.txt file
az storage container delete --name "dbnames" --account-key "$key" --account-name "$storage" --subscription "$subscriptionId"

echo "Deleted all the storage containers"