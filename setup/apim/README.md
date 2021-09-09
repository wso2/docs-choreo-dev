
## APIM Service provider and IdP setup script

### Description
Execute `run.sh` to perform the following.

 1. Create a service provider for choreo-apim-service
 2. Register idp representing choreo idp on the APIM
 3. Create a service provider for the application choreo console
 4. Create a service provider for the application choreo apim devportal
 5. Create a service provider for the application choreo step aggregator

### Usage
Export following environmental variables.
```bash
export APIM_URL="https://localhost:9443"
export APIM_ADMIN_USERNAME="admin"
export APIM_ADMIN_PASSWORD="admin"
```
Execute `run.sh`  script.

```bash
sh run.sh -e environment
```
Where `environment` is one of the following.
`dev`, `stage`, `prod`
