# Root Cause Analysis

Performing root cause analysis is crucial in identifying and rectifying the underlying problem for any anomalies detected in a system. Choreo Observability provides helpful insights out-of-the-box that assists in carrying out a root cause analysis for any service deployed on the Choreo platform.

!!! info
    For explanation, we are using the sample data available in the product.
    
## Get started with the sample service

1. Go to **Services** from the left navigation.
2. If this is your first service, then enter a name for the service and click **Create**. Skip step 3 and go to step 4.
       ![Create a new service](/assets/img/observability/first-service-creation.png)
3. Else, click **+ Create** to create a new service.
       ![Create a new service](/assets/img/observability/service-creation.png)
    1. Enter a name for the service and click **Create**.
4. From the left-hand menu, click **Observe**.
5. On the right-hand bottom, click the **Sample Service** icon.
       ![Sample service popup](/assets/img/observability/sample-service-pop-up.png)
6. Click **Try Sample Service**.

       ![Try sample service](/assets/img/observability/try-sample-service.png)

  
    
## Detecting anomalies and performing root cause analysis
You can detect the anomalies of the usage of a service by observing the **Throughput & Latency** graphs. The throughput graph shows the successful and erroneous requests that occurred during a period. The latency graph shows the latency of each request. You can observe these graphs at a lower granularity by choosing a shorter time range, which gives you a better understanding of the incidents.

### Analysing the throughput graph
The throughput graph will show the throughput of requests per hour for a selected timestamp. 

With the sample data, you will observe spikes in the throughput graph. Let's see how you can find
the root cause by analyzing this data.

Follow the steps below. 

1. Hover over the throughput graph at 01/04/2021 13:41:00. You can observe that 46.06 errors had occurred at the time. 
    
    !!!tip
        You can expand the graph by clicking and dragging the cursor over the period you want to drill down.
        
2. Click on the graph on 01/04/2021 at 13:41:00.
3. Observe the logs in the logs panel.
4. Note multiple logs are mentioning a connection error with the hr-service.
 
![Analyzing the throughput graph](/assets/img/observability/throughput-graph-analysis.png)
 
### Analyzing the latency graph

The latency graph shows the latencies of each request.

With the sample data, you will observe spikes in the latency graph. By analyzing the latency graph, you can identify which API invocation caused the error.  Let's see how to find the
root cause by analyzing this data.
![Analyzing the latency graph](/assets/img/observability/latency-graph-analysis.png)

Follow the steps below.

1. Hover over the latency graph and click at 01/04/2021 13:41:00.
2. Observe the latencies of each request listed in the low-code diagram.
3. Click on one request shown on the low code diagram (latencies). You can observe the status code for that request and thereby identify the exact API invocation that caused any error(s).

### Diagnostics View
The Diagnostics view provides the capability to view errors, throughput, latency, CPU, memory, and logs simultaneously for a particular event. You can detect and analyze errors and anomalies in detail via the diagnostics view.

![Diagnostic View](/assets/img/observability/diagnostics-view.png)

We refer to a horizontal section of the graph for a particular period as the **bin**. A bin consists of the following.

- **Start time and end time**- Denoted by the dotted lines separating each bin.
- **Logs**- Any logs that appeared during the bin's time range are listed on the logs column, along with the timestamp the logs started to appear and the log count for each log. The logs are listed according to precedence. The error logs will be listed first, followed by the info logs. Each bin shows a maximum of five log entries sorted based on the log count.
- **Error**- Number of HTTP errors occurred at the selected time.
- **TP**- Throughput of the requests at the selected time (req/s).  
- **Latency**- Latency of the request at the selected time (ms).
- **CPU**- CPU usage at the selected time (milicores).
- **Memory**- Memory usage at the selected time (MiB)

Let's see how to find the root cause by analyzing this data.

Follow the steps below.

1. Click on the **Diagnostics View** tab on the left panel. 

    !!!info
        The time range selected for the **Throughput & Latency** graphs will be applied to the **Diagnostics View** by default. We recommend capturing the time range you want to analyze in detail from the **Throughput & Latency** graph and then navigate to the **Diagnostics View**.
        
2. In the sample data, you can observe a couple of prominent errors appearing in the four bins.
3. In the second bin during the period 01/04/2021 13:28:59 to 01/04/2021 13:41:59, you can observe 2420 occurrences of the error log "error while connecting to the hr-service”, along with other errors which occurred at a lesser frequency.

    ![Diagnostic View - second bin(/assets/img/observability/second-bin.png/)
](/assets/img/observability/second-bin.png/)

4. Furthermore, when you look at the graphs at 17/02/2021 13:41:00, you can find a peak with 2221 error. At this exact timestamp, notice an increase in the throughput and a drop in latency. 
5. By analyzing the graphs as above, you can confirm that the cause for the latency drop and the throughput spike is the connectivity issue displayed by the error logs.
6. Observe that the error graph is also fluctuating over time as the service logs errors.
7.  You can observe similar behavior in the third and fourth bins as well.

   ![Diagnostic View - other bin](/assets/img/observability/other-bins.png)
   
   
Therefore, now you can conclude that the connectivity issue is the root cause for the intermittent anomalies detected in the throughput and the latencies of requests.
