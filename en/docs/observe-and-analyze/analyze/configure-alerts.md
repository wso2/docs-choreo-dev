# Configure Alerts

This section shows how you can configure alerts for your API manager deployments. These alerts allow you to proactively monitor your API ecosystem and take corrective measures for any abnormalities you find.

You can configure alerts for each environment within your organization. You can configure and add new, modify, or delete alerts per API. Optionally, you can specify a list of emails for each alert configuration.

Alerts are subject to a suppression policy to ensure that you do not receive duplicate alert notifications within a specific time interval. A 10-minute suppression window is applied by default, and it is not configurable at present. Note that the suppression policy is applicable per alert configuration.

!!! info
    - The maximum number of alerts that can be configured is 20 for each organization, environment, and tenant combination.
    - Adding an email to an alert configuration is optional. The maximum number of emails per alert configuration is limited to 5.
    
The alert can be a [latency alert](#latency-alerts) or a [traffic alert](#traffic-alerts).

## Latency alerts

Configure latency alerts to be notified if the response latency of APIs is greater than a predefined threshold. This is useful when you have APIs that should honor SLAs and when you want to know about slow APIs proactively.

To configure a new latency alert, follow the steps below:

1. In the **Insights** page, click **Alert Configuration** to open the **Alert Configuration** page.

	![Configure alerts](../../assets/img/insights/alert-configuration.png){.cInlineImage-full}

    The **Latency** tab is open by default.

2. Verify that you are in the correct organization, and select the required environment. 

3. In the **API Name** field, select the API for which you want to configure the alert.

    !!! Info
        Only the APIs that you have invoked at least once are listed here. For other APIs, you need to give the API name in the required format as instructed in the UI.
        
4. In the **Metric** field, select the required metric against which you want to evaluate the alert configuration.

    !!! tip
        The list includes all available options. If there are multiple metrics, you can select the required metric. If there is only one metric to select, that metric is selected by default, and the field is disabled. 

5. In the **Latency** field, specify the threshold in milliseconds.

    !!! info
        When the 95th percentile of the selected metric exceeds the threshold provided here, alerts are triggered.
        
6. If required, specify the list of emails that should be notified when the alert is generated in the **Emails** field.
        
7. Click **Add**.

Once an alert is successfully added, the alert configuration  is displayed in the table in the lower section of the page. Each configuration can be edited and removed using this table. If multiple emails are configured, they are displayed in a comma-separated list.

## Traffic alerts

Configure Traffic alerts to identify if the request count of APIs is greater than a predefined threshold. This is useful when you have APIs that are using backends with traffic limits or monetized backends that require your system to proactively scale depending on the incoming traffic.

To configure a new traffic alert, follow the steps below:

1. In the **Insights** page, click **Alert Configuration** to open the **Alert Configuration** page.

2. Click **Traffic** to open the **Traffic** tab.

3. Verify that you are in the correct organization, and select the required environment. 

4. In the **API Name** field, select the API for which you want to configure the alert.

    !!! Info
        Only the APIs that you have invoked at least once are listed here. For other APIs, you need to give the API name in the required format as instructed in the UI.
        
5. In the **Metric** field, select the required metric against which you want to evaluate the alert configuration.

    !!! tip
        The list includes all available options. If there are multiple metrics, you can select the required metric. If there is only one metric to select, that metric is selected by default, and the field is disabled.
        
6. In the **Threshold** field, specify the threshold number of requests per minute.

7. If required, specify the list of emails that should be notified when the alert is generated in the **Emails** field.

8. Click **Add**.

When added successfully, the alert configuration is displayed in the table in the lower section of the page. Each configuration can be edited and removed using this table. If multiple emails are configured, they are displayed in a comma-separated list.