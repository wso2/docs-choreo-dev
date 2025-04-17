# OpenSearch Logs Snapshot Restore

This guide provides step-by-step instructions for setting up a minimal OpenSearch cluster and restoring archived logs of a dataplane using OpenSearch snapshots.

## Prerequisites

- Kubernetes cluster access
- `kubectl` configured with appropriate permissions
- `helm` installed
- `jq` installed

## Setup and Installation

### 1. Install OpenSearch Operator

First, install the OpenSearch operator in the `observability` namespace using the following script:

```bash
chmod +x 1-install-opensearch-operator.sh
./1-install-opensearch-operator.sh
```

This script will:
- Create the `observability` namespace
- Add and update the OpenSearch operator Helm repository
- Install the OpenSearch operator
- Wait for the operator to be ready

### 2. Install OpenSearch Cluster

Next, modify the `opensearch-cluster/values.yaml` file to set the correct values for disk size and snapshot repository details and then install the Helm chart using the following script:

```bash
chmod +x 2-install-opensearch-cluster.sh
./2-install-opensearch-cluster.sh
```

This script will:
- Install the OpenSearch cluster using Helm
- Wait for the cluster health to become "green"
- Wait for all master pods to be running

## Snapshot and Restore Process

Navigate to the `snapshot-restore-scripts` directory and follow these steps:

### 1. Check Cluster Health

```bash
chmod +x 1-cluster-health.sh
./1-cluster-health.sh
```

This script verifies the health status of your OpenSearch cluster. Ensure it is in the "green" state before proceeding.

### 2. List Available Snapshots

```bash
chmod +x 2-snapshots.sh
./2-snapshots.sh
```

This script lists all available snapshots in the snapshot repository. Choose the snapshot(s) you want to restore. One snapshot contains logs for 30 days back. If you need logs for a longer period, you can restore multiple snapshots. If you need logs for a shorter period than 30 days, you can restore a set of indices in a single snapshot.

### 3. Restore Snapshots

```bash
chmod +x 3-restore-snapshots.sh
./3-restore-snapshots.sh <snapshot_name>
```

This script initiates the restoration process for the selected snapshots. Execute this script for each snapshot you want to restore.

>Note: If you want to restore logs for a shorter period than 30 days, choose the indices you want to restore and include them in the body of the restore snapshot request of the `3-restore-snapshots.sh` script.
Example:
```
echo "Restore snapshot"
curl --request POST \
     --location "https://localhost:9200/_snapshot/container-logs-automatic-snapshots/$SNAPSHOT_NAME/_restore" \
     --header 'Content-Type: application/json' \
     --header "Authorization: Basic $token" \
     --data-raw '{
        "indices": ["container-logs-2025-04-16","container-logs-2025-04-17"]
     }' \
     -k
```

### 4. Monitor Restore Status

```bash
chmod +x 4-snapshot-recovery-status.sh
./4-snapshot-recovery-status.sh
```
The script from step 3 will monitor the status of the ongoing snapshot restoration for 20 minutes. Use this script to monitor the status of the ongoing snapshot restoration if the restoration is taking longer than 20 minutes.

### 5. Fetch and Process Logs

After the restoration is complete, logs are available in the opensearch cluster and usual OpenSearch APIs can be used to query and fetch the logs. The following script is designed to fetch the logs of a specific choreo-component and a namespace, and save them to a file.

```bash
chmod +x 5-fetch-and-process-logs.sh
./5-fetch-and-process-logs.sh <choreo-component-id> <namespace>
```

Adjust the 'size' parameter in the script according to the approximate number of logs to be fetched.

## Troubleshooting

If you encounter any issues:

1. Check the status of OpenSearch pods:
   ```bash
   kubectl get pods -n observability
   ```
    3 master pods and 1 data pod should be running and ready.

2. Check OpenSearch cluster status:
   ```bash
   kubectl describe opensearchcluster -n observability
   ```
    OpenSearch cluster should be in the "green" state.

## Notes

- Ensure you have sufficient storage space for the snapshots
- The restore process may take time depending on the size of the snapshots
- Monitor the cluster health throughout the process
- Make sure all scripts have execute permissions before running them

## Support

For any issues or questions, please contact the choreo-observability team.
