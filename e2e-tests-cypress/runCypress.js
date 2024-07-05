
const cypress = require('cypress');
const { exec } = require('child_process');
process.env['ELECTRON_EXTRA_LAUNCH_ARGS'] = '--remote-debugging-port=9222';

const usersCount = 20;
const startingNumber = 13000;
//const usernamePrefix = 'wso2con1AdminTenant';
const usernamePrefix = 'perfAdmin';

 // Define test spec
 const testMethods = [
    {spec: 'cypress/e2e-stable/perf/0.1-con-perf-test.ts'}
   //  {spec: 'cypress/e2e-stable/perf/0.2-con-perf-test-webapp.ts'}
];


// Function to run Cypress tests for a specific user and test methods
async function runTests(username) {
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
                },
                exit: true,
                name: name
            });

            console.log(`${name} tests completed for user ${username}`);
            results[name] = testResults.totalFailed === 0 ? 'Pass' : 'Fail';
        } catch (error) {
            console.error(`Error running ${name} tests for user ${username}:`, error);
            results[name] = 'Error';
        }
    }
}

// Loop through users and run tests for each
async function runTestsAsync() {
    for (let i = startingNumber; i < startingNumber + usersCount; i++) {
        console.log(`Running tests for user ${i}`);
        const username = `${usernamePrefix}${i}@wso2.com`;
        runTests(username);
    }
}

// Run tests for each user
runTestsAsync();
