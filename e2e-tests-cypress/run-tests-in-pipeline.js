const cypress = require('cypress');

const args = process.argv.slice(2);

if (args.length == 2) {
  const buildNumber = args[0];
  const buildId = args[1];

  runTests(buildNumber, buildId);
} else {
  printUsage();
}

async function runTests(buildNumber, buildId) {
  cypress
    .run({
      headless: true,
      record: true,
      parallel: true,
      tag: buildNumber,
      ciBuildId: buildId,
      spec: ['cypress/e2e-stable/devportal/*.ts','cypress/e2e-stable/console/**/*','cypress/e2e-unstable/devportal/*.ts','cypress/e2e-unstable/console/**/*','!cypress/e2e-stable/console/1-component/perf/**'],
    })
    .then((results) => {
      console.log(results.totalPassed);
    })
    .catch((err) => {
      console.error(err)
    })
}


function printUsage() {
    console.log("\n");
    console.log("usage: node run-tests-in-pipeline.js <buildNumber> <buildId>")
    process.exit(1);
}