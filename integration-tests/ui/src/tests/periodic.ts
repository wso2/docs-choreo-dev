import { Selector, RequestLogger, t } from "testcafe";
import { getLocation } from "../utils/login-utils";
import page from "../model/page";
import * as config from "../../testcafe-run-config.json";
import * as userConfig from "../../choreo-app-config.json";

import {
  createNewApp,
  selectAPITrigger,
  createProperty,
  createRespond,
  callExternalEndpointPOST,
  WAIT_TIME_SHORT,
  WAIT_TIME_MEDIUM,
  saveLogs,
  enableDetailedLogs,
  deployToChoreo,
  generateAppName,
  openChoreoApp,
  createIfElement,
  createGithubIssue,
  createGmailSendElement,
  appNamePrefix,
  getElementFromSelectorTestId,
  switchToDeployView
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

const appName = generateAppName("app-1");

fixture("Application with connectors creation and deployment")
  .page(config.testURL)
  .beforeEach(async t => {
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

test.meta({ 'periodic': "true" })("Create the app", async (t) => {
  await createNewApp(t, appName);

  await t.expect(await getLocation()).contains("app/" + appName + "/develop", { timeout: WAIT_TIME_SHORT })
  await selectAPITrigger(t, "POST", "notify");

  // Zooming out
  for (let index = 0; index < 5; index++) {
    await t.click(getElementFromSelectorTestId("zoom-out-btn"), { speed: 0.5 });
  }

  // Create properties
  await createProperty(t, "json", "jsonMsg", "<json>checkpanic request.getJsonPayload()");
  await createProperty(t, "string", "destination", "(<json>checkpanic jsonMsg.destination).toString()");
  await createProperty(t, "string", "title", "(<json>checkpanic jsonMsg.title).toString()");
  await createProperty(t, "string", "message", "(<json>checkpanic jsonMsg.message).toString()");

  // Create first IF loop
  await t.click(Selector("#SmallPlus"), { speed: 0.5 });
  await createIfElement(t, "destination == \"github\"");
  let topIfElement = ".diagram-canvas > g > g > g:nth-child(3) > .main-condition-wrapper > .if-else";
  await t.click(Selector(topIfElement + " > .main-plus-wrapper > svg"));
  await createGithubIssue(t, userConfig.github.pat, userConfig.github.owner, userConfig.github.repo, "title", "message");
  await t.click(Selector(topIfElement + " > .main-plus-wrapper > svg"));
  await createRespond(t, "createIssueResponse.toJsonString()", true);

  // Create second IF loop
  await t.click(Selector(topIfElement + " > .else-line > .main-plus-wrapper > svg"));
  await createIfElement(t, "destination == \"email\"");
  let secondIfElement = topIfElement + " > .else-line > .main-condition-wrapper > .if-else";
  await t.click(Selector(secondIfElement + " > .main-plus-wrapper > svg"));
  await createGmailSendElement(
    t,
    userConfig.gmail.token,
    userConfig.gmail.refresh_url,
    userConfig.gmail.refresh_token,
    userConfig.gmail.client_id,
    userConfig.gmail.client_secret,
    userConfig.gmail.user_id,
    "choreo-test-user@wso2.com",
    "title",
    "message"
  );
  await t.click(Selector(secondIfElement + " > .main-plus-wrapper > svg"));
  await createRespond(t, "sendMessageResponse.toJsonString()", true);

  // Create third IF loop
  // await t.click(Selector(secondIfElement + " > .else-line > .main-plus-wrapper"));
  // await createIfElement(t, "destination == \"calendar\"");
  // let thirdIfElement = secondIfElement + " > .else-line > .main-condition-wrapper > .if-else";
  // await t.click(Selector(thirdIfElement + " > .main-plus-wrapper"));
  // await createCalendarEvent(
  //   t,
  //   userConfig.calendar.token,
  //   userConfig.calendar.refresh_url,
  //   userConfig.calendar.refresh_token,
  //   userConfig.calendar.client_id,
  //   userConfig.calendar.client_secret,
  //   userConfig.calendar.calendar_id,
  //   "title",
  //   "message"
  // );
  // await t.click(Selector(thirdIfElement + " > .main-plus-wrapper"));
  // await createRespond(t, "createEventResponse.toJsonString()", true);

  // Populate final else loop
  await t.click(Selector(secondIfElement + " > .else-line > .main-plus-wrapper > svg"));
  await createRespond(t, '"Error"', true);
});

test.meta({ 'periodic': "true" })("Deploy the app", async (t) => {
  await openChoreoApp(t, appName);

  await deployToChoreo(t, appName)
});

test.meta({ 'periodic': "true" })("Invoke the app", async (t) => {
  await openChoreoApp(t, appName);
  await switchToDeployView(t);

  await t.expect(getElementFromSelectorTestId("deploy-url").find("input").getAttribute('value')).notEql('',{timeout:WAIT_TIME_MEDIUM})
  const appURL = await getElementFromSelectorTestId("deploy-url").find("input").getAttribute('value');
  logger.info("test url : " + appURL);

  // Invoke github issue creation
  const testUrl = appURL + "/notify";
  let githubIssue = {
    "destination": "github",
    "title": `Issue occurred on ${appNamePrefix}`,
    "message": "This is a critical issue"
  }
  let response = await callExternalEndpointPOST(t, testUrl, githubIssue, 3)
  logger.info("Backend service response : " + JSON.stringify(response.data));
  await t.expect(response.data.title).eql(githubIssue.title);
  logger.info(`Githu issue with title \"Issue occurred on ${appNamePrefix}\" created successfully !`)
  
  // Invoke send email
  let sendEmail = {
    "destination": "email",
    "title": `Issue occurred on ${appNamePrefix}`,
    "message": "This is a critical issue"
  }
  response = await callExternalEndpointPOST(t, testUrl, sendEmail, 3)
  logger.info("Backend service response : " + JSON.stringify(response.data));
  await t.expect(response.status).eql(200);
});

test.meta({ 'periodic': "true" })("Check the test view", async (t) => {
  await openChoreoApp(t, appName);
  
  // Checking test view
  await t.click(getElementFromSelectorTestId("test"))
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT })
  await t.expect(await getLocation()).contains("app/" + appName + "/test", { timeout: WAIT_TIME_SHORT });
  
  logger.info("Testing invalid API key validation attempt scenario");
  await t.click(getElementFromSelectorTestId("postman"))
  await t.click(getElementFromSelectorTestId("click-here"));
  await t.expect(getElementFromSelectorTestId("api-key").visible).ok({ timeout: WAIT_TIME_SHORT })
  await t.typeText(getElementFromSelectorTestId('api-key'), 'dummyapikey');
  await t.expect(getElementFromSelectorTestId('api-key-error').exists).ok({ timeout: WAIT_TIME_SHORT });
  logger.info("Test phase successful!");
});
