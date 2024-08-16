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

# Add the DB names which needs to be restored
db_names_to_restore=()

# Change the following values according to the requirement
resource_group=""
server_name=""
subscription_id=""
elastic_pool=""
restore_time=""

# Restoring the databases
for element in "${db_names_to_restore[@]}"
do
  restoring_dbs+=("${element}_restore")
  echo "Executing the Restoring database command for db ${element}"
  az sql db restore --dest-name "${element}_restore" --name $element --resource-group $resource_group --server $server_name --subscription $subscription_id --time $restore_time --elastic-pool $elastic_pool --backup-storage-redundancy Local > ${element}_restore.json &
  sleep 10
  echo "${element} Restoring initiated with name ${element}_restore"
done

# Checking whether the databases are online

is_every_db_online=0

while [ "$is_every_db_online" != "1" ]; do
  echo "Checking for the restored DB status"
  echo "Waiting for 30 seconds until the DBs to become online"
  sleep 30
  for element in "${restoring_dbs[@]}"
  do
    status=$(az sql db show -g $resource_group -s $server_name -n $element --subscription $subscription_id | jq '.status')
    if [[ "$status" == "\"Online"\" ]]; then
      echo "Restored db ${element} is online"
      is_every_db_online=1
    else
      echo "DB ${element} is not yet online"
      is_every_db_online=0
    fi
  done
  if [ $is_every_db_online -eq 1 ]; then
    break
  fi
done

echo "Databases restored succcefully"

# Renaming the previous databases with "_old" suffix.

for element in "${db_names_to_restore[@]}"
do
  old_db="${element}_old"
  az sql db rename --name $element --new-name $old_db --resource-group $resource_group -s $server_name --subscription $subscription_id
  echo "${element} DB Renamed to ${old_db}"
done

echo "Original Databases renamed with _old suffix succesfully"

# Renaming the restored databases to the original name 

for element in "${db_names_to_restore[@]}"
do
  new_db="${element}_restore"
  az sql db rename --name $new_db --new-name $element --resource-group $resource_group -s $server_name --subscription $subscription_id
  echo "${element}_restore DB Renamed to ${element}"
done

echo "All databases restored succesfully"
