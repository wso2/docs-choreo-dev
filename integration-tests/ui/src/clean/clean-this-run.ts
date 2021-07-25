/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

import { RequestLogger, Selector } from "testcafe";
import { config } from "../../../ui/src/utils/config";
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

fixture.meta({ 'apim': "true" })("Cleaning up")
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

test.meta({'unstable': "true", 'periodic': "true"})("Delete Apps that are old or created by this run", async (t) => {
  
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
});

test.meta({'unstable': "true"})("Delete APIs that are old or created by this run", async (t) => {
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
      if ((isOldApp(apiName) || apiName.startsWith(appNamePrefix)) && !apiName.includes("EndToEndTestApi")) {
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
