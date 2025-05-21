# -------------------------------------------------------------------------------------
#
# Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 Inc. and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content.
#
# --------------------------------------------------------------------------------------

# Derived from official mssql image (our base image)
FROM mcr.microsoft.com/mssql/server:2019-latest

# Switch to root user for access to apt-get install
USER root

# Create  directory
RUN mkdir -p /db-script-data
WORKDIR /db-script-data

# Bundle files
COPY initialize-db-script.sh entrypoint.sh scripts/ /db-script-data/

# Grant permissions for the initialize-db-script script to be executable
RUN chmod +x /db-script-data/initialize-db-script.sh

# Switch back to mssql user and run the entrypoint script
USER mssql
ENTRYPOINT ["/bin/bash", "./entrypoint.sh"]
