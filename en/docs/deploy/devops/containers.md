# Containers

The Containers page includes information about the containers that comprise your component, including the currently deployed image tag and the commit from which it was built as well as compute resource usage limits imposed on the containers. 

![Containers View](../../assets/img/deploy/devops/containers/containers-view.png){.cInlineImage-full}

!!! info "Resource Limits"
    Resource limits ensure that a single component does not take up more resources than it needs to, which can affect other workloads on the data plane. If your process exceeds the allocated memory limit, the respective container will be forcefully shut down and restarted. If the allocated CPU limit is exceeded, the process will be throttled (which may cause significant latencies in compute and I/O operations).

* Components in Choreo are currently limited to a single main container.

## Editing Containers

To open the edit view, click the **Edit** button the respective Container you wish to modify.

![Containers View](../../assets/img/deploy/devops/containers/edit-container-form.png){.cInlineImage-full}

### Resource Requests and Limits

Drag the sliders to modify the resource requests and limits as necessary. A resource request cannot be less than its limit.
  > You can only modify resource requests and limits on private data planes.

### Image Pull Policy

- *Always* - The image is always pulled from the container registry, even if a matching tag is already present on the data plane.
- *If Not Present* - The image is only pulled if a matching image is not present in the data plane (recommended option).

### Container Ports

Specify the container port, service port and protocol. The service port is the port that is exposed outside of the container to your Project-scoped endpoint. If you're unsure, use the container port in both fields. 
> You do not need to configure the port manually for Ballerina components. This option is provided primarily to edit ports on containerized/Dockerfile-based components. 

### Container Command and Arguments *(Advanced)*

In certain scenarios (*Eg. running legacy or third-party applications*), you may need to provide or override the `ENTRYPOINT` of a container.

> This specifies the Entrypoint array and is not executed within a shell. The container image's `ENTRYPOINT` is used if this is not provided. Variable references `$(VAR_NAME)` are expanded using the container's environment. If a variable cannot be resolved, the reference in the input string will be unchanged. Double $$ are reduced to a single $, which allows for escaping the `$(VAR_NAME)` syntax: *i.e.* `"$$(VAR_NAME)"` will produce the string literal `"$(VAR_NAME)"`. Escaped references will never be expanded, regardless of whether the variable exists or not. 

![Container command and arguments example](../../assets/img/deploy/devops/containers/example-container-cmd-and-args.png){.cInlineImage-half}
