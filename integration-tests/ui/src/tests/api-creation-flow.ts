import { Selector, ClientFunction, RequestLogger } from "testcafe";
import { screen } from "@testing-library/testcafe";
import Axios, { AxiosResponse } from "axios";
import { getLocation } from "../utils/login-utils";
import page from "../model/page";
import * as config from "../../testcafe-run-config.json";
import { createNewApp, clearAppsIfExists, selectWebhookType, createProperty, createRespond, callExternalEndpoint, WAIT_TIME_SHORT, WAIT_TIME_MEDIUM, WAIT_TIME_EX_LONG, WAIT_TIME_LONG, saveLogs, enableDetailedLogs } from "../utils/choreo-utils";
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
  await selectWebhookType(t, "hello");
  await createProperty(t, "res", '"hello world"');
  await createRespond(t, "res");

  await t.wait(WAIT_TIME_SHORT);
  await t.click(screen.getByTestId("editor-run-btn"), { speed: 0.5 });
  logger.info("Started test run");

  await t
    .expect(screen.findAllByTestId("test-url").exists).ok({ timeout: WAIT_TIME_MEDIUM })
    .expect(
      screen.findAllByTestId("log-panel").withText("started HTTP/WS listener")
        .exists
    ).ok({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Retrieving the test URL successful");

  const testUrl = await screen.findAllByTestId("test-url").textContent;

  await t.wait(WAIT_TIME_SHORT);
  const response = await callExternalEndpoint(t, (testUrl + "/hello"), 3)

  logger.info("Backend service response : " + response);
  await t.expect(response).eql("hello world");
  logger.info("Hello world string recieved successfully !")
});


test("deploy hello world service", async (t) => {

  const appName = "sampleapi-" + Math.random().toString(36).substr(2, 5);
  await clearAppsIfExists(t);
  await createNewApp(t, appName);
  await t.expect(await getLocation()).contains("app/" + appName + "/develop", { timeout: WAIT_TIME_SHORT })
  await selectWebhookType(t, "hello");
  await createProperty(t, "res", '"hello world"');
  await createRespond(t, "res");

  await t.click(screen.getByTitle("deploy"))
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  await t.expect(await getLocation()).contains("app/" + appName + "/deploy", { timeout: WAIT_TIME_SHORT })

  logger.info("Succesfully Navigated to Deploy view")

  logger.info("Deploying application...")
  await t.click(screen.getByTestId("deploy-btn"), { speed: 0.5 })
    .expect(screen.findByTestId("checkout-loading").exists).ok({ timeout: WAIT_TIME_MEDIUM })
    .expect(screen.findByTestId("checkout-failed").exists).notOk({ timeout: WAIT_TIME_EX_LONG })
    .expect(screen.findByTestId("checkout-ok").exists).ok({ timeout: WAIT_TIME_EX_LONG })
  logger.info("Checkout phase successful!")


  logger.info("Starting build phase...")
  await t.expect(screen.findByTestId("build-loading").exists).ok({ timeout: WAIT_TIME_SHORT })
    .expect(screen.findByTestId("build-loading").exists).notOk({ timeout: WAIT_TIME_EX_LONG }) // todo - increase timeout
    .expect(screen.findByTestId("build-failed").exists).notOk({ timeout: WAIT_TIME_MEDIUM })
    .expect(screen.findByTestId("build-ok").exists).ok({ timeout: WAIT_TIME_EX_LONG });
  logger.info("Build phase successful!");

  await t.expect(screen.findByTestId("test-ok").exists).ok({ timeout: WAIT_TIME_LONG })
  logger.info("Test phase successful!");

  logger.info("Starting deploy phase...");
  await t
    .expect(screen.findByTestId("deploy-loading").exists).ok({ timeout: WAIT_TIME_SHORT })
    .expect(screen.findByTestId("deploy-loading").exists).notOk({ timeout: WAIT_TIME_EX_LONG })
    .expect(screen.findByTestId("deploy-failed").exists).notOk({ timeout: WAIT_TIME_MEDIUM })
    .expect(screen.findByTestId("deploy-ok").exists).ok({ timeout: WAIT_TIME_EX_LONG })
  logger.info("Deploy phase successful!")

  await t.wait(WAIT_TIME_MEDIUM);

  const testUrl = await screen.findAllByTestId("deploy-url").find("input").value;
  logger.info("test url : " + testUrl);
  await t.expect(testUrl.includes("https://")).ok();
  const response = await callExternalEndpoint(t, testUrl + "/hello", 3);

  console.log("Service response : " + response);
  await t.expect(response).eql("hello world");
  logger.info("Hello world string recieved successfully !")

  logger.info("Stopping deployed application")
  await t.click(screen.getByText("Stop"))
    .expect(screen.findByPlaceholderText("Please deploy to get access URL").exists).ok({ timeout: WAIT_TIME_LONG })
  logger.info("Undeloyed application successfully!")
})
