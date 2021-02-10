import { Selector, RequestLogger } from "testcafe";
import { screen } from "@testing-library/testcafe";
import { getLocation } from "../utils/login-utils";
import page from "../model/page";
import * as config from "../../testcafe-run-config.json";
import { goBacktoAppsList, generateAppName, WAIT_TIME_SHORT, WAIT_TIME_MEDIUM,
  WAIT_TIME_LONG, saveLogs, enableDetailedLogs, deleteApp } from "../utils/choreo-utils";
import { logger } from '../utils/logger'
const util = require('util');
const exec = util.promisify(require('child_process').exec);

declare const test: TestFn;

declare global {
    interface TestController {
      testRun: {
        test: {
          name: string;
        }
      };
    }
  }
  
const httpLogger = RequestLogger(undefined, {
    logRequestBody: true,
    logRequestHeaders: true,
    logResponseBody: true,
    logResponseHeaders: true,
    stringifyRequestBody: true
  });
  
fixture("App linking")
    .page(config.testURL)
    .beforeEach(async () => {
        await page.login();
        await enableDetailedLogs();
    })
    .requestHooks(httpLogger)
    .afterEach(async t => {
        const {log,error}:BrowserConsoleMessages = await t.getBrowserConsoleMessages();
        const httpRequests = httpLogger.requests;
        const data = [...log,...error];
        saveLogs(t,data,httpRequests)
        httpLogger.clear();
});;

test.skip("test app linking", async (t) => {

    await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
    logger.info("Page loaded successfully");

    logger.info("Start connecting a running app : " + "linking-test-app");
    await t
        .click(screen.getAllByTestId("link-ballerina-app"))
        .typeText(screen.getAllByPlaceholderText("App name"), "linking-test-app")
        .click(screen.getByText("Generate Secret"));
    await t.expect(screen.queryAllByTestId("copy-btn").exists).ok({ timeout: WAIT_TIME_MEDIUM });
    logger.info("Secret generated successfully ");

    async function runBallerinaApp(secret: string) {
        const secretConfig = 'secret=\\"' + secret + '\\"';
        const { stdout, stderr } = await exec('sh src/utils/applinking_test/run_ballerina.sh ' + secretConfig);
        console.log('stdout:', stdout);
        console.log('stderr:', stderr);
    }
    const secret = (await screen.getByPlaceholderText("Application secret").value).toString();
    logger.info("Secret value" + secret);
    await runBallerinaApp(secret);

    logger.info("Waiting for app to connect...")
    await t.expect(screen.findByText("Successfully Connected").exists).ok({ timeout: WAIT_TIME_LONG });
    await t.click(screen.findByText("Observe"));
    await t.expect(await getLocation()).contains("observe/", { timeout: WAIT_TIME_MEDIUM });
    logger.info("App Linking successful");
});

test.meta({'stable': "true"})("test anonymous app linking", async (t) => {
    let appName = generateAppName("linking");
    logger.info("Start connecting an anonymous app : " + appName);

    async function getAnnonAppUrl() {
        const { stdout, stderr } = await exec('sh src/utils/applinking_test/run_annonapp.sh');
        console.log('stdout:', stdout);
        console.log('stderr:', stderr);
        const url = /visit (http[^\s]+)/i.exec(stdout)[1];
        return url;
    }
    const obsUrl = await getAnnonAppUrl();
    logger.info("Retrieved observe URL from ballerina application : " + obsUrl);
    await t.navigateTo(obsUrl);
    await t.wait(20000);

    await t.expect(screen.getByText("Add to Choreo").exists).ok({ timeout: WAIT_TIME_SHORT })
        .click(screen.getByText("Add to Choreo"))
        .typeText(screen.findByPlaceholderText("Application name"), appName)
        .click(screen.findByText("Next"))
        // TODO : Check possibility to remove manual wait added to enable the copy btn
        .wait(2000)
        .expect(screen.getByTestId("copy-btn").exists).ok({ timeout: WAIT_TIME_SHORT })

    const linkingCommand = (await screen.getByPlaceholderText("App Linking command").value).toString();
    logger.info("Retrieved app linking command : " + linkingCommand);
    await exec(linkingCommand);
    await t.wait(WAIT_TIME_LONG);
    await t.expect(screen.getByText(appName, { exact: false }).exists).ok({ timeout: WAIT_TIME_MEDIUM });

    logger.info("Anonymous App Linking successful");

    await goBacktoAppsList(t);
    await deleteApp(t, appName, true);
});
