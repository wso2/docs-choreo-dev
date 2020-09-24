import { Selector, RequestLogger } from "testcafe";
import { getByText, getByLabelText } from "@testing-library/testcafe";
import { getLocation, getAccessToken } from "../utils/login-utils";
import page from "../model/page";
import * as config from "../../testcafe-user-config.json";
import {logger} from '../utils/logger'
import { saveLogs, enableDetailedLogs } from "../utils/choreo-utils";

declare const test: TestFn;
declare global {
  interface TestController {
    testRun: {
      test: {
        name: string;
      }
    };
  }
}
const httpLogger = RequestLogger(undefined, {
  logRequestBody: true,
  logRequestHeaders: true,
  logResponseBody: true,
  logResponseHeaders: true,
  stringifyRequestBody: true
});

fixture("User flows")
  .page(config.testURL)
  .beforeEach(async () => {
    await page.login();
    await enableDetailedLogs();
  }).requestHooks(httpLogger)
    .afterEach(async t => {
        const {log,error}:BrowserConsoleMessages = await t.getBrowserConsoleMessages();
        const httpRequests = httpLogger.requests;
        const data = [...log,...error];
        saveLogs(t,data,httpRequests)
        httpLogger.clear();
});

test("user login and logout redirection", async (t) => {
  logger.info("login logout flow")
  await t
    .click(getByLabelText("account of current user"))
    .expect(getByText(config.user.email).exists).ok()
    .expect(getByText("Logout").exists).ok()
    .click(getByText("Logout")).expect(getLocation()).contains(config.testURL + "login")
    .expect(getByText("Sign in with Google").exists).ok()
    .expect(getByText("Sign in with GitHub").exists).ok();
});
