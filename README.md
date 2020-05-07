# choreo-control-plane
The Choreo control plane repo contains all Kubernetes scripts which are related to cluster setup & configuration.
The `setup` directory contains scripts required to initial cluster setup, sealed secret generation and sealed ingress 
certificate generation. The `kustomize` directory contains the `base` Kubernetes scripts, in addition to the 
environment specific scripts for the `prod`, `stage`, `dev` & `local` environments.

This is the central gitops repo for the Kubernetes artifacts.

## First time setup
1. Copy secret properties file to the relevant directory
   For example, copy the files to secret/loc if you want to setup local environment.
   The following files need to be there.

    ### app-db.properties
    ```
    CHOREO_DB_PASSWORD=xxx
    ```
    
    ### appservice.properties
    ```
    PLATFORMER_RUDDER_PASSWORD=xxx
    IDP_CLIENT_SECRET=xxx
    GITHUB_TOKEN=xxx
    REDIS_PASSWORD=xxx
    ```
    
    ### loggingapi.properties
    ```
    AZURE_ACTIVEDIRECTORY_CLIENT_SECRET=xxx
    ```
    
    ### obsapi.properties
    ```
    AZURE_TIMESERIESINSIGHTS_CLIENT_SECRET=xxx
    ```
    
    ### perfanalyzer.properties
    ```
    AZURE_TIMESERIESINSIGHTS_CLIENT_SECRET=xxx
    CHOREO_PERF_DB_PASSWORD=xxx
    ```
    
    ### program-db.properties
    ```
    CHOREO_DB_PASSWORD=xxx
    ```
    
    ### telemetry.properties
    ```
    EVENTHUB_SHARED_ACCESS_SIGNATURE_KEY=xxx
    ```
    
    ### trace-db.properties
    ```
    CHOREO_DB_PASSWORD=xxx
    ```

2. Run `setup.sh` by providing the secret directory as the argument.

    ex: `./setup.sh -d=secret/loc -e=dev -i=true` for local setup with secret properties files in the directory as given
    in step 1, and with the -i=true option to create ingresses.

3. Run `kustomize build <env> | kubectl apply-f -` to generate and apply K8s artifacts related to a particular environment.

   ex: `kustomize build loc | kubectl apply-f -`

4. *(Local setup only)* Add the following entries to your /etc/hosts file

   ex:
    ```
    192.168.64.6 	programanalyzer.dev.choreo.local periscope.dev.choreo.local dev.choreo.local prod.choreo.local stage.choreo.local
    ```

   Use the proper node IP address.
   
5. Test whether everything is working fine by running the hello-service sample in 
    [https://github.com/wso2-enterprise/choreo.git](https://github.com/wso2-enterprise/choreo.git)   

    For local setup, add the following to the ballerina.conf of the sample:
    ```
    [b7a.observability.tracing.choreo.reporter]
    hostname="dev.choreo.local"
    port=443
    ```

That is it! All set!

## Change secrets
If you wish to change a secret, edit the relevant secret properties file and run `secretgen.sh` for the relevant environment
   ex: `./secretgen.sh -d=secret/loc -n=loc-choreo-system -o=../kustomize/loc/secret`

Next, run `kustomize build <env> | kubectl apply-f -`

## Change config
If you wish to change config in the relevant kustomize.yaml file which contains the configMapGenerator section,
and run `kustomize build <env> | kubectl apply-f -`

## TODO:
create PVs for each env (PVs don't have a namespace)
Refactor all configs into properties or YAML files

