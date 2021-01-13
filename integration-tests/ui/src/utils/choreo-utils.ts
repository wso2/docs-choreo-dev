import { Selector, ClientFunction } from "testcafe";
import { screen, within } from "@testing-library/testcafe";
import { logger } from './logger';
import Axios from "axios";
import * as fs from 'fs';
import {gunzipSync} from 'zlib'


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

export const getStorage = ClientFunction(() => localStorage.getItem("PORTAL_STATE"));
export const enableDetailedLogs = ClientFunction(() => {
  console.log("enabling detailed logs")
  window.enableDetailedLogs()

});

export const isWorkspaceUp = async () => {
  const content = await getStorage();
  const { appInfo: { isWaitingOnWorkspace } }: { appInfo: { isWaitingOnWorkspace: boolean } } = JSON.parse(content);
  logger.info("waiting for workspace : " + isWaitingOnWorkspace);
  return isWaitingOnWorkspace;
}

export const waitTillWorkspace = async (t: TestController) => {
  let attempt = 0;
  let ss = await isWorkspaceUp();

  while (ss === true && attempt <= 25) {
    await t.wait(7000);
    ss = await isWorkspaceUp();
    attempt++;
  }
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


export const createNewApp = async (t: TestController, name: string) => {
  logger.info("Creating a new application with name : " + name);
  await t
    .click(screen.getAllByTestId("create-with-choreo"))
    .typeText(screen.getAllByPlaceholderText("Application name"), name)
    .click(screen.getByText("Create"));
  await waitTillWorkspace(t);
  await t.wait(10000);
  await t.expect(Selector(".diagram-canvas").exists).ok({ timeout: WAIT_TIME_SHORT });
  logger.info("Application created successfully with name: " + name);
};

export const undeployAllApps = async (t: TestController) => {
  let deployedApps = await screen.findAllByText("Active").exists;
  let retryCount = 5;
  if (deployedApps && retryCount > 0) {
    await t.click(screen.findAllByText("Active"));
    await t.click(screen.getByTitle("deploy"))
    await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
    await t.click(screen.getByText("Stop"))
      .expect(screen.findByText("Deploy").exists).ok({ timeout: WAIT_TIME_MEDIUM })
      .click(screen.getByText("App list"))
      .expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });

    deployedApps = await screen.findAllByText("Active").exists;
    retryCount = retryCount - 1;
  }  
  await t.expect(screen.findAllByText("Active").exists).notOk();
}

export const clearAppsIfExists = async (t: TestController) => {
  await undeployAllApps(t);
    let appExists = await screen.queryAllByText("Time to create your first application").exists;
    let retryCount = 5;
  while (!appExists && retryCount > 0 ) {
    logger.info("An application exists, deleting the application");
    await t
      .hover(Selector(".MuiTableRow-hover"))
      .click(screen.getByText("Delete"))
      .click(within(screen.findByRole("dialog")).getByText("Delete"))
      .expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
    appExists = await screen.queryAllByText("Time to create your first application").exists;
    retryCount = retryCount - 1;
  }

  await t
    .expect(screen.queryAllByText("Time to create your first application").exists).ok({ timeout: 10000 });
  logger.info("Application deleted successfully");
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
  const webhookSourceFields = ['import','ballerina/http;', 'service', 'on', 'new', 'http:Listener(8090){']
  await waitTillWorkspace(t);
  switch (type) {
    case "Manual":
      await t.click(screen.getByText("Webhook"));
      break;
    case "API":
      await t
        .click(screen.getByText("API"))
        .expect(screen.findByPlaceholderText("Relative path from host").exists).ok({ timeout: WAIT_TIME_MEDIUM })
        .typeText(screen.queryByPlaceholderText("Relative path from host"), relativePath, { speed: 0.5 })
        .click(screen.getByText("Save API"), { speed: 0.5 });
      break;
  }
  await t
    .expect(screen.findAllByTestId("diagram-loader").exists).notOk({timeout: WAIT_TIME_LONG});
  await checkSourceCodeForValidation(t,webhookSourceFields)
  logger.info("selected " + type + "trigger type");
};


export const createProperty = async (t: TestController, name: string, expression: string) => {
  const variableSourceFields = ['var', name, '=',expression]

  logger.info("Creating the variable with expression : " + expression);
  await t
    .expect(screen.getByText("Statements").exists).ok()
    .click(screen.getByTestId("statement-options"), { speed: 0.5 })
    .hover(screen.getByText("Variable"), { speed: 0.5 })
    .click(screen.getByTestId("addVariable"), { speed: 0.5 })
    .selectText(screen.getByPlaceholderText("Enter Variable Name"))
    .pressKey("delete")
    .typeText(
      screen.getByPlaceholderText("Enter Variable Name"),
      name,
      { speed: 0.5 }
    )
    .click(Selector('.exp-editor .monaco-editor .view-line').nth(0))
    .wait(3000)
    .typeText(
      Selector('.exp-editor .monaco-editor .inputarea').nth(0),
      expression,
      { speed: 0.5 }
    )
    .wait(1000)
    .click(screen.getByText("Save"))
    .expect(screen.findByTestId("diagram-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  await checkSourceCodeForValidation(t,variableSourceFields)

  logger.info("Successfully created the variable with expression : " + expression);
};

export const createRespond = async (t: TestController, expression: string) => {
  const responseSourceFields = ['checkpanic', ' caller', '->','respond(','<','@untainted','>',expression,')']

  await t
    .click(Selector("#SmallPlus"), { speed: 0.5 })
    .click(Selector("#Plus_a"), { speed: 0.5 })
    .expect(screen.getByText("Statements").exists).ok({ timeout: 10000 })
    .click(screen.getByTestId("statement-options"), { speed: 0.5 })
    .hover(screen.getByText("Respond"), { speed: 0.5 })
    .click(screen.getByTestId("addrespond"), { speed: 0.5 })
    .click(Selector('.exp-editor .monaco-editor .view-line').nth(0))
    .wait(3000)
    .typeText(
      Selector('.exp-editor .monaco-editor .inputarea').nth(0),
      expression,
      { speed: 0.5 }
    )
    .wait(1000)
    .click(screen.getByText("Save"))
    .expect(screen.findByTestId("diagram-loader").exists).notOk({ timeout: WAIT_TIME_LONG });
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
  logger.info("Creating HTTP Connector...")
  await t
    .click(screen.getByTestId("api-options"), { speed: 0.5 })
    .expect(screen.getByText("Http").exists).ok({ timeout: 10000 })
    .click(screen.getByText("Http"), { speed: 0.5 })
    .click(Selector('.exp-editor .monaco-editor .view-line').nth(0))
      .wait(3000)
      .pressKey("backspace backspace")
      .typeText(
        Selector('.exp-editor .monaco-editor .inputarea').nth(0),
        "\"" + url + "\"",
        { speed: 0.5 }
      )
    .wait(1000)
    .click(screen.getByText(operation), { speed: 0.5 })
    .click(screen.getByText("Next"), { speed: 0.5 })
    .selectText(screen.getByPlaceholderText("Enter Response Variable Name"))
    .pressKey("delete")
    .typeText(screen.getByPlaceholderText("Enter Response Variable Name"), responseVariableName, { speed: 0.5 });
  if (typeof outputPayloadType !== 'undefined' && typeof outputPayloadVariable !== 'undefined') {
    await t
      .click(screen.getByText("Select Type"), { speed: 0.5 })
      .click(screen.getByText(outputPayloadType), { speed: 0.5 })
      .click(screen.getByPlaceholderText("Enter Payload Variable Name"), { speed: 0.5 })
      .selectText(screen.getByPlaceholderText("Enter Payload Variable Name"))
      .pressKey("delete")
      .typeText(screen.getByPlaceholderText("Enter Payload Variable Name"), outputPayloadVariable, { speed: 0.5 });
  } else {
    await t.click(screen.getByText("No Payload"), { speed: 0.5 });
  }
  await t.click(screen.getByText("Save & Done"), { speed: 0.5 })
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
  await screen.getAllByTestId("create-from-choreo-app").exists;
  await t
    .click(screen.getAllByTestId("create-from-choreo-app"))
    .expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
  await screen.findAllByText("Create API From Choreo Application");
  logger.info("Create api from choreo app form load successful!");

  // fill in details
  await screen.findByTestId("api-name").exists;
  await t.typeText(screen.findByTestId("api-name"), apiName, { speed: 0.5 });
  // api version is not input, since there's a default value: "1.0.0"
  await screen.findByTestId("choreo-app-selector").exists;
  await t.click(screen.findByTestId("choreo-app-selector"), { speed: 0.5 });
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
    .click(screen.getByText("Respond"))
    .typeText(
      Selector('.exp-editor .monaco-editor .inputarea').nth(0),
      expression,
      { speed: 0.5 }
    )
    .click(screen.getByText("Save"), { speed: 0.5 })
    .expect(screen.findAllByTestId("diagram-loader").exists).notOk({ timeout: WAIT_TIME_LONG });
  logger.info("Created API with a simple response : " + expression);
};

export const clearApisIfExists = async (t: TestController) => {
  let apiExists = await screen.queryAllByText("Time to create your first API").exists;
  let retryCount = 5;
  while (!apiExists && retryCount > 0) {
    logger.info("An api exists, deleting the application");
    await t
      .hover(Selector(".MuiTableRow-hover"))
      .click(screen.getByText("Delete"))
      .click(within(screen.findByRole("dialog")).getByText("Delete"))
      .expect(Selector("#backdrop-loader").exists).notOk({ timeout: WAIT_TIME_SHORT });
    apiExists = await screen.queryAllByText("Time to create your first API").exists;
    retryCount = retryCount - 1;
  }
  await t
    .expect(screen.queryAllByText("Time to create your first API").exists).ok({ timeout: 10000 });
  logger.info("APIs deleted successfully");
};

/**
 * Validating components by going through the source code view and checking the terms
 *
 * @param t -Test Controller
 * @param terms - Terms need to verify
 */
export const checkSourceCodeForValidation = async (t: TestController, terms: string[]) => {
  await t.expect(screen.getByTestId("code-view-btn").hasAttribute('disabled')).notOk({timeout: WAIT_TIME_LONG})
    .hover(Selector(".product-tour-code-view"))
    .click(Selector(".product-tour-code-view"))
  let sourceCodeStr = []
  const count = await Selector(".view-line").find('span>span').count
  for (let i = 0; i < count; i++) {
    const text = await Selector(".view-line").find('span>span').nth(i).innerText
    // Trim nbsp
    sourceCodeStr.push(text.replace(/\s/g, ''))
  }
  for (const term of terms) {
    await t.expect(sourceCodeStr).contains(term.replace(' ', ''));
  }
  await t.hover(Selector(screen.getByTestId("vertical-close-btn")))
    .click(Selector(screen.getByTestId("vertical-close-btn")))
}
