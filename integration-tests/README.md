# integration-tests

## 1. Setup

1. Setup following configurations as environment variables
    - CHOREO_ENDPOINT
    - STS_ENDPOINT
    - TEST_CHOREO_ORG_ID
    - TEST_CHOREO_ORG_HANDLE
    - TEST_CHOREO_ORG_UUID
    - STS_CLIENT_ID
    - STS_CLIENT_SECRET
2. Navigate to the `integration-tests` directory
## 2. Run

  run `mvn clean verify`

## 3. Folder Structure


```
integration-tests/src/test/java/com/wso2/choreo/
|───java/com/wso2/choreo/integration
|	|───common
|	|───config
|	└───tests
└───resources
	└───templates
        └───connector-builder
                get_connector_success.json
                publish_status_completed.json
                publish_success_ok.json
    citrus-application.properties
    log4j.properties
    testng.xml

```
**java/com/wso2/choreo/integration**

1. **/common**
    - Common Java implementations to run Choreo use-cases
2. **/tests**
    - Citrus integration tests written for Choreo use-cases
        -  **/connectorbuilder** - connector publishing related integration tests
        - ...

2. **/config**
    - Configurations needed to run common use-cases and integration tests

**resources**

1. **/templates**
    - Sample Json payload templates
        -  **/connectorbuilder** - connector publishing related Json payload templates
        - ...

## 4. Scenarios

<table>
	<thead>
		<tr>
			<th align="left">test source</th>
			<th align="left">Scenario</th>
			<th align="left">Work flow</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>1. /connectorbuilder</td>
			<td>1. Publish a connector </td>
			<td>
				1) Publish a connector <br/>
				2) Continuously check the status of publishing action<br/>
				2) Retrieved the details of the published connector<br/>
			</td>
		</tr>
    </tbody>

</table>
