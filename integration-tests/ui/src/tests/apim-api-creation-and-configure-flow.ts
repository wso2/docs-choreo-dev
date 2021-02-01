import { screen, within } from "@testing-library/testcafe";
import { RequestLogger, Selector } from "testcafe";
import * as config from "../../testcafe-run-config.json";
import page from "../model/page";
import {
  createApiFromChoreoApp, addApiSimpleResponse, clearAPIDocumentsIfExists,
  createNewApp, enableDetailedLogs, saveLogs, selectAPIType, generateAppName, generateApiName,
  WAIT_TIME_EX_LONG, WAIT_TIME_LONG, WAIT_TIME_MEDIUM, WAIT_TIME_SHORT
} from "../utils/choreo-utils";
import { logger } from '../utils/logger';
import { getLocation } from "../utils/login-utils";

declare const test: TestFn;
const appName = generateAppName("demoapp");
const apiName = generateApiName("demoapi");

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
  await createNewApp(t, appName);

  // adding api content
  await t.expect(await getLocation()).contains("app/" + appName + "/develop", { timeout: WAIT_TIME_SHORT })
  await selectAPIType(t, "hello");
  await addApiSimpleResponse(t, "\"hello world\"");

  // deploying app
  await t.click(screen.getByTestId("deploy"))
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

test.skip("Create and delete a new API", async (t) => {
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

test("Create a URL type document for an API", async (t) => {
  // click api tab
  await t.expect(screen.findAllByText("APIs").exists).ok();
  await t.click(screen.getByText("APIs"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  logger.info("API tab loaded successfully");

  // click on created api
  await t.click(screen.findByText(apiName), { speed: 0.5 });

  // Go to documents tab
  await t.expect(screen.findAllByText("Documents").exists).ok({ timeout: WAIT_TIME_SHORT });
  await t.click(screen.findAllByText("Documents"), { speed: 0.5 });
  await t.expect(screen.findAllByText("Documents").exists).ok();
  logger.info("Navigated to documents tab successfully");

  // delete available documents
  await clearAPIDocumentsIfExists(t);

  // Go to add new document page
  await t.expect(screen.findByTestId("add-new-document").exists).ok();
  await t.click(screen.findByTestId("add-new-document"), { speed: 0.5 });
  await t.expect(await getLocation()).contains("/documents/add", { timeout: WAIT_TIME_SHORT });
  await t.expect(screen.findByText("Add New Document").exists).ok();

  // Fill document creation form
  await t.expect(screen.findByTestId("document-name").exists).ok();
  await t.typeText(screen.findByTestId("document-name"), "API doc link", { speed: 0.5 });
  await t.expect(screen.findByTestId("document-summary").exists).ok();
  await t.typeText(screen.findByTestId("document-summary"), "This doc provides a URL contains the docs for"
      + " the sample API", { speed: 0.5 });
  await t.expect(screen.findByTestId("document-url").exists).ok();
  await t.typeText(screen.findByTestId("document-url"), "https://sampleurl.doc", { speed: 0.5 });

  // create document and wait for listing to load
  await t.expect(screen.findByText("Save").exists).ok();
  await t.click(screen.findByText("Save"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Document created successfully!");

  await screen.queryByText("URL").exists;
  logger.info("Created URL Source Document listed successfully!");
});

test("Create an Inline type document for an API", async (t) => {
  // click api tab
  await t.expect(screen.findAllByText("APIs").exists).ok();
  await t.click(screen.getByText("APIs"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  logger.info("API tab loaded successfully");

  // click on created api
  await t.click(screen.findByText(apiName), { speed: 0.5 });

  // Go to documents tab
  await t.expect(screen.findAllByText("Documents").exists).ok({ timeout: WAIT_TIME_SHORT });
  await t.click(screen.findAllByText("Documents"), { speed: 0.5 });
  await t.expect(screen.findAllByText("Documents").exists).ok();
  logger.info("Navigated to documents tab successfully");

  // Go to add new document page
  await t.expect(screen.findByTestId("add-new-document").exists).ok();
  await t.click(screen.findByTestId("add-new-document"), { speed: 0.5 });
  await t.expect(await getLocation()).contains("/documents/add", { timeout: WAIT_TIME_SHORT });
  await t.expect(screen.findByText("Add New Document").exists).ok();

  // Fill document creation form
  await t.expect(screen.findByTestId("document-name").exists).ok();
  await t.typeText(screen.findByTestId("document-name"), "Inline API doc", { speed: 0.5 });
  await t.expect(screen.findByTestId("document-summary").exists).ok();
  await t.typeText(screen.findByTestId("document-summary"), "This doc provides an inline document" +
      " contains the docs for the sample API", { speed: 0.5 });

  // Select inline content type
  await t.expect(screen.findByTestId("document-source-selector").exists).ok();
  await t.click(screen.findByTestId("document-source-selector"), { speed: 0.5 });
  await t.expect(screen.findByText("Inline").exists).ok();
  await t.click(screen.findByText("Inline"), { speed: 0.5 });

  // Add inline content
  await t.pressKey('tab');
  await t.pressKey('tab');
  await t.typeText(() =>  document.activeElement, "This is a sample document with inline content for an" +
      " API", { speed: 0.5 });
  await t.expect(screen.findByText("Add").exists).ok();
  await t.click(screen.findByText("Add"), { speed: 0.5 });

  // create document and wait for listing to load
  await t.expect(screen.findByText("Save").exists).ok();
  await t.click(screen.findByText("Save"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Inline source document created successfully!");

  await t.expect(screen.queryByText("Inline").exists).ok();
  logger.info("Created Inline source Document listed successfully!");
});

test("Create a Markdown type document for an API", async (t) => {
  // click api tab
  await t.expect(screen.findAllByText("APIs").exists).ok();
  await t.click(screen.getByText("APIs"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  logger.info("API tab loaded successfully");

  // click on created api
  await t.click(screen.findByText(apiName), { speed: 0.5 });

  // Go to documents tab
  await t.expect(screen.findAllByText("Documents").exists).ok({ timeout: WAIT_TIME_SHORT });
  await t.click(screen.findAllByText("Documents"), { speed: 0.5 });
  await t.expect(screen.findAllByText("Documents").exists).ok();
  logger.info("Navigated to documents tab successfully");

  // Go to add new document page
  await t.expect(screen.findByTestId("add-new-document").exists).ok();
  await t.click(screen.findByTestId("add-new-document"), { speed: 0.5 });
  await t.expect(await getLocation()).contains("/documents/add", { timeout: WAIT_TIME_SHORT });
  await t.expect(screen.findByText("Add New Document").exists).ok();

  // Fill document creation form
  await t.expect(screen.findByTestId("document-name").exists).ok();
  await t.typeText(screen.findByTestId("document-name"), "Markdown API doc", { speed: 0.5 });
  await t.expect(screen.findByTestId("document-summary").exists).ok();
  await t.typeText(screen.findByTestId("document-summary"), "This doc provides a markdown document" +
      " contains the docs for the sample API", { speed: 0.5 });

  // Select markdown content type
  await t.expect(screen.findByTestId("document-source-selector").exists).ok();
  await t.click(screen.findByTestId("document-source-selector"), { speed: 0.5 });
  await t.expect(screen.findByText("Markdown").exists).ok();
  await t.click(screen.findByText("Markdown"), { speed: 0.5 });

  // Add markdown content
  await t.pressKey('ctrl+a delete');
  await t.pressKey('T h i s space i s space a space s a m p l e space d o c u m e n t space w i t h space');
  await t.pressKey('m a r k d o w n space c o n t e n t space f o r space a n space A P I');
  await t.expect(screen.findByText("Add").exists).ok();
  await t.click(screen.findByText("Add"), { speed: 0.5 });

  // create document and wait for listing to load
  await t.expect(screen.findByText("Save").exists).ok();
  await t.click(screen.findByText("Save"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Markdown source document created successfully!");

  await t.expect(screen.queryByText("Markdown").exists).ok();
  logger.info("Created markdown source Document listed successfully!");
});

test("Create a File type document for an API", async (t) => {
  // click api tab
  await t.expect(screen.findAllByText("APIs").exists).ok();
  await t.click(screen.getByText("APIs"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("API tab loaded successfully");

  // click on created api
  await t.click(screen.findByText(apiName), { speed: 0.5 });

  // Go to documents tab
  await t.expect(screen.findAllByText("Documents").exists).ok({ timeout: WAIT_TIME_SHORT });
  await t.click(screen.findAllByText("Documents"), { speed: 0.5 });
  await t.expect(screen.findAllByText("Documents").exists).ok();
  logger.info("Navigated to documents tab successfully");

  // Go to add new document page
  await t.expect(screen.findByTestId("add-new-document").exists).ok();
  await t.click(screen.findByTestId("add-new-document"), { speed: 0.5 });
  await t.expect(await getLocation()).contains("/documents/add", { timeout: WAIT_TIME_SHORT });
  await t.expect(screen.findByText("Add New Document").exists).ok();

  // Fill document creation form
  await t.expect(screen.findByTestId("document-name").exists).ok();
  await t.typeText(screen.findByTestId("document-name"), "File API doc", { speed: 0.5 });
  await t.expect(screen.findByTestId("document-summary").exists).ok();
  await t.typeText(screen.findByTestId("document-summary"), "This doc provides a pdf document" +
      " contains the docs for the sample API", { speed: 0.5 });

  // Select file content type
  await t.expect(screen.findByTestId("document-source-selector").exists).ok();
  await t.click(screen.findByTestId("document-source-selector"), { speed: 0.5 });
  await t.expect(screen.findByText("File").exists).ok();
  await t.click(screen.findByText("File"), { speed: 0.5 });

  // File upload
  await t.expect(screen.findByTestId("upload-button").exists).ok();
  await t
      .setFilesToUpload(screen.findByTestId("file-input"),
          ['../../resources/sample_doc.pdf']).click(screen.findByTestId("upload-button"));

  // create document and wait for listing to loads
  await t.expect(screen.findByText("Save").exists).ok();
  await t.click(screen.findByText("Save"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("File source document created successfully!");

  await t.expect(screen.queryByText("File").exists).ok();
  logger.info("Created File source Document listed successfully!");
});

test("View different types of API documents", async (t) => {
  // click api tab
  await t.expect(screen.findAllByText("APIs").exists).ok();
  await t.click(screen.getByText("APIs"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("API tab loaded successfully");

  // click on created api
  await t.click(screen.findByText(apiName), { speed: 0.5 });

  // Go to documents tab
  await t.expect(screen.findAllByText("Documents").exists).ok({ timeout: WAIT_TIME_SHORT });
  await t.click(screen.findAllByText("Documents"), { speed: 0.5 });
  await t.expect(screen.findAllByText("Documents").exists).ok();
  logger.info("Navigated to documents tab successfully");

  // Go to view page
  await t.expect(screen.findByText("URL").exists).ok();
  await t.click(screen.findByText("URL"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(screen.findByText("View Document").exists).ok();
  logger.info("Document view page loaded successfully");

  // Verify the elements
  await t.expect(screen.getByTestId("document-name").exists).ok();
  await t.expect(screen.getByDisplayValue("API doc link").exists).ok();
  await t.expect(screen.getByTestId("document-summary").exists).ok();
  await t.expect(screen.getByDisplayValue("This doc provides a URL contains the docs for the sample API")
      .exists).ok();
  await t.expect(screen.getByTestId("document-type-selector").exists).ok();
  await t.expect(screen.getByText("How To").exists).ok();
  await t.expect(screen.getByTestId("document-source-selector").exists).ok();
  await t.expect(screen.getAllByText("URL").exists).ok();
  await t.expect(screen.getByTestId("document-url").exists).ok();
  await t.expect(screen.getByDisplayValue("https://sampleurl.doc").exists).ok();
  logger.info("Url source type document loaded successfully");

  // Check edit behavior
  await t.expect(screen.getByTestId("document-edit").exists).ok();
  await t.click(screen.findByTestId("document-edit"), { speed: 0.5 });
  await t.expect(screen.findByTestId("document-summary").exists).ok();
  await t.typeText(screen.findByTestId("document-summary"), "This doc modifies the URL contains the docs" +
      " for the sample API", { speed: 0.5, replace: true});
  await t.expect(screen.findByTestId("document-url").exists).ok();
  await t.typeText(screen.findByTestId("document-url"), "https://updatedurl.doc", { speed: 0.5,
    replace: true});

  // update document
  await t.expect(screen.findByText("Save").exists).ok();
  await t.click(screen.findByText("Save"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Updated document loaded successfully!");

  // Verify the updated elements
  await t.expect(screen.getByTestId("document-name").exists).ok();
  await t.expect(screen.getByDisplayValue("API doc link").exists).ok();
  await t.expect(screen.getByTestId("document-summary").exists).ok();
  await t.expect(screen.getByDisplayValue("This doc modifies the URL contains the docs for the sample API")
      .exists).ok();
  await t.expect(screen.getByTestId("document-type-selector").exists).ok();
  await t.expect(screen.getByText("How To").exists).ok();
  await t.expect(screen.getByTestId("document-source-selector").exists).ok();
  await t.expect(screen.getAllByText("URL").exists).ok();
  await t.expect(screen.getByTestId("document-url").exists).ok();
  await t.expect(screen.getByDisplayValue("https://updatedurl.doc").exists).ok();
  logger.info("Url source type document updated successfully");

  // Back to documents list
  await t.expect(screen.findByText("Cancel").exists).ok();
  await t.click(screen.findByText("Cancel"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(screen.findAllByText("Documents").exists).ok();
  logger.info("Returned to documents list page successfully");

  // View inline source type doc
  await t.expect(screen.findByText("Inline").exists).ok();
  await t.click(screen.findByText("Inline"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(screen.findByText("View Document").exists).ok();
  logger.info("Document view page loaded successfully");

  await t.expect(screen.getByTestId("document-name").exists).ok();
  await t.expect(screen.getByDisplayValue("Inline API doc").exists).ok();
  await t.expect(screen.getByTestId("document-summary").exists).ok();
  await t.expect(screen.getByDisplayValue("This doc provides an inline document contains the docs for the " +
      "sample API").exists).ok();
  await t.expect(screen.getByTestId("document-type-selector").exists).ok();
  await t.expect(screen.getByText("How To").exists).ok();
  await t.expect(screen.getByTestId("document-source-selector").exists).ok();
  await t.expect(screen.getByText("Inline").exists).ok();
  await t.expect(screen.getByTestId("edit-btn").exists).ok();
  await t.click(screen.findByTestId("edit-btn"), { speed: 0.5 });
  await t.expect(screen.getByText("This is a sample document with inline content for an API").exists).ok();
  logger.info("Inline source type document loaded successfully");

  await Selector('button').withText("Cancel").exists;
  await t.click(Selector('button').withText("Cancel").nth(1), { speed: 0.5 });

  // Check edit behavior
  await t.expect(screen.getByTestId("document-edit").exists).ok();
  await t.click(screen.findByTestId("document-edit"), { speed: 0.5 });
  await t.expect(screen.findByTestId("document-summary").exists).ok();
  await t.typeText(screen.findByTestId("document-summary"), "This doc contains modified inline document" +
      " for the sample API", { speed: 0.5, replace: true});
  await t.click(screen.findByTestId("edit-btn"), { speed: 0.5 });
  await t.pressKey('tab');
  await t.pressKey('tab');
  await t.typeText(() =>  document.activeElement, "This is an updated document with inline content for an"
      + " API", { speed: 0.5, replace: true });
  await t.expect(screen.findByText("Add").exists).ok();
  await t.click(screen.findByText("Add"), { speed: 0.5 });

  // update document
  await t.expect(screen.findByText("Save").exists).ok();
  await t.click(screen.findByText("Save"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Updated document loaded successfully!");

  // Verify the updated inline doc
  await t.expect(screen.getByTestId("document-name").exists).ok();
  await t.expect(screen.getByDisplayValue("Inline API doc").exists).ok();
  await t.expect(screen.getByTestId("document-summary").exists).ok();
  await t.expect(screen.getByDisplayValue("This doc contains modified inline document for the sample API")
      .exists).ok();
  await t.expect(screen.getByTestId("document-type-selector").exists).ok();
  await t.expect(screen.getByText("How To").exists).ok();
  await t.expect(screen.getByTestId("document-source-selector").exists).ok();
  await t.expect(screen.getByText("Inline").exists).ok();
  await t.expect(screen.getByTestId("edit-btn").exists).ok();
  await t.click(screen.findByTestId("edit-btn"), { speed: 0.5 });
  await t.expect(screen.getByText("This is an updated document with inline content for an API").exists).ok();
  logger.info("Inline source type document updated successfully");

  await t.expect(screen.findAllByText("Cancel").exists).ok();
  await t.click(screen.findAllByText("Cancel").nth(1), { speed: 0.5 });

  await t.expect(screen.findByText("Cancel").exists).ok();
  await t.click(screen.findByText("Cancel"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(screen.findAllByText("Documents").exists).ok();
  logger.info("Returned to documents list page successfully");

  // View markdown source type doc
  await t.expect(screen.findByText("Markdown").exists).ok();
  await t.click(screen.findByText("Markdown"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(screen.findByText("View Document").exists).ok();
  logger.info("Document view page loaded successfully");

  await t.expect(screen.getByTestId("document-name").exists).ok();
  await t.expect(screen.getByDisplayValue("Markdown API doc").exists).ok();
  await t.expect(screen.getByTestId("document-summary").exists).ok();
  await t.expect(screen.getByDisplayValue("This doc provides a markdown document contains the docs for the" +
      " sample API").exists).ok();
  await t.expect(screen.getByTestId("document-type-selector").exists).ok();
  await t.expect(screen.getByText("How To").exists).ok();
  await t.expect(screen.getByTestId("document-source-selector").exists).ok();
  await t.expect(screen.getByText("Markdown").exists).ok();
  await t.expect(screen.getByTestId("edit-btn").exists).ok();
  await t.click(screen.findByTestId("edit-btn"), { speed: 0.5 });
  await t.expect(screen.getByText("This is a sample document with markdown content for an API").exists).ok();
  logger.info("Markdown source type document loaded successfully");

  await t.expect(screen.findAllByText("Cancel").exists).ok();
  await t.click(screen.findAllByText("Cancel").nth(1), { speed: 0.5 });

  // Check edit behavior
  await t.expect(screen.getByTestId("document-edit").exists).ok();
  await t.click(screen.findByTestId("document-edit"), { speed: 0.5 });
  await t.expect(screen.findByTestId("document-summary").exists).ok();
  await t.typeText(screen.findByTestId("document-summary"), "This doc provides updated markdown document" +
      " for the sample API", { speed: 0.5, replace: true});
  await t.click(screen.findByTestId("edit-btn"), { speed: 0.5 });
  await t.pressKey('ctrl+a delete');
  await t.pressKey('T h i s space i s space a n space u p d a t e d space d o c u m e n t space w i t h space');
  await t.pressKey('m a r k d o w n space c o n t e n t space f o r space a n space A P I');
  await t.expect(screen.findByText("Add").exists).ok();
  await t.click(screen.findByText("Add"), { speed: 0.5 });

  // update document
  await t.expect(screen.findByText("Save").exists).ok();
  await t.click(screen.findByText("Save"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Updated markdown document loaded successfully!");

  // Verify the updated markdown doc
  await t.expect(screen.getByTestId("document-name").exists).ok();
  await t.expect(screen.getByDisplayValue("Markdown API doc").exists).ok();
  await t.expect(screen.getByTestId("document-summary").exists).ok();
  await t.expect(screen.getByDisplayValue("This doc provides updated markdown document" +
      " for the sample API").exists).ok();
  await t.expect(screen.getByTestId("document-type-selector").exists).ok();
  await t.expect(screen.getByText("How To").exists).ok();
  await t.expect(screen.getByTestId("document-source-selector").exists).ok();
  await t.expect(screen.getByText("Markdown").exists).ok();
  await t.expect(screen.getByTestId("edit-btn").exists).ok();
  await t.click(screen.findByTestId("edit-btn"), { speed: 0.5 });
  await t.expect(screen.getByText("This is an updated document with markdown content for an API").exists).ok();
  logger.info("Markdown source type document updated successfully");

  await t.expect(screen.findAllByText("Cancel").exists).ok();
  await t.click(screen.findAllByText("Cancel").nth(1), { speed: 0.5 });

  await t.expect(screen.findByText("Cancel").exists).ok();
  await t.click(screen.findByText("Cancel"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await screen.findAllByText("Documents").exists;
  logger.info("Returned to documents list page successfully");

  // View file source type doc
  await t.expect(screen.findByText("File").exists).ok();
  await t.click(screen.findByText("File"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(screen.findByText("View Document").exists).ok();
  logger.info("Document view page loaded successfully");

  await t.expect(screen.getByTestId("document-name").exists).ok();
  await t.expect(screen.getByDisplayValue("File API doc").exists).ok();
  await t.expect(screen.getByTestId("document-summary").exists).ok();
  await t.expect(screen.getByDisplayValue("This doc provides a pdf document contains the docs for the" +
      " sample API").exists).ok();
  await t.expect(screen.getByTestId("document-type-selector").exists).ok();
  await t.expect(screen.getByText("How To").exists).ok();
  await t.expect(screen.getByTestId("document-source-selector").exists).ok();
  await t.expect(screen.getAllByText("File").exists).ok();
  await t.expect(screen.getByTestId("document-filename").exists).ok();
  //await t.expect(Selector('input').withText("API_Documentation_V1.pdf").exists).ok();
  logger.info("File source type document loaded successfully");

  // Check edit behavior
  await t.expect(screen.getByTestId("document-edit").exists).ok();
  await t.click(screen.findByTestId("document-edit"), { speed: 0.5 });
  await t.expect(screen.findByTestId("document-summary").exists).ok();
  await t.typeText(screen.findByTestId("document-summary"), "This doc provides a pdf document for the API",
      { speed: 0.5, replace: true});

  // update document
  await t.expect(screen.findByText("Save").exists).ok();
  await t.click(screen.findByText("Save"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Updated File document loaded successfully!");

  // Verify the updated file doc
  await t.expect(screen.getByTestId("document-name").exists).ok();
  await t.expect(screen.getByDisplayValue("File API doc").exists).ok();
  await t.expect(screen.getByTestId("document-summary").exists).ok();
  await t.expect(screen.getByDisplayValue("This doc provides a pdf document for the API").exists).ok();
  await t.expect(screen.getByTestId("document-type-selector").exists).ok();
  await t.expect(screen.getByText("How To").exists).ok();
  await t.expect(screen.getByTestId("document-source-selector").exists).ok();
  await t.expect(screen.getAllByText("File").exists).ok();
  logger.info("File source type document updated successfully");

  await t.expect(screen.findByText("Cancel").exists).ok();
  await t.click(screen.findByText("Cancel"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await screen.findAllByText("Documents").exists;
  logger.info("Returned to documents list page successfully");
});

test("Delete an API document from document view page", async (t) => {
  // click api tab
  await t.expect(screen.findAllByText("APIs").exists).ok();
  await t.click(screen.getByText("APIs"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("API tab loaded successfully");

  // click on created api
  await t.click(screen.findByText(apiName), { speed: 0.5 });

  // Go to documents tab
  await t.expect(screen.findAllByText("Documents").exists).ok({ timeout: WAIT_TIME_SHORT });
  await t.click(screen.findAllByText("Documents"), { speed: 0.5 });
  await t.expect(screen.findAllByText("Documents").exists).ok();
  logger.info("Navigated to documents tab successfully");

  // Go to view page
  await t.expect(screen.findByText("URL").exists).ok();
  await t.click(screen.findByText("URL"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(screen.findByText("View Document").exists).ok();
  logger.info("Document view page loaded successfully");

  // Click delete button
  await t.expect(screen.getByTestId("document-delete").exists).ok();
  await t.click(screen.findByTestId("document-delete"), { speed: 0.5 });
  // Check delete confirmation dialog
  await t.expect(screen.findByText("Delete Document").exists).ok();
  await t.expect(screen.findByText("Delete").exists).ok();
  await t.click(screen.findByText("Delete"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(screen.findByText("URL").exists).notOk();

  logger.info("Document deleted successfully from a view/edit page!");
});

test.skip("Delete documents of an API", async (t) => {
  // click api tab
  await t.expect(screen.findAllByText("APIs").exists).ok();
  await t.click(screen.getByText("APIs"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("API tab loaded successfully");

  // click on created api
  await t.click(screen.findByText(apiName), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });

  // Go to documents tab
  await t.expect(screen.findAllByText("Documents").exists).ok({ timeout: WAIT_TIME_SHORT });
  await t.click(screen.findAllByText("Documents"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await screen.findAllByText("Documents").exists;
  logger.info("Navigated to documents tab successfully");

  // delete available documents
  await clearAPIDocumentsIfExists(t);
});
