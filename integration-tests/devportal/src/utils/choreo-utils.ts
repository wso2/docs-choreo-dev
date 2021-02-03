import { ClientFunction } from "testcafe";
import { screen } from "@testing-library/testcafe";
import * as fs from 'fs';
import { gunzipSync } from 'zlib'
import { Category, CategoryServiceFactory, CategoryConfiguration, LogLevel } from "typescript-logging";
import * as config from '../../testcafe-user-config.json';

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

export const getLocation = ClientFunction(() => document.location.href);
CategoryServiceFactory.setDefaultConfiguration(new CategoryConfiguration(LogLevel.Info));
export const logger = new Category("choreo");

export const saveLogs = async (t: TestController, browserLogs: string[], networkLogs: LoggedRequest[]) => {
  fs.mkdirSync("artifacts", { recursive: true });
  fs.writeFile("artifacts/" + t.browser.name + "-" + t.testRun.test.name.split(" ").join("-") + "log.txt", browserLogs.map(value => {
    return value + " \n"
  }), (err) => {
    if (err) throw err;
    console.log("File write complete");
  })


  const logs = networkLogs.map((loggedRequest) => {

    if (loggedRequest.request.url.includes(".js") ||
      loggedRequest.request.url.includes(".svg") ||
      loggedRequest.request.url.includes(".ico") ||
      loggedRequest.request.url.includes(".css") ||
      loggedRequest.request.url.includes(".woff2") ||
      loggedRequest.request.url.includes("/track")) {
      return null;
    }

    if (loggedRequest.response) {
      if (loggedRequest.response.headers['content-encoding'] == 'gzip') {
        const resData = (gunzipSync(loggedRequest.response.body as Buffer) as Buffer).toString();
        return {
          ...loggedRequest,
          response: {
            ...loggedRequest.response,
            body: resData.toString()

          }
        }
      }
      return loggedRequest;
    }
    return loggedRequest;

  })
  fs.writeFileSync("artifacts/" + t.browser.name + "-" + t.testRun.test.name.split(" ").join("-") + "http-log.json", JSON.stringify(logs));
}

export const devportalLogin = async (t: TestController) => {
  logger.info("Initiating developer portal login");
  await t.typeText(screen.getByPlaceholderText(/username/i), config.idpUsername, { speed: 0.5 });
  await t.typeText(screen.getByPlaceholderText(/password/i), config.apimPassword, { speed: 0.5 });
  await t.expect(screen.getByRole('button', { name: /continue/i }).exists).ok();
  await t.click(screen.getByRole('button', { name: /continue/i }), { speed: 0.5 });
  await t.expect(await getLocation()).contains("/sign-in", { timeout: WAIT_TIME_SHORT });

  logger.info("Verify if the test API loaded successfully");
  await t.expect(screen.findByText(/EndToEndTestApi/i).exists).ok();

  logger.info("Devportal login successful");
}
