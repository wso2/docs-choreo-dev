# Perform Root Cause Analysis

Choreo provides insightful observability features to drill down to the request level and confirm the root cause for anomalies you detect.
This guide describes how you can detect anomalies and perform root cause analysis on a service deployed in Choreo. 

    
## Access observability data of a service

Follow this procedure to try out the sample service:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev/).
2. Go to the **Observability** card and then click **Try Now**. This takes you to the **Observability** page. 
3. Move the pointer to the end of the row on the right for an active service, and then click **Observe**. If you do not have any active services, you can click **Try Sample**. 

    ![Access the Observability view](../assets/img/observability/access-observability.png){.cInlineImage-full}
        
You are directed to the sample service low-code view where you can see the following:

- Low-code diagram
- Throughput graph   
- Latency graph
- Logs
- Diagnostic data

Here you can detect anomalies and perform root cause analysis on the sample service.
    
## Detect anomalies and perform root cause analysis

To detect anomalies of the service, you must trace its executions from the code statement level to the machine it runs in. You can analyze observability data and logs to troubleshoot and identify the root cause for the anomalies you detect. 


### Analyze the throughput graph

The throughput graph depicts the total throughput of the service (which includes both successful and erroneous requests) during a selected time interval. You can analyze the spikes in the throughput graph to identify the root cause for requests that result in an error.
The following figure is an example of a throughput graph:

![Analyze the throughput graph](../assets/img/observability/throughput-graph-analysis.png){.cInlineImage-full}

The actions you can perform in a throughput graph are as follows:

- **View the number of successful requests and errors:**
  To do this, hold the pointer over the area of the graph that shows the required time interval. The throughput graph displays the number of successes over the graph as shown in the figure above (in this example, it displays 296 successes and 2512.43 errors). The low-code diagram displays the success rate for each API call.
    
- **View log entries:**
  If you click the required area on the graph, the log entries applicable for that period are displayed in the **Logs** pane. In this example, the log displays multiple entries indicating an error in connecting to the `hr-service`.
     
- **View data for a custom time range:**
  You can expand the graph by clicking and dragging the pointer over the period you want to drill down. The default graph displays data for a custom time range where the data points are at a lower granularity.
  
- **View details per request** 

    Once you view data for a custom time range, each request that was sent during the selected time range is displayed in a pane above the low-code diagram as shown below.
    
    ![Trace Requests](../assets/img/observability/view-details-per-request.png){.cInlineImage-full}

    This pane displays the latency, the time at which the service received the request, and the status.
    
    When you click on a specific request, the low-code diagram displays the following:
   
    - If an error has occurred, the error code is displayed near the relevant API call. In this example, the `500` error code has occurred when the request was processed by an API call.
    - The path (control flow) in which the service executed the request is highlighted in green. In this example, the highlighted path indicates that the selected request was processed on the `else` path of the first `if` statement, and on the `then` path of the second `if` statement.
    - The execution time is displayed per `if`, `while`, and `foreach` body. In this example, the execution time taken for the `if` body is `603 ??s`.
    
### Analyze the latency graph

The latency graph depicts the latency of requests over a selected time interval. You can analyze the spikes in the latency graph to identify the API invocations that result in error status.
The actions you can perform are as follows:

- **View details of a latency that has occurred:**
  To do this, go to the latency graph under the **Throughput & Latency** tab and click a data point where there is a spike. This displays the latency, start time, and status of the corresponding requests just above the low-code diagram as shown in the following figure:

    ![View latency details](../assets/img/observability/latency-details.png)
    
- **View details for a custom time range:**
   
    If you want to drill down to view details at a higher granularity, drag the pointer over a time range to view the graph for the selected period.
    
- **View details per request:**

    The latency graph allows you to view details for each individual request that the service received during a selected custom time range. For details, see [Analyze the throughput graph](#analyze-the-throughput-graph) - **View details per request** bullet point.


### Analyze diagnostic data

The **Diagnostic view** allows you to further drill down the observability data you view in the **Throughput & Latency** tab to identify possible root causes for anomalies of a service.
The following figure is an example of how you can view diagnostic data once you click on the **Diagnostics View** tab:
 
![Diagnostic data](../assets/img/observability/diagnostics-view.png){.cInlineImage-full}

!!!info
    The time range applied to the **Diagnostics view** by default is the same time range for which you viewed the **Throughput & Latency** before clicking on that tab. 
        
If you hold the pointer over a section where you see fluctuations in multiple bins, you can view the related logs as shown in the following figure:

![Fluctuations in diagnostic data](../assets/img/observability/second-bin.png){.cInlineImage-full}

In this example, the logs indicate a higher occurrence of `error while connecting to the hr-service`. Although there is another error also displayed, its error count is comparatively much lower (it has occurred only 810 times, whereas the `error while connecting to the hr-service` has occurred 1,608 times).

In the same example, if you further analyze the sample diagnostic data, you can identify the connectivity error as the most common error.  
Based on this, you can conclude that the root cause of the anomalous throughput and latencies is the connectivity issue. 
