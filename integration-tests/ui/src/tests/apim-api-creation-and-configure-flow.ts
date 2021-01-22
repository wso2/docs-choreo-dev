import { screen, within } from "@testing-library/testcafe";
import { RequestLogger, Selector } from "testcafe";
import * as config from "../../testcafe-run-config.json";
import page from "../model/page";
import {
  createApiFromChoreoApp, addApiSimpleResponse, clearAppsIfExists, clearApisIfExists, createNewApp, enableDetailedLogs, saveLogs,
  selectAPIType, WAIT_TIME_EX_LONG, WAIT_TIME_LONG, WAIT_TIME_MEDIUM, WAIT_TIME_SHORT
} from "../utils/choreo-utils";
import { logger } from '../utils/logger';
import { getLocation } from "../utils/login-utils";

declare const test: TestFn;
const appName = "demoapp-" + Math.random().toString(36).substr(2, 5);
const apiName = "demoapi" + Math.random().toString(36).substr(2, 5);

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

fixture("API creation and config flow")
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

test("Create API type choreo app", async (t) => {
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  logger.info("Page loaded successfully");

  await clearAppsIfExists(t);
  await createNewApp(t, appName);

  // adding api content
  await t.expect(await getLocation()).contains("app/" + appName + "/develop", { timeout: WAIT_TIME_SHORT })
  await selectAPIType(t, "hello");
  await addApiSimpleResponse(t, "\"hello world\"");

  // deploying app
  await t.click(screen.getByTitle("deploy"))
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  await t.expect(await getLocation()).contains("app/" + appName + "/deploy", { timeout: WAIT_TIME_SHORT })

  logger.info("Succesfully Navigated to Deploy view")

  logger.info("Deploying application...")
  await t.wait(WAIT_TIME_SHORT);
  await t.click(screen.getByTestId("deploy-btn"), { speed: 0.5 })
    .expect(screen.findByTestId("checkout-loading").exists).notOk({ timeout: WAIT_TIME_MEDIUM })
    .expect(screen.findByTestId("checkout-failed").exists).notOk({ timeout: WAIT_TIME_EX_LONG })
    .expect(screen.findByTestId("checkout-ok").exists).ok({ timeout: WAIT_TIME_EX_LONG })
  logger.info("Checkout phase successful!")


  logger.info("Starting build phase...")
  await t.expect(screen.findByTestId("build-loading").exists).ok({ timeout: WAIT_TIME_SHORT })
    .expect(screen.findByTestId("build-loading").exists).notOk({ timeout: WAIT_TIME_EX_LONG })
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

  // go back app list
  await t
    .click(screen.getByText("App list"))
    .expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  await screen.findAllByText("Active").exists;
  logger.info("Load app list successful!")
})

test("Create API from previously created choreo app", async (t) => {
  // click api tab
  await screen.findAllByText("APIs").exists;
  await t.click(screen.getByText("APIs"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  logger.info("Go to API tab successful!");

  // delete apis if exist
  await clearApisIfExists(t);

  // create new api from choreo app
  await createApiFromChoreoApp(t, apiName, appName);

  // test overview page loading
  await t.expect(await getLocation()).contains("/config/overview", { timeout: WAIT_TIME_SHORT });
  await screen.queryAllByText("Overview").exists;
  logger.info("Created API config view loaded successfully!");
});

test("Change design configurations of API", async (t) => {
  // click api tab
  await screen.findAllByText("APIs").exists;
  await t.click(screen.getByText("APIs"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  logger.info("Go to API tab successful!");

  // click on created api
  await t.click(screen.findByText(apiName), { speed: 0.5 });

  // Go to design configs tab
  await screen.findByText("Design Configurations").exists;
  await t.click(screen.findByText("Design Configurations"), { speed: 0.5 });
  await screen.findByText("Description").exists;
  logger.info("Navigated to design configuration tab successfully");

  // Enter a description and some tags
  await screen.findByTestId("toggle-edit-description").exists;
  await t.click(screen.findByTestId("toggle-edit-description"), { speed: 0.5 });
  await t.typeText(screen.findByTestId("description-input"), "This is a sample API", { speed: 0.5 });
  await screen.findByTestId("toggle-add-tags").exists;
  await t.click(screen.findByTestId("toggle-add-tags"), { speed: 0.5 });
  await screen.findByTestId("tag-input").exists;
  await t
    .typeText(screen.findByTestId("tag-input"), "sample", { speed: 0.5 })
    .pressKey("Enter", { speed: 0.5 });
  await t
    .typeText(screen.findByTestId("tag-input"), "test", { speed: 0.5 })
    .pressKey("Enter", { speed: 0.5 });
  await screen.findAllByText("sample").exists;
  await screen.findAllByText("api").exists;
  logger.info("Input description and tags successful");

  // Save design configs
  await screen.findByText("Save").exists;
  await t.click(screen.findByText("Save"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });

  // Verify Design config update by checking overview tab
  await screen.findByText("Overview").exists;
  await t.click(screen.findByText("Overview"), { speed: 0.5 });
  await screen.findByText("This is a sample API").exists;
  logger.info("Design configuration update successful");
});

test("Change subscriptions of API", async (t) => {
  // click api tab
  await screen.findAllByText("APIs").exists;
  await t.click(screen.getByText("APIs"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  logger.info("Go to API tab successful!");

  // click on created api
  await t.click(screen.findByText(apiName), { speed: 0.5 });

  // Go to Subscriptions tab
  await screen.findByText("Subscriptions").exists;
  await t.click(screen.findByText("Subscriptions"), { speed: 0.5 });
  await screen.findByText("Attach business plans to API").exists;
  logger.info("Navigated to Subscriptions tab successfully");

  // Add Gold and Silver business plan subscriptions
  await screen.findByTestId("checkbox-Gold").exists;
  await t.click(screen.findByTestId("checkbox-Gold"), { speed: 0.5 });
  await screen.findByTestId("checkbox-Silver").exists;
  await t.click(screen.findByTestId("checkbox-Silver"), { speed: 0.5 });

  // Save new suscriptions
  await screen.findByText("Save").exists;
  await t.click(screen.findByText("Save"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });

  // Verify Subscription update by checking overview tab
  await screen.findByText("Overview").exists;
  await t.click(screen.findByText("Overview"), { speed: 0.5 });
  await screen.findByText("Unlimited, Gold, Silver").exists;
  logger.info("Subscription update successful");
});

test("Change Business Info of API", async (t) => {
  // click api tab
  await screen.findAllByText("APIs").exists;
  await t.click(screen.getByText("APIs"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  logger.info("Go to API tab successful!");

  // click on created api
  await t.click(screen.findByText(apiName), { speed: 0.5 });

  // Go to Business Info tab
  await screen.findByText("Business Info").exists;
  await t.click(screen.findByText("Business Info"), { speed: 0.5 });
  await screen.findByText("Business Information").exists;
  logger.info("Navigated to Business Info tab successfully");

  // Edit Business Info details
  await screen.findByTestId("editButton-businessOwnerName").exists;
  await t.click(screen.findByTestId("editButton-businessOwnerName"), { speed: 0.5 });
  await screen.findByTestId("textbox-businessOwnerName").exists;
  await t.typeText(screen.findByTestId("textbox-businessOwnerName"), "John Doe", { speed: 0.5, replace: true });

  await screen.findByTestId("editButton-businessOwnerMail").exists;
  await t.click(screen.findByTestId("editButton-businessOwnerMail"), { speed: 0.5 });
  await screen.findByTestId("textbox-businessOwnerMail").exists;
  await t.typeText(screen.findByTestId("textbox-businessOwnerMail"), "johndoe@mail.com", { speed: 0.5, replace: true });

  await screen.findByTestId("editButton-technicalOwnerName").exists;
  await t.click(screen.findByTestId("editButton-technicalOwnerName"), { speed: 0.5 });
  await screen.findByTestId("textbox-technicalOwnerName").exists;
  await t.typeText(screen.findByTestId("textbox-technicalOwnerName"), "Jane Smith", { speed: 0.5, replace: true });

  await screen.findByTestId("editButton-technicalOwnerMail").exists;
  await t.click(screen.findByTestId("editButton-technicalOwnerMail"), { speed: 0.5 });
  await screen.findByTestId("textbox-technicalOwnerMail").exists;
  await t.typeText(screen.findByTestId("textbox-technicalOwnerMail"), "janesmith@mail.com", { speed: 0.5, replace: true });

  // Save new Business Info
  await screen.findByText("Save").exists;
  await t.click(screen.findByText("Save"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });

  // Verify Business Info update by checking overview tab
  await screen.findByText("Overview").exists;
  await t.click(screen.findByText("Overview"), { speed: 0.5 });
  await t.expect(screen.findByText("John Doe").exists).ok();
  await t.expect(screen.findByText("johndoe@mail.com").exists).ok();
  await t.expect(screen.findByText("Jane Smith").exists).ok();
  await t.expect(screen.findByText("janesmith@mail.com").exists).ok();
  logger.info("Business Info update successful");
});

test("Create and delete a new API", async (t) => {
  // click api tab
  await screen.findAllByText("APIs").exists;
  await t.click(screen.getByText("APIs"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  logger.info("Go to API tab successful!");

  // create new api from choreo app
  await screen.findAllByText("Create").exists;
  await t
    .click(screen.getByText("Create"))
    .expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });

  await createApiFromChoreoApp(t, apiName + "v2", appName);

  // test overview page loading
  await t.expect(await getLocation()).contains("/config/overview", { timeout: WAIT_TIME_SHORT });
  await screen.queryAllByText("Overview").exists;
  logger.info("Created API config view loaded successfully!");

  // delete new api and, test whether api list loading with deleted api removed
  await t
    .click(screen.getByRole('button', { name: /delete/i }))
    .click(within(screen.findByRole("dialog")).getByText("Delete"))
    .expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(await getLocation()).contains("?list=apis", { timeout: WAIT_TIME_SHORT });
});

test("Change Runtime Configurations of API", async (t) => {
  // click api tab
  await screen.findAllByText("APIs").exists;
  await t.click(screen.getByText("APIs"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  logger.info("Go to API tab successful!");

  // click on created api
  await t.click(screen.findByText(apiName), { speed: 0.5 });

  // Go to Runtime Configurations tab
  await screen.findByText("Runtime Configurations").exists;
  await t.click(screen.findByText("Runtime Configurations"), { speed: 0.5 });
  await screen.findAllByText("Runtime Configurations").exists;
  logger.info("Navigated to Runtime Configurations tab successfully");

  // Switch on CORS configuration and configure it
  await t.expect(screen.findByTestId("switch-cors-config").exists).ok();
  await t.click(screen.findByTestId("switch-cors-config"), { speed: 0.5 });
  await t.click(screen.getByText(/cors configuration/i), { speed: 0.5 });

  await t.click(screen.getByText(/allow all origins/i), { speed: 0.5 });
  await t.expect(screen.findByTestId("addBtn-origin").exists).ok();
  await t.click(screen.findByTestId("addBtn-origin"), { speed: 0.5 });
  await t.expect(screen.getByPlaceholderText(/type and press enter to add origins/i).exists).ok();
  await t
    .typeText(screen.getByPlaceholderText(/type and press enter to add origins/i), "some-origin.com", { speed: 0.5 })
    .pressKey("Enter", { speed: 0.5 });

  await t.expect(screen.findByTestId("addBtn-header").exists).ok();
  await t.click(screen.findByTestId("addBtn-header"), { speed: 0.5 });
  await t.expect(screen.getByPlaceholderText(/type and press enter to add headers/i).exists).ok();
  await t
    .typeText(screen.getByPlaceholderText(/type and press enter to add headers/i), "wso2-x", { speed: 0.5 })
    .pressKey("Enter", { speed: 0.5 });
  await t
    .typeText(screen.getByPlaceholderText(/type and press enter to add headers/i), "ex-security", { speed: 0.5 })
    .pressKey("Enter", { speed: 0.5 });
  await t
    .expect(Selector('#remove-item-ex-security').exists).ok()
    .click(Selector('#remove-item-ex-security'), { speed: 0.5 });

  await t
    .expect(Selector('#remove-item-DELETE').exists).ok()
    .click(Selector('#remove-item-DELETE'), { speed: 0.5 });

  await t.click(screen.getByRole('button', { name: /application level security/i }), { speed: 0.5 });
  await t.click(screen.getByText(/basic/i), { speed: 0.5 });

  // save changes
  await t
    .click(screen.getByRole('button', { name: /save/i }), { speed: 0.5 })
    .expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });

  // assert changes
  await t.expect(screen.getByTestId("checkbox-allow-all-origins")
    .find("input[type=checkbox]").nth(0).checked).eql(false);
  await t
    .expect(Selector('#remove-item-wso2-x').exists).ok()
    .expect(Selector('#remove-item-DELETE').exists).notOk()
    .expect(Selector('#remove-item-ex-security').exists).notOk()

  await t.expect(screen.getByTestId("checkbox-OAuth2")
    .find("input[type=checkbox]").nth(0).checked).eql(true);
  await t.expect(screen.getByTestId("checkbox-Basic")
    .find("input[type=checkbox]").nth(0).checked).eql(true);
});
