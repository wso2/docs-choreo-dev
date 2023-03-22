#!/bin/bash

# -------------------------------------------------------------------------------------
#
# Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 LLC. and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content."
#
# --------------------------------------------------------------------------------------

################################# Edit this part ####################################
# Add all DB Scripts here in the order that each should be executed.
# Used this approach instead of looping `"${SCRIPTS_DIR}"/*.sql` otherwise order should be
# maintained in files by prefixing 1, 2, 3.
declare -a DB_SCRIPTS=(
  "choreo_program_db_mssql.sql"
  "choreo_perf_db_mssql.sql"
  "choreo_apim_db_mssql.sql"
  "choreo_apim_shared_db_mssql.sql"
  "choreo_apim_user_db_mssql.sql"
  "choreo_testbase_db_mssql.sql"
  "choreo_app_db_mssql.sql"
)
HOST="localhost"             # hostname
USERNAME="sa"                # username
DATABASE_NAME="choreo_db"    # database name
#####################################################################################

# Waiting for server to startup as otherwise the creation of tables will not work
while true
do
  if /opt/mssql-tools/bin/sqlcmd -S "${HOST}" -U "${USERNAME}" -Q 'SELECT 1' > /dev/null; then
    break
  fi
  sleep 1s
  echo "Waiting for server to startup and user ${USERNAME} to be ready"
done

/opt/mssql-tools/bin/sqlcmd -S "${HOST}" -U "${USERNAME}" -Q 'CREATE DATABASE choreo_db'

# Run script in the defined order.
# Used this approach instead of looping `for f in "${SCRIPTS_DIR}"/*.sql; do`
for f in "${DB_SCRIPTS[@]}"; do
   echo "Executing script file: ${f}"
   /opt/mssql-tools/bin/sqlcmd -S "${HOST}" -U "${USERNAME}" -d "${DATABASE_NAME}" -i "${f}"
done
