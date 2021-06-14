# Perform Root Cause Analysis

Choreo provides insightful observability features to drill down to the request level and confirm the root cause for anomalies you detect.

This guide describes how you can detect anomalies and perform root cause analysis on a service deployed in Choreo. For demonstration, let’s use the observability sample service available in Choreo.

    
## Step 1: Get started with the sample service
Follow this procedure to try out the sample service:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev/).
2. Go to the **Observability** card and click **Try Now**. This takes you to the **Observability** page. 
3. Click **Try Sample**. You are directed to the sample service low-code view where you can see the following:
    - Low-code diagram
    - Throughput graph   
    - Latency graph
    - Logs
    - Diagnostic data

Now you are ready to detect anomalies and perform root cause analysis on the sample service.
    
## Step 2: Detect anomalies and perform root cause analysis

To detect anomalies of the service, you must trace its executions from the code statement level to the machine it runs in. You can analyze observability data and logs to troubleshoot and identify the root cause for the anomalies you detect. 

### Analyze the throughput graph
The throughput graph depicts successful and erroneous requests to the service during a selected time interval. You can analyze the spikes in the throughput graph to identify the root cause for requests that result in an error.

Follow this procedure to analyze the throughput graph:

1. Go to the throughput graph under the **Throughput & Latency** tab and hold the pointer over data points where you see spikes. You can observe the number of errors for the corresponding fluctuations.
    
    !!!tip
        You can expand the graph by clicking and dragging the cursor over the period you want to drill down. The default graph displays data for a custom time range where the data points are at a lower granularity. This allows the effective detection of anomalies.
        
2. Click on a data point where there is a spike and observe the log entries in the **Logs** pane.
 
    ![Analyze the throughput graph](../assets/img/observability/throughput-graph-analysis.png){.cInlineImage-full}

   Here, the log displays multiple entries indicating an error in connecting to the `hr-service`.

 
### Analyze the latency graph

The latency graph depicts the latency of requests over a selected time interval. You can analyze the spikes in the latency graph to identify the API invocations that result in error status.

Follow this procedure to analyze the latency graph:

1. Go to the latency graph under the **Throughput & Latency** tab and click on a data point where there is a spike. This displays the latency, start time, and status of the corresponding requests just above the low-code diagram.
   
    !!!tip
        If you want to drill down to view details at a higher granularity, drag the cursor over a time range to view the graph for the selected period.
        
2. Click on a latency value to observe the status code for that request.
 
    ![Analyze the latency graph](../assets/img/observability/latency-graph-analysis.png){.cInlineImage-full}

   Here, the status code is `500`, which means the request resulted in an error state.

### Analyze diagnostic data

The **Diagnostic View** allows you to simultaneously analyze observability data to identify possible root causes for anomalies of a service.

Follow this procedure to analyze diagnostic data:

1. Click the **Diagnostics View** tab.
 
    ![Diagnostic data](../assets/img/observability/diagnostics-view.png){.cInlineImage-full}

    !!!info
        The time range applied to the **Diagnostics View** by default is the same time range for which you viewed the **Throughput & Latency**. 
        
2. Hold the pointer over a section where you see fluctuations in multiple bins.

    ![Fluctuations in diagnostic data](../assets/img/observability/second-bin.png){.cInlineImage-full}

    Here, the logs indicate a higher occurrence of `error while connecting to the hr-service`. Although there are other errors also displayed, those error counts are comparatively much lower.

    If you further analyze the sample diagnostic data, you can identify the connectivity error as the most common error.  In such error timestamps, you can also see an increase in the throughput and a drop in latency. 
   
Therefore, the conclusion on the root cause for the anomalous throughput and latencies in the sample service here is the connectivity issue.
