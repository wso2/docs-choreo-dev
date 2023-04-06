# API Rate-Limiting in Choreo

Real world computation requirements are operating under finite number of computing resources. Considering this, Choreo 
provides API rate-limiting capabilities to control the traffic to your APIs. Choreo allows you to configure the 
rate-limiting considering different levels and time windows. By enabling the rate-limiting, you will be able to protect 
your APIs from malicious invocations and utilize the available computing resources efficiently. This documentation 
covers following aspects related to the API rate-limiting in Choreo.

  - [API level rate-limiting](#api-level-rate-limiting)
  - [Operation level rate-limiting](#operation-level-rate-limiting)
  - [Response headers related to the rate-limiting](#response-headers-related-to-rate-limiting)

To enable rate-limiting for a given API, you need to select the relevant API component and navigate to the **Manage** 
section. In there, under the **Resources** tab located under the **Settings** you can find the rate-limiting configurations. 
You can select the relevant time window and the required request count for the rate-limiting.The available time windows 
for the Choreo rate-limiting are `Per Minute` , `Per Hour`and `Per Day`. HTTP response code `429` will be there after 
exceeding the allocated request count.

## API level rate-limiting

API level rate limiting applies to the all the operations in the API. All the operations in the API will share the 
request count allocated for the specified time unit.

![API level rate-limit](../assets/img/manage/api-level-rate-limit.png){.cInlineImage-full}

## Operation level rate-limiting

In the operation level rate-limiting, you can configure different rate-limiting values for each operation. In this method,
you can define different rate-limiting values for each operation. This will allow you to protect the critical operations.

![Operation level rate-limit](../assets/img/manage/operation-level-rate-limit.png){.cInlineImage-full}

## Response headers related to the rate-limiting

Below table lists the response headers that are available with rate-limit enabled APIs. Considering the values that are
available in the response headers, you can have specific implementations to handle the desired rate-limiting scenarios.

| **Header Name**  | **Description** |
|------------------|-----------------|
| `x-ratelimit-limit`     | Denotes the request count allocated for the specified time unit       |
| `x-ratelimit-reset`     | Provides the time remaining to start the next rate-limiting time unit |
| `x-ratelimit-remaining` | Denotes the remaining request count for the specified time unit       |
| `x-ratelimit-enforced`  | Visible after exceeding the allocated request count                   |
