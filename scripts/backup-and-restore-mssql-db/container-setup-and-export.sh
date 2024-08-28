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

# echo "Getting the available database list of server : ${server} and elastic pool ${elasticPool}"
# databases=$(az sql db list --subscription "$subscriptionId"  --resource-group "$resourceGroup" --elastic-pool "$elasticPool" --server "$server" | jq -r '.[].name')
databases=(choreo-alerts-db choreo_usage_db)

# echo "Writing the available databases to a text file"
for line in "${databases[@]}"; do
    echo "$line" >> db-names-${currentDate}.txt
done

echo "Completed the file writing to the db-names.txt file"
sleep 5

echo "Creating the container to store the db-names-${currentDate}.txt file"  
response=$(az storage container exists --account-name "$storage" --account-key "${key}" --name dbnames --subscription "$subscriptionId")
exists=$(jq -r '.exists' <<< "$response")

if [[ "$exists" == "true" ]]; then
    echo "dbnames container already exists"
else
    echo "dbnames container not existing and creating a new one"
    az storage container create --name "dbnames" --account-key "$key" --account-name "$storage" --subscription "$subscriptionId"
fi

sleep 5

echo "Uploading the created file to the dbnames container in ${storage} Storage Account"
az storage blob upload  --account-name "$storage" --name "db-names-${currentDate}.txt" --account-key "$key"  --file db-names-${currentDate}.txt --blob-url "https://${storage}.blob.core.windows.net/dbnames/db-names-${currentDate}.txt"
echo "Upload completed"

# create a storage containers for databases
echo "Creating containers to export the databases"
for element in "${databases[@]}"
do
    output_container=$(echo $element | sed 's/-//g' | sed 's/_//g')
    response=$(az storage container exists --account-name "$storage" --account-key "${key}" --name "${output_container}container" --subscription "$subscriptionId")
    exists=$(jq -r '.exists' <<< "$response")
    if [[ "$exists" == "true" ]]; then
        echo "${output_container}container already exists"
    else
        echo "${output_container}container not existing and creating a new one"
        az storage container create --name "${output_container}container" --account-key "$key" --account-name "$storage" --subscription "$subscriptionId"
    fi
    
done
echo "Creating container for the databases are completed"

# Exporting the databases
for element in "${databases[@]}"
do
    output_container=$(echo $element | sed 's/-//g' | sed 's/_//g')
    echo "Executing the db export for the database ${element} to the storage container ${output_container}container"
    az sql db export --admin-password "$password" --admin-user "$login" --storage-key "$key" --storage-key-type StorageAccessKey --storage-uri "https://$storage.blob.core.windows.net/${output_container}container/$bacpac" --name "$element" --resource-group "$resourceGroup" --server "$server" --subscription "$subscriptionId" > "${element}.json" &
    sleep 5
done

