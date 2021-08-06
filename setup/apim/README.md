## APIM Service provider and IdP setup script

### Description
Execute `run.sh` to perform the following.

 1. Create a service provider for choreo-apim-service
 2. Register idp representing choreo idp on the APIM
 3. Create a service provider for the application choreo console
 4. Create a service provider for the application choreo apim devportal

### Usage
```bash
sh run -e environment
```
Where `environment` is one of the following.
`dev`, `stage`, `prod`
