# Scale Component Replicas

Services can be configured to automatically scale up or down in number *(i.e. the number of active replicas)* to handle traffic spikes and to also cut down on costs during periods of low usage.

!!! info "Note"
    Auto-scaling features are only available for paid subscriptions and on private data planes.<br/>
    Components will run in a single-replica, low-availability mode on the free tier.

- **Min replicas**: The minimum number of replicas to run at any given time (it is recommended to keep at least a minimum of `2`).
- **Max replicas**: The maximum number of replicas to scale up to. (This is restricted to a maximum of `5` on the cloud data plane, but is unrestricted on private data planes).
- **CPU threshold**: Once this threshold (%) of CPU usage is reached across all active instances, the number of active replicas will be scaled up until the average CPU utilization falls below this threshold.
- **Memory threshold**: Similar to the CPU threshold above, but applies to the average memory usage across all running replicas.

> Please note that it may take a while for scaling changes to propagate when modified. Changes may not be immediately visible in the Choreo Console.

!!! note "Running a fixed number of replicas"
    (As an example) If you only want to run exactly `3` replicas for a component, **set both the min and max replicas to `3`**.

![Scaling view](../../assets/img/deploy/devops/scaling/scaling-view.png){.cInlineImage-full}


!!! warning "Scaling to zero"
    While it's possible to set the minimum number of replicas to `0`, your component will not scale to zero automatically during low usage, at most it will go down to `1` replica.<br/>
    Setting min and max both to `0` will suspend the deployment.

