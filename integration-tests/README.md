# Integration-tests

## 1. Setup

You can execute integration tests against your own Choreo account in Dev using the following method,

1. Login to Choreo Console and check the response of the `/validate-user` call in the browser network tab and set up the
   following environment variables,

    - TEST_CHOREO_ORG_ID=<Your Org ID>
    - TEST_CHOREO_ORG_UUID=<Your Org UUID>
    - TEST_CHOREO_ORG_HANDLE=<Your Orh handle>

2. Setup following configurations as environment variables(Please talk to your EM or any QA team member to get these
   values for the respective env)
    - ALERT_MAIL_IMAP_PASS
    - GITHUB_PAT
    - GMAIL_API_CS
    - GMAIL_API_REFRESH_TOKEN
    - RESOURCE_AUTHZ_USER_PASSWORD

## 2. Run all tests

1. Login to Choreo Console and check the response of the STS `/token` call in the browser network tab and copy the value
   of the access_token
2. Navigate to the `integration-tests` directory
3. run `mvn clean verify -DToken=<Your access_token>` to execute the tests
4. Note the access token is only valid for 1 hour, so you will need to get a new access token to run the tests after the
   expiry takes place

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
|	|───apis
|	|───common
|	|───config
|	|───models
|	└───tests
|           └───connectorbuilder
|           └─── ...
|           EndpointConfig.java
└───resources
    |───scopes
    |   |───dev-scopes.yaml
    |   |───gen-scopes-yaml.py
    |   |───prod-scopes.yaml
    |   └───staging-scopes.yaml
    └───templates
            └───connectorbuilder
            |      get_connector_success.json
            |      publish_status_completed.json
            |      publish_success_ok.json
            └─── ...
       citrus-application.properties
       dev-env-config.yaml
       dev-security-env-config.yaml
       dp.xml      
       log4j2.properties
       prod-env-config.yaml
       security.xml
       staging-env-config.yaml
       testng.xml
```

**java/com/wso2/choreo/integration**

1. **/apis**
    - Java classes that represent the Choreo APIs that will be called by the tests.

2. **/common**
    - Common Java implementations to run Choreo use-cases

3. **/config**
    - Configurations needed to run common use-cases and Citrus integration tests

4. **/models**
    - Java classes that represent the Choreo data models

5. **/tests**
    - Citrus integration tests written for Choreo use-cases
        - **/connectorbuilder** - connector publishing related integration tests
        - ...
    - **EndpointConfig.java**
        - Spring bean configuration class that has the Citrus Endpoints defined

**resources**

1. **/scopes**
    - Scopes needed to be provided when requesting an access token from Choreo.
      These are Choreo environment specific and hence are maintained in separate yaml files.
      A Python script is provided to extract the env specific scopes from an existing access token to make it easier to maintain. For more details refer to the provided README in the directory.
        - **dev-scopes.yaml** - Scopes needed for Dev environment
        - **prod-scopes.yaml** - Scopes needed for Prod environment
        - **staging-scopes.yaml** - Scopes needed for Staging environment
        - **gen-scopes-yaml.py** - Python script to generate scopes yaml file

2. **/templates**
    - Sample Json payload templates used in Citrus tests

3. **citrus-application.properties**
    - SpringBoot entry point used by the Citrus framework to run the tests.

4. **dev-env-config.yaml**
    - Environment specific configurations for Dev environment.

5. **dev-security-env-config.yaml**
    - Lower privilege user configuration used by the security tests in the Dev environment.

6. **dp.xml**
    - testng xml file to run the Data Plane specific tests. This is mainly used for testing the Private Data Plane setup.

7. **log4j2.properties**
    - Log4j2 configuration file to configure the logging levels during test runs. Debug and Wire logging is enabled by default.

8. **prod-env-config.yaml**
    - Environment specific configurations for Prod environment.

9. **security.xml**
    - testng xml file to run the security tests.

10. **staging-env-config.yaml**
    - Environment specific configurations for Staging environment.

11. **testng.xml**
    - testng xml file to run Choreo integration tests.

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

- For a yaml configuration, add the config to the _dev-env-config.yaml_, _staging-env-config.yaml_ and
  _prod-env-config.yaml_
  (These do **not** need to be explicitly setup at Azure pipeline level)

- Configs that are only set as env variables do not need to be added to the yaml and must be configured
  at Azure pipeline level.

## 7. Adding a new test

- Create a new test class under the `tests` package and remember to add the test class to the `testng.xml` file. If you
  do not add the test class to the `testng.xml` file, the test will not be executed in the pipeline.
