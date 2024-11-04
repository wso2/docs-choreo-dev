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

source variables.sh

function reset_database_user_password {
    local database=$1
    local user=$2
    local key_ref=$3
    local permissions=("${@:4}")
    echo "Resetting the password for user : $user"
    user_key_ref=$(echo "$key_ref" | sed 's/^"//g; s/"$//g')
    password=$(az keyvault secret show --name "$user_key_ref" --vault-name dev-csi-64 --query value)
    db_pass=$(az keyvault secret show --name mssql-administrator-login-password --vault-name choreo-dev-tf-keyvault --query value)
    db_user=$(az keyvault secret show --name mssql-administrator-login-name --vault-name choreo-dev-tf-keyvault --query value)
    db_username=$(echo "$db_user" | sed 's/^"//g; s/"$//g')
    db_password=$(echo "$db_pass" | sed 's/^"//g; s/"$//g')
    user_password=$(echo "$db_pass" | sed 's/^"//g; s/"$//g')
    sqlcmd -S "${mssql_server_name}.database.windows.net" -U "$db_username" -d "$database" -P "$db_password" -Q "DROP USER $user"
    sqlcmd -S "${mssql_server_name}.database.windows.net" -U "$db_username" -d "$database" -P "$db_password" -Q "CREATE USER $user WITH PASSWORD = $password"
    echo "Adding the permission to user : ${user}"
    for permission in "${permissions[@]}"; do
        echo "Adding permission : ${permission} to ${user}"
        permission_format=$(echo "$permission" | sed 's/^"//g; s/"$//g')
        sqlcmd -S "${mssql_server_name}.database.windows.net" -U "$db_username" -d "$database" -P "$db_password" -Q "GRANT ${permission_format} ON DATABASE::${database} TO ${user}"
    done
}

if [[ ! -d "$db_data_directory_path" ]]; then
    echo "Directory '$db_data_directory_path' does not exist."
    exit 1
fi

for file in "$db_data_directory_path"/*; do
    if [[ -f "$file" ]]; then
        database=$(basename "$file" .json)
        json_content=$(jq . < "$file")
        users=$(jq -r '.users[]' <<< "$json_content")
        db_users=($users)
        echo "Database is : $database"
        for element in "${db_users[@]}"; do
            user_ref=$(jq --arg key "$element" '.[$key]' <<< "$json_content")
            permissions=$(jq --arg key "${element}_permissions" '.[$key]' <<< "$json_content")
            user_permissions=$(jq -r '.[]' <<< "$permissions")
            array_permissions=($user_permissions)
            reset_database_user_password "$database" "$element" "$user_ref" "${array_permissions[@]}"
        done
    fi
done