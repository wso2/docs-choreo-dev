#!/bin/bash

show_usage_and_exit() {
   echo "Usage: $0 [-S REQUIRED_OPTION] [-K REQUIRED_OPTION] [-H REQUIRED_OPTION] [-U REQUIRED_OPTION] [-P REQUIRED_OPTION]" >&2
   echo "This script iterate through and execute database init schemas"
   echo "Mandatory arguments:"
   echo "     -S    Path to the directory/file contains schema(s)"
   echo "     -K    Name of the Azure Key Vault where database user passwords exist"
   echo "     -H    Host name of the database server"
   echo "     -U    Username of the DDL user / admin user"
   echo "     -P    Password of the DDL user / admin user"
   exit 1
}

SCRIPTS_PATH=""
KEY_VAULT_NAME=""
DATABASE_SERVER_NAME=""
DATABASE_SERVER_DDL_USER_NAME=""
DATABASE_SERVER_DDL_USER_PASSWORD=""

while getopts ":K:H:U:P:h" FLAG; do
    case $FLAG in
        K)
            KEY_VAULT_NAME=$OPTARG
            ;;
        H)
            DATABASE_SERVER_NAME=$OPTARG
            ;;
        U)
            DATABASE_SERVER_DDL_USER_NAME=$OPTARG
            ;;
        P)
            DATABASE_SERVER_DDL_USER_PASSWORD=$OPTARG
            ;;
        h)
            show_usage_and_exit
            ;;
	      \?)
            # Invalid option
            echo "Invalid option: -$OPTARG" >&2
            exit 1
            ;;
        :)
            # Missing argument for an option that requires one
            echo "Option -$OPTARG requires an argument" >&2
            exit 1
            ;;
    esac
done


# Get the database user password from the Azure Key Vault
get_database_user_password() {
  local key_vault_name=$1
  local secret_name=$2
  local __resultvar=$3

  # Check if both arguments are provided
  if [ -z "$key_vault_name" ] || [ -z "$secret_name" ]; then
    echo "Usage: get_secret <key_vault_name> <secret_name> <result_variable>"
    return 1
  fi

  local secret_value
  secret_value=$(az keyvault secret show --vault-name "$key_vault_name" --name "$secret_name" --query "value" -o tsv 2>/dev/null)

  # Check if the command was successful
  if [ $? -ne 0 ] || [ -z "$secret_value" ]; then
    return 1
  fi

  # Return the secret value
  eval "$__resultvar"="'$secret_value'"
  return 0

}

# Execute the database schema against the database
execute_database_schema() {
  local db_server_name=$1
  local db_server_ddl_user_name=$2
  local db_server_ddl_user_password=$3
  local db_name=$4
  local db_schema_file=$5

  # Check if all arguments are provided
  if [ -z "$db_server_name" ] || [ -z "$db_server_ddl_user_name" ] || [ -z "$db_server_ddl_user_password" ] || [ -z "$db_name" ] || [ -z "$db_schema_file" ]; then
    echo "Usage: execute_database_schema <db_server_name> <db_server_ddl_user_name> <db_server_ddl_user_password> <db_name> <db_schema_file>"
    return 1
  fi

  sqlcmd -S $db_server_name -U $db_server_ddl_user_name -P $db_server_ddl_user_password -d $db_name -i $db_schema_file 2>/dev/null

  # Check if the command was successful
  if [ $? -ne 0 ]; then
    return 1
  fi

  return 0

}

mkdir "intermediate_dir"

if [ -d "$SCRIPTS_PATH" ]; then
  cp -r "$SCRIPTS_PATH"/* "intermediate_dir/"
else
  cp $SCRIPTS_PATH "intermediate_dir/"
fi

# Directory to iterate over
DIRECTORY="intermediate_dir"

total_file_count=0
valid_file_count=0
successful_execution_count=0
failed_execution_count=0

# Iterate over each file in the directory
for FILE_PATH in "$DIRECTORY"/*; do
    if [ -f "$FILE_PATH" ]; then

        ((total_file_count++))

        FILE_NAME="${FILE_PATH##*/}"

        echo "Executing the script $FILE_NAME..."

        # Chose only the files with the naming format {database name}_mssql.sql
        if [[ "$FILE_NAME" == *_mssql.sql ]]; then

            ((valid_file_count++))

            # Remove the longest match of "_mssql.sql" from the end of the filename
            DATABASE_NAME="${FILE_NAME%_mssql.sql}"

            # Replace underscores with hyphens
            MODIFIED_STRING="${DATABASE_NAME//_/-}"

            # Append "-mssql-password" to the modified string
            SECRET_NAME="${MODIFIED_STRING}-mssql-password"

            if get_database_user_password "$KEY_VAULT_NAME" "$SECRET_NAME" DATABASE_USER_PASSWORD; then

                # Replace the placeholder for db user password with the actual password
                sed -i "s/\${$SECRET_NAME}/${DATABASE_USER_PASSWORD}/g" "$FILE_PATH"

                if execute_database_schema "$DATABASE_SERVER_NAME" "$DATABASE_SERVER_DDL_USER_NAME" "$DATABASE_SERVER_DDL_USER_PASSWORD" "$DATABASE_NAME" "$FILE_PATH"; then
                    ((successful_execution_count++))
                    echo "Executed the database schema successfully."
                else
                    ((failed_execution_count++))
                    echo "Error: Failed to execute the database schema file."
                fi

            else
                ((failed_execution_count++))
                echo "Error: Failed to retrieve the database user password from the Key Vault."
            fi
        else
            echo "Invalid file name format: $FILE_NAME. File name should be in the format, {database name}_mssql.sql"
        fi
        echo
    fi
done

rm -rf "intermediate_dir"

echo "Total file count: $total_file_count"
echo "Valid file count: $valid_file_count"
echo "Successfule execution count: $successful_execution_count"
echo "Failed execution count: $failed_execution_count"
