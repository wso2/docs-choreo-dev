# Configure Alerts

This section explains how you can configure alerts for your Choreo components. Setting up alerts allows you to proactively monitor your components ecosystem and take corrective measures when necessary.

!!! tip
    Setting up alerts in only available in the component level.

## Alert Types

Choreo supports the following types of alerts to help you monitor and manage your components effectively:

- [Latency alerts](#latency-alerts)
- [Traffic alerts](#traffic-alerts)
- [Resource alerts](#resource-alerts)
- [Log alerts](#log-alerts)
- [Build failure alerts](#build-failure-alerts)
- [Status code alerts](#status-code-alerts)

### Latency Alerts

Latency alerts notify you if the response latency of a component exceeds a predefined threshold in a given time period. This is useful for components that need to meet specific SLAs and for proactively identifying slow components.

Configurable parameters

| **Parameter** | **Description**                                                                 |
|---------------|---------------------------------------------------------------------------------|
| Metric        | 99th, 95th, 90th, or 50th percentile.                                           |
| Threshold     | Latency in milliseconds (e.g.: 1800).                                           |
| Period        | Duration the threshold must be exceeded (e.g.: 5 minutes).                      |

### Traffic Alerts

Traffic alerts notify you when the request count of a component exceeds a predefined threshold. This is useful for managing components with backend traffic limits or monetized backends that require proactive scaling based on incoming traffic.

Configurable parameters

| **Parameter** | **Description**                                                                 |
|---------------|---------------------------------------------------------------------------------|
| Threshold     | Requests per minute (e.g.: 200).                                                |
| Period        | Monitoring window (e.g.: 5 minutes).                                            |

### Resource Alerts

Resource alerts notify you when your component’s CPU or memory usage exceeds the defined thresholds. This ensures you can fix the resources allocations early to avoid performance issues or downtimes.

Configurable parameters

| **Parameter** | **Description**                                                                 |
|---------------|---------------------------------------------------------------------------------|
| Metric        | CPU or Memory.                                                                  |
| Threshold     | **mCPU** for CPU and **MiB** for Memory(e.g.: 1000).                            |
| Period        | Duration the threshold must be exceeded (e.g.: 5 minutes).                      |

!!! Tip
    - **CPU**: mCPU (milliCPU) measures CPU usage in fractions of a core, where 1000m = 1 full core.
    - **Memory**: MiB (Mebibyte) measures memory in binary units, where 1 MiB = 2^20^ bytes.

### Log Alerts

Log alerts trigger notifications when a specific phrase appears **a specific number of times** in your component logs within a defined Time window. This helps to identify recurring issues or critical errors quickly, enabling faster troubleshooting.

Configurable parameters

| **Parameter** | **Description**                                                                 |
|---------------|---------------------------------------------------------------------------------|
| Search Phrase | Keyword or phrase to look for to trigger the alert (e.g.: failed).                                               |
| Count         | Minimum number of occurrences to trigger the alert (e.g.: 10).                  |
| Interval      | Time window for counting occurrences (e.g.: 5 minutes).                         |

### Build Failure Alerts

Build failure alerts inform you if a build failure occurs for your component. This is essential for maintaining smooth development workflows.

### Status Code Alerts

Status code alert triggers when your component returns specific HTTP error(s) (e.g.: **403** Forbidden, **500** Internal Error). These alerts help to detect issues affecting your component’s availability.

Configurable parameters

| **Parameter** | **Description**                                                                 |
|---------------|---------------------------------------------------------------------------------|
| Status Code   | Error code or series (e.g.: 400:Bad Request).                                   |
| Count         | Minimum number of occurrences (e.g.: 5).                                        |
| Interval      | Time window (e.g.: 5 minutes).                                                  |

!!! note
    Status code alerts are only supported for API proxy component types.

## Configure Alert

Follow these steps to configure an alert:

1. Navigate to the component you wish to configure alerts for.

    !!! info
        You need to have a scope of **Choreo DevOps** or **Choreo Platform Engineer** inorder to create Alerts.  

2. In the Choreo left menu, click **Observability** and then click **Alerts**.
3. Click **Create Alert Rule** to create a new alert rule.

    ![Create Alert Rule](../../assets/img/monitoring-and-insights/alerts/alert-creation.png){.cInlineImage-full}

4. Select the **[Alert Type](#alert-types)** you want to create.
5. Select the **Environment** you want to create the alert for.
6. Select the **Deployment Track** or **Version** as required for the component.
7. Configure the remaining fields specific to your selected alert type.
8. In the **Emails** field, specify the list of emails that should be notified when the alert is triggered.

    !!! note
        - When adding an email, enter the required email and press enter to add it.
        - You can add a maximum of 5 email addresses per alert.

9. You can configure additional parameters in **Advanced Configurations** dropdown as needed, which vary based on your alert type.
10. The **Explanation window** provides a concise summary of the configured alert based on your alert configurations.
11. Click **Create** to save and activate your alert rule.

    !!! info
        - You can configure a maximum of 10 alerts per component.

12. Once successfully added, your alert will be listed in the **Configure Alerts** pane alongside any existing alerts for the component.
13. Each alert can be **edited**, **removed** and **disabled** or **enabled** via this pane.

    !!! note
        when editing an alert, you can't edit the **Alert Type**, **Environment** and **Deployment Track**.

## Alert History & Notifications

### View Alert History

You can check the past alerts that have triggered for your component when you click the  **Alerts History** pane in Choreo Alerts. You can filter the alert history by **Alert Type**, **Environment**, **Deployment Track** or **Version** and **Time Range**.

!!! note  
    When filtering, **API Proxy components** show a **Version** filter and other components display a **Deployment Track** filter, based on their monitoring context.  

You can click on an alert to expand it and see more details of the triggered alert.

### Email Notifications

When an alert is triggered, **recipients** added to the alert rule receive an email with **alert details** including a direct **Alert View link** to Choreo console.

![Email Notification](../../assets/img/monitoring-and-insights/alerts/email-notification.png){.cInlineImage-full}
