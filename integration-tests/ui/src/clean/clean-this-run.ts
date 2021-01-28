import { screen, within } from "@testing-library/testcafe";
import { RequestLogger, Selector } from "testcafe";
import * as config from "../../testcafe-run-config.json";
import page from "../model/page";
import {
  clearAppsIfExists, clearApisIfExists, enableDetailedLogs, saveLogs, WAIT_TIME_SHORT, isOldApp, deleteApp, deleteApi, goToApiListView
} from "../utils/choreo-utils";
import { logger } from '../utils/logger';

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

fixture("Cleaning up the test run")
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

test("Delete all Apps and Apis of this run", async (t) => {
  await clearAppsIfExists(t);

  await goToApiListView(t);

  await clearApisIfExists(t);
});

test.skip("Delete old Apps and Apis", async (t) => {
  
  let appsToBeDeleted = [];
  while (true) {
    let apps = await Selector(".MuiTableRow-root.MuiTableRow-hover");
    let appCount = await apps.count;

    for (let index = 0; index < appCount; index++) {
      let app = await apps.nth(index);
      let appName = await app.child("td").nth(0).textContent;
      if (isOldApp(appName)) {
        appsToBeDeleted.push(appName);
      }
    }

    let nextButtonExist = await Selector("span[title='Next'] > button").exists;
    if (!nextButtonExist) {
      break;
    }
    await t.click(Selector("span[title='Next'] > button"), { speed: 0.5 });
  }

  for (let index = 0; index < appsToBeDeleted.length; index++) {
    logger.info("Deleting application: " + appsToBeDeleted[index])
    await deleteApp(t, appsToBeDeleted[index]);
  }
  
  await goToApiListView(t);

  let apisToBeDeleted = [];
  while (true) {
    let apis = await Selector(".MuiTableRow-root.MuiTableRow-hover");
    let apiCount = await apis.count;

    for (let index = 0; index < apiCount; index++) {
      let api = await apis.nth(index);
      let apiName = await api.child("td").nth(0).textContent;
      if (isOldApp(apiName)) {
        apisToBeDeleted.push(apiName);
      }
    }

    let nextButtonExist = await Selector("span[title='Next'] > button").exists;
    if (!nextButtonExist) {
      break;
    }
    await t.click(Selector("span[title='Next'] > button"), { speed: 0.5 });
  }

  for (let index = 0; index < apisToBeDeleted.length; index++) {
    logger.info("Deleting api: " + apisToBeDeleted[index])
    await deleteApi(t, apisToBeDeleted[index]);
  }
});
