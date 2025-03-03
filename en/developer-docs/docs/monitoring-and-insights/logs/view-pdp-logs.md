# View Private Data Plane (PDP) Logs 

Choreo offers the capability to access runtime logs through its console. However, in cases where viewing logs for your PDP is not supported by Choreo yet, you can still view the runtime logs of your components via the log analyzing solution provided by your cloud vendor as a workaround.

## Prerequisites

Before you try out this guide, complete the following:

1. Go to [https://console.choreo.dev/](https://console.choreo.dev/), and sign in using your preferred method.
2. Select your component from **Components Listing**. This will open the **Overview** page of your component.
3. In the left navigation menu, click **Runtime** under **DevOps**.
4. Copy the `Release ID` and the `Namespace`. Save it for later.

## View Private Data Plane (PDP) logs with Azure Log Analytics

You can view your PDP logs with Azure Log Analytics by following the steps below: 

1. Go to https://portal.azure.com/.
2. Follow the [Azure Log Analytics Tutorial](https://learn.microsoft.com/en-us/azure/azure-monitor/logs/log-analytics-tutorial#open-log-analytics) and open log analytics of your relative log analytics workspace.
3. Copy and paste the query below into the query editor. 
4. Replace the `<START_TIME_STAMP EX: 2023-04-10T07:07:31.684Z>` and `<END_TIME_STAMP EX: 2023-04-21T07:27:31.684Z>` values as required. Replace the '<RELEASE_ID>' and '<NAMESPACE>' with the values you copied by following the steps in the [prerequisites](#prerequisites) section. Replace the `<OPTIONAL SEARCH PHRASE>` with your search term, or leave it blank if you don't require any search filtering.
5. Run the query to extract the relevant logs.

```SQL
let startDateTime = datetime('<START_TIME_STAMP EX: 2023-04-10T07:07:31.684Z>');
let endDateTime = datetime('<END_TIME_STAMP EX: 2023-04-21T07:27:31.684Z>');
let releaseId = '<RELEASE_ID>';
let namespace = '<NAMESPACE>';
let searchPhrase = '<OPTIONAL SEARCH PHRASE>';
let startDateTimeKPI = iff(datetime_diff('second', endDateTime, startDateTime) > 60, startDateTime, endDateTime - 2m);let endDateTimeKPI = iff(datetime_diff('second', endDateTime, startDateTime) > 60, endDateTime, startDateTime + 2m);let filteredLogLevels = dynamic([]);
let hasNoLevelFilter = array_length(filteredLogLevels) == 0;
let commonKeys = dynamic(['time', 'level', 'module', 'traceId', 'spanId', 'message']);
let ContainerIdList = KubePodInventory
| where TimeGenerated > startDateTimeKPI and TimeGenerated < endDateTimeKPI
| where Namespace == namespace
| where extractjson('$.[0].release_id', PodLabel) == releaseId
| distinct ContainerID;
let data = ContainerLog
| where TimeGenerated > startDateTime and TimeGenerated < endDateTime
| where ContainerID in (ContainerIdList)
| where searchPhrase == "" or LogEntry contains searchPhrase
| top 126 by TimeGenerated desc
| extend logs = parse_json(LogEntry)
| project TimeGenerated, 
LogLevel = iif(isempty(logs['level']), iff(LogEntrySource == 'stderr', 'ERROR', 'INFO'), logs['level']), 
LogEntry = iif(isempty(logs['message']), logs, logs['message']),
KeyValuePair = bag_remove_keys(logs, commonKeys)
| where hasNoLevelFilter or LogLevel in (filteredLogLevels);
let lastTimeStamp = data 
| top 1 by TimeGenerated asc | project TimeGenerated;
let trimmedData = data | where TimeGenerated > toscalar(lastTimeStamp)| sort by TimeGenerated desc;
let selected = iff(toscalar(data | count) == 126, 'trimmedData', 'data');
let choose = (selector:string){   union   (trimmedData | where selector == 'trimmedData'),    (data | where selector == 'data')};
choose(selected);
```

## View Private Data Plane (PDP) logs with Amazon CloudWatch

1. Go to https://portal.azure.com/.
2. Follow the [AWS Analyzing Log Data documentation](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/AnalyzingLogData.html) and open **Log Insights**.
3. Select the relevant region and the relevant log group (the log group string has the cluster name and “/application” appended at the end).
4. Select the required time range. 
4. Copy the query below and paste it into the query editor. 
5. Replace the `<RELEASE_ID>` with the values you copied by following the steps in the [prerequisites](#prerequisites) section. Replace the `<OPTIONAL SEARCH PHRASE>` with your search term, or leave it blank if you don't require any search filtering.
6. Run the query to extract the logs.

```  SQL 
fields @timestamp, @message
| filter kubernetes.labels.release_id == "<RELEASE_ID>"
| filter @message like "<OPTIONAL SEARCH PHRASE>"
```
