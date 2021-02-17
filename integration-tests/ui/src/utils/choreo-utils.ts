import { Selector, ClientFunction } from "testcafe";
import { screen, within } from "@testing-library/testcafe";
import { logger } from './logger';
import Axios from "axios";
import * as fs from 'fs';
import {gunzipSync} from 'zlib'
import {getLocation} from "./login-utils";


declare global {
  interface Window {
    enableDetailedLogs: () => void;
    disableDetailedLogs: () => void;
  }
}

export const WAIT_TIME_SHORT = 10000; // 10 sec
export const WAIT_TIME_MEDIUM = 60000; // 1 min
export const WAIT_TIME_LONG = 240000; // 4 min
export const WAIT_TIME_EX_LONG = 600000; // 10 min
export const DAY = 86400000;

export const appNamePrefix = 'a' + Date.now();
export const getStorage = ClientFunction(() => localStorage.getItem("PORTAL_STATE"));
export const enableDetailedLogs = ClientFunction(() => {
  console.log("enabling detailed logs")
  window.enableDetailedLogs()

});

export const waitTillWorkspace = async (t: TestController) => {
  await t.expect(getElementFromSelectorTestId("setting-up-workspace").exists).notOk({ timeout: WAIT_TIME_LONG })
}


/**
 * Checks if the Performance Drill Down is loading. It does so by checking all 3 parameters
 * isLoading, isAdvanceLoading and isBannerDataLoading in the local storage of the browser.
 *
 * @returns true if at least one parameter specified in the description above is true
 */
export const isPerformanceDrillDownLoading = async () => {
  const localStorageContent = await getStorage();
  const { obsViewState: { analysisInfo: { isLoading } } } : { obsViewState: { analysisInfo: { isLoading: boolean } } } = JSON.parse(localStorageContent);
  const { obsViewState: { analysisInfo: { isAdvanceLoading } } }: { obsViewState: { analysisInfo: { isAdvanceLoading: boolean } } } = JSON.parse(localStorageContent);
  const { obsViewState: { analysisInfo: { isBannerDataLoading } } }: { obsViewState: { analysisInfo: { isBannerDataLoading: boolean } } } = JSON.parse(localStorageContent);
  return (isLoading || isAdvanceLoading || isBannerDataLoading);
}

/**
 * Create name for app.
 *
 * @returns true name for a new app
 */
export const generateAppName = (name: string) => {
  return appNamePrefix + "-" + name;
}

/**
 * Create name for api.
 *
 * @returns true name for a new api
 */
export const generateApiName = (name: string) => {
  return appNamePrefix + name;
}

/**
 * Waits for the Performance Drill Down to complete loading. It does so by waiting for isPerformanceDrillDownLoading()
 * to return false within 25 attempts
 *
 * @param t - Test Controller
 */
export const waitForPerformanceDrillDown = async (t: TestController) => {
  let attempt = 1;
  let ss = await isPerformanceDrillDownLoading();

  while (ss === true && attempt <= 25) {
    await t.wait(1000);
    logger.info("Waiting for Performance Drill Down to load...");
    ss = await isPerformanceDrillDownLoading();
    attempt++;
  }
}

export const goToApiListView = async (t: TestController) => {
  await t.click(getElementFromSelectorTestId("apis-tab"), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await t.expect(getElementFromSelectorTestId("apis-tab").hasClass("Mui-selected")).ok();
  logger.info("Go to API tab successful!");
}

export const openApi = async (t: TestController, name: string) => {
  await searchApis(t, name);
  await t.click(screen.findByText(name), { speed: 0.5 });
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  logger.info("Open API successful!");
}

export const isOldApp = (name: string) => {
  if (name.length > 13) {
    let timestamp = Number(name.substring(1,14));
    if (!isNaN(timestamp)) {
      let currentTime = Date.now();
      if ((currentTime - timestamp) < DAY*7) {
        return false;
      }
    }
  }
  return true;
}

export const createNewApp = async (t: TestController, name: string) => {
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  await t.expect(getElementFromSelectorTestId("applications-tab").hasClass("Mui-selected")).ok();
  logger.info("Page loaded successfully");

  logger.info("Creating a new application with name : " + name);
  let newApplicationButtonExists = await getElementFromSelectorTestId("create-application-btn").exists;

  if (newApplicationButtonExists) {
    await t.click(getElementFromSelectorTestId("create-application-btn"));
  } else {
    await t.click(getElementFromSelectorTestId("create-with-choreo"));
  }

  await t
    .typeText(getElementFromSelectorTestId("application-name"), name)
    .click(Selector("#create-with-choreo-btn"));
  await waitTillWorkspace(t);
  await t.expect(Selector(".diagram-canvas").exists).ok({ timeout: WAIT_TIME_SHORT });
  logger.info("Application created successfully with name: " + name);
};

export const undeployApp = async (t: TestController, name: string, strict: boolean) => {
  // Check if apps are listed
  let appsExist = await Selector(".MuiTableRow-root.MuiTableRow-hover").exists;
  if (strict) {
    await t.expect(appsExist).ok();
  }

  if (appsExist) {
    await searchApps(t, name);

    appsExist = await Selector(".MuiTableRow-root.MuiTableRow-hover").exists;
    if (strict) {
      await t.expect(Selector(".MuiTableRow-root.MuiTableRow-hover").count).eql(1, "Only one app should exists");
    }

    if (appsExist) {
      let app = await Selector(".MuiTableRow-root.MuiTableRow-hover");
      if (strict) {
        let appName = await app.child("td").nth(0).textContent; 
        await t.expect(appName).eql(name, "App name mismatch");
      }
      
      let activeStatus = await app.child("td").nth(2).textContent;
      
      if (activeStatus == "Active") {
        logger.info("Undeploying the application: " + name);

        await t.click(app);
        await t.click(getElementFromSelectorTestId("deploy"))
        await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
        await t.click(screen.getByText("Stop"))
          .expect(getElementFromSelectorTestId("deploy-ok").exists).notOk({timeout: WAIT_TIME_EX_LONG});

        await goBacktoAppsList(t);
        
        if (strict) {
          await searchApps(t, name);
          await t
            .expect(Selector(".MuiTableRow-root.MuiTableRow-hover").child("td").nth(2).textContent)
            .notEql("Active", "App should be undeployed.");
        }
      }
    }

    await resetAppSearch(t);
  }
}

export const deleteApp = async (t: TestController, name: string, strict: boolean) => {
  // Undeploy the app if active.
  await undeployApp(t, name, strict);

  // Check if apps are listed
  let appsExist = await Selector(".MuiTableRow-root.MuiTableRow-hover").exists;
  if (strict) {
    await t.expect(appsExist).ok();
  }

  if (appsExist) {
    await searchApps(t, name);

    appsExist = await Selector(".MuiTableRow-root.MuiTableRow-hover").exists;
    if (strict) {
      await t.expect(Selector(".MuiTableRow-root.MuiTableRow-hover").count).eql(1, "Only one app should exists");
    }

    if (appsExist) {
      let app = await Selector(".MuiTableRow-root.MuiTableRow-hover");
      if (strict) {
        let appName = await app.child("td").nth(0).textContent;
        await t.expect(appName).eql(name, "App name mismatch");
      }
    
      logger.info("Deleting the application: " + name);
      await t
        .hover(app)
        .click(getElementFromSelectorTestId("delete-btn"))
        .click(getElementFromSelectorTestId("delete-app"))
        .expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });

      appsExist = await Selector(".MuiTableRow-root.MuiTableRow-hover").exists;

      if (strict && appsExist) {
        await searchApps(t, name);
        await t.expect(Selector(".MuiTableRow-root.MuiTableRow-hover").exists).notOk("App should be deleted.");
      }
    }

    await resetAppSearch(t);
  }
}

export const goBacktoAppsList = async(t: TestController) => {
  await t.click(getElementFromSelectorTestId("app-list-btn"));
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  logger.info("Apps page loaded successfully");
}

export const searchApps = async (t: TestController, name: string) => {
  let searchButtonExists = await getElementFromSelectorTestId("search-btn").exists;
  if (searchButtonExists) {
    await t.hover(getElementFromSelectorTestId("search-btn"));
  }
  await t
    .selectText(Selector(".MuiInputBase-input.MuiInput-input"))
    .pressKey("delete")
    .typeText(
      Selector(".MuiInputBase-input.MuiInput-input"),
      name,
      { speed: 0.5 }
    );
}

export const resetAppSearch = async (t: TestController) => {
  let searchButtonExists = await getElementFromSelectorTestId("search-btn").exists;
  if (searchButtonExists) {
    await t.hover(getElementFromSelectorTestId("search-btn"));
  }
  let searchBoxExists = await Selector(".MuiInputBase-input.MuiInput-input").exists;
  if (searchBoxExists) {
    await t
      .selectText(Selector(".MuiInputBase-input.MuiInput-input"))
      .pressKey("delete");
  }
}

export const goBacktoApisList = async(t: TestController) => {
  await t.click(getElementFromSelectorTestId("api-list"));
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  logger.info("Apis page loaded successfully");
}

export const searchApis = async (t: TestController, name: string) => {
  let searchButtonExists = await getElementFromSelectorTestId("api-search-btn").exists;
  if (searchButtonExists) {
    await t.hover(getElementFromSelectorTestId("api-search-btn"));
  }
  await t
    .selectText(Selector("input[aria-label='Search']"))
    .pressKey("delete")
    .typeText(
      Selector("input[aria-label='Search']"),
      name,
      { speed: 0.5 }
    );
}

export const resetApiSearch = async (t: TestController) => {
  let searchButtonExists = await getElementFromSelectorTestId("api-search-btn").exists;
  if (searchButtonExists) {
    await t.hover(getElementFromSelectorTestId("api-search-btn"));
  }
  let searchBoxExists = await Selector("input[aria-label='Search']").exists;
  if (searchBoxExists) {
    await t
      .selectText(Selector("input[aria-label='Search']"))
      .pressKey("delete");
  }
}

export const clearAPIDocumentsIfExists = async (t: TestController) => {
  let documentExists = await screen.queryAllByTestId('delete-document').exists;
  let retryCount = 5;
  while (documentExists && retryCount > 0) {
    logger.info("A document exists, deleting that document");
    // Trigger the delete button
    await t.expect(screen.findAllByTestId("delete-document").exists).ok();
    await t.click(screen.queryAllByTestId("delete-document").nth(0), { speed: 0.5 });
    // Check delete confirmation dialog
    await t.expect(screen.findByText("Delete Document").exists).ok();
    await t.expect(screen.findByText("Delete").exists).ok();
    await t.click(screen.findByText("Delete"), { speed: 0.5 });
    await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });

    documentExists = await screen.queryAllByTestId('delete-document').exists;
    retryCount = retryCount - 1;
  }
  logger.info("Documents deleted successfully!");
};

/**
 * Selects the Application trigger
 *
 * @param t - Test Controller
 * @param type - Trigger type ("Manual", "Webhook")
 * @param [relativePath] - Relative Path to be used with Webhook Trigger
 *
 */
export const selectTrigger = async (t: TestController, type: string, relativePath?: string) => {
  const webhookSourceFields = ['import ballerina/http;', 'service on new http:Listener(8090) {',`resource function get ${relativePath}(http:Caller caller, http:Request req) {` ]
  await waitTillWorkspace(t);
  switch (type) {
    case "Manual":
      await t.click(screen.findByText("Webhook"));
      break;
    case "API":
      await t
        .click(getElementFromSelectorTestId("api-trigger"))
        .expect(getElementFromSelectorTestId("api-path").exists).ok({ timeout: WAIT_TIME_MEDIUM })
        .typeText(getElementFromSelectorTestId("api-path"), relativePath, { speed: 0.5 })
        .click(getElementFromSelectorTestId("save-btn"), { speed: 0.5 });
      break;
  }
  await t
  .expect(screen.findAllByTestId("diagram-loader").exists).notOk({timeout: WAIT_TIME_LONG});
  await checkSourceCodeForValidation(t,webhookSourceFields)
  logger.info("selected " + type + "trigger type");
};

export const createProperty = async (t: TestController, type: string, name: string, expression: string) => {
  const variableSourceFields = [type, name, '=',expression]

  logger.info("Creating the variable with expression : " + expression);
  await t
    .expect(getElementFromSelectorTestId("statement-options").exists).ok()
    .click(getElementFromSelectorTestId("statement-options"), { speed: 0.5 })
    .hover(getElementFromSelectorTestId("addVariable"), { speed: 0.5 })
    .click(getElementFromSelectorTestId("addVariable"), { speed: 0.5 })
    .click(screen.getByTestId("undefinedvar"), { speed: 0.5 })
    .click(Selector('li').withAttribute('data-value',type))
    .selectText(within(getElementFromSelectorTestId('variable-name')).getByRole('textbox'))
    .pressKey("delete")
    .typeText(
      getElementFromSelectorTestId("variable-name"),
      name,
      { speed: 0.5 }
    );
  if (type == "string"){
    await t
      .click(Selector('.exp-editor .monaco-editor .view-line').nth(0))
      .wait(3000)
      .pressKey("backspace backspace");
  }
  await t
    .click(Selector('.exp-editor .monaco-editor .view-line').nth(0))
    .typeText(
      Selector('.exp-editor .monaco-editor .inputarea').nth(0),
      expression,
      { speed: 0.5 }
    )
    .expect(getElementFromSelectorTestId("save-btn").parent().parent().hasAttribute('disabled')).notOk( {timeout: WAIT_TIME_LONG})
    .click(getElementFromSelectorTestId("save-btn"))
    .expect(getElementFromSelectorTestId("diagram-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await checkSourceCodeForValidation(t,variableSourceFields)

  logger.info("Successfully created the variable with expression : " + expression);
};


export const deployToChoreo = async (t: TestController, appName: string) => {
  logger.info('Deploying app to Choreo');
  await t.wait(WAIT_TIME_SHORT);

  await t.click(getElementFromSelectorTestId('deploy'))
  await t.expect(Selector("#backdrop-loader").exists).notOk({timeout: WAIT_TIME_SHORT});
  await t.expect(await getLocation()).contains("app/" + appName + "/deploy", {timeout: WAIT_TIME_SHORT})

  logger.info("Succesfully Navigated to Deploy view")

  logger.info("Deploying application...")
  await t.wait(WAIT_TIME_SHORT);
  await t.expect(getElementFromSelectorTestId("deploy-btn").exists).ok({timeout:WAIT_TIME_MEDIUM})
      .click(getElementFromSelectorTestId("deploy-btn"), {speed: 0.5})
      .expect(getElementFromSelectorTestId("checkout-loading").exists).ok({timeout: WAIT_TIME_MEDIUM})
      .expect(getElementFromSelectorTestId("checkout-failed").exists).notOk({timeout: WAIT_TIME_EX_LONG})
      .expect(getElementFromSelectorTestId("checkout-ok").exists).ok({timeout: WAIT_TIME_EX_LONG})
  logger.info("Checkout phase successful!")


  logger.info("Starting build phase...")
  await t.expect(getElementFromSelectorTestId("build-loading").exists).ok({timeout: WAIT_TIME_SHORT})
      .expect(getElementFromSelectorTestId("build-loading").exists).notOk({timeout: WAIT_TIME_EX_LONG}) // todo - increase timeout
      .expect(getElementFromSelectorTestId("build-failed").exists).notOk({timeout: WAIT_TIME_MEDIUM})
      .expect(getElementFromSelectorTestId("build-ok").exists).ok({timeout: WAIT_TIME_EX_LONG});
  logger.info("Build phase successful!");

  await t.expect(getElementFromSelectorTestId("test-ok").exists).ok({timeout: WAIT_TIME_LONG})
  logger.info("Test phase successful!");

  logger.info("Starting deploy phase...");
  await t
      .expect(getElementFromSelectorTestId("deploy-loading").exists).ok({timeout: WAIT_TIME_SHORT})
      .expect(getElementFromSelectorTestId("deploy-loading").exists).notOk({timeout: WAIT_TIME_EX_LONG})
      .expect(getElementFromSelectorTestId("deploy-failed").exists).notOk({timeout: WAIT_TIME_MEDIUM})
      .expect(getElementFromSelectorTestId("deploy-ok").exists).ok({timeout: WAIT_TIME_EX_LONG})
  logger.info("Deploy phase successful!")
  await t.expect(getElementFromSelectorTestId("deploy-url").find("input").getAttribute('value')).notEql('',{timeout:WAIT_TIME_MEDIUM})
  const appURL = await getElementFromSelectorTestId("deploy-url").find("input").getAttribute('value');
  logger.info("test url : " + appURL);
  await t.expect(appURL.includes("https://")).ok();
  return appURL
};


export const createLog = async (t: TestController, logType: string, expression: string) => {
  const logSourceField = [`log:print("${expression}");`]

  logger.info(`Creating a log for type ${logType} with content '${expression}'`);
  await t
      .click(Selector("#SmallPlus"), {speed: 0.5})
      .click(Selector("#Plus_a"), {speed: 0.5})
      .expect(getElementFromSelectorTestId("statement-options").exists).ok()
      .click(getElementFromSelectorTestId("statement-options"), {speed: 0.5})
      .hover(screen.findByText("Log"), {speed: 0.5})
      .click(screen.findByText("Log"), {speed: 0.5})
      .click(getElementFromSelectorTestId("Info"), {speed: 0.5})
      .click(screen.getAllByText(logType).nth(1), {speed: 0.5})
      .click(Selector('.exp-editor .monaco-editor .view-line').nth(0))
      .pressKey('backspace backspace')
      .typeText(
          Selector('.exp-editor .monaco-editor .inputarea').nth(0),
          `"${expression}"`,
          {speed: 0.5}
      );
  await t.expect(Selector('[data-testid="log-save-btn"]').parent().parent().hasAttribute('disabled')).notOk({timeout: WAIT_TIME_LONG})
      .click(getElementFromSelectorTestId("log-save-btn"))
      .expect(getElementFromSelectorTestId("diagram-loader").exists).notOk({timeout: WAIT_TIME_LONG});
  await checkSourceCodeForValidation(t,logSourceField)

  logger.info(`Successfully added log type ${logType} with expression : ${expression}`);
};

export const createRespond = async (t: TestController, expression: string) => {
  const responseSourceFields = [`checkpanic caller->respond(${expression});`]

  await t
    .click(Selector("#SmallPlus"), { speed: 0.5 })
    .click(Selector("#Plus_a"), { speed: 0.5 })
    .expect(getElementFromSelectorTestId("statement-options").exists).ok({ timeout: 10000 })
    .click(getElementFromSelectorTestId("statement-options"), { speed: 0.5 })
    .hover(getElementFromSelectorTestId("addrespond"), { speed: 0.5 })
    .click(getElementFromSelectorTestId("addrespond"), { speed: 0.5 })
    .click(Selector('.exp-editor .monaco-editor .view-line').nth(0))
    .typeText(
      Selector('.exp-editor .monaco-editor .inputarea').nth(0),
      expression,
      { speed: 0.5 }
    )
    .expect(getElementFromSelectorTestId("save-btn").parent().parent().hasAttribute('disabled')).notOk({timeout: WAIT_TIME_MEDIUM})
    .click(getElementFromSelectorTestId("save-btn"))
    .expect(getElementFromSelectorTestId("diagram-loader").exists).notOk({ timeout: WAIT_TIME_LONG });
  await checkSourceCodeForValidation(t,responseSourceFields)

  logger.info("Created respond action with variable : " + expression);
};

export const callExternalEndpoint = async (t: TestController, URL: string, attempts: number) => {
  let res = "";
  let attempt = 0
  while (res === "" && attempt <= attempts) {
    await t.wait(5000);

    try {
      logger.info("Test URL : " + (URL));
      const response = await Axios.get(URL);
      res = response.data;
    } catch (err) {
      logger.error("Error while getting the test response", err);
    }
    logger.info("Attempt : " + attempt + " calling the endpoint.. response: " + res);
    attempt++;
  }
  return res;
}

/**
 * Calling deployed app via the url
 * @param t
 * @param URL
 * @param attempts
 * @param maxFailAttempts
 */
export const callDeployedApp = async (t: TestController, URL: string, attempts: number, maxFailAttempts: number) => {
  let attempt = 1;
  let failureCount = 0
  logger.info("Calling deployed app via : " + (URL));
  return new Promise(async (resolve, reject) => {
    while (attempt <= attempts) {
      try {
        logger.info("Test URL : " + (URL));
        const response = await Axios.get(URL);
        logger.info("Response received", response.data);
        attempt++;
        logger.info("Attempt : " + attempt + " calling the endpoint.");

      } catch (err) {
        logger.error("Failing Attempt : " + attempt + " calling the endpoint.", err);
        failureCount++;
      }
      if (failureCount >= maxFailAttempts) {
        reject()
      }
      // This timeout added to avoid sampling, requests get sampled if there is not time gap leads to invalid assertion
      await t.wait(500)
    }
    resolve()
  })
}

export const callExternalEndpointPOST = async (t: TestController, URL: string, requestBody: object, attempts: number) => {
  let response;
  let attempt = 0
  while (!response && attempt <= attempts) {
    await t.wait(5000);

    try {
      logger.info("Test URL: " + (URL));
      response = await Axios.post(URL, requestBody);
    } catch (err) {
      logger.error("Error while getting the test response", err);
    }
    logger.info("Attempt: " + attempt + " calling the endpoint..");
    attempt++;
  }
  return response;
}

export const saveLogs = async (t: TestController, browserLogs: string[], networkLogs: LoggedRequest[]) => {
  fs.mkdirSync("artifacts", { recursive: true });
  fs.writeFile("artifacts/" + t.browser.name + "-" + t.testRun.test.name.split(" ").join("-") + "log.txt", browserLogs.map(value => {
    return value + " \n"
  }), (err) => {
    if (err) throw err;
    console.log("File write complete");
  })


  const logs = networkLogs.map((loggedRequest)=> {

    if(loggedRequest.request.url.includes(".js") ||
      loggedRequest.request.url.includes(".svg") ||
      loggedRequest.request.url.includes(".ico") ||
      loggedRequest.request.url.includes(".css") ||
      loggedRequest.request.url.includes(".woff2")||
      loggedRequest.request.url.includes("/track")){
      return null;
    }

    if(loggedRequest.response){
      if(loggedRequest.response.headers['content-encoding']== 'gzip'){
        const resData = (gunzipSync(loggedRequest.response.body as Buffer) as Buffer).toString();
        return {
          ...loggedRequest,
          response: {...loggedRequest.response,
            body:resData.toString()

          }
        }
      }
      return loggedRequest;
    }
    return loggedRequest;

})
  fs.writeFileSync("artifacts/" + t.browser.name + "-" + t.testRun.test.name.split(" ").join("-") + "http-log.json", JSON.stringify(logs));
}


/**
 * Creates an HTTP connector using the Low Code Editor
 *
 * @param t - Test Controller
 * @param url - URL
 * @param operation - HTTP operation
 * @param responseVariableName - Response Variable Name
 * @param [outputPayloadType] - Output Payload Type
 * @param [outputPayloadVariable] - Output Payload Variable Name
 *
 */
export const createHttpConnector = async (t: TestController, url: string, operation: string, responseVariableName: string,
                                          outputPayloadType?: string, outputPayloadVariable?: string) => {
  const httpSourceFields = [`http:Client httpEndpoint = new ("${url}");`]

  logger.info("Creating HTTP Connector...")
  await t
    .click(getElementFromSelectorTestId("api-options"), { speed: 0.5 })
    .expect(getElementFromSelectorTestId("http").exists).ok({ timeout: 10000 })
    .click(getElementFromSelectorTestId("http"), { speed: 0.5 })
    .expect(getElementFromSelectorTestId("http-save-next").visible).ok({ timeout: WAIT_TIME_MEDIUM })
    .click(Selector('.exp-editor .monaco-editor .view-line').nth(0))
    .pressKey("backspace backspace")
    .typeText(
      Selector('.exp-editor .monaco-editor .inputarea').nth(0),
      "\"" + url + "\"",
      { speed: 0.5 }
    )
    .click(screen.findByText(operation), { speed: 0.5 })
    .expect(getElementFromSelectorTestId("http-save-next").parent().parent().hasAttribute('disabled')).notOk({timeout: WAIT_TIME_MEDIUM})
    .click(getElementFromSelectorTestId("http-save-next"), { speed: 0.5 })
    // TODO : Need to add source code validation for this response variable
    .selectText(within(getElementFromSelectorTestId('response-variable-name')).getByRole('textbox'))
    .pressKey("delete")
    .typeText(within(getElementFromSelectorTestId('response-variable-name')).getByRole('textbox'), responseVariableName, { speed: 0.5 });
  if (typeof outputPayloadType !== 'undefined' && typeof outputPayloadVariable !== 'undefined') {
    await t
      .click(screen.findByText("Select Type"), { speed: 0.5 })
      .click(screen.findByText(outputPayloadType), { speed: 0.5 })
      .click(getElementFromSelectorTestId("payload-variable-name"), { speed: 0.5 })
      .selectText(getElementFromSelectorTestId("payload-variable-name"))
      .pressKey("delete")
      .typeText(getElementFromSelectorTestId("payload-variable-name"), outputPayloadVariable, { speed: 0.5 });
  } else {
    await t.click(screen.findByText("No Payload"), { speed: 0.5 });
  }
  await t.expect(getElementFromSelectorTestId("http-save-done").parent().parent().hasAttribute('disabled')).notOk( {timeout: WAIT_TIME_SHORT})
  await t.click(getElementFromSelectorTestId("http-save-done"), { speed: 0.5 })
    .expect(getElementFromSelectorTestId("diagram-loader").exists).notOk({ timeout: WAIT_TIME_LONG });
  await checkSourceCodeForValidation(t,httpSourceFields)

  logger.info("Successfully created an HTTP Connector!")
}

export const selectAPIType = async (t: TestController, name: string) => {
  await waitTillWorkspace(t);
  await t
    .click(screen.getByText("API"))
    .expect(screen.findByPlaceholderText("Relative path from host").exists).ok({ timeout: WAIT_TIME_MEDIUM })
    .typeText(screen.queryByPlaceholderText("Relative path from host"), name, { speed: 0.5 })
    .click(screen.getByText("Save API"), { speed: 0.5 })
    .expect(screen.findAllByTestId("diagram-loader").exists).notOk({ timeout: WAIT_TIME_LONG });
  logger.info("selected api app type with name : " + name);
};

export const createApiFromChoreoApp = async (t: TestController, apiName: string, appName: string) => {
  let createApiButtonExist = await getElementFromSelectorTestId("create-api-btn").exists;
  if (createApiButtonExist) {
    await t.click(getElementFromSelectorTestId("create-api-btn"));
  }

  await getElementFromSelectorTestId("create-from-choreo-app").exists;
  await t
    .click(getElementFromSelectorTestId("create-from-choreo-app"))
    .expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  await screen.findAllByText("Create API From Choreo Application");
  logger.info("Create api from choreo app form load successful!");

  // fill in details
  await getElementFromSelectorTestId("api-name").exists;
  await t.typeText(getElementFromSelectorTestId("api-name"), apiName, { speed: 0.5 });
  // api version is not input, since there's a default value: "1.0.0"
  await getElementFromSelectorTestId("choreo-app-selector").exists;
  await t.click(getElementFromSelectorTestId("choreo-app-selector"), { speed: 0.5 });
  await screen.findByText(appName).exists;
  await t.click(screen.findByText(appName), { speed: 0.5 });

  // click create
  await t.expect(screen.getByRole('button', { name: /create/i }).exists).ok();
  await t.click(screen.getByRole('button', { name: /create/i }), { speed: 0.5 });
  await t.wait(WAIT_TIME_MEDIUM);
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Created API with name: " + apiName);
};

export const addApiSimpleResponse = async (t: TestController, expression: string) => {
  await t.expect(screen.findAllByText("Respond").exists).ok({ timeout: WAIT_TIME_SHORT });
  await t
    .click(screen.getByTestId("addrespond"), { speed: 0.5 })
    .click(Selector('.exp-editor .monaco-editor .view-line').nth(0))
    .typeText(
      Selector('.exp-editor .monaco-editor .inputarea').nth(0),
      expression,
      { speed: 0.5 }
    )
    .wait(1000)
    .click(screen.getByText("Save"), { speed: 0.5 })
    .expect(screen.findAllByTestId("diagram-loader").exists).notOk({ timeout: WAIT_TIME_LONG });
  logger.info("Created API with a simple response : " + expression);
};

export const deleteApi = async (t: TestController, name: string, strict: boolean) => {
  // Check if apis are listed
  let apisExist = await Selector(".MuiTableRow-root.MuiTableRow-hover").exists;
  if (strict) {
    await t.expect(apisExist).ok();
  }

  if (apisExist) {
    await searchApis(t, name);

    apisExist = await Selector(".MuiTableRow-root.MuiTableRow-hover").exists;
    if (strict) {
      await t.expect(Selector(".MuiTableRow-root.MuiTableRow-hover").count).eql(1, "Only one api should exists");
    }

    if (apisExist) {
      let api = await Selector(".MuiTableRow-root.MuiTableRow-hover");
      if (strict) {
        let apiName = await api.child("td").nth(0).find("p").innerText;
        await t.expect(apiName).eql(name, "Api name mismatch");  
      }

      logger.info("Deleting the api: " + name);
      await t
        .hover(api)
        .click(getElementFromSelectorTestId("api-delete-btn"))
        .click(getElementFromSelectorTestId("delete-api"))
        .expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });

      apisExist = await Selector(".MuiTableRow-root.MuiTableRow-hover").exists;

      if (strict && apisExist) {
        await searchApis(t, name);
        await t.expect(Selector(".MuiTableRow-root.MuiTableRow-hover").exists).notOk("Api should be deleted.");
      }
    }

    await resetApiSearch(t);
  }
}

/**
 * Validating components by going through the source code view and checking the terms
 *
 * @param t -Test Controller
 * @param sourceLines
 */
export const checkSourceCodeForValidation = async (t: TestController, sourceLines: string[]) => {
  await t.expect(getElementFromSelectorTestId("code-view-btn").hasAttribute('disabled')).notOk({timeout: WAIT_TIME_LONG})
    .hover(Selector(".product-tour-code-view"))
    .click(Selector(".product-tour-code-view"))
    for (const sourceLine of sourceLines) {
      await t.expect(Selector(".view-line").withText(sourceLine.replace(/\s/g,'\u00a0')).exists).ok({timeout:WAIT_TIME_SHORT})
    }
  await t.hover(Selector(getElementFromSelectorTestId("vertical-close-btn")))
    .click(Selector(getElementFromSelectorTestId("vertical-close-btn")))
}

export function getElementFromSelectorTestId(testId: string): Selector {
  const selectorText = '[data-testid="' + testId + '"';
  return (Selector(selectorText));
}
