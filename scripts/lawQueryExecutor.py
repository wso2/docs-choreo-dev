import os
import pandas as pd
from datetime import datetime, timedelta
from azure.monitor.query import LogsQueryClient, LogsQueryStatus
from azure.identity import EnvironmentCredential
from azure.core.exceptions import HttpResponseError

credential = EnvironmentCredential()
client = LogsQueryClient(credential)

query = """ContainerLog | where LogEntry contains "debug" | project ContainerID, LogEntry | join kind=leftouter (KubePodInventory | extend ["ControllerName"]=trim_end(@"-[^-]*$", ControllerName) | distinct ContainerID, ControllerName) on ContainerID | where not(isempty(ControllerName)) | summarize DebugLogCount = count() by ControllerName | order by DebugLogCount"""

start_time=datetime.now() - timedelta(days=7)
end_time=datetime.now()

try:
    response_prod = client.query_workspace(
        workspace_id=os.environ.get('LOG_WORKSPACE_ID_PROD'),
        query=query,
        timespan=(start_time, end_time)
        )
    if response_prod.status == LogsQueryStatus.PARTIAL:
        error = response_prod.partial_error
        data_prod = response_prod.partial_data
        print(error.message)
    elif response_prod.status == LogsQueryStatus.SUCCESS:
        data_prod  = response_prod.tables
    for table in data_prod:
        df_prod = pd.DataFrame(data=table.rows, columns=table.columns)
        df_prod.head(20).to_json(r'debug_logs_production.json')
        df_prod.to_csv(r'debug_logs_production.csv')

except HttpResponseError as err:
    print("something fatal happened")
    print (err)

try:
    response_stg = client.query_workspace(
        workspace_id=os.environ.get('LOG_WORKSPACE_ID_STG'),
        query=query,
        timespan=(start_time, end_time)
        )
    if response_stg.status == LogsQueryStatus.PARTIAL:
        error = response_stg.partial_error
        data_stg = response_stg.partial_data
        print(error.message)
    elif response_stg.status == LogsQueryStatus.SUCCESS:
        data_stg = response_stg.tables
    for table in data_stg:
        df_stg = pd.DataFrame(data=table.rows, columns=table.columns)
        df_stg.head(20).to_json(r'debug_logs_stage.json')
        df_stg.to_csv(r'debug_logs_stage.csv')

except HttpResponseError as err:
    print("something fatal happened")
    print (err)
