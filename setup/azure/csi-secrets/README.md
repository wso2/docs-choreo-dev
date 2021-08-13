### How to run Key Vault Secrets/Certs uploader script

##### Step 1

This script comes up with 4 properties files.

* choreo-system-secrets.properties

  >This properties file contains the secrets which need to be added to system key vault.
  >
  >Here the dummy secret value `xxxxxxxxxxxxxxxxxxxx` should be replace with the actual secret value.

* choreo-apim-secrets.properties

  >This properties file contains the secrets which need to be added to apim key vault.
  >
  >Here the dummy secret value `xxxxxxxxxxxxxxxxxxxx` should be replace with the actual secret value.
  
* choreo-apim-pems.properties

  >This properties file contains the .pem files which need to be added to apim key vault as secrets.
  >
  >Here the dummy .pem file path `/path/to/pemfile` should be replace with the actual file path.
  
* choreo-apim-certs.properties

  >This properties file contains the certificate which need to be added to apim key vault as certificates.
  >
  >Here the dummy cert file path `/path/to/certificate` should be replace with the actual file path.

##### Step 2

Run the bash scripts as shown as below with suitable arguments.

```bash kv-secret-uploader.sh -v <vault_name> -i <input_file> -o <output_file> -t <type>```

* -v or --vault 
  
  Name of the Azure Key Vault in which secrets/certs need to be created
* -i or --input 

  Path of the properties file created in Step 1
* -o or --output

  Path of the output file to which obeject versions of created secrets/certs will get printed

* -t or --type

  Type of secrets will get created in this execution. Possible values are:-

   - secret
     > Use for secrets which are provided as key-value pairs. `choreo-system-secrets.properties` and `choreo-apim-secrets.properties` are the possible input properties files that can be used with this type
   - pem
     > Use for secrets which are provided as .pem files. `choreo-apim-pems.properties` is the possible input properties file that can be used with this type
   - cert 
     > Use for addition of certificates. `choreo-apim-certs.properties` is the possible input properties file that can be used with this type

Example:-

```bash kv-secret-uploader.sh -v dev-csi-64 -i choreo-system-secrets.properties -o object-versions.txt -t secret```

<br>

Once the command is successfully executed, object versions of the created secrets will be printed in the output file in below format.

> --- Object versions of the secrets/certificates ---
> 
> secret5=2203ea3d2f994765bd7b77610bd5d8b7
> 
> secret6=866d89360e9a43a781b51fc60cc3eecc
> 
> secret7=1a324546f2b14612bc217d826c7213b3
