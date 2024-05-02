## Prerequisites

- Have `jq` installed

## Steps to run the script

- Connect to Azure AKS Control plane cluster with the relevant Kube config for the intended environment.
- Port forward the rudder-component service with the following command

```
kubectl port-forward svc/dp-rudder 3002:80 -n dev-choreo-system
```

- Export the environment variable to point the port forwarded rudder service using the below command.

```
export RUDDER_HOST="localhost:3002"
```

- Run the restoration script using the below command and perform the intended action.

```
bash dp-artifacts-restoration.sh
```

- At the end of the execution of the script, two files are produced namely `output_file.txt` and `failure_output.txt`.
  The first file contains the information logs for the redeployment and the second file tracks the id of the relevant
  level
  which has got failed while calling the admin API.
