# How to Add a New Super Tenant IDP

## Setup Directory and Metadata Files
1. **Create a New Directory**: Navigate to the `sts-configs/idps` directory and create a new directory with the name of the IDP.
2. **Create Metadata XMLs**: Place the IDP metadata XML files (`create_idp.xml`, `delete_idp.xml`) into the newly created directory (`sts-configs/idps/<your-idp>`), following the format of existing IDPs.

## Update Configuration
3. **Update Common Configuration**: Add new IDP related configurations to the `common.sh` file.

# How to Create an IDP in the STS

## Execution
1. **Run the Script**: Execute the `create_idps.sh` script located in the `sts-configs` directory.

# How to Add a New Super Tenant SP

## Setup Directory and Metadata Files
1. **Create a New Directory**: Navigate to the `sts-configs/sps` directory and create a new directory with the name of the SP.
2. **Create Metadata XMLs**: Place the SP metadata XML files (`create_sp_payload.xml`, `get_sp_payload.xml`, `update_sp_payload.xml`, `create_oauth_app_payload.xml`) into the newly created directory (`sts-configs/sps/<your-sp>`), following the format of existing SPs.
3. **Add Payload Files**: Add payload files to the `sts-configs/sps` directory and update them with the correct SP-related unique values.

## Update Configuration
4. **Update Common Configuration**: Add new SP related configurations to the `common.sh` file.
# How to Create an SP in the STS

## Prerequisites

Before adding a new IDP or SP, ensure that the following environment variables are set up with suitable values in the `sts-configs/common.sh` file.

```bash
ENVIRONMENT_PREFIX="perf"  # Modify as necessary for environment (e.g., dev, stage, prod)
APIM_ADMIN_USERNAME="admin"  # Replace with APIM admin username
APIM_ADMIN_PASSWORD="admin"  # Replace with APIM admin password
APIM_URL="https://localhost:9443"  # Modify with the URL to APIM
````

## Execution
1. **Run the Script**: Execute the `create_sps.sh` script located in the `sts-configs` directory.
