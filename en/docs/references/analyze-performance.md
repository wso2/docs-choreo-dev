# Performance Analysis

The performance Analyzer tool is an AI-based tool that generates intelligent estimations of the performance of service-based applications based on historical data. To generate the forecasts, it uses advanced Machine Learning techniques.

Making accurate performance estimations before releasing an application you developed is important due to the following reasons:

- When you create Service-level Agreements (SLAs) for your application, an accurate forecast of performance allows you to provide valuable insights relating to the scalability of the application and the ways to optimize its performance.
- You can avoid missing important performance characteristics as a result of combining multiple applications including external ones.
- You can correctly assess the cost and feasibility of running the application in a production environment.

## Assumptions

The Performance Analyzer tool generates forecasts based on the following assumptions:

- For service-based applications the overhead of non-I/O operations is negligible.
- The service/integration being evaluated is allocated sufficient resources, and therefore, it is not a bottleneck.
- Sufficient historical data is available for each I/O call considered during the analysis.

## Access the Performance Analyzer tool

To analyze the performance of a service/integration before deploying it, open the service/integration and click the **Performance Forecast** icon. 

![Access tool](../assets/img/perf-analyzer/access-perf-analyzer-tool.png){.cInlineImage-full}

## Forecast performance

Once you access the Performance Analyzer tool, the performance forecast is displayed as shown in the example below:

![Performance forecast](../assets/img/perf-analyzer/performance-forecast.png){.cInlineImage-full}

You can derive insights about the performance as follows:

- You can view the overall throughput and latency above the graphs. In the example given above, the overall forecast is as follows:

    - **Throughput**: 168.52 requests per second.
    - **Latency**: 59.34 milliseconds per request.

- The **Throughput** graph shows the throughput forecasted by the Performance Analyzer for different user counts. You can hold the pointer over a specific user count to view the forecasted throughput for that user count

    ![Throughput forecast](../assets/img/perf-analyzer/throughput-forecast.png)
    
    In the above example, the **Throughput** graph indicates that when the user count is 50 (i.e., 50 users are using the service simultaneously), the forecasted throughput is 144.79 requests per second.
    
- The **Latency** graph shows the latency forecasted by the Performance Analyzer for different user counts. You can hold the pointer over a specific user count to view the forecasted latency for that user count.

    ![Latency forecast](../assets/img/perf-analyzer/latency-forecast.png)
    
    In the above example, the **Latency** graph indicates that when the user count is 50, the forecasted time taken to process one request is 345.33 milliseconds.
    
- If you want to check the forecasted latency per API call for a specific user count, click over that user count in either the **Throughput** or **Latency** graph.

    ![Latency per connector forecast](../assets/img/perf-analyzer/latency-per-connector-forecast.png){.cInlineImage-full}
    
    In the above example, the `COVID19Client` API call takes 127.51 milliseconds and the `worldBankClient` API call takes 60.16 seconds to process one request. 




