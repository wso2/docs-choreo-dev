# -------------------------------------------------------------------------------------
#
# Copyright (c) 2020, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 Inc. and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
# You may not alter or remove any copyright or other notice from copies of this content.
#
# --------------------------------------------------------------------------------------

# Derived from official mysql image (our base image)
FROM mysql:5.7

# Add the content of the sql-scripts/ directory to your image
# All scripts in docker-entrypoint-initdb.d/ are automatically
# executed during container startup
COPY scripts/choreo_testbase_db.sql /docker-entrypoint-initdb.d/
COPY scripts/choreo_app_db.sql /docker-entrypoint-initdb.d/
COPY scripts/choreo_perf_db.sql /docker-entrypoint-initdb.d/
COPY scripts/choreo_program_db.sql /docker-entrypoint-initdb.d/
COPY scripts/choreo_apim_db.sql /docker-entrypoint-initdb.d/
COPY scripts/choreo_apim_shared_db.sql /docker-entrypoint-initdb.d/
COPY scripts/choreo_apim_user_db.sql /docker-entrypoint-initdb.d/
COPY scripts/configure_dbs.sql /docker-entrypoint-initdb.d/
