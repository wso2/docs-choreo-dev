# Performance Analysis

The Performance Analyzer tool is an AI-based tool that generates intelligent estimations of the performance of integration-based services and APIs based on historical data. To generate the forecasts, it uses advanced machine learning techniques.
Making accurate performance estimations before releasing an application you've developed is important due to the following reasons:

- When you create service-level agreements (SLAs) for your application, an accurate forecast of performance allows you to provide valuable insights relating to the scalability of the application and the ways to optimize its performance.
- You can avoid missing important performance characteristics as a result of combining multiple applications including external ones.
- You can correctly assess the cost and feasibility of running the application in a production environment.

Traditionally, the above is achieved via performance debugging which involves measuring the performance of services by load testing them. The Performance Analyzer allows you to carry out performance measurement at the application development stage, significantly reducing the time and cost spent on performance debugging.

## Assumptions

The Performance Analyzer generates forecasts based on the following assumptions:

- For service-based applications the overhead of non-I/O operations is negligible.
- The service being evaluated is allocated sufficient resources, and therefore, it is not a bottleneck.
- Sufficient historical data is available for each service operation or API call considered during the analysis.