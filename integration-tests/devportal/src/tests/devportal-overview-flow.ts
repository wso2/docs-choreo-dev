import { RequestLogger } from "testcafe";
import { screen } from "@testing-library/testcafe";
import * as config from "../../testcafe-run-config.json";
import { saveLogs, logger, devportalLogin } from "../utils/choreo-utils";


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

fixture("Developer Portal test")
    .page(config.testURL)
    .requestHooks(httpLogger)
    .afterEach(async t => {
        const { log, error }: BrowserConsoleMessages = await t.getBrowserConsoleMessages();
        const httpRequests = httpLogger.requests;
        const data = [...log, ...error];
        saveLogs(t, data, httpRequests)
        httpLogger.clear();
    });


test("Overview page click Try Api button", async (t) => {
    await devportalLogin(t);

    logger.info("Click on the Try Api button to switch to Try Out tab");
    await screen.getByRole('button', { name: /try api try api/i }).exists;
    await t.click(screen.getByRole('button', { name: /try api try api/i }), { speed: 0.5 });
});
