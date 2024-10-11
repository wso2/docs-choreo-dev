# Monitor the Kafka Service

Use metrics and logs to monitor and ensure the health and performance of your Kafka services.


## Service Metrics

Service metrics offer real-time insights into your Kafka service’s performance. You can view these metrics by navigating to the **Metrics** tab on the service page.

The service metrics available in Choreo Console include the following:

- **CPU usage:** Shows the percentage of CPU resources consumed by the service.
- **Disk space usage:** Represents the percentage of disk space utilized by the service.
- **Disk iops (reads):** Indicates the input/output operations per second (IOPS) for disk reads.
- **Disk iops (writes):** Indicates the input/output operations per second (IOPS) for disk writes.
- **Load average:** Shows the 5-minute average CPU load, indicating the system's computational load.
- **Memory usage:** Represents the percentage of memory resources utilized by the service.
- **Network received:** Indicates the amount of network traffic received by the service, measured in bytes per second.
- **Network transmitted:** Indicates the amount of network traffic transmitted by the service, also measured in bytes per second.

## Service Logs

Service logs contain detailed records of Kafka service activity, such as producer and consumer operations, connection statuses, and error reports. Logs are crucial for troubleshooting, identifying issues, and understanding the flow of messages through your system.

You can access service logs from the **Logs** tab on the service page. Logs are retained for up to 4 days.
