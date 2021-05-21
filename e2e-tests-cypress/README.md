# e2e-tests-cypress

## Setup
1. Proceed to `e2e-tests-cypress` and run 
`npm install`

2. [Optional] Configure the following properties in `cypress.env.json`, only if you need to execute `cypress/e2e/console/login-logout-flow.ts`.

```text
username
password
```

3. [Optional] If the `Choreo console` is running locally, update the following properties in `cypress.env.json`.

```text
loginURL
baseUrl
```

## Folder structure

The organization of the folder structure is based on the recomedations found at https://docs.cypress.io/guides/core-concepts/writing-and-organizing-tests

```
e2e-tests-cypress
|	cypress.env.json [1]
|	cypress.json [2]
|
└───cypress
│	└───e2e [3]
│	└───fixtures [4]
│	└───plugins [5]
│	└───support [6]
└───node_modules [7]
```

1. **cypress.env.json** is used to define variables that are accessible via `Cypress.env` in e2e tests(https://docs.cypress.io/guides/guides/environment-variables#Option-2-cypress-env-json).

2. **cypress.json** is used to store Cypress runtime configurations(https://docs.cypress.io/guides/references/configuration#cypress-json) for tweaking the behavior of Cypress. The custom test folder structure is defined here enabling Cyprus to execute the tests.

3. **e2e** Contains the End to End test cases. New test cases must be added to this directory and can be further organized into subdirectories for better organization.

4. **fixtures** are external pieces of static data that can be used by your tests. We should not hard code data in the test case. It should drive from an external source like CSV, HTML or JSON(https://docs.cypress.io/api/commands/fixture).

5. **plugins** contain the plugins or listeners. By default, Cypress will automatically include the plugins file “cypress/plugins/index.js” before every test it runs. You can programmatically alter the resolved configuration and environment variables using plugins, Eg. If we have to inject customized options to browsers like accepting the certificate, or do any activity on test case pass or fail or to handle any other events like handling screenshots. They enable you to extend or modify the existing behavior of Cypress(https://docs.cypress.io/guides/tooling/plugins-guide).

6. **support** writes customized commands or reusable methods that are available for usage in all of your spec/test files. This file runs before every single spec file. That’s why you don’t have to import this file in every single one of your spec files.  The “support” file is a great place to put reusable behavior such as Custom Commands or global overrides that you want to be applied and available to all of your spec files.

7. **node_modules** is the folder where NPM installs all the project dependencies.



## Test execution

### Interactive mode
1. Run the following command to open the Cypress app
`npx cypress open`

<p align="center">
   <img src="images/cypress-app.png" height="400" alt="cypress-app">
</p>

2. In the Cypress app all the spec files can be found. Click on a spec file to run it. This will open the cypress runner that allows you to see commands as they execute while also viewing the application under test.

<p align="center">
   <img src="images/test-runner.png" height="400" alt="test-runner">
</p>

### Headless mode
1. Use below commands to run tests in headless mode

	* Run all the spec files in the project
	> `npm run local`

	* Run one spec file
	> `npx cypress run --spec "cypress/e2e/<path/to/spec/file>"`

		ex: `npx cypress run --spec "cypress/e2e/console/clean/clean-this-run.ts"`

	* Run multiple spec files
	> `npx cypress run --spec "cypress/e2e/<path/to/spec/file1>,cypress/e2e/<path/to/spec/file2>"`

		ex: `npx cypress run --spec "cypress/e2e/console/clean/clean-this-run.ts,cypress/e2e/console/integrations/clone-and-edit-flow.ts"`

	* Run all spec files in a folder
	> `npx cypress run --spec "cypress/e2e/<path/to/folder>/**/*"`

		ex: `npx cypress run --spec "cypress/e2e/console/integrations/**/*"`

3. After running the tests in headless mode, following artifacts can be found

- videos - for each spec file, a seperate video will be created
	- Location: `cypress/videos`
- screenshots - screenshot will be captured when a failure happens during a test run
	- Location: `cypress/screenshots`
- reports - reports will be generated only if the `npm run test` is used
	- Location: `cypress/reports`


## Sceanarios
Scenarios covered by the End to End tests.

|Scenario    |	Work flow	|	Cypress Spec Name |
|:----------|:-------------|:------|
|1. Login & logout choreo  |1. Login to choreo with valid credential (gmail) | login-logout-flow.ts|
||		2. Logout ||				
||
|2. Service from scratch   |  	1. Create a service "hello world"	|	service-deploy-from-scratch.ts |
||					2. Run & Test ||
||					3. Deploy	||
||					4. Back to list	||
||					5. Delete (Cannot delete active app) ||
||
|3. Service from sample |	1. Create from sample (Echo service) |service-deploy-sample.ts |
||			2. Run & Test ||	
||			3. Deploy	||	
||			4. Stop deploy	||	
||			5. Back to list and delete	||			
||			6. Delete ||					
||
|4. Integration from scratch  |	1. Create an integration "Send a message in gmail when an issue in github is commented"	|	integration-deploy-from-scratch.ts |
||		2. Run & Test ||
||		3. Deploy	||
||		4. Back to list and try to delete ||
||		5. Delete (Cannot delete active app) ||		
||
|5. Integration from sample	 |1. Create from sample "G calender event to twilio SMS"	|	integration-run-sample.ts |
||		2. Run & Test ||
||		3. Deploy ||
||		4. Stop deploy ||
||		5. Back to list and delete ||			
||		6. Delete ||		
||
|6. Trigger (Schedule) |	1. Create a schedule trigger that runs every 1 minute	|	schedule-trigger-flow.ts |
||		2. Add a hello world log ||		
||		3. Check expression editor diagnostics ||		
||		4. Run the app and check whether the log prints	||
||		5. Deploy the app and check whether the log prints once	 ||		
||		6. Stop deploy	||
||		7. Delete App ||		
||
|7. Dev portal - API comments	| 1. Visit API Overview	|	api-comment-flow.ts |
||		2. Add rating ||
||		3. Update rating ||		
||
|8. Dev Portal - API ratings	| 1. Visit API Overview |	api-rating-flow.ts|
||		2. Add comment	||
||		3. Delete comment ||		
||
|9. Devportal - Credentials and Try out	| 1.Visit API Overview	|	credentials-try-out-flow.ts |
||		2. Visit API Credentials ||	
||		3. Select contract	||
||		4. Generate Credentials	||	
||		5. Visit TryOut	||
||		6. Click "Get Test Key"	||	
||		7. Try out API	||
||		8. Visit Credentials ||
||		9. Remove credentials ||	
||
|10. Devportal - Applications, Subscriptions and Try out |	1. Visit Application	|	application-try-out-flow.ts |
||		2. Create application ||
||		3. Generate OAuth2 token ||	
||		4. Generate API Key	||
||		5. Add a subscription to API ||		
||		6. Visit API TryOut	||
||		7. Select Application	||
||		8. Click "Get Test Key"	||	
||		9. Try out API	||					
||
|11. Cleanup |	1. Cleanup test Application and test API	|	clean-this-run.ts |
||		2. Cleanup created subscriptions ||		
||
|12. API from Service |	1. Create an API from service(sample app)	|	api-from-choreo-service.ts |
||		2. Verify Overview page	||
||		3. Change & update runtime configs	||		
||		4. Try test console	||
||		5. Delete using REST API ||	
||
|13. API from REST endpoint	| 1. Create an API by providing a REST endpoint	|	api-from-rest-endpoint-flow.ts |
||		2. Verify Overview page	||
||		3. Change Resources	||
||		4. Quick deploy and revision ||
||		5. Publish 	||
||		6. Try test console	||
||		7. Delete API from overview ||		
||
|14. API from OAS |	1. Create & publish API from OAS| api-from-oas-flow.ts |
||		2. Verify Overview page	||
||		3. Change endpoint	||
||		4. Change subscriptions	||
||		5. Create revison from + button and deploy  ||			
||		6. Try  test console ||
||		7. Delete API from overview	||			
||
|15. Observability overview ( Throughput/Latency graphs and tracing a request)	| 1. Create a service | observability-test-flow.ts |
||	2. Navigate to Observability view ||
||	3. Access the sample service ||
||	4. Verfiy the Throughput / Latency graphs. 	||
||	5. Trace a single request and verify the status code, avg Latency. ||
||					
|16. Logs view	|1. Create a service | observability-test-flow.ts |
||	2. Navigate to Observability view ||
||	3. Access the sample service ||
||	4. Assert available logs, test log searching and logs download	||
||					
|17. Low code form AI suggestions	| 1. Create a service | service-deploy-from-scratch.ts |
||	2. Create variable property ||
||	3. Add HTTP connector with AI suggestion of previous variable ||
||	4. Assert the code to check whether the Ai suggestion added to low ||code form ||
||					
|18. Test run hello world service | 1. Create a service | service-deploy-from-scratch.ts |
||	2. Create a respond to the service ||
||	3. Click Test & run button  ||
||	4. Assert the test URL ||	
||					
|19. Test postman view	| 1. Create a service | service-deploy-from-scratch.ts |
||	2. Create a respond to the service ||
||	3. Navigate to test view ||
||	4. Access the postman view ||
||	5. Insert an invalid API key ||
||	6. Assert for the API key error ||	
||					
|20. Delete Apps and APIs that are old or created by this run |	Cleanup all the apps that are cretaed before 7 days or created in the current test run	| clean-this-run.ts |
||					
|21. Invite members	|1. Go to settings | invite-members.ts |
||	2. Go to Invite members ||
||	3. Add developer group and add email -> Invite ||
||	4. Check pending invitations ||	
||					
|22. Add members to groups	| 1. Go to settings | group-list-views.ts |
||	2. Go to groups tab ||
||	3. Select Admin group ||
||	4. Search the member and add ||
||	5. Check the member list ||	
||					
|23. Test Anonymous app linking (via "Add to Choreo") | 1. Navigate to Home page after Login | anonymous-app-linking.ts |
||	2. Run a Ballerina project in the background with Choreo enabled ||
||	3. capture and navigate to ObsURL ||
||	4. Add to Choreo with creation of a new app ||
||	5. Copy the linking command ||
||	6. Run the linking command in background ||
||	7. Check for successful linking ||
||	8. Clean up the created app	||			
||										
|24. Generate on-prem key | 1. Go to settings | generate-onprem-key-flow.ts |
||	2. Go to on-prem keys tab ||
||	3. Generate key ||
||	4. Edit the key name ||
||	5. Regenerate key ||
||	6. Delete the key ||					
||					
|25. Clone and edit integrations |	1. Go to integrations | deploy-run-sample.ts |
||	2. Go to "Use Prebuilt" ||
||	3. Select "Google calendar to twilio msg" sample ||
||	4. Clone and Edit ||
||	5. Fill calander and Twilio configs ||
||	6. Deploy"	||	
||					
|26. Groups list view |1. Go to settings. | group-list-views.ts |
||	2. Go to Organization->Groups tab ||
||	3. Click "Add group" button and add a group giving the details ||
||	4. Search the group from search field ||
||	5. Click the group and type a existing member name in the search field ||
||	6. Add the member ||
||	7. Remove the added member ||
||	8. Go back to groups ||
||	9. Delete the new group ||