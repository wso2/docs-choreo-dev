# Alerts

Alerts are notifications sent by the Choreo Alert Manager when the components that run in the production environment are not functioning as expected. Whenever a critical error (e.g., out-of-memory error) occurs, the component logs an error, and the Alert Manager notifies the members of your organization with admin rights via an email. This email contains a link to the [**Observability**](/observe-and-analyze/observe/observability-overview.md) tab of the component where the metrics and logs applicable to the time interval in which the error occurred are highlighted. The Alert Publisher collects and sends alerts to Alert Manager every five minutes, which then decides when to send these alerts to the users.


## Types of alert email notifications

When an alert occurs for the first time during the alerting interval, the Choreo Alert Manager sends an alert email immediately. When there are multiple occurrences of the same type of alert for a particular component, the Choreo Alert Manager suppresses the alerts for 15 minutes and generates a single email that specifies the event count to denote how many such errors occurred during an alerting interval.

## Types of alerts

### Out-of-memory alert

If the Kubernetes pod that runs your component goes out of memory, it restarts immediately. However, during that interval, the service becomes unavailable and the requests that it was processing at the time can become erroneous.
Due to this, the out-of-memory error can be very adverse for your component. Therefore, when an out-of-memory error occurs, the admin members of the organization that owns the component receive an alert email similar to the sample given below.


![Out of Memory alert email](/assets/img/alerting/oom-email.png){.cInlineImage-full}

This email contains details about your component and the number of times this event occurred within the alert monitoring period.
You can check the logs and the memory usage during that time interval by clicking **Check in Portal**. This takes you to the **Observe** tab of that particular component. You can also contact WSO2 for support via the **Contact us** link provided at the bottom of the alert email.

#### Troubleshoot out-of-memory error

When you click **Check in Portal**, you are redirected to the **Observe** tab of the component in the Choreo Console.

![OOM Troubleshooting](/assets/img/alerting/oom-troubleshooting.png){.cInlineImage-full}

The item marked **1** in the above image shows the time bin where the out-of-memory error that occurred is highlighted and the other logs that occurred during that time are displayed within the scrollable **Logs** column. The column also displays the number of times the log occurred.

Item **2** is the one-hour time range around the point of time at which the error occurred (i.e. error time +/- 30mins). In the **Diagnostic View** tab, this time range is further divided into 5 similar-sized time bins. In this scenario, the size of each bin is 12 minutes. These 12-minute bins are marked **3**.

![Diagnostic view](/assets/img/alerting/diagnostic-view-values.png){.cInlineImage-half}

When you hover over the graph, you can view the memory usage. You can note that it has increased steadily during the time interval and reached the maximum value. This causes the application to log the out-of-memory error and restart.

#### Select a custom time range

If you want to change the automatically selected time interval (i.e., one hour) for debugging purposes, do as follows:

1. Click the drop-down bar for time selection (marked in the image below).

    ![Custom Time range](/assets/img/alerting/custom-time-range.png){.cInlineImage-half}

2. Click **Custom**.
   
3. Specify the time range for which you want to view logs in the **Diagnostics View** tab by entering the required times in the **From** and **To** fields.

4. Click **Apply**.

For more information on how to use the **Observe** tab for root cause analysis, see [Root Cause Analysis](/observe-and-analyze/observe/root-cause-analysis.md).

### Application error alert

This alert is triggered when you use the **`log:printError()`** function in your component and the component logs an error via that. Such errors indicate that your component is unable to function as designed, and therefore you are notified via email so that you can troubleshoot them. The following is a sample of such a notification email. 

![Application Error email](/assets/img/alerting/application-error-email.png){.cInlineImage-full}

This email provides details about the component. You can click **Check in Portal** to open the **Observe** tab in the Choreo Console for this component. You can also get support from WSO2 via the **Contact us** link provided in the email.

#### Troubleshoot application error

Once you click **Check in Portal** in the notification email, you are redirected to the **Observe** tab of the component in the Choreo Console.

![Application Error Troubleshooting](/assets/img/alerting/application-error-troubleshooting.png){.cInlineImage-full}

This opens the **Throughput and Latency** view in which the request that caused the error log is automatically selected (see **1**  in the image above). The graphs in this tab display the throughput and latency for this request. The color of the graph indicates whether the request was successfully processed or whether it resulted in an error. 

In addition, you can observe the following:

- The error log that triggered the alert mail (marked **2** in the image).

- The time range for which the error log applies (marked **3** in the image). However, you can scroll to view more logs that occurred during the time range selected for debugging purposes. This time range is selected via the drop-down field marked **5** in the image. You can update this time range as explained under [Select a custom time range](#select-a-custom-time-range).

- The status and the latency of the request (marked **4** in the image).

### Anomaly alert 

This alert is triggered when average latency exhibits a considerable upward shift compared to what the system observed during the last five minutes for a resource function. Latency spikes and upward latency shifts are considered high latency.

**Anomaly alerting is disabled by default**. An organization administrator needs to enable it. 

Choreo alerts a user of this anomaly via email. This email includes details to identify the component and the resource function, anomaly time stamp, etc. The email has a link that takes a user to the components' latency graph in the observability view pane. A user can view the latency graph by clicking on the link **Check in Portal** in the email and thereby [perform an RCA](https://wso2.com/choreo/docs/observability/root-cause-analysis/) for the anomaly. 

The anomaly detector issues two types of alerts:
The immediate alert as as soon as the anomaly is detected
The aggregated alert in which it aggregates all the anomalies detected in the last 15 minutes. The anomaly detecter sends this following the immediate alert.
