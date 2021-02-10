import { RequestLogger, Selector } from "testcafe";
import * as config from "../../testcafe-run-config.json";
import { screen } from "@testing-library/testcafe";
import page from "../model/page";
import {
  enableDetailedLogs, saveLogs, appNamePrefix, isOldApp, deleteApp, deleteApi, goToApiListView
} from "../utils/choreo-utils";

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

fixture("Cleaning up")
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

test.meta({'stable': "true", 'unstable': "true"})("Delete Apps and APIs that are old or created by this run", async (t) => {
  
  let appsToBeDeleted = [];
  while (true) {
    let apps = await Selector(".MuiTableRow-root.MuiTableRow-hover");
    let appCount = await apps.count;
    if (appCount == 0) {
      break;
    }

    for (let index = 0; index < appCount; index++) {
      let app = await apps.nth(index);
      let appName = await app.child("td").nth(0).textContent;
      if (isOldApp(appName) || appName.startsWith(appNamePrefix)) {
        appsToBeDeleted.push(appName);
      }
    }

    let nextButtonDisabled = await screen.getByText("chevron_right").parent().parent().hasClass("Mui-disabled");
    if (nextButtonDisabled) {
      break;
    }
    await t.click(screen.getByText("chevron_right").parent().parent(), { speed: 0.5 });
  }

  for (let index = 0; index < appsToBeDeleted.length; index++) {
    await deleteApp(t, appsToBeDeleted[index], false);
  }
  
  await goToApiListView(t);

  let apisToBeDeleted = [];
  while (true) {
    let apis = await Selector(".MuiTableRow-root.MuiTableRow-hover");
    let apiCount = await apis.count;
    if (apiCount == 0) {
      break;
    }

    for (let index = 0; index < apiCount; index++) {
      let api = await apis.nth(index);
      let apiName = await api.child("td").nth(0).find("p").innerText
      if (isOldApp(apiName) || apiName.startsWith(appNamePrefix)) {
        apisToBeDeleted.push(apiName);
      }
    }

    let nextButtonDisabled = await screen.getByText("chevron_right").parent().parent().hasClass("Mui-disabled");
    if (nextButtonDisabled) {
      break;
    }
    await t.click(screen.getByText("chevron_right").parent().parent(), { speed: 0.5 });
  }

  for (let index = 0; index < apisToBeDeleted.length; index++) {
    await deleteApi(t, apisToBeDeleted[index], false);
  }
});
