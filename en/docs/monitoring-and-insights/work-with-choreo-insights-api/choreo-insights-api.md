# Choreo Insights API

The Choreo Insights API is a GraphQL API that allows you to retrieve data from external systems based on specific criteria. This guide describes the operations and schema-defined types you can use to retrieve data via the Choreo Insights API.

 - **Allowed operations:** [Queries](#query)
 - **Schema-defined types:** [Objects](#objects), [Inputs](#inputs), [Enums](#enums), and [Scalars](#scalars)

##Try out

You can try out the Choreo Insights API with your data via [GraphQL Explorer](graphiql-explorer/index.html).

!!! warning
    The GraphQL Explorer is currently not compatible with the Safari web browser due to a [known issue](https://developer.apple.com/forums/thread/658688).

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
can configure the <code>provider</code> parameter to further filter the APIs by a specific provider.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">provider</td>
<td valign="top"><a href="#string">String</a></td>
<td>

The name of the API provider.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>listApplications</strong></td>
<td valign="top">[<a href="#application">Application</a>]</td>
<td>

Fetches all the applications with the given combination of tenant ID, environment ID, and organization ID. Optionally, you
can configure the <code>applicationFilter</code> parameter to further filter the applications by a specific application filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">applicationFilter</td>
<td valign="top"><a href="#applicationfilter">ApplicationFilter</a></td>
<td>

The application filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>listProviders</strong></td>
<td valign="top">[<a href="#provider">Provider</a>]</td>
<td>

Fetches all the API providers with the given combination of tenant ID, environment ID, and organization ID.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>listSubscribers</strong></td>
<td valign="top">[<a href="#subscriber">Subscriber</a>]</td>
<td>

Fetches all the API subscribers with the given combination of tenant ID, environment ID, and organization ID. Optionally, you
can configure the <code>subscriberFilter</code> parameter to further filter the subscribers.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">subscriberFilter</td>
<td valign="top"><a href="#subscriberfilter">SubscriberFilter</a></td>
<td>

The subscriber filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>listOrganizations</strong></td>
<td valign="top">[<a href="#organization">Organization</a>]</td>
<td>

Fetches all the organizations of a user.

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

The organization filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">projectId</td>
<td valign="top"><a href="#string">String</a></td>
<td>

The project ID by which the results need to be filtered. It is optional to specify a value for this
parameter. Note that providing a project ID with an on-prem key is not allowed.

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

The tenant filter.

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

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

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

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

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

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getLatencySummary</strong></td>
<td valign="top"><a href="#latencysummary">LatencySummary</a></td>
<td>

Returns the response latency value (95th percentile value) for each time granularity within the specified time range
for the given combination of tenant ID, environment ID, and organization ID. Optionally, you can configure the
<code>latencySummaryFilter</code> parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">filter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">latencySummaryFilter</td>
<td valign="top"><a href="#latencysummaryfilter">LatencySummaryFilter</a></td>
<td>

The latency summary filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getSuccessSummary</strong></td>
<td valign="top"><a href="#successsummary">SuccessSummary</a></td>
<td>

Returns the number of successful (2xx response code) hit count for each time granularity within the specified time
range for the given combination of tenant ID, environment ID, and organization ID. Optionally, you can configure the
<code>granularity</code> parameter to override the default granularity value.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">filter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">granularity</td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getErrorSummary</strong></td>
<td valign="top"><a href="#errorsummary">ErrorSummary</a></td>
<td>

Returns the number of unsuccessful hits (i.e., the total of both <code>4xx</code> and <code>5xx</code> response codes) for each time
granularity within the specified time range for the given combination of tenant ID, environment ID, and organization
ID. Optionally, you can configure the <code>granularity</code> parameter to override the default granularity value.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">filter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">granularity</td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getErrorsByCategory</strong></td>
<td valign="top"><a href="#errorsbycategory">ErrorsByCategory</a></td>
<td>

Returns the number of proxy errors (i.e., the total of both <code>4xx</code> and <code>5xx</code> response codes) by each error category
for each time granularity within the specified time range for the given combination of tenant ID, environment ID,
and organization ID. Optionally, you can configure the <code>errorsByCategoryFilter</code> parameter to further filter the
results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">errorsByCategoryFilter</td>
<td valign="top"><a href="#errorsbycategoryfilter">ErrorsByCategoryFilter</a></td>
<td>

The errors by category filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getErrorsDetails</strong></td>
<td valign="top"><a href="#detailsoferrors">DetailsOfErrors</a></td>
<td>

Returns details of each proxy error (for both <code>4xx</code> and <code>5xx</code> response codes) within the specified time range for
the given combination of tenant ID, environment ID, and organization ID. Optionally, you can configure the
<code>errorsDetailsFilter</code> parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">errorsDetailsFilter</td>
<td valign="top"><a href="#errorsdetailsfilter">ErrorsDetailsFilter</a></td>
<td>

The error details filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getProxyTargetErrorsOverTime</strong></td>
<td valign="top">[<a href="#errorsbystatuscodecategory">ErrorsByStatusCodeCategory</a>]</td>
<td>

Returns a list of hit counts for proxy and target errors for each time granularity within the specified time range
for the given combination of tenant ID, environment ID, and organization ID. The errors in each proxy and target
result set are grouped as <code>4xx</code>, <code>5xx</code>, and <code>total</code>. Optionally, you can configure the <code>errorsByStatusCodeFilter</code>
parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">errorsByStatusCodeFilter</td>
<td valign="top"><a href="#errorsbystatuscodefilter">ErrorsByStatusCodeFilter</a></td>
<td>

The errors by status code filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getErrorsByStatusCode</strong></td>
<td valign="top"><a href="#errorsbystatuscode">ErrorsByStatusCode</a></td>
<td>

Returns the hit count for each error response code (<code>401</code>, <code>404</code>, and <code>500</code> etc.,) for each API within the specified
time range for the given combination of tenant ID, environment ID, and organization ID.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">errorCountByStatusCodeFilter</td>
<td valign="top"><a href="#errorcountbystatuscodefilter">ErrorCountByStatusCodeFilter</a>!</td>
<td>

The error count by status code filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getTotalErrorsByAPI</strong></td>
<td valign="top"><a href="#totalerror">TotalError</a></td>
<td>

Returns the total proxy error hit count for an API across for the given combination of tenant ID, environment ID,
and organization ID. Optionally, you can configure the <code>filter</code> parameter to further filter the results by a given
time range.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">filter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a></td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">apiId</td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The API ID.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getAPIUsageOverTime</strong></td>
<td valign="top">[<a href="#apiusageovertime">APIUsageOverTime</a>]</td>
<td>

Returns a list of details related to the usage of each API over time for each time granularity within the specified
time range for the given combination of tenant ID, environment ID, and organization ID. Optionally, you can
configure the <code>apiUsageOvertimeFilter</code> parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">apiUsageOvertimeFilter</td>
<td valign="top"><a href="#apiusageovertimefilter">APIUsageOverTimeFilter</a></td>
<td>

The API usage overtime filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getAPIUsageByAppOverTime</strong></td>
<td valign="top"><a href="#apiusagebyappovertime">APIUsageByAppOverTime</a></td>
<td>

Returns API usage by each application over time for each time granularity within the specified time range for the
given combination of tenant ID, environment ID, and organization ID. Optionally, you can configure the
<code>apiUsageOvertimeFilter</code> parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">apiUsageOvertimeFilter</td>
<td valign="top"><a href="#apiusageovertimefilter">APIUsageOverTimeFilter</a></td>
<td>

The API usage overtime filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getAPIUsageByBackendOverTime</strong></td>
<td valign="top"><a href="#apiusagebybackendovertime">APIUsageByBackendOverTime</a></td>
<td>

Returns API usage by each backend over time for each time granularity within the specified time range for the given
combination of tenant ID, environment ID, and organization ID. Optionally, you can configure the
<code>apiUsageByBackendOverTimeFilter</code> parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">apiUsageByBackendOverTimeFilter</td>
<td valign="top"><a href="#apiusagebybackendovertimefilter">APIUsageByBackendOverTimeFilter</a></td>
<td>

The API usage by backend overtime filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getResourceUsage</strong></td>
<td valign="top"><a href="#resourceusages">ResourceUsages</a></td>
<td>

Returns API usage by resource within the specified time range for the given combination of tenant ID, environment
ID, and organization ID. Optionally, you can configure the <code>resourceUsageFilter</code> parameter to further filter the
results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">resourceUsageFilter</td>
<td valign="top"><a href="#resourceusagefilter">ResourceUsageFilter</a></td>
<td>

The resource usage filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getTotalTrafficByAPI</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Returns the total hit count for an API for the given combination of tenant ID, environment ID, and organization ID.
Optionally, you can configure the <code>filter</code> parameter to further filter the results by a given time range.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">filter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a></td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">apiId</td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The API ID.

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

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">limit</td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The limit for the slow APIs list.

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

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">latencyFilter</td>
<td valign="top"><a href="#latencyfilter">LatencyFilter</a>!</td>
<td>

The latency filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getOverallLatencyByAPI</strong></td>
<td valign="top"><a href="#overalllatency">OverallLatency</a></td>
<td>

Returns the overall response latency (95th percentile value) for an API for the given combination of tenant ID,
environment ID, and organization ID. Optionally, you can configure the <code>filter</code> parameter to further filter the
results by a given time range.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">filter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a></td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">apiId</td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The API ID.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getCacheHitsAndMisses</strong></td>
<td valign="top"><a href="#cachehits">CacheHits</a></td>
<td>

Return the total number of response cache hits, misses, and hit percentage for an API over time for each time
granularity within the specified time range for the given combination of tenant ID, environment ID, and
organization ID.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">cacheFilter</td>
<td valign="top"><a href="#cachefilter">CacheFilter</a></td>
<td>

The cache filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getTopPlatforms</strong></td>
<td valign="top">[<a href="#platform">Platform</a>]</td>
<td>

Returns a list of top platforms ranked based on the hit count within the specified time range for the given
combination of tenant ID, environment ID, and organization ID. The result list includes the top nine platforms with
the respective hit count, and all the rest of the platforms are labeled as **Other** with the cumulated hit count.
Optionally, you can configure the <code>deviceFilter</code> parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">deviceFilter</td>
<td valign="top"><a href="#devicefilter">DeviceFilter</a></td>
<td>

The device filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getTopUserAgents</strong></td>
<td valign="top">[<a href="#useragent">UserAgent</a>]</td>
<td>

Returns a list of top user agents ranked based on the hit count within the specified time range for the given
combination of tenant ID, environment ID, and organization ID. The result list includes the top nine user agents
with the respective hit count, and all the rest of the platforms are labeled as **Other** with the cumulated hit
count. Optionally, you can configure the <code>deviceFilter</code> parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">deviceFilter</td>
<td valign="top"><a href="#devicefilter">DeviceFilter</a></td>
<td>

The device filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getAPIUsageByGeoLocation</strong></td>
<td valign="top">[<a href="#usagebygeolocation">UsageByGeoLocation</a>]</td>
<td>

NOTE: Currently, this operation returns data only for on-premise environments.

Returns API usage by country within the specified time range for the given combination of tenant ID, environment ID,
and organization ID. Optionally, you can configure the <code>geoLocationFilter</code> parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">geoLocationFilter</td>
<td valign="top"><a href="#geolocationfilter">GeoLocationFilter</a></td>
<td>

The geolocation filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getAPIsUsageByApplications</strong></td>
<td valign="top">[<a href="#apiusagebyapplication">APIUsageByApplication</a>]</td>
<td>

Returns details relating to the usage of each API by application within the specified time range for the given
combination of tenant ID, environment ID, and organization ID. Optionally, you can configure the
<code>apiUsageByAppFilter</code> parameter to further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">apiUsageByAppFilter</td>
<td valign="top"><a href="#apiusagebyappfilter">APIUsageByAppFilter</a></td>
<td>

The API usage by app filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getTopAPIsByAlertCount</strong></td>
<td valign="top"><a href="#topapisbyalertcount">TopAPIsByAlertCount</a></td>
<td>

Returns top APIs ranked based on the alert count within the specified time range for the given combination of tenant
ID, environment ID, and organization ID. Optionally, you can configure the <code>topAPIsByAlertCountFilter</code> parameter to
further filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">topAPIsByAlertCountFilter</td>
<td valign="top"><a href="#topapisbyalertcountfilter">TopAPIsByAlertCountFilter</a></td>
<td>

The top APIs by alert count filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getAlertSummary</strong></td>
<td valign="top"><a href="#alertsummaries">AlertSummaries</a></td>
<td>

Returns a summary for each alert within the specified time range for the given combination of tenant ID,
environment ID, and organization ID. Optionally, you can configure the <code>alertSummaryFilter</code> parameter to further
filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">alertSummaryFilter</td>
<td valign="top"><a href="#alertsummaryfilter">AlertSummaryFilter</a></td>
<td>

The alert summary filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getSuccessAPIsUsageByApplications</strong></td>
<td valign="top">[<a href="#successapiusagebyapplication">SuccessAPIUsageByApplication</a>]</td>
<td>

NOTE: This operation can only be invoked via using an on-prem key as the authentication header.

Returns a list of successful hit counts (i.e., 2xx response codes) within the specified time range for each API,
grouped by each application. Optionally, you can configure the <code>successAPIUsageByAppFilter</code> parameter to further
filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">successAPIUsageByAppFilter</td>
<td valign="top"><a href="#successapiusagebyappfilter">SuccessAPIUsageByAppFilter</a></td>
<td>

The successful API usage by application filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getSuccessAPIsUsageByApplicationsWithOnPremKey</strong></td>
<td valign="top">[<a href="#successapiusagebyapplication">SuccessAPIUsageByApplication</a>]</td>
<td>

Returns a list of successful hit counts (i.e., 2xx response codes) within the specified time range for each API,
grouped by each application. Optionally, you can configure the <code>successAPIUsageByAppFilter</code> parameter to further
filter the results.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">onPremKey</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The value of the On-Prem key.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">successAPIUsageByAppFilter</td>
<td valign="top"><a href="#successapiusagebyappfilter">SuccessAPIUsageByAppFilter</a></td>
<td>

The successful API usage by application filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getCustomReportOvertime</strong></td>
<td valign="top"><a href="#customreportovertime">CustomReportOvertime</a></td>
<td>

Returns the summary of hits or latency data as specified for the selected time duration, grouped by time spans.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">metric</td>
<td valign="top"><a href="#metric">Metric</a>!</td>
<td>

The metric for which data needs to be retrieved.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">groupByFields</td>
<td valign="top">[<a href="#groupbyfield">GroupByField</a>]!</td>
<td>

The column/field by which data needs to be retrieved.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">groupByValues</td>
<td valign="top">[<a href="#string">String</a>]!</td>
<td>

The value used to filter by the <code>groupByField</code>.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">granularity</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The granularity value for which data is retrieved.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getCustomReportTopOvertime</strong></td>
<td valign="top"><a href="#customreporttopovertime">CustomReportTopOvertime</a></td>
<td>

Returns the summary of hits or latency data as specified for the selected time duration, grouped by time spans for
the top N values for the selected groupByField.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">timeFilter</td>
<td valign="top"><a href="#timefilter">TimeFilter</a>!</td>
<td>

The time filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">metric</td>
<td valign="top"><a href="#metric">Metric</a>!</td>
<td>

The metric for which data needs to be retrieved.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">groupByFields</td>
<td valign="top">[<a href="#groupbyfield">GroupByField</a>]!</td>
<td>

The column/field by which the data should be grouped.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">groupByValues</td>
<td valign="top">[<a href="#string">String</a>]!</td>
<td>

The value used to filter by the <code>groupByField</code>.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">granularity</td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The granularity value for which data is retrieved.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>getGroupByValues</strong></td>
<td valign="top">[[<a href="#string">String</a>]]</td>
<td>

Returns a list of value sets by which you can group the data available for the selected <code>groupBy</code> fields.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">dataFilter</td>
<td valign="top"><a href="#datafilter">DataFilter</a>!</td>
<td>

The data filter.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">groupByFields</td>
<td valign="top">[<a href="#groupbyfield">GroupByField</a>]!</td>
<td>

The column/field for which the API needs to retrieve distinct values.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">groupByValues</td>
<td valign="top">[<a href="#string">String</a>]!</td>
<td>

The value used to filter by the <code>groupByField</code>.

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

The API name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>version</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The API version.

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

Represents a timestamp value that is calculated based on the specified granularity value. For example, if the
granularity value is <code>1d</code>, the timestamp represents the start of the day calculated adhering to the timezone
provided in the time filter.
e.g., <code>timeSpan: '2021-06-21T00:00:00.0000000+05:30'</code>

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

A list containing the API usage values across APIs, grouped by the application. Returns an empty array if no data is
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

The Application ID.

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

A list containing the alert summary for each alert. Returns an empty array if no data is available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>pagination</strong></td>
<td valign="top"><a href="#pagination">Pagination</a>!</td>
<td>

The pagination details.

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

Represents a timestamp value that is calculated based on the specified granularity value. For example, if the
granularity value is <code>1d</code>, the timestamp represents the start of the day calculated adhering to the timezone
provided in the time filter.
e.g., <code>timeSpan: '2021-06-21T00:00:00.0000000+05:30'</code>

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>category</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The alert category. Possible values are <code>LATENCY</code> and <code>TRAFFIC</code>.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>metric</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The alert metric. Possible values are <code>RESPONSE_LATENCY</code> and <code>TOTAL_TRAFFIC</code>.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>severity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The severity level of the alert. Possible values are <code>LOW</code>, <code>MEDIUM</code>, and <code>HIGH</code>.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>message</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The alert details.

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

Represents a timestamp value that is calculated based on the specified granularity value. For example, if the
granularity value is <code>1d</code>, the timestamp represents the start of the day calculated adhering to the timezone
provided in the time filter.
e.g., <code>timeSpan: '2021-06-21T00:00:00.0000000+05:30'</code>

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

### CustomReportGroupBy

Represents the usage data overtime for the selected parameters of a specific <code>groupByValue</code>.

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
<td colspan="2" valign="top"><strong>groupByValue</strong></td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The distinct group-by value that is used as an ID.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>usage</strong></td>
<td valign="top">[<a href="#customreportusage">CustomReportUsage</a>]</td>
<td>

A list containing the usage values. This returns an empty array if no data is available.

</td>
</tr>
</tbody>
</table>

### CustomReportOvertime

Represents usage data overtime for the selected parameters.

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
<td valign="top">[<a href="#customreportusage">CustomReportUsage</a>]</td>
<td>

A list containing the usage values. This returns an empty array if no data is available.

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

### CustomReportTopOvertime

Represents usage data overtime for the selected parameters grouped by the top N <code>groupByValues</code>.

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
<td valign="top">[<a href="#customreportgroupby">CustomReportGroupBy</a>]</td>
<td>

A list containing the usage values grouped by the selected <code>groupByField</code>. This returns an empty array if no data is
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

### CustomReportUsage

Represents usage data for a single timestamp in <code>CustomReports</code>.

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

Represents a timestamp value that is calculated based on the specified granularity value. For example, if the
granularity value is <code>1d</code>, the timestamp represents the start of the day calculated adhering to the timezone
provided in the time filter.
e.g., <code>timeSpan: '2021-06-21T00:00:00.0000000+05:30'</code>

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>value</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

The usage value (Hit Count/ Latency).

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

The pagination details.

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
<td colspan="2" valign="top"><strong>externalEnvId</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The external environment ID.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>internalEnvId</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The internal environment ID.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>sandboxEnvId</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The sandbox environment ID.

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
<td valign="top"><a href="#environmenttype">EnvironmentType</a>!</td>
<td>

The environment type. A Choreo environment is labeled as <code>CHOREO</code>, a private Choreo environment is labeled as
<code>CHOREO_PRIVATE</code>, and the on-premise environment is labeled as <code>ON_PREM</code>.

</td>
</tr>
</tbody>
</table>

### ErrorCountByCategory

Represents the number of errors from each category for a single timestamp. The timestamp is calculated according to the
provided granularity value. If you select some categories, the error counts are retrieved only for those categories,
and a <code>null</code> value is shown for the other categories that are not selected.

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

Represents a timestamp value that is calculated based on the specified granularity value. For example, if the
granularity value is <code>1d</code>, the timestamp represents the start of the day calculated adhering to the timezone
provided in the time filter.
e.g., <code>timeSpan: '2021-06-21T00:00:00.0000000+05:30'</code>

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

Represents a timestamp value that is calculated based on the specified granularity value. For example, if the
granularity value is <code>1d</code>, the timestamp represents the start of the day calculated adhering to the timezone
provided in the time filter.
e.g., <code>timeSpan: '2021-06-21T00:00:00.0000000+05:30'</code>

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

Represents errors by the main status code (<code>4xx</code> or <code>5xx</code>) and the total error count.

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

Provides the unsuccessful usage (i.e., <code>4xx</code> and <code>5xx</code> response codes) summary across all APIs.

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

Represents the unsuccessful request count (i.e., <code>4xx</code> and <code>5xx</code> response codes) for a single timestamp. The timestamp
is calculated based on the granularity value provided.

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

Represents a timestamp value that is calculated based on the specified granularity value. For example, if the
granularity value is <code>1d</code>, the timestamp represents the start of the day calculated adhering to the timezone
provided in the time filter.
e.g., <code>timeSpan: '2021-06-21T00:00:00.0000000+05:30'</code>

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

A list containing the error count for each status code for each API. Returns an empty array if no data is available.

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

Represents the error category values (grouped as <code>4xx</code>, <code>5xx</code>, and <code>total</code>) for both proxy and target errors for a
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

Represents a timestamp value that is calculated based on the specified granularity value. For example, if the
granularity value is <code>1d</code>, the timestamp represents the start of the day calculated adhering to the timezone
provided in the time filter.
e.g., <code>timeSpan: '2021-06-21T00:00:00.0000000+05:30'</code>

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>proxy</strong></td>
<td valign="top"><a href="#errorstatuscodecategorycounts">ErrorStatusCodeCategoryCounts</a></td>
<td>

Represents proxy-related errors categorized as client errors (<code>4xx</code> response codes) or server errors (<code>5xx</code> response
codes), and the total of both categories.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>target</strong></td>
<td valign="top"><a href="#errorstatuscodecategorycounts">ErrorStatusCodeCategoryCounts</a></td>
<td>

Represents target-related errors categorized as client errors (<code>4xx</code> response codes) or server errors (<code>5xx</code>
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

Represents a timestamp value that is calculated based on the specified granularity value. For example, if the
granularity value is <code>1d</code>, the timestamp represents the start of the day calculated adhering to the timezone
provided in the time filter.
e.g., <code>timeSpan: '2021-06-21T00:00:00.0000000+05:30'</code>

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

Represents a timestamp value that is calculated based on the specified granularity value. For example, if the
granularity value is <code>1d</code>, the timestamp represents the start of the day calculated adhering to the timezone
provided in the time filter.
e.g., <code>timeSpan: '2021-06-21T00:00:00.0000000+05:30'</code>

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

### Organization

Represents an organization.

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

The organization ID.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>uuid</strong></td>
<td valign="top"><a href="#id">ID</a>!</td>
<td>

The organization UUID.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>handle</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The organization handle name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>name</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The organization name.

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

The sorting order of the result set. Possible values are <code>asc</code> and <code>desc</code>.

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

Represents successful usages(that have resulted in the <code>2xx</code> response code) of an API by an application.

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

Represents the successful request count (i.e., requests that have received the <code>2xx</code> response code) for a single
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

Represents a timestamp value that is calculated based on the specified granularity value. For example, if the
granularity value is <code>1d</code>, the timestamp represents the start of the day calculated adhering to the timezone
provided in the time filter.
e.g., <code>timeSpan: '2021-06-21T00:00:00.0000000+05:30'</code>

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

The pagination details.

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

The results are filtered for the list of APIs you specify here. A maximum of five API IDs can be defined.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>appIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The results are filtered for the list of applications you specify here. A maximum of five application IDs can be
defined.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>subscribers</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The results are filtered for the list of subscribers you specify here. A maximum of five subscriber IDs can be
defined.

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

The results are filtered for the list of APIs you specify here. A maximum of five API IDs can be defined.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value that is used for data retrieval. If the granularity value is not set, the default granularity
for the related time range is applied. The possible granularity values that you can specify are <code>1m</code>, <code>15m</code>, <code>1h</code>,
<code>1d</code>, and <code>7d</code>.

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

The results are filtered for the list of APIs you specify here. A maximum of five API IDs can be defined.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>appIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The results are filtered for the list of applications you specify here. A maximum of five application IDs can be
defined.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value that is used for data retrieval. If the granularity value is not set, the default granularity
for the related time range is applied. The possible granularity values that you can specify are <code>1m</code>, <code>15m</code>, <code>1h</code>,
<code>1d</code>, and <code>7d</code>.

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

The pagination filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>searchFilter</strong></td>
<td valign="top"><a href="#searchfilter">SearchFilter</a></td>
<td>

The search filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>apiIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The results are filtered for the list of APIs you specify here. A maximum of five API IDs can be defined.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>category</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The alert category by which the results need to be filtered. The available categories to select are <code>TRAFFIC</code> and
<code>LATENCY</code>.

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

The results are filtered for the list of APIs you specify here. A maximum of five API IDs can be defined.

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

The results are filtered by the API ID specified here.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value that is used for data retrieval. If the granularity value is not set, the default granularity
for the related time range is applied. The possible granularity values that you can specify are <code>1m</code>, <code>15m</code>, <code>1h</code>,
<code>1d</code>, and <code>7d</code>.

</td>
</tr>
</tbody>
</table>

### DataFilter

Filters results by the given combination of organization, environment, and tenant.

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
<td valign="top"><a href="#string">String</a></td>
<td>

The environment ID by which the results need to be filtered. It is required to specify a value for this parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>environmentIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The environment IDs by which the results need to be filtered. It is required to specify a value for this parameter.

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
<tr>
<td colspan="2" valign="top"><strong>projectId</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The project ID by which the results need to be filtered. It is optional to specify a value for this
parameter. Note that providing a project ID with an on-prem key is not allowed.

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

The results are filtered for the list of APIs you specify here. A maximum of five API IDs can be defined.

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

The results are filtered by the API ID specified here.

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

The error code type. If this parameter is not configured, both client-related error response codes (<code>401</code>, <code>404</code>,
etc.) and server-related response codes (<code>500</code>, <code>501</code>, etc.) are included in the result set.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>paginationFilter</strong></td>
<td valign="top"><a href="#paginationfilter">PaginationFilter</a></td>
<td>

The pagination filter.

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

The ID of the API.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>categories</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The list of categories by which the results need to be filtered. Category values that you can specify here are
<code>AUTH</code>, <code>TARGET_CONNECTIVITY</code>, <code>THROTTLED</code>, and <code>OTHER</code>.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value that is used for data retrieval. If the granularity value is not set, the default granularity
for the related time range is applied. The possible granularity values that you can specify are <code>1m</code>, <code>15m</code>, <code>1h</code>,
<code>1d</code>, and <code>7d</code>.

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

The results are filtered by the API ID specified here.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value that is used for data retrieval. If the granularity value is not set, the default granularity
for the related time range is applied. The possible granularity values that you can specify are <code>1m</code>, <code>15m</code>, <code>1h</code>,
<code>1d</code>, and <code>7d</code>.

</td>
</tr>
</tbody>
</table>

### ErrorsDetailsFilter

Filters the results for error details.

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

The results are filtered by the API ID specified here.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>appId</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The application ID by which the results need to be filtered.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>category</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The error category by which the results need to be filtered. The error category that you can specify here must be
one of the <code>AUTH</code> <code>TARGET_CONNECTIVITY</code>, <code>THROTTLED</code>, and <code>OTHER</code> values.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value that is used for data retrieval. If the granularity value is not set, the default granularity
for the related time range is applied. The possible granularity values that you can specify are <code>1m</code>, <code>15m</code>, <code>1h</code>,
<code>1d</code>, and <code>7d</code>.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>paginationFilter</strong></td>
<td valign="top"><a href="#paginationfilter">PaginationFilter</a></td>
<td>

The pagination filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>searchFilter</strong></td>
<td valign="top"><a href="#searchfilter">SearchFilter</a></td>
<td>

The search filter.

</td>
</tr>
</tbody>
</table>

### GeoLocationFilter

Filters the API usage by geolocation results.

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

The results are filtered for the list of APIs you specify here.

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

The granularity value that is used for data retrieval. If the granularity value is not set, the default granularity
for the related time range is applied. The possible granularity values that you can specify are <code>1m</code>, <code>15m</code>, <code>1h</code>,
<code>1d</code>, and <code>7d</code>.

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

The results are filtered by the API ID specified here.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The granularity value that is used for data retrieval. If the granularity value is not set, the default granularity
for the related time range is applied. The possible granularity values that you can specify are <code>1m</code>, <code>15m</code>, <code>1h</code>,
<code>1d</code>, and <code>7d</code>.

</td>
</tr>
</tbody>
</table>

### OrgFilter

Filters results for the given organization.

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

The total number of rows in the result set. This value must be a positive integer. If this limit value is not set,
the total number of rows is five by default.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>offset</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

The offset value to set when filtering results. This value must be zero or a positive integer. If you do not specify
an offset value, the default offset value (i.e., <code>0</code>) applies.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>sortBy</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The column name by which the results are sorted. If the <code>sortBy</code> value is not set, the default column of the related
operation that is available for sorting purposes is used.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>sortOrder</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

The order in which the results are sorted. Possible values are <code>asc</code> (to sort in ascending order) and <code>desc</code> (to
sort in descending order). If no value is specified, the results are sorted in ascending order by default (except
for count-specific operations such as <code>getTopAPIsByAlertCount</code> where the results are always sorted in descending
order).

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

The pagination filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>searchFilter</strong></td>
<td valign="top"><a href="#searchfilter">SearchFilter</a></td>
<td>

The search filter.

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

The list of API IDs that need to be searched with the given search text. Note that it is not possible to define API
IDs without a search text in the search filter.

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

Filters the subscriber results.

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

The results are filtered for the list of APIs you specify here. A maximum of five API IDs can be defined.

</td>
</tr>
</tbody>
</table>

### SuccessAPIUsageByAppFilter

Filters successful API usage by application results.

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

The list of API IDs by which the results need to be filtered.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>tenantDomains</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The tenant domain name by which the results need to be filtered.

</td>
</tr>
</tbody>
</table>

### TenantDataFilter

Filters results by the tenant.

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
<td valign="top"><a href="#string">String</a></td>
<td>

The ID of the environment by which the results need to be filtered. It is required to specify a value for this
parameter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>environmentIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

The environment IDs by which the results need to be filtered. It is required to specify a value for this parameter.

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

The start date of the time range. The date specified here must be a date earlier than the date specified via the
<code>to</code> parameter. If the defined date and time entry do not have a timezone, the <code>UTC</code> time zone (z) is used. It is
required to specify a value for this parameter.
e.g., <code>'2021-08-16T12:00:00.000+05:30'</code>.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>to</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

The end date of the time range. The date specified here must be a date later than the date specified via the <code>from</code>
parameter. If the defined date and time entry do not have a timezone, the <code>UTC</code> time zone (z) is used. It is
required to specify a value for this parameter.
e.g., <code>'2021-08-16T12:30:00.000+05:30'</code>.

</td>
</tr>
</tbody>
</table>

### TopAPIsByAlertCountFilter

Filters the top APIs by alert count results.

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

The pagination filter.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>searchFilter</strong></td>
<td valign="top"><a href="#searchfilter">SearchFilter</a></td>
<td>

The search filter.

</td>
</tr>
</tbody>
</table>

## Enums

### EnvironmentType

Represents an error type.

<table>
<thead>
<th align="left">Value</th>
<th align="left">Description</th>
</thead>
<tbody>
<tr>
<td valign="top"><strong>CHOREO</strong></td>
<td>

The Choreo environments.

</td>
</tr>
<tr>
<td valign="top"><strong>CHOREO_PRIVATE</strong></td>
<td>

The private data-plane Choreo environments.

</td>
</tr>
<tr>
<td valign="top"><strong>ON_PREM</strong></td>
<td>

The On-Premise environments.

</td>
</tr>
</tbody>
</table>

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

The client-side errors.

</td>
</tr>
<tr>
<td valign="top"><strong>_5XX</strong></td>
<td>

The server-side errors.

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

The proxy-related errors.

</td>
</tr>
<tr>
<td valign="top"><strong>TARGET</strong></td>
<td>

The target-related errors.

</td>
</tr>
</tbody>
</table>

### GroupByField

Represents an <code>groupByField</code> used in <code>CustomReports</code>.

<table>
<thead>
<th align="left">Value</th>
<th align="left">Description</th>
</thead>
<tbody>
<tr>
<td valign="top"><strong>API_NAME</strong></td>
<td>

The API name.

</td>
</tr>
<tr>
<td valign="top"><strong>API_VERSION</strong></td>
<td>

The API version.

</td>
</tr>
<tr>
<td valign="top"><strong>API_RESOURCE_TEMPLATE</strong></td>
<td>

The API resource template.

</td>
</tr>
<tr>
<td valign="top"><strong>API_METHOD</strong></td>
<td>

The API method.

</td>
</tr>
<tr>
<td valign="top"><strong>API_CREATOR</strong></td>
<td>

The API creator.

</td>
</tr>
<tr>
<td valign="top"><strong>APPLICATION_NAME</strong></td>
<td>

The name of the application.

</td>
</tr>
<tr>
<td valign="top"><strong>APPLICATION_OWNER</strong></td>
<td>

The owner of the application.

</td>
</tr>
<tr>
<td valign="top"><strong>DESTINATION</strong></td>
<td>

The destination.

</td>
</tr>
<tr>
<td valign="top"><strong>USER_AGENT</strong></td>
<td>

The user Agent.

</td>
</tr>
<tr>
<td valign="top"><strong>PLATFORM</strong></td>
<td>

The platform.

</td>
</tr>
<tr>
<td valign="top"><strong>TARGET_RESPONSE_CODE</strong></td>
<td>

The target response code.

</td>
</tr>
</tbody>
</table>

### Metric

Represents a metric used in <code>CustomReports</code>.

<table>
<thead>
<th align="left">Value</th>
<th align="left">Description</th>
</thead>
<tbody>
<tr>
<td valign="top"><strong>HIT_COUNT</strong></td>
<td>

The number of API calls.

</td>
</tr>
<tr>
<td valign="top"><strong>RESPONSE_CACHE_HIT</strong></td>
<td>

The number of API calls that used the response cache.

</td>
</tr>
<tr>
<td valign="top"><strong>REQUEST_MEDIATION_LATENCY</strong></td>
<td>

The request mediation latency.

</td>
</tr>
<tr>
<td valign="top"><strong>RESPONSE_MEDIATION_LATENCY</strong></td>
<td>

The response mediation latency.

</td>
</tr>
<tr>
<td valign="top"><strong>BACKEND_LATENCY</strong></td>
<td>

The backend latency.

</td>
</tr>
<tr>
<td valign="top"><strong>TOTAL_LATENCY</strong></td>
<td>

The total latency.

</td>
</tr>
<tr>
<td valign="top"><strong>API_ERRORS</strong></td>
<td>

The number of hits for which API errors are returned.

</td>
</tr>
<tr>
<td valign="top"><strong>TARGET_ERRORS</strong></td>
<td>

The number of hits for which target errors are returned.

</td>
</tr>
</tbody>
</table>

## Scalars

### Boolean

The <code>Boolean</code> scalar type represents <code>true</code> or <code>false</code>.

### Float

The <code>Float</code> scalar type represents signed double-precision fractional values as specified by [IEEE 754](https://en.wikipedia.org/wiki/IEEE_floating_point).

### ID

The <code>ID</code> scalar type represents a unique identifier, often used to re-fetch an object or as the key for a cache. The ID type appears in a JSON response as a String; however, it is not intended to be human-readable. When expected as an input type, any string (such as <code>"4"</code>) or integer (such as <code>4</code>) input value is accepted as an ID.

### Int

The <code>Int</code> scalar type represents non-fractional signed whole numeric values. Int can represent values between -(2^31) and 2^31 - 1.

### String

The <code>String</code> scalar type represents textual data, represented as UTF-8 character sequences. The String type is most often used by GraphQL to represent free-form human-readable text.

