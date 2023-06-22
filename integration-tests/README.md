# integration-tests

## 1. Setup

You can execute integration tests against your own Choreo account in Dev using the following method,

1. Login to Choreo Console and check the response of the `/validate-user` call in the browser network tab and setup the following environment variables,
      
   - TEST_CHOREO_ORG_ID=<Your Org ID>
   - TEST_CHOREO_ORG_UUID=<Your Org UUID>
   - TEST_CHOREO_ORG_HANDLE=<Your Orh handle>
    
2. Setup following configurations as environment variables(Please talk to your EM or any QA team member to get these values for the respective env)
   - ALERT_MAIL_IMAP_PASS
   - GITHUB_PAT
   - GMAIL_API_CS
   - GMAIL_API_REFRESH_TOKEN


## 2. Run all tests

1. Login to Choreo Console and check the response of the STS `/token` call in the browser network tab and copy the value of the access_token
2. Navigate to the `integration-tests` directory
3. run `mvn clean verify -DToken=<Your access_token>` to execute the tests
4. Note the access token is only valid for 1 hour, so you will need to get a new access token to run the tests after the expiry takes place

## 3. Run DP tests

1. Navigate to the `integration-tests` directory
2. run `sh dp-test-runner.sh <staging/prod> <staging-access-token/prod-access-token>`

## 4. Run a specific test in IntelliJ

1. Repeat the previous steps 1 to get you access_token.
2. Right-click on the test you want to run and select the `Modify Run Configuration...` option.
3. Go to `JVM Settings > VM Options` and enter the following before clicking on OK,
     `-ea -DToken=<Your access_token>`
4. Now you can run the individual test through IntelliJ
            

## 5. Directory Structure

```
integration-tests/src/test
|
|───java/com/wso2/choreo/integration
|	|───common
|	|───config
|	└───tests
|           └───connectorbuilder
|           └─── ...
|           EndpointConfig.java
└───resources
	└───templates
            └───connectorbuilder
            |      get_connector_success.json
            |      publish_status_completed.json
            |      publish_success_ok.json
            └─── ...
       citrus-application.properties
       log4j.properties
       testng.xml
```

**java/com/wso2/choreo/integration**

1. **/common**
   - Common Java implementations to run Choreo use-cases

2. **/config**
   - Configurations needed to run common use-cases and Citrus integration tests

3. **/tests**
   - Citrus integration tests written for Choreo use-cases
      - **/connectorbuilder** - connector publishing related integration tests
      - ...
   - **EndpointConfig.java**
      - Spring bean configuration class that has the Citrus Endpoints defined

**resources**

1. **/templates**
   - Sample Json payload templates
      - **/connectorbuilder** - connector publishing related Json payload templates
      - ...

## 6. Adding a new test configuration

Test configurations are supported in 2 ways

1. Defined environment variables
2. Defined as yaml configuration

When a config is evaluated at the test level, precedence will **first** be given to environment variables
and then yaml configurations. Therefor a yaml configuration can be overridden by setting the corresponding 
env variable.

**Note**: _Give preference to defining new configs as yaml configurations because they are easier to manage.
Env variables should only be considered values such as credentials that need to remain private._

**Steps for defining a new configuration**

- Add the new configuration name to the `ConfigDefinition.java` enum. This will act as the unique identifier
   of the configuration.

- For a yaml configuration, add the config to the _dev-env-config.yaml_, _staging-env-config.yaml_ and _prod-env-config.yaml_
   (These do **not** need to be explicitly setup at Azure pipeline level)

- Configs that are only set as env variables do not need to be added to the yaml and must be configured
at Azure pipeline level.

## 5. Scenarios

<table>
    <thead>
        <tr>
            <th>test source</th>
            <th>Scenario</th>
            <th>Work flow</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>connectorbuilder</td>
            <td>Publish a connector</td>
            <td>
                1) Publish a connector <br/>
                2) Continuously check the status of publishing action <br/>
                3) Retrieved the details of the published connector <br/>
            </td>
        </tr>
        <tr>
            <td>createAPIProxyFromScratch</td>
            <td>Check valid and invalid APInames for proxy</td>
            <td>
                1) Check APIName Validation for APIProxy Creation <br>
                2) Check APIBasePath Validation forA PIProxyCreation <br>
            </td>
        </tr>
        <tr>
            <td>createProjectIT</td>
            <td>Project creation</td>
            <td>
                1) Check project creation <br>
            </td>
        </tr>
        <tr>
            <td>getCommitListIT</td>
            <td>Check CommitList</td>
            <td>
                1) Get commit list <br>
            </td>
        </tr>
        <tr>
            <td>createComponentIT</td>
            <td>Create a component, check status and delete</td>
            <td>
                1) Create RESTAPI component <br/>
                2) Check created component status <br/>
                3) Delete the component <br/>
            </td>
        </tr>
        <tr>
            <td>oomAlertIT</td>
            <td>Check OOM alert</td>
            <td>
                1) Verify OOM alert using IMAP <br/>
            </td>
        </tr>
        <tr>
            <td>deployIT</td>
            <td>Deploy RestAPI component</td>
            <td>
                1) Verify RestAPI component deployment <br/>
            </td>
        </tr>
        <tr>
            <td>addConfigurationsIT</td>
            <td>Add Configurations</td>
            <td>
                1) Verify adding configurations to a component <br/>
            </td>
        </tr>
        <tr>
            <td>createTriggerIT</td>
            <td>Create webhook component and deploy</td>
            <td>
                1) Verify adding webhook component <br/>
                2) Verify retrieving the created component <br/>
                3) Verify deploying the component <br/>
            </td>
        </tr>
        <tr>
            <td>insightsAPIIT</td>
            <td>Get test environments and check insights metrics</td>
            <td>
                1) Verify test environments <br/>
                2) Verify invoke utility Operations <br/>
                3) Verify insights overview results <br/>
            </td>
        </tr>
        <tr>
            <td>insightsAuthAPIIT</td>
            <td>Get insights auth token</td>
            <td>
                1) Verify auth token <br/>
            </td>
        </tr>
        <tr>
            <td>insightsAlertAPIIT</td>
            <td>Verify the insights metrics in env list</td>
            <td>
                1) Verify get traffic <br/>
                2) Verify post traffic <br/>
                3) Verify put traffic <br/>
                4) Verify delete traffic <br/>
                5) Verify get latency <br/>
                6) Verify post latency <br/>
                7) Verify put latency <br/>
                8) Verify delete latency <br/>
            </td>
        <tr>
            <td>createUserManagedComponent</td>
            <td>Create BYOR component using GH,deploy,test and delete</td>
            <td>
                1) Verify creating user managed component <br/>
                2) Verify created component status <br/>
                3) Verify initial PR Generation <br/>
                4) Verify PR merge <br/>
                5) Verify component retrieval <br/>
                6) Verify component deployment <br/>
                7) Verify component deployment status <br/>
                8) Verify API invocation <br/>
                9) Verify component retrieval for deleted repo <br/>
               10) Verify component deletion  <br/>
            </td>
        </tr>
        <tr>
            <td>createUserManagedComponentNonEmptyRoot</td>
            <td>Create BYOR component with existing code in root directory using GH,deploy,test and delete</td>
            <td>
                1) Verify creating user managed component <br/>
                2) Verify created component status <br/>
                3) Verify initial PR Generation <br/>
                4) Verify PR merge <br/>
                5) Verify component retrieval <br/>
                6) Verify component deployment <br/>
                7) Verify component deployment status <br/>
                8) Verify API invocation <br/>
                9) Verify component deletion  <br/>
            </td>
        </tr>
        <tr>
            <td>createUserManagedComponentNonEmptySub</td>
            <td>Create BYOR component with existing code in sub directory using GH,deploy,test and delete</td>
            <td>
                1) Verify creating user managed component <br/>
                2) Verify created component status <br/>
                3) Verify initial PR Generation <br/>
                4) Verify PR merge <br/>
                5) Verify component retrieval <br/>
                6) Verify component deployment <br/>
                7) Verify component deployment status <br/>
                8) Verify API invocation <br/>
               9) Verify component deletion <br/>
            </td>
        </tr>
        <tr>
            <td>createDeployInvokeWebhookIT</td>
            <td>Create webhook component,deploy,test,observability logs and delete</td>
            <td>
                1) Verify creating user managed component <br/>
                2) Verify created component status <br/>
                3) Verify initial PR Generation <br/>
                4) Verify PR merge <br/>
                5) Verify getting sha of webhookBal <br/>
                6) Verify commit <br/>
                7) Verify component retrieval <br/>
                8) Verify component deployment <br/>
                9) Verify component deployment status <br/>
                10) Verify API invocation <br/>
                11) Verify fetch observabilityId <br/>
                12) Verify observabilityLogs <br/> 
                13) Verify component deletion <br/>
                14) Verify github repo deletion <br/>
            </td>
        </tr>
        <tr>
            <td>observabilityAPITestCase</td>
            <td>Verify observability metrics from a RESTAPI component</td>
            <td>
                1) Verify observability AST <br/>
                2) Verify observability metrics density <br/>
                3) Verify observability metric density histogram <br/>
                4) Verify observability stats <br/>
                5) Verify observability trace list <br/>
                6) Verify observability trace information <br/>
            </td>
        </tr>
        <tr>
            <td>loggingAPITestCase</td>
            <td>Verify observability metrics from a RESTAPI component</td>
            <td>
                1) Verify observability grouped logs <br/>
                2) Verify observability live logs <br/>
                2) Verify observability logs download <br/>
            </td>
        </tr>
        <tr>
            <td>sysObsAPITestCase</td>
            <td>Verify observability metrics from a RESTAPI component</td>
            <td>
                1) Verify observability system metrics <br/>
            </td>
        </tr>
        <tr>
            <td>themeManagementTestCase</td>
            <td>Update custom theme assets, palette and typography</td>
            <td>
                1) Verify updating assets <br/>
                2) Verify updating typography <br/>
                3) Verify updating color palette <br/>
            </td>
        </tr>
        <tr>
            <td>createMaxAPIRevisionsUsingDeployments</td>
            <td>Create revision to exceed API revision limit reached with deployments</td>
            <td>
                1) Verify creating revision using a deployment to exceed API revision limit <br/>
                2) Verify creating revision using Settings page to exceed API revision limit <br/>
                3) Verify revision count after exceeding API revision limit <br/>
            </td>
        </tr>
        <tr>
            <td>createMaxAPIRevisionsUsingSettingsPage</td>
            <td>Create revision to exceed API revision limit reached with revision creation in Settings page</td>
            <td>
                1) Verify creating revision with a deployment to reach API revision limit <br/>
                2) Verify getting revision to delete <br/>
                3) Verify deleting oldest undeployed revision <br/>
                4) Verify creating backup revision for existing state <br/>
                5) Verify restoring revision for existing state <br/>
                6) Verify creating revision for new state <br/>
                7) Verify deploying revision with new state <br/>
                8) Verify querying build by version <br/>
                9) Verify creating revision in project manager <br/>
                10) Verify restoring backup revision <br/>
                11) Verify deleting backup revision <br/>
                12) Verify revision count after exceeding API revision limit <br/>
            </td>
        </tr>
    </tbody>
</table>
