# Health Checks

Health Checks ensure that your running containers are always healthy and ready to serve traffic.

## Liveness Probes

Liveness probes run periodically on your container and restarts it if the probe fails.
<br/>
This allows your containers to self-heal in scenarios where the application may have crashed or become unresponsive.

## Readiness Probes

Similar to livess probes, readiness probes run periodically throughout the lifecycle of your container.
<br/>
But unlike livess probes it will not restart the container if the probe fails, instead it will stop the container from receiving network traffic.

!!! warning "Readiness probes on single replicas"
    Be mindful when configuring readiness probes on a single running replica. If the readiness probe fails your application will stop receiving traffic altogether as there is only a single active replica. <br/>
    The application may not recover unless the liveness probe (if configured) also fails and restarts the container.

## Probe Types

The following probe types can be configured for both readiness and liveness probes.

### HTTP `GET` Request

A HTTP `GET` request is sent to a specified port and path on the container. A response status code within the range of 200-399 is considered a success.

- Additional HTTP headers can be configured as needed.
- It is recommended to create a `/healthz` or `/health` endpoint on your service as per convention for this purpose.

![HTTP GET Probe](../../assets/img/deploy/devops/healthchecks/http-get-probe.png){.cInlineImage-half}

### TCP Connection Probe

The probe will attempt to open a socket to the container on the specified port.</br>
If it cannot establish a TCP connection it will be considered a failure.

### Execute a command

The probe will execute the given script inside the container. A non-zero return from the command will be considered a failure.
Eg. `["cat", "/tmp/healthy"]` will be considered healthy if the file `/tmp/healthy` is present, if not it will be considered a failure (non-zero exit code).</br>
The application would be responsible for writing and mainting this file in the specified location in this scenario.

## Configure Liveness and Readiness Probes

1. Click **Create** on the Health Checks view.

2. Configure the liveness and readiness probes as necessary (both are optional). 
> Probes can be edited and/or removed later at any time.

3. Once configured, go to the **Runtime** view, refresh and ensure that your application works as expected. If the container does not start check the **events and conditions** to see if any of the probes are causing the container to fail.

![HTTP GET Probe](../../assets/img/deploy/devops/healthchecks/confgure-probes.png){.cInlineImage-full}
