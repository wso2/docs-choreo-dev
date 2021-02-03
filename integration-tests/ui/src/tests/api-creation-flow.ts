import { Selector, RequestLogger } from "testcafe";
import { screen } from "@testing-library/testcafe";
import { getLocation } from "../utils/login-utils";
import page from "../model/page";
import * as config from "../../testcafe-run-config.json";
import {
  createNewApp,
  clearAppsIfExists,
  selectTrigger,
  createProperty,
  createRespond,
  callExternalEndpoint,
  WAIT_TIME_SHORT,
  WAIT_TIME_MEDIUM,
  WAIT_TIME_LONG,
  saveLogs,
  enableDetailedLogs,
  deployToChoreo,
  getElementFromSelectorTestId
} from "../utils/choreo-utils";
import { logger } from '../utils/logger'


declare const test: TestFn;

const httpLogger = RequestLogger(undefined, {
  logRequestBody: true,
  logRequestHeaders: true,
  logResponseBody: true,
  logResponseHeaders: true,
  stringifyRequestBody: true
});

declare global {
  interface TestController {
    testRun: {
      test: {
        name: string;
      }
    };
  }
}

fixture("Application test run  and deployment")
  .page(config.testURL)
  .beforeEach(async () => {
    await page.login();
    await enableDetailedLogs();
  })
  .requestHooks(httpLogger)
  .afterEach(async t => {
    const { log, error }: BrowserConsoleMessages = await t.getBrowserConsoleMessages();
    const httpRequests = httpLogger.requests;
    const data = [...log, ...error];
    saveLogs(t, data, httpRequests)
    httpLogger.clear();

  });


test("test run hello world service ", async (t) => {

  const appName = "sampleapi-" + Math.random().toString(36).substr(2, 5);
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  logger.info("Page loaded successfully");
  await clearAppsIfExists(t);
  await createNewApp(t, appName);

  await t.expect(await getLocation()).contains("app/" + appName + "/develop", { timeout: WAIT_TIME_SHORT })
  await selectTrigger(t, "API", "hello");
  await createProperty(t, "var", "res", '"hello world"');
  await createRespond(t, "res");

  await t.expect(getElementFromSelectorTestId("editor-run-btn").visible).ok({ timeout: WAIT_TIME_SHORT })
  await t.click(getElementFromSelectorTestId("editor-run-btn"), { speed: 0.5 });
  logger.info("Started test run");

  await t
    .expect(getElementFromSelectorTestId("test-url").exists).ok({ timeout: WAIT_TIME_MEDIUM })
    .expect(
      getElementFromSelectorTestId("log-panel").withText("started HTTP/WS listener")
        .exists
    ).ok({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Retrieving the test URL successful");

  const testUrl = await getElementFromSelectorTestId("test-url").textContent;

  const response = await callExternalEndpoint(t, (testUrl + "/hello"), 3)

  logger.info("Backend service response : " + response);
  await t.expect(response).eql("hello world");
  logger.info("Hello world string recieved successfully !")
});

test("test postman view", async (t) => {
  const appName = "sampleapi-" + Math.random().toString(36).substr(2, 5);
  await clearAppsIfExists(t);
  await createNewApp(t, appName);
  await t.expect(await getLocation()).contains("app/" + appName + "/develop", { timeout: WAIT_TIME_SHORT })
  await selectTrigger(t, "API", "hello");
  await createProperty(t, "var", "res", '"hello world"');
  await createRespond(t, "res");

  await t.click(getElementFromSelectorTestId("test"))
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT })
  await t.expect(await getLocation()).contains("app/" + appName + "/test", { timeout: WAIT_TIME_SHORT });

  logger.info("Testing invalid API key validation attempt scenario");
  await t.click(getElementFromSelectorTestId("try-out"))
  await t.click(getElementFromSelectorTestId("click-here"));
  await t.expect(getElementFromSelectorTestId("api-key").visible).ok({ timeout: WAIT_TIME_SHORT })
  await t.typeText(getElementFromSelectorTestId('api-key'), 'dummyapikey');
  await t.expect(getElementFromSelectorTestId('api-key-error').exists).notOk({ timeout: WAIT_TIME_SHORT });
  logger.info("Test phase successful!");
});

test("deploy hello world service", async (t) => {

  const appName = "sampleapi-" + Math.random().toString(36).substr(2, 5);
  await clearAppsIfExists(t);
  await createNewApp(t, appName);
  await t.expect(await getLocation()).contains("app/" + appName + "/develop", { timeout: WAIT_TIME_SHORT })
  await selectTrigger(t, "API", "hello");
  await createProperty(t, "var", "res", '"hello world"');
  await createRespond(t, "res");

  const testUrl = await deployToChoreo(t,appName)
  logger.info("test url : " + testUrl);
  await t.expect(testUrl.includes("https://")).ok();
  const response = await callExternalEndpoint(t, testUrl + "/hello", 3);

  console.log("Service response : " + response);
  await t.expect(response).eql("hello world");
  logger.info("Hello world string recieved successfully !")

  logger.info("Stopping deployed application")
  await t.click(screen.getByText("Stop"))
    .expect(getElementFromSelectorTestId("deploy-url").exists).ok({ timeout: WAIT_TIME_LONG })
  logger.info("Undeloyed application successfully!")
})
