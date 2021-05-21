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