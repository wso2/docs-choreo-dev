# Choreo Insights API

Choreo Insights API is a GraphQL API that allows you to fetch data in a flexible manner by defining the criteria with precision. This guide explains the different ways in which you can fetch data via this API.

 - **Allowed operations:** [Queries](#query)
 - **Schema defined types:** [Objects](#objects), [Inputs](#inputs), [Enums](#enums) and [Scalars](#scalars)

## Query
The query root of the Choreo Insights GraphQL API.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>listAllAPI</strong></td>
<td valign="top">[<a href="#api">API</a>]</td>
<td>

Fetches all the APIs with the given combination of tenant ID, environment ID, and organization ID. Optionally, you
can configure the `provider` parameter to further filter the APIs by a specific provider.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">provider</td>
<td valign="top"><a href="#string">String</a></td>
<td>

The name of the API provider

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data Filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>listApplications</strong></td>
<td valign="top">[<a href="#application">Application</a>]</td>
<td>

Fetches all the APIs with the given combination of tenant ID, environment ID, and organization ID. Optionally, you
can configure the `applicationFilter` parameter to further filter the Applications by a specific application filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">applicationFilter</td>
<td valign="top"><a href="#applicationfilter">ApplicationFilter</a></td>
<td>

Application filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data Filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>listProviders</strong></td>
<td valign="top">[<a href="#provider">Provider</a>]</td>
<td>

Fetches all the APIs with the given combination of tenant ID, environment ID, and organization ID.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data Filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>listSubscribers</strong></td>
<td valign="top">[<a href="#subscriber">Subscriber</a>]</td>
<td>

Fetches all the APIs with the given combination of tenant ID, environment ID, and organization ID. Optionally, you
can configure the `subscriberFilter` parameter to further filter the subscribers.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">subscriberFilter</td>
<td valign="top"><a href="#subscriberfilter">SubscriberFilter</a></td>
<td>

Subscriber filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>listEnvironments</strong></td>
<td valign="top">[<a href="#environment">Environment</a>]</td>
<td>

Fetches all the environments of an organization.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">org</td>
<td valign="top"><a href="#orgfilter">OrgFilter</a>!</td>
<td>

Organization filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>listTenants</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

Fetches all the tenants with the given combination of environment ID and organization ID.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">tenantDataFilter</td>
<td valign="top"><a href="#tenantdatafilter">TenantDataFilter</a>!</td>
<td>

Tenant Filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getTotalTraffic</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Returns the total traffic during the given time range for the specified combination of tenant ID, environment ID,
and organization ID.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">filter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getTotalErrors</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Returns the total number of errors that occurred during the given time range for the specified combination of tenant
ID, environment ID, and organization ID.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">filter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getOverallLatency</strong></td>
<td valign="top"><a href="#float">Float</a></td>
<td>

Returns the overall response latency (95th percentile value) during the given time range for the specified
combination of tenant ID, environment ID, and organization ID.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">filter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getLatencySummary</strong></td>
<td valign="top"><a href="#latencysummary">LatencySummary</a></td>
<td>

Returns the response latency value (95th percentile value) for each time granularity within the specified time range
for the given combination of tenant ID, environment ID, and organization ID. Optionally, you can configure the
`latencySummaryFilter` parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">filter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">latencySummaryFilter</td>
<td valign="top"><a href="#latencysummaryfilter">LatencySummaryFilter</a></td>
<td>

Latency summary filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getSuccessSummary</strong></td>
<td valign="top"><a href="#successsummary">SuccessSummary</a></td>
<td>

Returns the number of successful (2xx response code) hit count for each time granularity within the specified time
range for the given combination of tenant ID, environment ID, and organization ID. Optionally, you can configure the
`granularity` parameter to override the default granularity value.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">filter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">granularity</td>
<td valign="top"><a href="#string">String</a></td>
<td>

Granularity value

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getErrorSummary</strong></td>
<td valign="top"><a href="#errorsummary">ErrorSummary</a></td>
<td>

Returns the number of unsuccessful (total of both 4xx and 5xx response codes) hit count for each time granularity
within the specified time range for the given combination of tenant ID, environment ID, and organization ID.
Optionally, you can configure the `granularity` parameter to override the default granularity value.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">filter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">granularity</td>
<td valign="top"><a href="#string">String</a></td>
<td>

Granularity value

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getErrorsByCategory</strong></td>
<td valign="top"><a href="#errorsbycategory">ErrorsByCategory</a></td>
<td>

Returns the number of proxy errors (total of both 4xx and 5xx response codes) hit count by each error category for
each time granularity within the specified time range for the given combination of tenant ID, environment ID, and
organization ID. Optionally, you can configure the `errorsByCategoryFilter` parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">errorsByCategoryFilter</td>
<td valign="top"><a href="#errorsbycategoryfilter">ErrorsByCategoryFilter</a></td>
<td>

Errors by category filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getErrorsDetails</strong></td>
<td valign="top"><a href="#detailsoferrors">DetailsOfErrors</a></td>
<td>

Returns details of each proxy error (for both 4xx and 5xx response codes) within the specified time range for the
given combination of tenant ID, environment ID, and organization ID. Optionally, you can configure the
`errorsDetailsFilter` parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">errorsDetailsFilter</td>
<td valign="top"><a href="#errorsdetailsfilter">ErrorsDetailsFilter</a></td>
<td>

Error details filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getProxyTargetErrorsOverTime</strong></td>
<td valign="top">[<a href="#errorsbystatuscodecategory">ErrorsByStatusCodeCategory</a>]</td>
<td>

Returns a list of hit counts for proxy and target errors for each time granularity within the specified time range
for the given combination of tenant ID, environment ID, and organization ID. The errors in each proxy and target
result set are grouped as `4xx`, `5xx`, and `total`. Optionally, you can configure the `errorsByStatusCodeFilter`
parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">errorsByStatusCodeFilter</td>
<td valign="top"><a href="#errorsbystatuscodefilter">ErrorsByStatusCodeFilter</a></td>
<td>

Errors by status code filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getErrorsByStatusCode</strong></td>
<td valign="top"><a href="#errorsbystatuscode">ErrorsByStatusCode</a></td>
<td>

Returns the hit count for each error response code (401, 404, and 500 etc.,) for each API within the specified time
range for the given combination of tenant ID, environment ID, and organization ID.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">errorCountByStatusCodeFilter</td>
<td valign="top"><a href="#errorcountbystatuscodefilter">ErrorCountByStatusCodeFilter</a></td>
<td>

Error count by status code filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getTotalErrorsByAPI</strong></td>
<td valign="top"><a href="#totalerror">TotalError</a></td>
<td>

Returns the total proxy error hit count for an API across for the given combination of tenant ID, environment ID,
and organization ID. Optionally, you can configure the `filter` parameter to further filter the results by a given
time range.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">filter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a></td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">apiId</td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

API Id

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getAPIUsageOverTime</strong></td>
<td valign="top">[<a href="#apiusageovertime">APIUsageOverTime</a>]</td>
<td>

Returns a list of usage details for each API over time for each time granularity within the specified time range for
the given combination of tenant ID, environment ID, and organization ID. Optionally, you can configure the
`apiUsageOvertimeFilter` parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">apiUsageOvertimeFilter</td>
<td valign="top"><a href="#apiusageovertimefilter">APIUsageOverTimeFilter</a></td>
<td>

API usage overtime time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getAPIUsageByAppOverTime</strong></td>
<td valign="top"><a href="#apiusagebyappovertime">APIUsageByAppOverTime</a></td>
<td>

Returns API usage by each application over time for each time granularity within the specified time range for the
given combination of tenant ID, environment ID, and organization ID. Optionally, you can configure the
`apiUsageOvertimeFilter` parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">apiUsageOvertimeFilter</td>
<td valign="top"><a href="#apiusageovertimefilter">APIUsageOverTimeFilter</a></td>
<td>

API usage overtime filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getAPIUsageByBackendOverTime</strong></td>
<td valign="top"><a href="#apiusagebybackendovertime">APIUsageByBackendOverTime</a></td>
<td>

Returns API usage by each backend over time for each time granularity within the specified time range for the given
combination of tenant ID, environment ID, and organization ID. Optionally, you can configure the
`apiUsageByBackendOverTimeFilter` parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">apiUsageByBackendOverTimeFilter</td>
<td valign="top"><a href="#apiusagebybackendovertimefilter">APIUsageByBackendOverTimeFilter</a></td>
<td>

API usage by backend overtime filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getResourceUsage</strong></td>
<td valign="top"><a href="#resourceusages">ResourceUsages</a></td>
<td>

Returns API usage by resource within the specified time range for the given combination of tenant ID, environment
ID, and organization ID. Optionally, you can configure the `resourceUsageFilter` parameter to further filter the
results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">resourceUsageFilter</td>
<td valign="top"><a href="#resourceusagefilter">ResourceUsageFilter</a></td>
<td>

Resource usage filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getTotalTrafficByAPI</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Returns the total hit count for an API for the given combination of tenant ID, environment ID, and organization ID.
Optionally, you can configure the `filter` parameter to further filter the results by a given time range.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">filter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a></td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">apiId</td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

API Id

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>topSlowestAPIs</strong></td>
<td valign="top">[<a href="#slowapi">SlowAPI</a>]</td>
<td>

Returns a list of top slowest APIs based on the response latency (95th percentile value) within the specified time
range for the given combination of tenant ID, environment ID, and organization ID.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">filter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">limit</td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Limit for the slow APIs list

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getLatency</strong></td>
<td valign="top"><a href="#apilatency">APILatency</a></td>
<td>

Returns different latency category values (95th percentile values) for a given API over time for each time
granularity within the specified time range for the given combination of tenant ID, environment ID, and organization
ID.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">latencyFilter</td>
<td valign="top"><a href="#latencyfilter">LatencyFilter</a>!</td>
<td>

Latency filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getOverallLatencyByAPI</strong></td>
<td valign="top"><a href="#overalllatency">OverallLatency</a></td>
<td>

Returns the overall response latency (95th percentile value) for an API for the given combination of tenant ID,
environment ID, and organization ID. Optionally, you can configure the `filter` parameter to further filter the
results by a given time range.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">filter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a></td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">apiId</td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

API Id

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getCacheHitsAndMisses</strong></td>
<td valign="top"><a href="#cachehits">CacheHits</a></td>
<td>

Return the total number of response cache hits, misses and hit percentage for an API over time for each time
granularity within the specified time range for the given combination of tenant ID, environment ID, and
organization ID.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">cacheFilter</td>
<td valign="top"><a href="#cachefilter">CacheFilter</a>!</td>
<td>

Cache filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getTopPlatforms</strong></td>
<td valign="top">[<a href="#platform">Platform</a>]</td>
<td>

Returns a list of top platforms ranked based on the hit count within the specified time range for the given
combination of tenant ID, environment ID, and organization ID. The result list includes the top nine platforms with
the respective hit count, and all the rest of the platforms are labelled as **Other** with the cumulated hit count.
Optionally, you can configure the `deviceFilter` parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">deviceFilter</td>
<td valign="top"><a href="#devicefilter">DeviceFilter</a></td>
<td>

Device filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getTopUserAgents</strong></td>
<td valign="top">[<a href="#useragent">UserAgent</a>]</td>
<td>

Returns a list of top user agents ranked based on the hit count within the specified time range for the given
combination of tenant ID, environment ID, and organization ID. The result list includes the top nine user agents
with the respective hit count, and all the rest of the platforms are labelled as **Other** with the cumulated hit
count. Optionally, you can configure the `deviceFilter` parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">deviceFilter</td>
<td valign="top"><a href="#devicefilter">DeviceFilter</a></td>
<td>

Device filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getAPIUsageByGeoLocation</strong></td>
<td valign="top"><a href="#apiusagebygeolocation">APIUsageByGeoLocation</a></td>
<td>

NOTE: The data required for this operation is not yet collected in the database. Hence, this operation will return
empty results.

Returns API usage by country within the specified time range for the given combination of tenant ID, environment ID,
and organization ID. Optionally, you can configure the `geoLocationFilter` parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">geoLocationFilter</td>
<td valign="top"><a href="#geolocationfilter">GeoLocationFilter</a></td>
<td>

Geo location filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getAPIsUsageByApplications</strong></td>
<td valign="top">[<a href="#apiusagebyapplication">APIUsageByApplication</a>]</td>
<td>

Returns usage details of each API by application within the specified time range for the given combination of
tenant ID, environment ID, and organization ID. Optionally, you can configure the `apiUsageByAppFilter` parameter to
further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">apiUsageByAppFilter</td>
<td valign="top"><a href="#apiusagebyappfilter">APIUsageByAppFilter</a></td>
<td>

API usage by app filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getTopAPIsByAlertCount</strong></td>
<td valign="top"><a href="#topapisbyalertcount">TopAPIsByAlertCount</a></td>
<td>

Returns top APIs ranked based on the alert count within the specified time range for the given combination of tenant
ID, environment ID, and organization ID. Optionally, you can configure the `topAPIsByAlertCountFilter` parameter to
further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">topAPIsByAlertCountFilter</td>
<td valign="top"><a href="#topapisbyalertcountfilter">TopAPIsByAlertCountFilter</a></td>
<td>

Top APIs by alert count filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getAlertSummary</strong></td>
<td valign="top"><a href="#alertsummaries">AlertSummaries</a></td>
<td>

Returns a summary for each alert within the specified time range for the given combination of tenant ID,
environment ID, and organization ID. Optionally, you can configure the `alertSummaryFilter` parameter to further
filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">alertSummaryFilter</td>
<td valign="top"><a href="#alertsummaryfilter">AlertSummaryFilter</a></td>
<td>

Alert summary filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

Data filter

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getSuccessAPIsUsageByApplications</strong></td>
<td valign="top">[<a href="#successapiusagebyapplication">SuccessAPIUsageByApplication</a>]</td>
<td>

NOTE: This operations can only be invoked via using an on-prem key as the authentication header.

Returns a list of successful hit counts (i.e., 2xx response codes) within the specified time range for each API,
grouped by each application. Optionally, you can configure the `successAPIUsageByAppFilter` parameter to further
filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

Time filter

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">successAPIUsageByAppFilter</td>
<td valign="top"><a href="#successapiusagebyappfilter">SuccessAPIUsageByAppFilter</a></td>
<td>

Success API usage by APP filter

</td>
</tr>
</tbody>
</table>

## Objects

### API

Represents API details.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The ID of the API.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>name</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

API name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>version</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

API version.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>provider</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The API provider name.

</td>
</tr>
</tbody>
</table>

### APILatency

Represents latency values for a single API over time.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>summary</strong></td>
<td valign="top">[<a href="#latency">Latency</a>]</td>
<td>

A list containing latency category values over time. Returns an empty array if no data is available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The granularity value for which data is retrieved.

</td>
</tr>
</tbody>
</table>

### APIUsage

Represents the API usage for a single timestamp. The timestamp is calculated based on the granularity value provided.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>timeSpan</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Represents a timestamp value that is calculated based on the specified granularity value. For example, granularity
value is `1d`, the timestamp represents the start of the day calculated adhering to the timezone provided in the
time filter.
e.g., timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The API usage count.

</td>
</tr>
</tbody>
</table>

### APIUsageByApp

Represents API usage across APIs by a single application.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>applicationId</strong></td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The application ID.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationName</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The application name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationOwner</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The name of the application owner.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>usage</strong></td>
<td valign="top">[<a href="#apiusage">APIUsage</a>]</td>
<td>

A list containing the APIs usage values. Returns an empty array if no data is available.

</td>
</tr>
</tbody>
</table>

### APIUsageByAppOverTime

Represents API usage across APIs by applications over time.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>usage</strong></td>
<td valign="top">[<a href="#apiusagebyapp">APIUsageByApp</a>]</td>
<td>

A list containing the API usage values across APIs by each application. Returns an empty array if no data is
available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The granularity value for which data is retrieved.

</td>
</tr>
</tbody>
</table>

### APIUsageByApplication

Represents a single API usage by a single application.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The ID of the API.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationId</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The Application Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationName</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The application name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationOwner</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The name of the application owner.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The usage of the API by the application.

</td>
</tr>
</tbody>
</table>

### APIUsageByBackend

Represents API usage across APIs by a single backend.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>backend</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The name of the backend.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>usage</strong></td>
<td valign="top">[<a href="#apiusage">APIUsage</a>]</td>
<td>

A list containing the APIs usage values. Returns an empty array if no data is available.

</td>
</tr>
</tbody>
</table>

### APIUsageByBackendOverTime

Represents API usage across APIs by each backend over time.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>usage</strong></td>
<td valign="top">[<a href="#apiusagebybackend">APIUsageByBackend</a>]</td>
<td>

A list containing the API usage values across APIs by each backend. Returns an empty array if no data is available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The granularity value for which data is retrieved.

</td>
</tr>
</tbody>
</table>

### APIUsageByGeoLocation

Represents a single API usage across different countries.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#id">ID</a></td>
<td>

The ID of the API.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>usageByGeoLocation</strong></td>
<td valign="top">[<a href="#usagebygeolocation">UsageByGeoLocation</a>]</td>
<td>

A List containing usages of API in different countries. Returns an empty array if no data is available.

</td>
</tr>
</tbody>
</table>

### APIUsageOverTime

Represents a single API usage over time.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#id">ID</a></td>
<td>

The ID of the API.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>usage</strong></td>
<td valign="top">[<a href="#apiusage">APIUsage</a>]</td>
<td>

A list containing the API usage values. Returns an empty array if no data is available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The granularity value for which data is retrieved.

</td>
</tr>
</tbody>
</table>

### AlertSummaries

Represents alert summaries.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>usage</strong></td>
<td valign="top">[<a href="#alertsummary">AlertSummary</a>]</td>
<td>

A list containing alert summary for each alert. Returns an empty array if no data is available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>pagination</strong></td>
<td valign="top"><a href="#pagination">Pagination</a>!</td>
<td>

Pagination details.

</td>
</tr>
</tbody>
</table>

### AlertSummary

Represents a summary for an alert.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The ID of the API.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>timestamp</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Represents a timestamp value that is calculated based on the specified granularity value. For example, granularity
value is `1d`, the timestamp represents the start of the day calculated adhering to the timezone provided in the
time filter.
e.g., timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>category</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The alert category. Possible values are `LATENCY` and `TRAFFIC`.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>metric</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The alert metric. Possible values are `RESPONSE_LATENCY` and `TOTAL_TRAFFIC`.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>severity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The severity level of the alert. Possible values are `LOW`, `MEDIUM`, and `HIGH`.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>message</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Alert details.

</td>
</tr>
</tbody>
</table>

### Application

Represents application details.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The Application ID.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>name</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The application name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>owner</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The name of the application owner.

</td>
</tr>
</tbody>
</table>

### CacheHit

Represents the response cache hits and misses for a single timestamp. The timestamp is calculated based on the
granularity value provided.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>timeSpan</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Represents a timestamp value that is calculated based on the specified granularity value. For example, granularity
value is `1d`, the timestamp represents the start of the day calculated adhering to the timezone provided in the
time filter.
e.g., timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hits</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The number of times the response cache was hit.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>misses</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The number of times the response cache was missed.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hitPercentage</strong></td>
<td valign="top"><a href="#float">Float</a>!</td>
<td>

The response cache hits as a percentage of the sum of hits and misses.

</td>
</tr>
</tbody>
</table>

### CacheHits

Represents the response cache hits and misses over time.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>summary</strong></td>
<td valign="top">[<a href="#cachehit">CacheHit</a>]</td>
<td>

A list containing cache hits, misses, and the hit percentage over time. Returns an empty array if no data is
available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The granularity value for which data is retrieved.

</td>
</tr>
</tbody>
</table>

### DetailsOfErrors

Represents error details over time.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>usage</strong></td>
<td valign="top">[<a href="#errordetails">ErrorDetails</a>]!</td>
<td>

A list containing error details over time. Returns an empty array if no data is available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>pagination</strong></td>
<td valign="top"><a href="#pagination">Pagination</a>!</td>
<td>

Pagination details.

</td>
</tr>
</tbody>
</table>

### Environment

Represents an environment.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>id</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The environment ID.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>name</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The environment name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>type</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The environment type. A Choreo environment is labelled as `CHOREO`, and the on premise environment is labelled as
`ON_PREM`.

</td>
</tr>
</tbody>
</table>

### ErrorCountByCategory

Represents the number of errors from each category for a single timestamp. The timestamp is calculated according to the
provided granularity value. If you select some categories, the error counts are retrieved only for those categories,
and a `null` value is shown for the other categories that are not selected.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>timeSpan</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Represents a timestamp value that is calculated based on the specified granularity value. For example, granularity
value is `1d`, the timestamp represents the start of the day calculated adhering to the timezone provided in the
time filter.
e.g., timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>auth</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The count of authorization errors.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>targetConnectivity</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The count of target connectivity errors.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>throttled</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The count of throttling errors.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>other</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The count of other errors.

</td>
</tr>
</tbody>
</table>

### ErrorCountByCode

Represents the error count for a single error status code.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>statusCode</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The error status code.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The error count.

</td>
</tr>
</tbody>
</table>

### ErrorCountByCodeForAPI

Represents the error count for each status code for a single API.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The ID of the API.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>errorCountByCode</strong></td>
<td valign="top">[<a href="#errorcountbycode">ErrorCountByCode</a>]</td>
<td>

A list containing the error count for each error status code. Returns an empty array if no data is available.

</td>
</tr>
</tbody>
</table>

### ErrorDetails

Represents details of an error for a single timestamp. The timestamp is calculated according to the granularity value
provided.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#id">ID</a></td>
<td>

The ID of the API.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>timeSpan</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Represents a timestamp value that is calculated based on the specified granularity value. For example, granularity
value is `1d`, the timestamp represents the start of the day calculated adhering to the timezone provided in the
time filter.
e.g., timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationId</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The Application ID.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationName</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The application name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationOwner</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The name of the application owner.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>reason</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The reason for the error.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The error count.

</td>
</tr>
</tbody>
</table>

### ErrorStatusCodeCategoryCounts

Represents errors by the main status code (4xx or 5xx) and the total error count.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>_4xx</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The number of client errors.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>_5xx</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The number of server errors.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>total</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The total number of client and server errors.

</td>
</tr>
</tbody>
</table>

### ErrorSummary

Provides the unsuccessful usage (i.e., 4xx and 5xx response codes) summary across all APIs.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>summary</strong></td>
<td valign="top">[<a href="#errorvalue">ErrorValue</a>]!</td>
<td>

A list containing unsuccessful usage values. Returns an empty array if no data is available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The granularity value for which data is retrieved.

</td>
</tr>
</tbody>
</table>

### ErrorValue

Represents the unsuccessful request count (i.e., 4xx and 5xx response codes) for a single timestamp. The timestamp is
calculated based on the granularity value provided.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>timeSpan</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Represents a timestamp value that is calculated based on the specified granularity value. For example, granularity
value is `1d`, the timestamp represents the start of the day calculated adhering to the timezone provided in the
time filter.
e.g., timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>errorCount</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Represents the error request count.

</td>
</tr>
</tbody>
</table>

### ErrorsByCategory

Represents the errors by category over time.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>errors</strong></td>
<td valign="top">[<a href="#errorcountbycategory">ErrorCountByCategory</a>]</td>
<td>

A list containing the error count for each error category over time. Returns an empty array if no data is available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The granularity value for which data is retrieved.

</td>
</tr>
</tbody>
</table>

### ErrorsByStatusCode

Represents the error counts grouped by status code for each API.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>errors</strong></td>
<td valign="top">[<a href="#errorcountbycodeforapi">ErrorCountByCodeForAPI</a>]!</td>
<td>

A list containing the error count for each status code of each API. Returns an empty array if no data is available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>pagination</strong></td>
<td valign="top"><a href="#pagination">Pagination</a>!</td>
<td>

Pagination details.

</td>
</tr>
</tbody>
</table>

### ErrorsByStatusCodeCategory

Represents the error category values (grouped as `4xx`, `5xx`, and `total`) for both proxy and target errors for a
single timestamp. The timestamp is calculated based on the granularity value provided.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>timeSpan</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Represents a timestamp value that is calculated based on the specified granularity value. For example, granularity
value is `1d`, the timestamp represents the start of the day calculated adhering to the timezone provided in the
time filter.
e.g., timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>proxy</strong></td>
<td valign="top"><a href="#errorstatuscodecategorycounts">ErrorStatusCodeCategoryCounts</a></td>
<td>

Represents proxy-related errors categorized as client errors (`4xx` response codes) or server errors (`5xx` response
codes), and the total of both categories.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>target</strong></td>
<td valign="top"><a href="#errorstatuscodecategorycounts">ErrorStatusCodeCategoryCounts</a></td>
<td>

Represents target-related errors categorized as client errors (`4xx` response codes) or server errors (`5xx`
response codes), and the total of both categories.

</td>
</tr>
</tbody>
</table>

### Latency

Represents latency values for each latency category for a single timestamp. The timestamp is calculated based on the
granularity value provided.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>timeSpan</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Represents a timestamp value that is calculated based on the specified granularity value. For example, granularity
value is `1d`, the timestamp represents the start of the day calculated adhering to the timezone provided in the
time filter.
e.g., timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>response</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The response latency (95th percentile) in milliseconds (ms).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>backend</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The backend latency (95th percentile) in milliseconds (ms).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>requestMediation</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The request mediation latency (95th percentile) in milliseconds (ms).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>responseMediation</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The response mediation latency (95th percentile) in milliseconds (ms).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>responseMedian</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The median (50th percentile) of the response latency in milliseconds (ms).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>backendMedian</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The median (50th percentile) of the backend latency in milliseconds (ms).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>requestMediationMedian</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The median (50th percentile) of the request mediation latency in milliseconds (ms).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>responseMediationMedian</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The median (50th percentile) of the response mediation latency in milliseconds (ms).

</td>
</tr>
</tbody>
</table>

### LatencySummary

Provides the latency summary.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>summary</strong></td>
<td valign="top">[<a href="#latencyvalue">LatencyValue</a>]!</td>
<td>

A list containing latency values. Returns an empty array if no data is available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The granularity value for which data is retrieved.

</td>
</tr>
</tbody>
</table>

### LatencyValue

Represents the latency value for a single timestamp. The timestamp is calculated according to the provided granularity
value.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>timeSpan</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Represents a timestamp value that is calculated based on the specified granularity value. For example, granularity
value is `1d`, the timestamp represents the start of the day calculated adhering to the timezone provided in the
time filter.
e.g., timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>latencyTime</strong></td>
<td valign="top"><a href="#float">Float</a>!</td>
<td>

Represents the latency (95th percentile) time in milliseconds (ms).

</td>
</tr>
</tbody>
</table>

### OverallLatency

Represents the overall latency values.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>response</strong></td>
<td valign="top"><a href="#float">Float</a></td>
<td>

The response latency (95th percentile value) in milliseconds (ms).

</td>
</tr>
</tbody>
</table>

### Pagination

Represents pagination details.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>total</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The total number of results.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>limit</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The number of items in the result set.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>offset</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The offset value for the result set.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>sortBy</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The sorting column name of the result set.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>sortOrder</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The sorting order of the result set. Possible values are `asc` and `desc`.

</td>
</tr>
</tbody>
</table>

### Platform

Represents a platform.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>platform</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The name of the platform.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The usage of the platform.

</td>
</tr>
</tbody>
</table>

### Provider

Represents API Provider details.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>name</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The API provider name.

</td>
</tr>
</tbody>
</table>

### ResourceUsage

Represents the usage of a single API resource.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The ID of the API.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>apiResourceTemplate</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The API resource template.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>apiMethod</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The API method.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The usage of the API resource.

</td>
</tr>
</tbody>
</table>

### ResourceUsages

Represents API resource usages.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>usage</strong></td>
<td valign="top">[<a href="#resourceusage">ResourceUsage</a>]</td>
<td>

A list with the usage of each API resource. Returns an empty array if no data is available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>pagination</strong></td>
<td valign="top"><a href="#pagination">Pagination</a>!</td>
<td>

Pagination details.

</td>
</tr>
</tbody>
</table>

### SlowAPI

A slow API identified based on the response latency (95th percentile).

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The ID of the API.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>latency</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Represents the latency (95th percentile) time in milliseconds (ms).

</td>
</tr>
</tbody>
</table>

### Subscriber

Represents API Subscriber details.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>name</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The name of the API subscriber.

</td>
</tr>
</tbody>
</table>

### SuccessAPIUsageByApplication

Represents successful usages(that have resulted in the `2xx` response code) of an API by an application.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The ID of the API.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>apiName</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The name of the API.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>apiVersion</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The version of the API.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>apiCreatorTenantDomain</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The tenant domain of the API creator.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationId</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The application ID.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationName</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The name of the application.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationOwner</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The name of the application owner.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td></td>
</tr>
</tbody>
</table>

### SuccessSummary

Provides the successful (2xx response code) usage summary across all the APIs.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>summary</strong></td>
<td valign="top">[<a href="#successvalue">SuccessValue</a>]!</td>
<td>

A list containing successful usage values. Returns an empty array if no data is available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The granularity value for which data is retrieved.

</td>
</tr>
</tbody>
</table>

### SuccessValue

Represents the successful request count (i.e., requests that have received the `2xx` response code) for a single
timestamp. The timestamp is calculated according to the granularity value provided.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>timeSpan</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Represents a timestamp value that is calculated based on the specified granularity value. For example, granularity
value is `1d`, the timestamp represents the start of the day calculated adhering to the timezone provided in the
time filter.
e.g., timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>requestCount</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Represents the successful request count.

</td>
</tr>
</tbody>
</table>

### TopAPIByAlertCount

Represents a top API by alert count.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The ID of the API.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The alert count.

</td>
</tr>
</tbody>
</table>

### TopAPIsByAlertCount

Represents the top APIs by alert count.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>usage</strong></td>
<td valign="top">[<a href="#topapibyalertcount">TopAPIByAlertCount</a>]</td>
<td>

A list containing top APIs by alert count. Returns an empty array if no data is available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>pagination</strong></td>
<td valign="top"><a href="#pagination">Pagination</a>!</td>
<td>

Pagination details.

</td>
</tr>
</tbody>
</table>

### TotalError

Represents the total errors.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>proxy</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The proxy-related error count.

</td>
</tr>
</tbody>
</table>

### UsageByGeoLocation

Represents the usage of a single API in a single country.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>country</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The name of the country.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The usage of the API.

</td>
</tr>
</tbody>
</table>

### UserAgent

Represents a user agent.

<table>
<thead>
<tr>
<th align="left">Field</th>
<th align="right">Argument</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>userAgent</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The name of the user agent.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The usage of the user agent.

</td>
</tr>
</tbody>
</table>

## Inputs

### APIUsageByAppFilter

Filters the API usage by application results.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The results are filtered for the list of APIs you specify here. A maximum of five API IDs can be defined. This is
an optional parameter that can be used if there is a requirement to filter results for selected APIs.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>appIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The results are filtered for the list of applications you specify here. A maximum of five application IDs can be
defined. This is an optional parameter that can be used if there is a requirement to filter results for selected
applications.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>subscribers</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The results are filtered for the list of subscribers you specify here. A maximum of five subscriber IDs can be
defined. This is an optional parameter that can be used if there is a requirement to filter results for selected
subscribers.

</td>
</tr>
</tbody>
</table>

### APIUsageByBackendOverTimeFilter

Filters the results for API usage by backend over time.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The results are filtered for the list of APIs you specify here. A maximum of five API IDs can be defined. This is
an optional parameter that can be used if there is a requirement to filter results for the selected APIs.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value that is used for data retrieval. This parameter is optional. If the granularity value is not
set, the default granularity for the related time range is applied. The possible granularity values that you can
specify are `1m`, `15m`, `1h`, `1d`, and `7d`.

</td>
</tr>
</tbody>
</table>

### APIUsageOverTimeFilter

Filters the results for API usage over time.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The results are filtered for the list of APIs you specify here. A maximum of five API IDs can be defined. This is
an optional parameter that can be used if there is a requirement to filter results for the selected APIs.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>appIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The results are filtered for the list of applications you specify here. A maximum of five application IDs can be
defined. This is an optional parameter that can be used if there is a requirement to filter results for selected
applications.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value that is used for data retrieval. This parameter is optional. If the granularity value is not
set, the default granularity for the related time range is applied. The possible granularity values that you can
specify are `1m`, `15m`, `1h`, `1d`, and `7d`.

</td>
</tr>
</tbody>
</table>

### AlertSummaryFilter

Filters the alerts summary results.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>paginationFilter</strong></td>
<td valign="top"><a href="#paginationfilter">PaginationFilter</a></td>
<td>

The pagination filter. This is an optional parameter. Therefore, if there is no requirement to filter by this
parameter, omit this parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>searchFilter</strong></td>
<td valign="top"><a href="#searchfilter">SearchFilter</a></td>
<td>

The search filter. This is an optional parameter. Therefore, if there is no requirement to filter by this parameter,
omit this parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>apiIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The results are filtered for the list of APIs you specify here. A maximum of five API IDs can be defined. This is
an optional parameter that can be used if there is a requirement to filter results for the selected APIs.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>category</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Alert category where the results needs to be filtered with. Category needs to be one the 'TRAFFIC', 'LATENCY'
values. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field.

</td>
</tr>
</tbody>
</table>

### ApplicationFilter

Filters applications by the application owner and API IDs.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>owner</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td></td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>apiIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The results are filtered for the list of APIs you specify here. A maximum of five API IDs can be defined. This is
an optional parameter that can be used if there is a requirement to filter results for the selected APIs.

</td>
</tr>
</tbody>
</table>

### CacheFilter

Filters response cache results.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The results are filtered by the API ID specified here. This is an optional parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value that is used for data retrieval. This parameter is optional. If the granularity value is not
set, the default granularity for the related time range is applied. The possible granularity values that you can
specify are `1m`, `15m`, `1h`, `1d`, and `7d`.

</td>
</tr>
</tbody>
</table>

### DataFilter

Filters results by organization, environment and tenant.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>orgId</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The organization by which the results need to be filtered. It is required to specify a value for this parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>environmentId</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Environment ID where the results needs to be filtered with. It is required to specify a value for this parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>tenant</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The name of the tenant by which the results need to be filtered. It is required to specify a value for this
parameter.

</td>
</tr>
</tbody>
</table>

### DeviceFilter

Filters results related to devices.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The results are filtered for the list of APIs you specify here. A maximum of five API IDs can be defined. This is
an optional parameter that can be used if there is a requirement to filter results for the selected APIs.

</td>
</tr>
</tbody>
</table>

### ErrorCountByStatusCodeFilter

Filters errors by status code results.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The results are filtered by the API ID specified here. This is an optional parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>errorType</strong></td>
<td valign="top"><a href="#errortype">ErrorType</a>!</td>
<td>

The type of the error. It is required to specify a value for this parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>errorCodeType</strong></td>
<td valign="top"><a href="#errorcodetype">ErrorCodeType</a></td>
<td>

Error code type. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field.
If this field is not provided, result set will include both client related error response codes(401, 404, etc.) and
server related error response codes.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>paginationFilter</strong></td>
<td valign="top"><a href="#paginationfilter">PaginationFilter</a></td>
<td>

The pagination filter. This is an optional parameter. Therefore, if there is no requirement to filter by this
parameter, omit this parameter.

</td>
</tr>
</tbody>
</table>

### ErrorsByCategoryFilter

Filters errors by category results.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The ID of the API. This field is optional. Hence, if there is no need of filtering by this attribute, omit the
field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>categories</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

List of categories where the results needs to be filtered with. Allowed error category values for this list are
'AUTH', 'TARGET_CONNECTIVITY', 'THROTTLED' and 'OTHER'. This field is optional. Hence, if there is no need of
filtering by this attribute, omit the field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value that is used for data retrieval. This parameter is optional. If the granularity value is not
set, the default granularity for the related time range is applied. The possible granularity values that you can
specify are `1m`, `15m`, `1h`, `1d`, and `7d`.

</td>
</tr>
</tbody>
</table>

### ErrorsByStatusCodeFilter

Filters errors by status code results.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The results are filtered by the API ID specified here. This is an optional parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value that is used for data retrieval. This parameter is optional. If the granularity value is not
set, the default granularity for the related time range is applied. The possible granularity values that you can
specify are `1m`, `15m`, `1h`, `1d`, and `7d`.

</td>
</tr>
</tbody>
</table>

### ErrorsDetailsFilter

Filters error details results.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The results are filtered by the API ID specified here. This is an optional parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>appId</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Application ID where the results needs to be filtered with. This field is optional. Hence, if there is no need of
filtering by this attribute, omit the field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>category</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Error category where the results needs to be filtered with. Error category value must be one of 'AUTH',
'TARGET_CONNECTIVITY', 'THROTTLED' and 'OTHER' values. This field is optional. Hence, if there is no need of
filtering by this attribute, omit the field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value that is used for data retrieval. This parameter is optional. If the granularity value is not
set, the default granularity for the related time range is applied. The possible granularity values that you can
specify are `1m`, `15m`, `1h`, `1d`, and `7d`.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>paginationFilter</strong></td>
<td valign="top"><a href="#paginationfilter">PaginationFilter</a></td>
<td>

The pagination filter. This is an optional parameter. Therefore, if there is no requirement to filter by this
parameter, omit this parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>searchFilter</strong></td>
<td valign="top"><a href="#searchfilter">SearchFilter</a></td>
<td>

The search filter. This is an optional parameter. Therefore, if there is no requirement to filter by this parameter,
omit this parameter.

</td>
</tr>
</tbody>
</table>

### GeoLocationFilter

Filters API usage by geolocation results.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The results are filtered by the API ID specified here. This is an optional parameter.

</td>
</tr>
</tbody>
</table>

### LatencyFilter

Filters latency results.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The results are filtered by the API ID specified here. It is required to specify a value for this parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value that is used for data retrieval. This parameter is optional. If the granularity value is not
set, the default granularity for the related time range is applied. The possible granularity values that you can
specify are `1m`, `15m`, `1h`, `1d`, and `7d`.

</td>
</tr>
</tbody>
</table>

### LatencySummaryFilter

Filters latency summary results.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The results are filtered by the API ID specified here. This is an optional parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value that is used for data retrieval. This parameter is optional. If the granularity value is not
set, the default granularity for the related time range is applied. The possible granularity values that you can
specify are `1m`, `15m`, `1h`, `1d`, and `7d`.

</td>
</tr>
</tbody>
</table>

### OrgFilter

Filters results by organization.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>orgId</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The ID of the organization by which the results need to be filtered. It is required to specify a value for this
parameter.

</td>
</tr>
</tbody>
</table>

### PaginationFilter

Filters related to pagination.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>limit</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Total number of rows in the result set. This value must be a positive integer. This field is optional. Hence, if
there is no need of filtering by this attribute, omit the field. If limit value is not set, default limit value of
5 will be used.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>offset</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Offset value to set when filtering results. This value must be a positive integer. This field is optional. Hence, if
there is no need of filtering by this attribute, omit the field. If offset value is not set, default offset value of
0 will be used.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>sortBy</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Column name which will be used to sort results. This field is optional. Hence, if there is no need of filtering by
this attribute, omit the field. If sortBy value is not set, default sortBy column name for the related operation
will be used.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>sortOrder</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Order which will be used to sort results. The sortOrder value must be one of 'asc' or 'desc' values. This field is
optional. Hence, if there is no need of filtering by this attribute, omit the field. If sortOrder value is not set,
default sortOrder value of 'asc' will be used(with the exemption of setting 'desc' for count specific operations ex:
getTopAPIsByAlertCount).

</td>
</tr>
</tbody>
</table>

### ResourceUsageFilter

Filters resource usage results.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>paginationFilter</strong></td>
<td valign="top"><a href="#paginationfilter">PaginationFilter</a></td>
<td>

The pagination filter. This is an optional parameter. Therefore, if there is no requirement to filter by this
parameter, omit this parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>searchFilter</strong></td>
<td valign="top"><a href="#searchfilter">SearchFilter</a></td>
<td>

The search filter. This is an optional parameter. Therefore, if there is no requirement to filter by this parameter,
omit this parameter.

</td>
</tr>
</tbody>
</table>

### SearchFilter

Filters results by searching matching results for the provided search text.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

List of API Ids where the 'searchtext' needs to be searched. This field is optional. Hence, if there is
no need of filtering by this attribute, omit the field. Please note that it is not possible to define apiIds
only without a search text in the search filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>searchText</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Text which needs to be searched among the results. It is required to specify a value for this parameter.

</td>
</tr>
</tbody>
</table>

### SubscriberFilter

Filters subscribers results.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The results are filtered for the list of APIs you specify here. A maximum of five API IDs can be defined. This is
an optional parameter that can be used if there is a requirement to filter results for the selected APIs.

</td>
</tr>
</tbody>
</table>

### SuccessAPIUsageByAppFilter

Filters successful APU usage by application results.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>apiIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

List of API Ids where the results needs to be filtered with. This field is optional. Hence, if there is no need of
filtering by this attribute, omit the field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>tenantDomains</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

Tenant domain name where the results needs to be filtered with. This field is optional. Hence, if there is no need
of filtering by this attribute, omit the field.

</td>
</tr>
</tbody>
</table>

### TenantDataFilter

Filters results by tenant.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>orgId</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The ID of the organization by which the results need to be filtered. It is required to specify a value for this
parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>envId</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The ID of the environment by which the results need to be filtered. It is required to specify a value for this
parameter.

</td>
</tr>
</tbody>
</table>

### TimeFilter

Filters results by the provided date range.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>from</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Start date of the time range. This 'from' date must be a date before the date defined in 'to'. If the defined date
time entry does not have a timezone, UTC time zone(z) will be used. Example value: '2021-08-16T12:30:00.000+05:30'.
It is required to specify a value for this parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>to</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

End date of the time range. If the defined date time entry does not have a timezone, UTC time zone(z) will be used.
Example value: '2021-08-16T12:30:00.000+05:30'. It is required to specify a value for this parameter.

</td>
</tr>
</tbody>
</table>

### TopAPIsByAlertCountFilter

Filters top APIs by alert count results.

<table>
<thead>
<tr>
<th colspan="2" align="left">Field</th>
<th align="left">Type</th>
<th align="left">Description</th>
</tr>
</thead>
<tbody>
<tr>
<td colspan="2" valign="top"><strong>paginationFilter</strong></td>
<td valign="top"><a href="#paginationfilter">PaginationFilter</a></td>
<td>

The pagination filter. This is an optional parameter. Therefore, if there is no requirement to filter by this
parameter, omit this parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>searchFilter</strong></td>
<td valign="top"><a href="#searchfilter">SearchFilter</a></td>
<td>

The search filter. This is an optional parameter. Therefore, if there is no requirement to filter by this parameter,
omit this parameter.

</td>
</tr>
</tbody>
</table>

## Enums

### ErrorCodeType

Represents an error code type.

<table>
<thead>
<th align="left">Value</th>
<th align="left">Description</th>
</thead>
<tbody>
<tr>
<td valign="top"><strong>_4XX</strong></td>
<td>

Client side errors.

</td>
</tr>
<tr>
<td valign="top"><strong>_5XX</strong></td>
<td>

Server side errors.

</td>
</tr>
</tbody>
</table>

### ErrorType

Represents an error type.

<table>
<thead>
<th align="left">Value</th>
<th align="left">Description</th>
</thead>
<tbody>
<tr>
<td valign="top"><strong>PROXY</strong></td>
<td>

Proxy related errors.

</td>
</tr>
<tr>
<td valign="top"><strong>TARGET</strong></td>
<td>

Target related errors.

</td>
</tr>
</tbody>
</table>

## Scalars

### Boolean

The `Boolean` scalar type represents `true` or `false`.

### Float

The `Float` scalar type represents signed double-precision fractional values as specified by [IEEE 754](https://en.wikipedia.org/wiki/IEEE_floating_point).

### ID

The `ID` scalar type represents a unique identifier, often used to refetch an object or as key for a cache. The ID type appears in a JSON response as a String; however, it is not intended to be human-readable. When expected as an input type, any string (such as `"4"`) or integer (such as `4`) input value will be accepted as an ID.

### Int

The `Int` scalar type represents non-fractional signed whole numeric values. Int can represent values between -(2^31) and 2^31 - 1.

### String

The `String` scalar type represents textual data, represented as UTF-8 character sequences. The String type is most often used by GraphQL to represent free-form human-readable text.

