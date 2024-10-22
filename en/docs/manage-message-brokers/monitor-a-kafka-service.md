# Monitor a Kafka Service

You can monitor the health and performance of your Kafka services via the service metrics and service logs.  

## Service metrics

Service metrics provide real-time insights into the performance of your Kafka service.

To view service metrics for a Kafka service, go to the **Metrics** tab on the service details page.

The service metrics include the following:

- **CPU Usage %**: Displays the percentage of CPU resources consumed by the service.
- **Disk Usage %**: Represents the percentage of disk space utilized by the service.
- **Disk IO Reads**: Displays the input/output operations per second (IOPS) for disk reads.
- **Disk IO Writes**: Displays the input/output operations per second (IOPS) for disk writes.
- **Load Average**: Displays the 5-minute average CPU load, indicating the system's computational load.
- **Memory Available %**: Represents the percentage of memory resources utilized by the service.
- **Network Received**: Indicates the volume of network traffic received by the service in bytes per second.
- **Network Sent**: Indicates the amount of network traffic transmitted by the service in bytes per second.

## Service logs

Service logs provide a detailed record of Kafka service activity, such as producer and consumer operations, connection statuses, and error reports. These logs are essential for troubleshooting issues and analyzing message flow. Logs are retained for up to 4 days.

To view Choreo-managed Kafka service logs, go to the **Logs** tab on the service details page.
