# Choreo Insights API

Choreo Insights API is a GraphQL API. It offers the flexibility and the ability to define precisely the data you want to fetch. This guide walks you through to find out what data you can retrieve:
 
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

Fetches all the APIs by tenant Id, environment Id and organization Id. If the optional field of provider is given,
API list will be further filtered by the given provider name.

</td>
</tr>
<tr>
<td colspan="2" align="right" valign="top">provider</td>
<td valign="top"><a href="#string">String</a></td>
<td>

API provider name

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

Fetches all the applications by tenant Id, environment Id and organization Id. If the optional field of
applicationFilter is provided, application list will be further filtered by the given application filter.

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

Fetches all the API providers by tenant Id, environment Id and organization Id.

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

Fetches all the API subscribers by tenant Id, environment Id and organization Id. If the optional field of
subscriberFilter is provided, subscriber list will be further filtered by the given subscriber filter.

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

Fetches all the tenants by environment Id and organization Id.

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

Returns the total traffic for a given time range and by provided tenant Id, environment Id and organization Id.

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

Returns the total errors for a given time range and by provided tenant Id, environment Id and organization Id.

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

Returns the overall response latency(95th percentile value) for a given time range by provided tenant Id,
environment Id and organization Id.

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

Returns response latency value(95th percentile value) for each time interval(which is calculated according to the
granularity value) for a given time range and by provided tenant Id, environment Id and organization Id. If
the optional field of latencySummaryFilter is provided, result set will be further filtered by the given latency
summary filter.

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

Returns the number of successful(2xx response code) hit count for each time interval(which is calculated according
to the granularity value) for a given time range by provided tenant Id, environment Id and organization Id. If the
optional field of granularity is not provided, default granularity value will be used for time interval
calculation.

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

Returns the number of unsuccessful(total of both 4xx and 5xx response codes) hit count for each time interval(which
is calculated according to the granularity value) for a given time range by provided tenant Id, environment Id and
organization Id. If the optional field of granularity is not provided, default granularity value will be used
for time interval calculation.

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

Returns the number of proxy errors(total of both 4xx and 5xx response codes) hit count by each error category for
each time interval(which is calculated according to the granularity value) for a given time range by the provided
tenant Id, environment Id and organization Id. If the optional field of errorsByCategoryFilter is provided,
result set will be further filtered by the given errors by category filter.

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

Returns details of each proxy error(for both 4xx and 5xx response codes) for a given time range by the provided
tenant Id, environment Id and organization Id. If the optional field of errorsDetailsFilter is provided, result set
will be further filtered by the given error details filter.

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

Returns a list of hit counts for proxy and target errors by each time interval(which is calculated according to the
granularity value) for a given time range and by provided tenant Id, environment Id and organization Id. Each proxy
and target error category result set contains breakdown of errors as 4xx, 5xx and total. If the optional field of
errorsByStatusCodeFilter is provided, the result set will be further filtered by the given errors by status code
filter.

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

Returns hit count for each error response code(401, 404, 500, etc.) for each API for a given time range and by
provided tenant Id, environment Id and organization Id.

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

Returns the total proxy error hit count for an API across all the data available time range and by provided tenant
Id, environment Id and organization Id. If the optional time filter is provided, that time range will be used to
filter the error count.

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

Returns a list of each API usage over time for each time interval(which is calculated according to the
granularity value) for a given time range and by provided tenant Id, environment Id and organization Id. If the
optional field of apiUsageOvertimeFilter is provided, the result set will be further filtered by the given api
usage overtime filter.

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

Returns API usage by each application over time for each time interval(which is calculated according to the
granularity value) for a given time range and by provided tenant Id, environment Id and organization Id. If the
optional field of apiUsageOvertimeFilter is provided, the result set will be further filtered by the given api
usage overtime filter.

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

Returns API usage by each backend over time for each time interval(which is calculated according to the
granularity value)for a given time range and by provided tenant Id, environment Id and organization Id. If the
optional field of apiUsageByBackendOverTimeFilter is provided, the result set will be further filtered by the given
api usage by backend overtime filter.

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

Returns API usage by resource for a given time range and by provided tenant Id, environment Id and organization Id.
If the optional field of resourceUsageFilter is provided, the result set will be further filtered by the given
resource usage filter.

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

Returns the total hit count for an API across all the data available time range and by provided tenant Id,
environment Id and organization Id. If the optional time filter is provided, that time range will be used to filter
the hit count.

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

Returns a list of top slowest APIs by the response latency(95th percentile value) for a given time range and by
provided tenant Id, environment Id and organization Id.

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

Returns different latency category values(95th percentile values) for a given API over time for each time
interval(which is calculated according to the granularity value)for a given time range and by provided tenant Id,
environment Id and organization Id.

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

Returns the overall response latency(95th percentile value) for an API across all the data available time range and
by provided tenant Id, environment Id and organization Id. If the optional time filter is provided, that time range
will be used to filter the latency value.

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
interval(which is calculated according to the granularity value) for a given time range by provided tenant Id,
environment Id and organization Id.

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

Returns a list of top platforms by hit count for a given time range and by provided tenant Id, environment Id and
organization Id. The returned list will include top 9 platforms with the respective hit count and all the other
platforms labelled as 'Other' with the respective cumulated hit count. If the optional field of deviceFilter
is provided, the result set will be further filtered by the given device filter.

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

Returns a list of top user agent by hit count for a given time range and by provided tenant Id, environment Id and
organization Id. The returned list will include top 9 user agents with the respective hit count and all the other
user agents labelled as 'Other' with the respective cumulated hit count. If the optional field of deviceFilter
is provided, the result set will be further filtered by the given device filter.

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

NOTE: The required data for this operation is not yet collected in the database. Hence, this operation will not
return empty results.

Returns API usage by country for a given time range and by provided tenant Id, environment Id and organization Id.
If the optional field of geoLocationFilter is provided, the result set will be further filtered by the given
geolocation filter.

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

Returns a list of each API usage by each application for a given time range and by provided tenant Id, environment
Id and organization Id. If the optional field of apiUsageByAppFilter is provided, the result set will be further
filtered by the given api usage by app filter.

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

Returns top APIs by the alert count for a given time range and by provided tenant Id, environment Id and
organization Id. If the optional field of topAPIsByAlertCountFilter is provided, the result set will be further
filtered by the given top APIs by alert count filter.

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
<td valign="top"><a href="#topapisbyalertcountfilter">TopAPIsByAlertCountFilter</a>!</td>
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

Returns a summary of each alerts for a given time range and by provided tenant Id, environment Id and organization
Id. If the optional field of alertSummaryFilter is provided, the result set will be further filtered by the given
alert summary filter.

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
<td valign="top"><a href="#alertsummaryfilter">AlertSummaryFilter</a>!</td>
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

NOTE: This operations can be invoked only via using an on-prem key as the authentication header.

Returns a list of successful(2xx response codes) hit counts for each API by each application for a given time range.
If the optional field of successAPIUsageByAppFilter is provided, the result set will be further filtered by the
given success API usage by app filter.

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

API Id.

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

API provider name.

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

List containing latency category values over time. Returns an empty array if no data available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Granularity value used for data retrieval.

</td>
</tr>
</tbody>
</table>

### APIUsage

Represents API usage for a single timestamp. Timestamp is calculated according to the provided granularity value.

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

Represents a timestamp value calculated according to the used granularity value. For an example if the used
granularity value is '1d', timeSpan will represents start of the day calculated adhering to the timezone provided in
the TimeFilter.
Ex: timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

API usage count.

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

Application Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationName</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Application name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationOwner</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Application owner name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>usage</strong></td>
<td valign="top">[<a href="#apiusage">APIUsage</a>]</td>
<td>

List containing the APIs usage values. Returns an empty array if no data available.

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

List containing the API usage values across APIs by each application. Returns an empty array if no data available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Granularity value used for data retrieval.

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

API Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationId</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Application Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationName</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Application name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationOwner</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Application owner name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Usage of API by the application.

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

Backend name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>usage</strong></td>
<td valign="top">[<a href="#apiusage">APIUsage</a>]</td>
<td>

List containing the APIs usage values. Returns an empty array if no data available.

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

List containing the API usage values across APIs by each backend. Returns an empty array if no data available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Granularity value used for data retrieval.

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

API Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>usageByGeoLocation</strong></td>
<td valign="top">[<a href="#usagebygeolocation">UsageByGeoLocation</a>]</td>
<td>

List containing usages of API in different countries. Returns an empty array if no data available.

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

API Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>usage</strong></td>
<td valign="top">[<a href="#apiusage">APIUsage</a>]</td>
<td>

List containing the API usage values. Returns an empty array if no data available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Granularity value used for data retrieval.

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

List containing alert summary for each alert. Returns an empty array if no data available.

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

API Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>timestamp</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Represents a timestamp value calculated according to the used granularity value. For an example if the used
granularity value is '1d', timeSpan will represents start of the day calculated adhering to the timezone provided in
the TimeFilter.
Ex: timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>category</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Alert category(Latency or Traffic).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>metric</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Alert metric(Response Latency or Total Traffic).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>severity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Severity level of the alert(Low, Moderate or High)

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

Represents Application details.

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

Application Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>name</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Application name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>owner</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Application owner name.

</td>
</tr>
</tbody>
</table>

### CacheHit

Represents response cache hits and misses values for a single timestamp. Timestamp is calculated according to the
provided granularity value.

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

Represents a timestamp value calculated according to the used granularity value. For an example if the used
granularity value is '1d', timeSpan will represents start of the day calculated adhering to the timezone provided in
the TimeFilter.
Ex: timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hits</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Number of times response cache hit .

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>misses</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Number of times response cache missed.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>hitPercentage</strong></td>
<td valign="top"><a href="#float">Float</a>!</td>
<td>

Response cache hits as a percentage to total of hits and misses.

</td>
</tr>
</tbody>
</table>

### CacheHits

Represents response cache hits and misses over time.

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

List containing cache hits, misses and hit percentage over time. Returns an empty array if no data available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Granularity value used for data retrieval.

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

List containing error details over time. Returns an empty array if no data available.

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

Environment Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>name</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Environment name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>type</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Environment type. Choreo environment will be labelled as 'CHOREO' and on premise environments will be labelled as
'ON_PREM'.

</td>
</tr>
</tbody>
</table>

### ErrorCountByCategory

Represents number of errors for each category for a single timestamp. Timestamp is calculated according to the provided
granularity value. If categories are selected, then unselected category field will have null as the value while other
selected categories containing error count.

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

Represents a timestamp value calculated according to the used granularity value. For an example if the used
granularity value is '1d', timeSpan will represents start of the day calculated adhering to the timezone provided in
the TimeFilter.
Ex: timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>auth</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Authorization related error count.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>targetConnectivity</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Target Connectivity related error count.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>throttled</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Throttling related error count.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>other</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Other errors count.

</td>
</tr>
</tbody>
</table>

### ErrorCountByCode

Represents error count for a single error status code.

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

Error status code.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Error count.

</td>
</tr>
</tbody>
</table>

### ErrorCountByCodeForAPI

Represents error count for each status code for a single API.

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

Api Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>errorCountByCode</strong></td>
<td valign="top">[<a href="#errorcountbycode">ErrorCountByCode</a>]</td>
<td>

List containing error count for each error status code. Returns an empty array if no data available.

</td>
</tr>
</tbody>
</table>

### ErrorDetails

Represents details of an error for a single timestamp. Timestamp is calculated according to the provided granularity
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
<td colspan="2" valign="top"><strong>apiId</strong></td>
<td valign="top"><a href="#id">ID</a></td>
<td>

API Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>timeSpan</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Represents a timestamp value calculated according to the used granularity value. For an example if the used
granularity value is '1d', timeSpan will represents start of the day calculated adhering to the timezone provided in
the TimeFilter.
Ex: timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationId</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Application Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationName</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Application name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationOwner</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Application owner name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>reason</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Error reason.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Error count.

</td>
</tr>
</tbody>
</table>

### ErrorStatusCodeCategoryCounts

Represents errors by main status code category(4xx or 5xx) and the total error count.

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

Number of client errors(4xx response codes).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>_5xx</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Number of server errors(5xx response codes).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>total</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Total of client and server errors.

</td>
</tr>
</tbody>
</table>

### ErrorSummary

Provides unsuccessful(4xx and 5xx response code) usage summary across all the APIs.

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

List containing unsuccessful usage values. Returns an empty array if no data available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Granularity value used for data retrieval.

</td>
</tr>
</tbody>
</table>

### ErrorValue

Represents unsuccessful request count(4xx and 5xx response code) for a single timestamp. Timestamp is calculated
according to the provided granularity value.

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

Represents a timestamp value calculated according to the used granularity value. For an example if the used
granularity value is '1d', timeSpan will represents start of the day calculated adhering to the timezone provided in
the TimeFilter.
Ex: timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>errorCount</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Represents error request count.

</td>
</tr>
</tbody>
</table>

### ErrorsByCategory

Represents errors by category over time for error categories.

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

List containing error count for each error category over time. Returns an empty array if no data available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Granularity value used for data retrieval.

</td>
</tr>
</tbody>
</table>

### ErrorsByStatusCode

Represents error counts for each status code for each API for all the APIs.

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

List containing error count for each status code for each API. Returns an empty array if no data available.

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

Represents error category values(as 4xx, 5xx and total) for both proxy and target errors for a single timestamp.
Timestamp is calculated according to the provided granularity value.

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

Represents a timestamp value calculated according to the used granularity value. For an example if the used
granularity value is '1d', timeSpan will represents start of the day calculated adhering to the timezone provided in
the TimeFilter.
Ex: timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>proxy</strong></td>
<td valign="top"><a href="#errorstatuscodecategorycounts">ErrorStatusCodeCategoryCounts</a></td>
<td>

Represents proxy related errors categorized as client errors(4xx response codes) or server errors(5xx response
codes) and total of both categories.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>target</strong></td>
<td valign="top"><a href="#errorstatuscodecategorycounts">ErrorStatusCodeCategoryCounts</a></td>
<td>

Represents target related errors categorized as client errors(4xx response codes) or server errors(5xx response
codes) and total of both categories.

</td>
</tr>
</tbody>
</table>

### Latency

Represents latency values for each latency category for a single timestamp. Timestamp is calculated according to the
provided granularity value.

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

Represents a timestamp value calculated according to the used granularity value. For an example if the used
granularity value is '1d', timeSpan will represents start of the day calculated adhering to the timezone provided in
the TimeFilter.
Ex: timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>response</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Response latency(95th percentile) in milliseconds(ms).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>backend</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Backend latency(95th percentile) in milliseconds(ms).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>requestMediation</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Request mediation latency(95th percentile) in milliseconds(ms).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>responseMediation</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Response mediation latency(95th percentile) in milliseconds(ms).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>responseMedian</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Median(50th percentile) of the response latency in milliseconds(ms).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>backendMedian</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Median(50th percentile) of the backend latency in milliseconds(ms).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>requestMediationMedian</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Median(50th percentile) of the request mediation latency in milliseconds(ms).

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>responseMediationMedian</strong></td>
<td valign="top"><a href="#int">Int</a></td>
<td>

Median(50th percentile) of the response mediation latency in milliseconds(ms).

</td>
</tr>
</tbody>
</table>

### LatencySummary

Provides latency summary.

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

List containing latency values. Returns an empty array if no data available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Granularity value used for data retrieval.

</td>
</tr>
</tbody>
</table>

### LatencyValue

Represents latency value for a single timestamp. Timestamp is calculated according to the provided granularity value.

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

Represents a timestamp value calculated according to the used granularity value. For an example if the used
granularity value is '1d', timeSpan will represents start of the day calculated adhering to the timezone provided in
the TimeFilter.
Ex: timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>latencyTime</strong></td>
<td valign="top"><a href="#float">Float</a>!</td>
<td>

Represents Latency(95th percentile) time in milliseconds(ms).

</td>
</tr>
</tbody>
</table>

### OverallLatency

Represents overall latency values.

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

Response latency(95th percentile value) in milliseconds(ms).

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

Total number of results.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>limit</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Number of items in ht result set.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>offset</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Offset value for the result set.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>sortBy</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Result set sort by column name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>sortOrder</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Result set sort by order(asc or desc).

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

Platform name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Usage of platform.

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

API provider name.

</td>
</tr>
</tbody>
</table>

### ResourceUsage

Represents usage of a single API resource.

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

API Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>apiResourceTemplate</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

API resource template.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>apiMethod</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

API method.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Usage of API resource.

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

List containing usages of each resource of APIs. Returns an empty array if no data available.

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

A slow API calculated according to the response latency(95th percentile).

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

API Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>latency</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Represents Latency(95th percentile) time in milliseconds(ms).

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

API subscriber name.

</td>
</tr>
</tbody>
</table>

### SuccessAPIUsageByApplication

Represents successful(2xx response code) usages of an API by an application.

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

API Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>apiName</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

API name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>apiVersion</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

API version.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>apiCreatorTenantDomain</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Tenant domain of the API creator.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationId</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Application Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationName</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Application name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>applicationOwner</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Application name owner.

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

Provides successful(2xx response code) usage summary across all the APIs.

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

List containing successful usage values. Returns an empty array if no data available.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Granularity value used for data retrieval.

</td>
</tr>
</tbody>
</table>

### SuccessValue

Represents successful request count(2xx response code) for a single timestamp. Timestamp is calculated according to the
provided granularity value.

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

Represents a timestamp value calculated according to the used granularity value. For an example if the used
granularity value is '1d', timeSpan will represents start of the day calculated adhering to the timezone provided in
the TimeFilter.
Ex: timeSpan: '2021-06-21T00:00:00.0000000+05:30'

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>requestCount</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Represents successful request count.

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

API Id.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Alert count.

</td>
</tr>
</tbody>
</table>

### TopAPIsByAlertCount

Represents top APIs by alert count.

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

List containing top APIs by alert count. Returns an empty array if no data available.

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

Represents total errors.

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

Proxy related error count.

</td>
</tr>
</tbody>
</table>

### UsageByGeoLocation

Represents usage of a single API in a single country.

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

Country name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Usage of API.

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

User agent name.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>count</strong></td>
<td valign="top"><a href="#int">Int</a>!</td>
<td>

Usage of user agent.

</td>
</tr>
</tbody>
</table>

## Inputs

### APIUsageByAppFilter

Filters API usage by application results.

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

List of API Ids where the results needs to be filtered with. The maximum number of API Ids that can be defined is
limited to 5. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>appIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

List of application Ids where the results needs to be filtered with. The maximum number of application Ids that can
be defined is limited to 5. This field is optional. Hence, if there is no need of filtering by this attribute, omit
the field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>subscribers</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

List of names od subscribers where the results needs to be filtered with. The maximum number of subscribers that
can be defined is limited to 5. This field is optional. Hence, if there is no need of filtering by this attribute,
omit the field.

</td>
</tr>
</tbody>
</table>

### APIUsageByBackendOverTimeFilter

Filters API usage by backend overtime results.

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

List of API Ids where the results needs to be filtered with. The maximum number of API Ids that can be defined is
limited to 5. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Granularity value which will be used in data retrieval. This field is optional. Hence, if granularity value is not
set, default granularity for the related time range will be applied. Granularity value must be one of '1m', '15m',
'1h', '1d' or '7d' values.

</td>
</tr>
</tbody>
</table>

### APIUsageOverTimeFilter

Filters API usage overtime results.

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

List of API Ids where the results needs to be filtered with. The maximum number of API Ids that can be defined is
limited to 5. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>appIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

List of application Ids where the results needs to be filtered with. The maximum number of application Ids that can
be defined is limited to 5. This field is optional. Hence, if there is no need of filtering by this attribute, omit
the field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Granularity value which will be used in data retrieval. This field is optional. Hence, if granularity value is not
set, default granularity for the related time range will be applied. Granularity value must be one of '1m', '15m',
'1h', '1d' or '7d' values.

</td>
</tr>
</tbody>
</table>

### AlertSummaryFilter

Filters alerts summary results.

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

Pagination filter. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field
.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>searchFilter</strong></td>
<td valign="top"><a href="#searchfilter">SearchFilter</a></td>
<td>

Search filter. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>apiIds</strong></td>
<td valign="top">[<a href="#string">String</a>]</td>
<td>

List of API Ids where the results needs to be filtered with. The maximum number of API Ids that can be defined is
limited to 5. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field.

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

Filter applications by application owner and API Ids.

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

List of API Ids where the results needs to be filtered with. The maximum number of API Ids that can be defined is
limited to 5. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field.

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

API Id where the results needs to be filtered with. This field is optional. Hence, if there is no need of filtering
by this attribute, omit the field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Granularity value which will be used in data retrieval. This field is optional. Hence, if granularity value is not
set, default granularity for the related time range will be applied. Granularity value must be one of '1m', '15m',
'1h', '1d' or '7d' values.

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

Organization id where the results needs to be filtered with. This is a required field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>environmentId</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Environment id where the results needs to be filtered with. This is a required field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>tenant</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Tenant name where the results needs to be filtered with. This is a required field.

</td>
</tr>
</tbody>
</table>

### DeviceFilter

Filters devices related results.

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

List of API Ids where the results needs to be filtered with. The maximum number of API Ids that can be defined is
limited to 5. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field.

</td>
</tr>
</tbody>
</table>

### ErrorCountByStatusCodeFilter

Filters error by status code results.

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

API Id where the results needs to be filtered with. This field is optional. Hence, if there is no need of filtering
by this attribute, omit the field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>errorType</strong></td>
<td valign="top"><a href="#errortype">ErrorType</a>!</td>
<td>

Error type. This is a required field.

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

Pagination filter. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field
.

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

API Id. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field.

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

Granularity value which will be used in data retrieval. This field is optional. Hence, if granularity value is not
set, default granularity for the related time range will be applied. Granularity value must be one of '1m', '15m',
'1h', '1d' or '7d' values.

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

API Id where the results needs to be filtered with. This field is optional. Hence, if there is no need of filtering
by this attribute, omit the field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Granularity value which will be used in data retrieval. This field is optional. Hence, if granularity value is not
set, default granularity for the related time range will be applied. Granularity value must be one of '1m', '15m',
'1h', '1d' or '7d' values.

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

API Id where the results needs to be filtered with. This field is optional. Hence, if there is no need of filtering
by this attribute, omit the field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>appId</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Application Id where the results needs to be filtered with. This field is optional. Hence, if there is no need of
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

Granularity value which will be used in data retrieval. This field is optional. Hence, if granularity value is not
set, default granularity for the related time range will be applied. Granularity value must be one of '1m', '15m',
'1h', '1d' or '7d' values.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>paginationFilter</strong></td>
<td valign="top"><a href="#paginationfilter">PaginationFilter</a></td>
<td>

Pagination filter. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field
.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>searchFilter</strong></td>
<td valign="top"><a href="#searchfilter">SearchFilter</a></td>
<td>

Search filter. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field.

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

API Id where the results needs to be filtered with. This field is optional. Hence, if there is no need of filtering
by this attribute, omit the field.

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

API Id where the results needs to be filtered with. This field is required.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Granularity value which will be used in data retrieval. This field is optional. Hence, if granularity value is not
set, default granularity for the related time range will be applied. Granularity value must be one of '1m', '15m',
'1h', '1d' or '7d' values.

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

API Id where the results needs to be filtered with. This field is optional. Hence, if there is no need of filtering
by this attribute, omit the field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>granularity</strong></td>
<td valign="top"><a href="#string">String</a></td>
<td>

Granularity value which will be used in data retrieval. This field is optional. Hence, if granularity value is not
set, default granularity for the related time range will be applied. Granularity value must be one of '1m', '15m',
'1h', '1d' or '7d' values.

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

Organization id where the results needs to be filtered with. This is a required field.

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

Pagination filter. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field
.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>searchFilter</strong></td>
<td valign="top"><a href="#searchfilter">SearchFilter</a></td>
<td>

Search filter. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field.

</td>
</tr>
</tbody>
</table>

### SearchFilter

Filter results by searching matching results for the provided search text.

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

Text which needs to be searched among the results. This field is required.

</td>
</tr>
</tbody>
</table>

### SubscriberFilter

Filter subscribers results.

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

List of API Ids where the results needs to be filtered with. The maximum number of API Ids that can be defined is
limited to 5. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field.

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

Organization id where the results needs to be filtered with. This is a required field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>envId</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

Environment id where the results needs to be filtered with. This is a required field.

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
This is a required field.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>to</strong></td>
<td valign="top"><a href="#string">String</a>!</td>
<td>

End date of the time range. If the defined date time entry does not have a timezone, UTC time zone(z) will be used.
Example value: '2021-08-16T12:30:00.000+05:30'. This is a required field.

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

Pagination filter. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field
.

</td>
</tr>
<tr>
<td colspan="2" valign="top"><strong>searchFilter</strong></td>
<td valign="top"><a href="#searchfilter">SearchFilter</a></td>
<td>

Search filter. This field is optional. Hence, if there is no need of filtering by this attribute, omit the field.

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

