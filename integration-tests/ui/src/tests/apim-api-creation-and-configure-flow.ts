import { screen, within } from "@testing-library/testcafe";
import { RequestLogger, Selector } from "testcafe";
import * as config from "../../testcafe-run-config.json";
import page from "../model/page";
import {
  createApiFromChoreoApp, createRespond, clearAPIDocumentsIfExists,
  createNewApp, enableDetailedLogs, saveLogs, selectAPIType, generateAppName, generateApiName, WAIT_TIME_MEDIUM,
  WAIT_TIME_SHORT, goToApiListView, openApi, deleteApi, deployToChoreo, getElementFromSelectorTestId
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

test.meta({'unstable': "true"})("Create API type choreo app", async (t) => {
  await createNewApp(t, appName);

  // adding api content
  await t.expect(await getLocation()).contains("app/" + appName + "/develop", { timeout: WAIT_TIME_SHORT });
  await selectAPIType(t, "hello");
  await createRespond(t, "\"hello world\"", true);

  // deploying app
  await deployToChoreo(t, appName);

  // go back app list
  await t
      .click(getElementFromSelectorTestId("app-list-btn"))
      .expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  await getElementFromSelectorTestId("active-status").exists;
  logger.info("Load app list successful!")
})

test.meta({'unstable': "true"})("Create API from previously created choreo app", async (t) => {
  // go to api tab
  await goToApiListView(t);

  // create new api from choreo app
  await createApiFromChoreoApp(t, apiName, appName);

  // test overview page loading
  await t.expect(await getLocation()).contains("/config/overview", { timeout: WAIT_TIME_SHORT });
  await getElementFromSelectorTestId("Overview").exists;
  logger.info("Created API config view loaded successfully!");
});

test.meta({'unstable': "true"})("Change design configurations of API", async (t) => {
  // go to api tab
  await goToApiListView(t);

  await openApi(t, apiName);

  // Go to design configs tab
  await getElementFromSelectorTestId("Design Configurations").exists;
  await t.click(getElementFromSelectorTestId("Design Configurations"), { speed: 0.5 });
  await getElementFromSelectorTestId("design-config-description").exists;
  logger.info("Navigated to design configuration tab successfully");

  // Enter a description and some tags
  await getElementFromSelectorTestId("toggle-edit-description").exists;
  await t.click(getElementFromSelectorTestId("toggle-edit-description"), { speed: 0.5 });
  await t.typeText(getElementFromSelectorTestId("description-input"), "This is a sample API", { speed: 0.5 });
  await getElementFromSelectorTestId("toggle-add-tags").exists;
  await t.click(getElementFromSelectorTestId("toggle-add-tags"), { speed: 0.5 });
  await getElementFromSelectorTestId("tag-input").exists;
  await t
    .typeText(getElementFromSelectorTestId("tag-input"), "sample", { speed: 0.5 })
    .pressKey("Enter", { speed: 0.5 });
  await t
    .typeText(getElementFromSelectorTestId("tag-input"), "test", { speed: 0.5 })
    .pressKey("Enter", { speed: 0.5 });
  await screen.findAllByText("sample").exists;
  await screen.findAllByText("api").exists;
  logger.info("Input description and tags successful");

  // Save design configs
  await getElementFromSelectorTestId("design-config-save-btn").exists;
  await t.click(getElementFromSelectorTestId("design-config-save-btn"), { speed: 0.5 });
  await t.expect(Selector("#circular-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });

  // Verify Design config update by checking overview tab
  await getElementFromSelectorTestId("Overview").exists;
  await t.click(getElementFromSelectorTestId("Overview"), { speed: 0.5 });
  await screen.findByText("This is a sample API").exists;
  logger.info("Design configuration update successful");
});

test.meta({'unstable': "true"})("Change subscriptions of API", async (t) => {
  // go to api tab
  await goToApiListView(t);

  await openApi(t, apiName);

  // Go to Subscriptions tab
  await getElementFromSelectorTestId("Subscriptions").exists;
  await t.click(getElementFromSelectorTestId("Subscriptions"), { speed: 0.5 });
  await getElementFromSelectorTestId("tab-header").exists;
  logger.info("Navigated to Subscriptions tab successfully");

  // Add Gold and Silver business plan subscriptions
  await getElementFromSelectorTestId("checkbox-Gold").exists;
  await t.click(getElementFromSelectorTestId("checkbox-Gold"), { speed: 0.5 });
  await getElementFromSelectorTestId("checkbox-Silver").exists;
  await t.click(getElementFromSelectorTestId("checkbox-Silver"), { speed: 0.5 });

  // Save new suscriptions
  await getElementFromSelectorTestId("subscription-save-btn").exists;
  await t.click(getElementFromSelectorTestId("subscription-save-btn"), { speed: 0.5 });
  await t.expect(Selector("#circular-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });

  // Verify Subscription update by checking overview tab
  await getElementFromSelectorTestId("Overview").exists;
  await t.click(getElementFromSelectorTestId("Overview"), { speed: 0.5 });
  await screen.findByText("Unlimited, Gold, Silver").exists;
  logger.info("Subscription update successful");
});

test.meta({'unstable': "true"})("Change Business Info of API", async (t) => {
  // go to api tab
  await goToApiListView(t);

  await openApi(t, apiName);

  // Go to Business Info tab
  await getElementFromSelectorTestId("Business Info").exists;
  await t.click(getElementFromSelectorTestId("Business Info"), { speed: 0.5 });
  await getElementFromSelectorTestId("tab-header").exists;
  logger.info("Navigated to Business Info tab successfully");

  // Edit Business Info details
  await getElementFromSelectorTestId("editButton-businessOwnerName").exists;
  await t.click(getElementFromSelectorTestId("editButton-businessOwnerName"), { speed: 0.5 });
  await getElementFromSelectorTestId("textbox-businessOwnerName").exists;
  await t.typeText(getElementFromSelectorTestId("textbox-businessOwnerName"), "John Doe", { speed: 0.5, replace: true });

  await getElementFromSelectorTestId("editButton-businessOwnerMail").exists;
  await t.click(getElementFromSelectorTestId("editButton-businessOwnerMail"), { speed: 0.5 });
  await getElementFromSelectorTestId("textbox-businessOwnerMail").exists;
  await t.typeText(getElementFromSelectorTestId("textbox-businessOwnerMail"), "johndoe@mail.com", { speed: 0.5, replace: true });

  await getElementFromSelectorTestId("editButton-technicalOwnerName").exists;
  await t.click(getElementFromSelectorTestId("editButton-technicalOwnerName"), { speed: 0.5 });
  await getElementFromSelectorTestId("textbox-technicalOwnerName").exists;
  await t.typeText(getElementFromSelectorTestId("textbox-technicalOwnerName"), "Jane Smith", { speed: 0.5, replace: true });

  await getElementFromSelectorTestId("editButton-technicalOwnerMail").exists;
  await t.click(getElementFromSelectorTestId("editButton-technicalOwnerMail"), { speed: 0.5 });
  await getElementFromSelectorTestId("textbox-technicalOwnerMail").exists;
  await t.typeText(getElementFromSelectorTestId("textbox-technicalOwnerMail"), "janesmith@mail.com", { speed: 0.5, replace: true });

  // Save new Business Info
  await getElementFromSelectorTestId("business-info-save-btn").exists;
  await t.click(getElementFromSelectorTestId("business-info-save-btn"), { speed: 0.5 });
  await t.expect(Selector("#circular-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });

  // Verify Business Info update by checking overview tab
  await getElementFromSelectorTestId("Overview").exists;
  await t.click(getElementFromSelectorTestId("Overview"), { speed: 0.5 });
  await t.expect(screen.findByText("John Doe").exists).ok();
  await t.expect(screen.findByText("johndoe@mail.com").exists).ok();
  await t.expect(screen.findByText("Jane Smith").exists).ok();
  await t.expect(screen.findByText("janesmith@mail.com").exists).ok();
  logger.info("Business Info update successful");
});

test.meta({'unstable': "true"})("Change Runtime Configurations of API", async (t) => {
  // go to api tab
  await goToApiListView(t);

  await openApi(t, apiName);

  // Go to Runtime Configurations tab
  await getElementFromSelectorTestId("Runtime Configurations").exists;
  await t.click(getElementFromSelectorTestId("Runtime Configurations"), { speed: 0.5 });
  await getElementFromSelectorTestId("tab-header").exists;
  logger.info("Navigated to Runtime Configurations tab successfully");

  // Switch on CORS configuration and configure it
  await t.expect(getElementFromSelectorTestId("switch-cors-config").exists).ok();
  await t.click(getElementFromSelectorTestId("switch-cors-config"), { speed: 0.5 });
  await t.click(getElementFromSelectorTestId("cors-config-label"), { speed: 0.5 });

  await t.click(getElementFromSelectorTestId("checkbox-allow-all-origins"), { speed: 0.5 });
  // await t.click(getElementFromSelectorTestId("allow-all-origins-label"), { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("addBtn-origin").exists).ok();
  await t.click(getElementFromSelectorTestId("addBtn-origin"), { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("type and press enter to add origins").exists).ok();
  await t
    .typeText(getElementFromSelectorTestId("type and press enter to add origins"), "some-origin.com", { speed: 0.5 })
    .pressKey("Enter", { speed: 0.5 });

  await t.expect(getElementFromSelectorTestId("addBtn-header").exists).ok();
  await t.click(getElementFromSelectorTestId("addBtn-header"), { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("type and press enter to add headers").exists).ok();
  await t
    .typeText(getElementFromSelectorTestId("type and press enter to add headers"), "wso2-x", { speed: 0.5 })
    .pressKey("Enter", { speed: 0.5 });
  await t
    .typeText(getElementFromSelectorTestId("type and press enter to add headers"), "ex-security", { speed: 0.5 })
    .pressKey("Enter", { speed: 0.5 });
  await t
    .expect(Selector('#remove-item-ex-security').exists).ok()
    .click(Selector('#remove-item-ex-security'), { speed: 0.5 });

  await t
    .expect(Selector('#remove-item-DELETE').exists).ok()
    .click(Selector('#remove-item-DELETE'), { speed: 0.5 });

  await t.click(getElementFromSelectorTestId("application-level-sec-label"), { speed: 0.5 });
  await t.click(getElementFromSelectorTestId("checkbox-Api Key"), { speed: 0.5 });

  // save changes
  await t
    .click(getElementFromSelectorTestId("runtime-config-save-btn"), { speed: 0.5 })
    .expect(Selector("#circular-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });

  // assert changes
  await t.expect(getElementFromSelectorTestId("checkbox-allow-all-origins")
    .find("input[type=checkbox]").nth(0).checked).eql(false);
  await t
    .expect(Selector('#remove-item-wso2-x').exists).ok()
    .expect(Selector('#remove-item-DELETE').exists).notOk()
    .expect(Selector('#remove-item-ex-security').exists).notOk()

  await t.expect(getElementFromSelectorTestId("checkbox-OAuth2")
    .find("input[type=checkbox]").nth(0).checked).eql(true);
  await t.expect(getElementFromSelectorTestId("checkbox-Api Key")
    .find("input[type=checkbox]").nth(0).checked).eql(true);
});

test.meta({'unstable': "true"})("Create a URL type document for an API", async (t) => {
  // go to api tab
  await goToApiListView(t);

  await openApi(t, apiName);

  // Go to documents tab
  await t.expect(getElementFromSelectorTestId("Documents").exists).ok({ timeout: WAIT_TIME_SHORT });
  await t.click(getElementFromSelectorTestId("Documents"), { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("tab-header").exists).ok();
  logger.info("Navigated to documents tab successfully");

  // delete available documents
  await clearAPIDocumentsIfExists(t);

  // Go to add new document page
  await t.expect(getElementFromSelectorTestId("add-new-document").exists).ok();
  await t.click(getElementFromSelectorTestId("add-new-document"), { speed: 0.5 });
  await t.expect(await getLocation()).contains("/documents/add", { timeout: WAIT_TIME_SHORT });
  await t.expect(getElementFromSelectorTestId("page-header").exists).ok();

  // Fill document creation form
  await t.expect(getElementFromSelectorTestId("document-name").exists).ok();
  await t.typeText(getElementFromSelectorTestId("document-name"), "API doc link", { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("document-summary").exists).ok();
  await t.typeText(getElementFromSelectorTestId("document-summary"), "This doc provides a URL contains the docs for"
      + " the sample API", { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("document-url").exists).ok();
  await t.typeText(getElementFromSelectorTestId("document-url"), "https://sampleurl.doc", { speed: 0.5 });

  // create document and wait for listing to load
  await t.expect(getElementFromSelectorTestId("create-document").exists).ok();
  await t.click(getElementFromSelectorTestId("create-document"), { speed: 0.5 });
  await t.expect(Selector("#circular-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Document created successfully!");

  await screen.queryByText("URL").exists;
  logger.info("Created URL Source Document listed successfully!");
});

test.meta({'unstable': "true"})("Create an Inline type document for an API", async (t) => {
  // go to api tab
  await goToApiListView(t);

  await openApi(t, apiName);

  // Go to documents tab
  await t.expect(getElementFromSelectorTestId("Documents").exists).ok({ timeout: WAIT_TIME_SHORT });
  await t.click(getElementFromSelectorTestId("Documents"), { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("tab-header").exists).ok();
  logger.info("Navigated to documents tab successfully");

  // Go to add new document page
  await t.expect(getElementFromSelectorTestId("add-new-document").exists).ok();
  await t.click(getElementFromSelectorTestId("add-new-document"), { speed: 0.5 });
  await t.expect(await getLocation()).contains("/documents/add", { timeout: WAIT_TIME_SHORT });
  await t.expect(getElementFromSelectorTestId("page-header").exists).ok();

  // Fill document creation form
  await t.expect(getElementFromSelectorTestId("document-name").exists).ok();
  await t.typeText(getElementFromSelectorTestId("document-name"), "Inline API doc", { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("document-summary").exists).ok();
  await t.typeText(getElementFromSelectorTestId("document-summary"), "This doc provides an inline document" +
      " contains the docs for the sample API", { speed: 0.5 });

  // Select inline content type
  await t.expect(getElementFromSelectorTestId("document-source-selector").exists).ok();
  await t.click(getElementFromSelectorTestId("document-source-selector"), { speed: 0.5 });
  await t.expect(screen.findByText("Inline").exists).ok();
  await t.click(screen.findByText("Inline"), { speed: 0.5 });

  // Add inline content
  await t.pressKey('tab');
  await t.pressKey('tab');
  await t.typeText(() =>  document.activeElement, "This is a sample document with inline content for an" +
      " API", { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("text-editor-add-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("text-editor-add-btn"), { speed: 0.5 });

  // create document and wait for listing to load
  await t.expect(getElementFromSelectorTestId("create-document").exists).ok();
  await t.click(getElementFromSelectorTestId("create-document"), { speed: 0.5 });
  await t.expect(Selector("#circular-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Inline source document created successfully!");

  await t.expect(screen.queryByText("Inline").exists).ok();
  logger.info("Created Inline source Document listed successfully!");
});

test.meta({'unstable': "true"})("Create a Markdown type document for an API", async (t) => {
  // go to api tab
  await goToApiListView(t);

  await openApi(t, apiName);

  // Go to documents tab
  await t.expect(getElementFromSelectorTestId("Documents").exists).ok({ timeout: WAIT_TIME_SHORT });
  await t.click(getElementFromSelectorTestId("Documents"), { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("tab-header").exists).ok();
  logger.info("Navigated to documents tab successfully");

  // Go to add new document page
  await t.expect(getElementFromSelectorTestId("add-new-document").exists).ok();
  await t.click(getElementFromSelectorTestId("add-new-document"), { speed: 0.5 });
  await t.expect(await getLocation()).contains("/documents/add", { timeout: WAIT_TIME_SHORT });
  await t.expect(getElementFromSelectorTestId("page-header").exists).ok();

  // Fill document creation form
  await t.expect(getElementFromSelectorTestId("document-name").exists).ok();
  await t.typeText(getElementFromSelectorTestId("document-name"), "Markdown API doc", { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("document-summary").exists).ok();
  await t.typeText(getElementFromSelectorTestId("document-summary"), "This doc provides a markdown document" +
      " contains the docs for the sample API", { speed: 0.5 });

  // Select markdown content type
  await t.expect(getElementFromSelectorTestId("document-source-selector").exists).ok();
  await t.click(getElementFromSelectorTestId("document-source-selector"), { speed: 0.5 });
  await t.expect(screen.findByText("Markdown").exists).ok();
  await t.click(screen.findByText("Markdown"), { speed: 0.5 });

  // Add markdown content
  await t.expect(getElementFromSelectorTestId("markdown-editor-add-btn").exists).ok({ timeout: WAIT_TIME_SHORT });
  const isMac: boolean = process.platform === "darwin";
  await t.pressKey(isMac ? 'meta+a delete' : 'ctrl+a delete');
  await t.pressKey('T h i s space i s space a space s a m p l e space d o c u m e n t space w i t h space');
  await t.pressKey('m a r k d o w n space c o n t e n t space f o r space a n space A P I');
  await t.expect(getElementFromSelectorTestId("markdown-editor-add-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("markdown-editor-add-btn"), { speed: 0.5 });

  // create document and wait for listing to load
  await t.expect(getElementFromSelectorTestId("create-document").exists).ok();
  await t.click(getElementFromSelectorTestId("create-document"), { speed: 0.5 });
  await t.expect(Selector("#circular-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Markdown source document created successfully!");

  await t.expect(screen.queryByText("Markdown").exists).ok();
  logger.info("Created markdown source Document listed successfully!");
});

test.meta({'unstable': "true"})("Create a File type document for an API", async (t) => {
  // go to api tab
  await goToApiListView(t);

  await openApi(t, apiName);

  // Go to documents tab
  await t.click(getElementFromSelectorTestId("Documents"), { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("tab-header").exists).ok();
  logger.info("Navigated to documents tab successfully");

  // Go to add new document page
  await t.expect(getElementFromSelectorTestId("add-new-document").exists).ok();
  await t.click(getElementFromSelectorTestId("add-new-document"), { speed: 0.5 });
  await t.expect(await getLocation()).contains("/documents/add", { timeout: WAIT_TIME_SHORT });
  await t.expect(getElementFromSelectorTestId("page-header").exists).ok();

  // Fill document creation form
  await t.expect(getElementFromSelectorTestId("document-name").exists).ok();
  await t.typeText(getElementFromSelectorTestId("document-name"), "File API doc", { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("document-summary").exists).ok();
  await t.typeText(getElementFromSelectorTestId("document-summary"), "This doc provides a pdf document" +
      " contains the docs for the sample API", { speed: 0.5 });

  // Select file content type
  await t.expect(getElementFromSelectorTestId("document-source-selector").exists).ok();
  await t.click(getElementFromSelectorTestId("document-source-selector"), { speed: 0.5 });
  await t.expect(screen.findByText("File").exists).ok();
  await t.click(screen.findByText("File"), { speed: 0.5 });

  // File upload
  await t.expect(getElementFromSelectorTestId("upload-button").exists).ok();
  await t
      .setFilesToUpload(getElementFromSelectorTestId("file-input"),
          ['../../resources/sample_doc.pdf']).click(screen.findByTestId("upload-button"));

  // create document and wait for listing to loads
  await t.expect(getElementFromSelectorTestId("create-document").exists).ok();
  await t.click(getElementFromSelectorTestId("create-document"), { speed: 0.5 });
  await t.expect(Selector("#circular-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("File source document created successfully!");

  await t.expect(screen.queryByText("File").exists).ok();
  logger.info("Created File source Document listed successfully!");
});

test.meta({'unstable': "true"})("View different types of API documents", async (t) => {
  // go to api tab
  await goToApiListView(t);

  await openApi(t, apiName);

  // Go to documents tab
  await t.expect(getElementFromSelectorTestId("Documents").exists).ok({ timeout: WAIT_TIME_SHORT });
  await t.click(getElementFromSelectorTestId("Documents"), { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("tab-header").exists).ok();
  logger.info("Navigated to documents tab successfully");

  // Go to view page
  await t.expect(screen.findByText("API doc link").exists).ok();
  await t.click(screen.findByText("API doc link"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(getElementFromSelectorTestId("tab-header").exists).ok();
  logger.info("Document view page loaded successfully");

  // Verify the elements
  await t.expect(getElementFromSelectorTestId("document-name").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-name").find("input")
      .getAttribute('value')).eql("API doc link");
  await t.expect(getElementFromSelectorTestId("document-summary").exists).ok();
  await t.expect(screen.getByDisplayValue("This doc provides a URL contains the docs for the sample API")
      .exists).ok();
  await t.expect(getElementFromSelectorTestId("document-type-selector").exists).ok();
  await t.expect(screen.getByText("How To").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-source-selector").exists).ok();
  await t.expect(screen.getAllByText("URL").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-url").find("input")
      .getAttribute('value')).eql("https://sampleurl.doc");
  logger.info("Url source type document loaded successfully");

  // Check edit behavior
  await t.expect(getElementFromSelectorTestId("document-edit").exists).ok();
  await t.click(getElementFromSelectorTestId("document-edit"), { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("document-summary").exists).ok();
  await t.typeText(getElementFromSelectorTestId("document-summary"), "This doc modifies the URL contains the docs" +
      " for the sample API", { speed: 0.5, replace: true});
  await t.expect(getElementFromSelectorTestId("document-url").exists).ok();
  await t.typeText(getElementFromSelectorTestId("document-url"), "https://updatedurl.doc", { speed: 0.5,
    replace: true});

  // update document
  await t.expect(getElementFromSelectorTestId("view-edit-save-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("view-edit-save-btn"), { speed: 0.5 });
  await t.expect(Selector("#circular-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Updated document loaded successfully!");

  // Verify the updated elements
  await t.expect(getElementFromSelectorTestId("document-name").find("input")
      .getAttribute('value')).eql("API doc link");
  await t.expect(getElementFromSelectorTestId("document-summary").exists).ok();
  await t.expect(screen.getByDisplayValue("This doc modifies the URL contains the docs for the sample API")
      .exists).ok();
  await t.expect(getElementFromSelectorTestId("document-type-selector").exists).ok();
  await t.expect(screen.getByText("How To").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-source-selector").exists).ok();
  await t.expect(screen.getAllByText("URL").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-url").find("input")
      .getAttribute('value')).eql("https://updatedurl.doc");

  // Back to documents list
  await t.expect(getElementFromSelectorTestId("view-edit-cancel-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("view-edit-cancel-btn"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(screen.findAllByText("Documents").exists).ok();
  logger.info("Returned to documents list page successfully");

  // View inline source type doc
  await t.expect(screen.findByText("Inline").exists).ok();
  await t.click(screen.findByText("Inline"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(getElementFromSelectorTestId("tab-header").exists).ok();
  logger.info("Document view page loaded successfully");

  await t.expect(getElementFromSelectorTestId("document-name").find("input")
      .getAttribute('value')).eql("Inline API doc");
  await t.expect(getElementFromSelectorTestId("document-summary").exists).ok();
  await t.expect(screen.getByDisplayValue("This doc provides an inline document contains the docs for the " +
      "sample API").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-type-selector").exists).ok();
  await t.expect(screen.getByText("How To").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-source-selector").exists).ok();
  await t.expect(screen.getByText("Inline").exists).ok();
  await t.expect(getElementFromSelectorTestId("edit-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("edit-btn"), { speed: 0.5 });
  await t.expect(screen.getByText("This is a sample document with inline content for an API").exists).ok();
  logger.info("Inline source type document loaded successfully");

  await t.expect(getElementFromSelectorTestId("text-editor-cancel-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("text-editor-cancel-btn"), { speed: 0.5 });

  // Check edit behavior
  await t.expect(getElementFromSelectorTestId("document-edit").exists).ok();
  await t.click(getElementFromSelectorTestId("document-edit"), { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("document-summary").exists).ok();
  await t.typeText(getElementFromSelectorTestId("document-summary"), "This doc contains modified inline document" +
      " for the sample API", { speed: 0.5, replace: true});
  await t.click(getElementFromSelectorTestId("edit-btn"), { speed: 0.5 });
  await t.pressKey('tab');
  await t.pressKey('tab');
  await t.typeText(() =>  document.activeElement, "This is an updated document with inline content for an"
      + " API", { speed: 0.5, replace: true });
  await t.expect(getElementFromSelectorTestId("text-editor-add-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("text-editor-add-btn"), { speed: 0.5 });

  // update document
  await t.expect(getElementFromSelectorTestId("view-edit-save-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("view-edit-save-btn"), { speed: 0.5 });
  await t.expect(Selector("#circular-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Updated document loaded successfully!");

  // Verify the updated inline doc
  await t.expect(getElementFromSelectorTestId("document-name").find("input")
      .getAttribute('value')).eql("Inline API doc");
  await t.expect(getElementFromSelectorTestId("document-summary").exists).ok();
  await t.expect(screen.getByDisplayValue("This doc contains modified inline document for the sample API")
      .exists).ok();
  await t.expect(getElementFromSelectorTestId("document-type-selector").exists).ok();
  await t.expect(screen.getByText("How To").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-source-selector").exists).ok();
  await t.expect(screen.getByText("Inline").exists).ok();
  await t.expect(getElementFromSelectorTestId("edit-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("edit-btn"), { speed: 0.5 });
  await t.expect(screen.getByText("This is an updated document with inline content for an API").exists).ok();
  logger.info("Inline source type document updated successfully");

  await t.expect(getElementFromSelectorTestId("text-editor-cancel-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("text-editor-cancel-btn"), { speed: 0.5 });

  await t.expect(getElementFromSelectorTestId("view-edit-cancel-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("view-edit-cancel-btn"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(screen.findAllByText("Documents").exists).ok();
  logger.info("Returned to documents list page successfully");

  // View markdown source type doc
  await t.expect(screen.findByText("Markdown").exists).ok();
  await t.click(screen.findByText("Markdown"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(getElementFromSelectorTestId("tab-header").exists).ok();
  logger.info("Document view page loaded successfully");

  await t.expect(getElementFromSelectorTestId("document-name").find("input")
      .getAttribute('value')).eql("Markdown API doc");
  await t.expect(getElementFromSelectorTestId("document-summary").exists).ok();
  await t.expect(screen.getByDisplayValue("This doc provides a markdown document contains the docs for the" +
      " sample API").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-type-selector").exists).ok();
  await t.expect(screen.getByText("How To").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-source-selector").exists).ok();
  await t.expect(screen.getByText("Markdown").exists).ok();
  await t.expect(getElementFromSelectorTestId("edit-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("edit-btn"), { speed: 0.5 });
  await t.expect(screen.getByText("This is a sample document with markdown content for an API").exists).ok();
  logger.info("Markdown source type document loaded successfully");

  await t.expect(getElementFromSelectorTestId("markdown-editor-cancel-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("markdown-editor-cancel-btn"), { speed: 0.5 });

  // Check edit behavior
  await t.expect(getElementFromSelectorTestId("document-edit").exists).ok();
  await t.click(getElementFromSelectorTestId("document-edit"), { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("document-summary").exists).ok();
  await t.typeText(getElementFromSelectorTestId("document-summary"), "This doc provides updated markdown document" +
      " for the sample API", { speed: 0.5, replace: true});
  await t.click(getElementFromSelectorTestId("edit-btn"), { speed: 0.5 });

  const isMac: boolean = process.platform === "darwin";
  await t.pressKey(isMac ? 'meta+a delete' : 'ctrl+a delete');
  await t.pressKey('T h i s space i s space a n space u p d a t e d space d o c u m e n t space w i t h space');
  await t.pressKey('m a r k d o w n space c o n t e n t space f o r space a n space A P I');
  await t.expect(getElementFromSelectorTestId("markdown-editor-add-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("markdown-editor-add-btn"), { speed: 0.5 });

  // update document
  await t.expect(getElementFromSelectorTestId("view-edit-save-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("view-edit-save-btn"), { speed: 0.5 });
  await t.expect(Selector("#circular-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Updated markdown document loaded successfully!");

  // Verify the updated markdown doc
  await t.expect(getElementFromSelectorTestId("document-name").find("input")
      .getAttribute('value')).eql("Markdown API doc");
  await t.expect(getElementFromSelectorTestId("document-summary").exists).ok();
  await t.expect(screen.getByDisplayValue("This doc provides updated markdown document" +
      " for the sample API").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-type-selector").exists).ok();
  await t.expect(screen.getByText("How To").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-source-selector").exists).ok();
  await t.expect(screen.getByText("Markdown").exists).ok();
  await t.expect(getElementFromSelectorTestId("edit-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("edit-btn"), { speed: 0.5 });
  await t.expect(screen.getByText("This is an updated document with markdown content for an API").exists).ok();
  logger.info("Markdown source type document updated successfully");

  await t.expect(getElementFromSelectorTestId("markdown-editor-cancel-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("markdown-editor-cancel-btn"), { speed: 0.5 });

  await t.expect(getElementFromSelectorTestId("view-edit-cancel-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("view-edit-cancel-btn"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await screen.findAllByText("Documents").exists;
  logger.info("Returned to documents list page successfully");

  // View file source type doc
  await t.expect(screen.findByText("File").exists).ok();
  await t.click(screen.findByText("File"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(getElementFromSelectorTestId("tab-header").exists).ok();
  logger.info("Document view page loaded successfully");

  await t.expect(getElementFromSelectorTestId("document-name").find("input")
      .getAttribute('value')).eql("File API doc");
  await t.expect(getElementFromSelectorTestId("document-summary").exists).ok();
  await t.expect(screen.getByDisplayValue("This doc provides a pdf document contains the docs for the" +
      " sample API").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-type-selector").exists).ok();
  await t.expect(screen.getByText("How To").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-source-selector").exists).ok();
  await t.expect(screen.getAllByText("File").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-filename").exists).ok();
  //await t.expect(Selector('input').withText("API_Documentation_V1.pdf").exists).ok();
  logger.info("File source type document loaded successfully");

  // Check edit behavior
  await t.expect(getElementFromSelectorTestId("document-edit").exists).ok();
  await t.click(getElementFromSelectorTestId("document-edit"), { speed: 0.5 });
  await t.expect(getElementFromSelectorTestId("document-summary").exists).ok();
  await t.typeText(getElementFromSelectorTestId("document-summary"), "This doc provides a pdf document for the API",
      { speed: 0.5, replace: true});

  // update document
  await t.expect(getElementFromSelectorTestId("view-edit-save-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("view-edit-save-btn"), { speed: 0.5 });
  await t.expect(Selector("#circular-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Updated File document loaded successfully!");

  // Verify the updated file doc
  await t.expect(getElementFromSelectorTestId("document-name").find("input")
      .getAttribute('value')).eql("File API doc");
  await t.expect(getElementFromSelectorTestId("document-summary").exists).ok();
  await t.expect(screen.getByDisplayValue("This doc provides a pdf document for the API").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-type-selector").exists).ok();
  await t.expect(screen.getByText("How To").exists).ok();
  await t.expect(getElementFromSelectorTestId("document-source-selector").exists).ok();
  await t.expect(screen.getAllByText("File").exists).ok();
  logger.info("File source type document updated successfully");

  await t.expect(getElementFromSelectorTestId("view-edit-cancel-btn").exists).ok();
  await t.click(getElementFromSelectorTestId("view-edit-cancel-btn"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await screen.findAllByText("Documents").exists;
  logger.info("Returned to documents list page successfully");
});

test.meta({'unstable': "true"})("Delete an API document from document view page", async (t) => {
  // go to api tab
  await goToApiListView(t);

  await openApi(t, apiName);

  // Go to documents tab
  await t.expect(getElementFromSelectorTestId("Documents").exists).ok({ timeout: WAIT_TIME_SHORT });
  await t.click(getElementFromSelectorTestId("Documents"), { speed: 0.5 });
  await t.expect(screen.findAllByText("Documents").exists).ok();
  logger.info("Navigated to documents tab successfully");

  // Go to view page
  await t.expect(screen.findByText("URL").exists).ok();
  await t.click(screen.findByText("URL"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(getElementFromSelectorTestId("tab-header").exists).ok();
  logger.info("Document view page loaded successfully");

  // Click delete button
  await t.expect(getElementFromSelectorTestId("document-delete").exists).ok();
  await t.click(getElementFromSelectorTestId("document-delete"), { speed: 0.5 });
  // Check delete confirmation dialog
  await t.expect(getElementFromSelectorTestId("Delete Document").exists).ok();
  await t.expect(getElementFromSelectorTestId("delete-api").exists).ok();
  await t.click(getElementFromSelectorTestId("delete-api"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(screen.findByText("URL").exists).notOk();

  logger.info("Document deleted successfully from a view/edit page!");
});

test.skip("Delete documents of an API", async (t) => {
  // go to api tab
  await goToApiListView(t);

  await openApi(t, apiName);

  // Go to documents tab
  await t.expect(screen.findAllByText("Documents").exists).ok({ timeout: WAIT_TIME_SHORT });
  await t.click(screen.findAllByText("Documents"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await screen.findAllByText("Documents").exists;
  logger.info("Navigated to documents tab successfully");

  // delete available documents
  await clearAPIDocumentsIfExists(t);
});

test.meta({'unstable': "true"})("Delete an API", async (t) => {
  // go to api tab
  await goToApiListView(t);

  await deleteApi(t, apiName, true);
});
