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

export const selectWebhookType = async (t: TestController, name: string) => {
  await waitTillWorkspace(t);
  await t
    .click(screen.getByText("Webhook"))
    .expect(screen.findByPlaceholderText("Relative path from host").exists).ok({ timeout: WAIT_TIME_MEDIUM })
    .typeText(screen.queryByPlaceholderText("Relative path from host"), name, { speed: 0.5 })
    .click(screen.getByText("Save"), { speed: 0.5 })
    .expect(screen.findAllByTestId("diagram-loader").exists).ok({ timeout: WAIT_TIME_SHORT })
    .expect(screen.findAllByTestId("diagram-loader").exists).notOk({ timeout: WAIT_TIME_LONG });
  logger.info("selected webhook app type with name : " + name);
};

export const createProperty = async (t: TestController, name: string, expression: string) => {
  logger.info("Creating the property with expression : " + expression);
  await t
    .expect(screen.getByText("Statements").exists).ok()
    .click(screen.getByTestId("statement-options"), { speed: 0.5 })
    .hover(screen.getByText("Property"), { speed: 0.5 })
    .click(screen.getByTestId("addproperty"), { speed: 0.5 })
    .selectText(screen.getByPlaceholderText("Enter Property Name"))
    .pressKey("delete")
    .typeText(
      screen.getByPlaceholderText("Enter Property Name"),
      name,
      { speed: 0.5 }
    )
    .click(screen.getByText("Define Expression"), { speed: 0.5 })
    .typeText(
      screen.getByPlaceholderText('eg: "Hello world"'),
      expression,
      { speed: 0.5 }
    )
    .click(screen.getByText("Save"))
    .expect(screen.findByTestId("diagram-loader").exists).ok({ timeout: WAIT_TIME_SHORT })
    .expect(screen.findByTestId("diagram-loader").exists).notOk({ timeout: WAIT_TIME_MEDIUM });
  logger.info("Successfully created the property with expression : " + expression);
};

export const createRespond = async (t: TestController, expression: string) => {
  await t
    .expect(screen.queryAllByText("Statements").exists).ok({ timeout: 10000 })
    .click(Selector("#SmallPlus"), { speed: 0.5 })
    .click(Selector("#Plus_a"), { speed: 0.5 })
    .expect(screen.queryAllByText("Loading").exists).notOk({ timeout: 20000 })
    .click(screen.getByTestId("statement-options"), { speed: 0.5 })
    .hover(screen.getByText("Respond"), { speed: 0.5 })
    .click(screen.getByTestId("addrespond"), { speed: 0.5 })
    .typeText(
      screen.getByPlaceholderText('eg: "Executed successfully!"'),
      "res",
      { speed: 0.5 }
    )
    .click(screen.getByText("Save"))
    .expect(screen.findByTestId("diagram-loader").exists).ok({ timeout: WAIT_TIME_SHORT })
    .expect(screen.findByTestId("diagram-loader").exists).notOk({ timeout: WAIT_TIME_LONG });
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
