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

# Add the DB names which needs to be deleted
db_names_to_delete=()

# Change the following values according to the requirement
resource_group=""
server_name=""
subscription_id=""
suffix=""

# Deleting the databases
for element in "${db_names_to_delete[@]}"
do
  echo "Executing the Delete database command for db ${element}${suffix}"
  az sql db delete --name "${element}${suffix}" --resource-group "${resource_group}" --server "${server_name}" --subscription "${subscription_id}" -y
  echo "${element}${suffix} db delete succesfully"
done