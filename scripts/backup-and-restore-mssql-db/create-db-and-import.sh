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
az storage blob download -c dbnames -n db-names.txt --account-name "$storage" --account-key "$key" > db-names.txt

echo "Completed downloading the file"

databases=()

# Read the file line by line and add each line to the array
echo "Reading the file data and adding the database names to an array"
while IFS= read -r line; do
  databases+=("$line")
done < db-names.txt

echo "Creating the elastic pool to add the databases"
az sql elastic-pool create --name "$elasticPool" --resource-group "$resourceGroup" --subscription "$subscriptionId" --server "$server" -e Standard -f Gen5 --max-size 120GB -z false

# Creating the databases and importing the data to it
for element in "${databases[@]}"
do
    echo "Creating a database with the name ${element}"
    az sql db create --name "${element}" --elastic-pool "$elasticPool" --resource-group "$resourceGroup" --subscription "$subscriptionId" --server "$server" --backup-storage-redundancy Local > "${element}.json"
    echo "DB Created succesfully!"
    output_container=$(echo ${"$element" | sed 's/_//g'})
    sleep 5
    echo "Importing the data to the ${element} database from the storage account"
    az sql db import --auth-type SQL -s "$server" -n "${element}_backup" -g "$resourceGroup" -p "$password "-u "$login" --storage-key "$key" --storage-key-type StorageAccessKey --storage-uri "https://$storage.blob.core.windows.net/${output_container}container/$bacpac" --subscription "$subscriptionId" &
    sleep 5
done

