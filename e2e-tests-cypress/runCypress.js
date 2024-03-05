const path = require('path');
const { exec } = require('child_process');
const cypress = require('cypress');
const { createObjectCsvWriter } = require('csv-writer');

const usersCount = 2;
const startingNumber = 26000;
const usernamePrefix = 'wso2con1AdminTenant';
const cypressCommand = 'cypress run --headless';
const GRAPHQL_URL =  "https://apis.st.choreo.dev/projects/1.0.0/graphql";

// Function to run Cypress tests for a specific user and test methods
async function runTests(username, testMethods) {
  console.log('Username:', username);
  
  const results = {};
  
  // Run each test method
  for (const { name, spec } of testMethods) {
    try {
      const testResults = await cypress.run({
        headless: true,
        parallel: false,
        spec: [spec],
        env: {
          perfUsername: username
        }
      });

      console.log(`${name} tests completed for user ${username}`);
      results[name] = testResults.totalFailed === 0 ? 'Pass' : 'Fail';
      
      // Intercept the GraphQL request
      const interception = testResults.runs[0].video && testResults.runs[0].video.interceptions?.find(interception => interception.request.url.includes(GRAPHQL_URL));

      // Write the intercepted data to CSV file
      if (interception) {
        const csvWriter = createObjectCsvWriter({
          path: path.join(__dirname, `${username}_${name}_intercepted_results.csv`),
          header: [
            { id: 'requestBody', title: 'Request Body' },
            { id: 'responseBody', title: 'Response Body' }
          ]
        });

        const records = [{ requestBody: interception.request.body, responseBody: interception.response.body }];
        await csvWriter.writeRecords(records);
      }
    } catch (error) {
      console.error(`Error running ${name} tests for user ${username}:`, error);
      results[name] = 'Error';
    }
  }
  
  // Write results to CSV file
  const csvWriter = createObjectCsvWriter({
    path: path.join(__dirname, `${username}_test_results.csv`),
    header: [
      { id: 'testName', title: 'Test Name' },
      { id: 'testResult', title: 'Test Result' }
    ]
  });

  const records = Object.entries(results).map(([name, result]) => ({ testName: name, testResult: result }));
  await csvWriter.writeRecords(records);
}

// Define test methods
const testMethods = [
  { name: 'Login with multiple users concurrently', spec: 'cypress/e2e-stable/console/1-component/perf/0.1-con-perf-test-copy.ts' },
  { name: 'Creating a project', spec: 'cypress/e2e-stable/console/1-component/perf/0.1-con-perf-test-copy.ts' },
  { name: 'Verify Ballerina service component creation', spec: 'cypress/e2e-stable/console/1-component/perf/0.1-con-perf-test-copy.ts' },
  { name: 'Build the component', spec: 'cypress/e2e-stable/console/1-component/perf/0.1-con-perf-test-copy.ts' },
  { name: 'Deploying the component with Public level visibility', spec: 'cypress/e2e-stable/console/1-component/perf/0.1-con-perf-test-copy.ts' },
  
];

// Loop through users and run tests for each
(async () => {
  for (let i = startingNumber; i < startingNumber + usersCount; i++) {
    const username = `${usernamePrefix}${i}@wso2.com`;
    await runTests(username, testMethods);
  }
})();


