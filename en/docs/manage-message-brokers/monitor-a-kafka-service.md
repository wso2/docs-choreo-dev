# Monitor a Kafka Service

You can monitor the health and performance of your Kafka services via the service metrics and service logs.  

## Service metrics

Service metrics provide real-time insights into the performance of your Kafka service.

To view service metrics for a Kafka service, go to the **Metrics** tab on the service details page.

The service metrics available in the Choreo Console include the following:

- **CPU usage:** This shows the percentage of CPU resources consumed by the service.
- **Disk space usage:** Represents the percentage of disk space utilized by the service.
- **Disk read input/output operations per second (IOPS):** Indicates the IOPS for disk reads.
- **Disk write input/output operations per second (IOPS):** Indicates the IOPS for disk writes.
- **Load average:** Shows the 5-minute average CPU load, indicating the system's computational load.
- **Memory usage:** Represents the percentage of memory resources utilized by the service.
- **Network received:** Indicates the amount of network traffic received by the service, measured in bytes per second.
- **Network transmitted:** Indicates the amount of network traffic transmitted by the service in bytes per second.

## Service logs

Service logs contain detailed records of the Kafka service activity such as producer and consumer operations, connection statuses, and error reports. Logs are crucial for troubleshooting, identifying issues, and understanding the flow of messages through your system. Service logs are retained for up to 4 days.

To view service logs for a Kafka service, go to the **Logs** tab on the service details page.
