import { Selector, RequestLogger } from "testcafe";
import { screen } from "@testing-library/testcafe";
import { getLocation } from "../utils/login-utils";
import page from "../model/page";
import { config } from "../../../ui/src/utils/config";
import { checkSourceCodeForValidation, createNewApp, createProperty, deleteApp, generateAppName, goBacktoAppsList, selectTrigger, WAIT_TIME_SHORT, WAIT_TIME_MEDIUM, saveLogs, enableDetailedLogs } from "../utils/choreo-utils";
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

fixture("Data Mapper")
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

/*
* Following test checks whether AI suggestions returned by the Data Mapper service is shown in low code forms.
*
* This test creates a Choreo Application with an API trigger and adds a URL as a string variable. Then a HTTP connector
* is added. When doing so, it clicks on the URL field and selects the first suggestion. (If there is a Data Mapper AI
* suggestion, it would always appear first). Finally, the source code is checked to verify that the URL for the HTTP
* connector is the previous variable.
*/
test.meta({'stable': "true"})("Low code form AI suggestions", async (t) => {
  logger.info("Starting Data Mapper Low code form AI suggestion test...");
  const appName = generateAppName("datamapper");
  await createNewApp(t, appName);
  await t.expect(await getLocation()).contains("app/" + appName + "/develop", { timeout: WAIT_TIME_SHORT });
  await selectTrigger(t, "API", "test");
  await createProperty(t, "string", "url", '"https://console.choreo.dev"');
  logger.info("Adding HTTP connector with AI suggestion of previous variable")
  await t
    .click(Selector("#SmallPlus"), { speed: 0.5 })
    .click(Selector("#Plus_a"), { speed: 0.5 })
    .click(screen.getByTestId("api-options"), { speed: 0.5 })
    .click(screen.getByTestId("http"), { speed: 0.5 })
    .click(Selector('.exp-editor .monaco-editor .view-line').nth(0))
    .wait(3000)
    .pressKey("backspace backspace")
    .wait(3000)
    .pressKey("enter")
    .wait(3000)
    .click(screen.getByText("GET"), { speed: 0.5 })
    .click(screen.getByTestId("http-save-next"), { speed: 0.5 })
    .click(screen.getByText("No Payload"), { speed: 0.5 })
    .click(screen.getByTestId("http-save-done"), { speed: 0.5 });
  logger.info("HTTP connector added successfully!");
  await t.expect(screen.findByTestId("diagram-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  const variableSourceFields = ['http:Client httpEndpoint = check new (url);']
  await checkSourceCodeForValidation(t,variableSourceFields);
  logger.info("Data Mapper AI suggestion added to Low Code form successfully!");
  await goBacktoAppsList(t);
  await deleteApp(t, appName, true);
});
