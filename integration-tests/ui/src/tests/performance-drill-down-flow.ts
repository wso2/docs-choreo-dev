import { Selector, RequestLogger } from "testcafe";
import { screen } from "@testing-library/testcafe";
import { getLocation } from "../utils/login-utils";
import page from "../model/page";
import * as config from "../../testcafe-run-config.json";
import { createNewApp, createHttpConnector, goBacktoAppsList, getStorage, 
  waitForPerformanceDrillDown, selectTrigger, generateAppName, WAIT_TIME_SHORT,
  saveLogs, enableDetailedLogs, deleteApp } from "../utils/choreo-utils";
import { logger } from '../utils/logger'


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

fixture("Performance Analyzer")
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

test ("Performance Drill Down test", async (t) => {
  const ENDPOINT = config.performanceAnalyzerTest.endpoint;
  const EXPECTED_BANNER_TPS = config.performanceAnalyzerTest.expectedBannerTps;
  const EXPECTED_BANNER_LATENCY = config.performanceAnalyzerTest.expectedBannerLatency;
  const EXPECTED_GRAPH_DATA_LATENCY: number[] = config.performanceAnalyzerTest.expectedGraphDataLatency;
  const EXPECTED_GRAPH_DATA_TPS: number[] = config.performanceAnalyzerTest.expectedGraphDataTps;

  logger.info("Starting Performance Analyzer Performance Drill Down test...");

  const appName = generateAppName("analyzer");
  await createNewApp(t, appName);
  await t.expect(await getLocation()).contains("app/" + appName + "/develop", { timeout: WAIT_TIME_SHORT });
  await selectTrigger(t, "API", "test");

  await createHttpConnector(t,ENDPOINT, "GET", "response");
  await t.wait(WAIT_TIME_SHORT);

  await t.click(screen.getByTestId("analyze-btn"), { speed: 0.5 })
  await waitForPerformanceDrillDown(t);
  const localStorageContent = await getStorage();
  logger.info("Testing Performance Drill Down Banner data...");
  const { obsViewState: { analysisInfo: { bannerData: { tps } } } }: { obsViewState: { analysisInfo: { bannerData: { tps: number } } } } = JSON.parse(localStorageContent);
  const { obsViewState: { analysisInfo: { bannerData: { latency } } } }: { obsViewState: { analysisInfo: { bannerData: { latency: number } } } } = JSON.parse(localStorageContent);
  await t.expect(tps).eql(EXPECTED_BANNER_TPS);
  logger.info("Expected banner TPS matches actual TPS (TPS(req/s): " + tps + ")");
  await t.expect(latency).eql(EXPECTED_BANNER_LATENCY);
  logger.info("Expected banner latency matches actual latency (Latency(ms): " + latency + ")");

  logger.info("Testing Performance Drill Down graph data...");
  const { obsViewState: { analysisInfo: { graphData } } }: { obsViewState: { analysisInfo: { graphData: object[] } } }  = JSON.parse(localStorageContent);
  var actualTps, actualLatency, concurrency, thinkTime;
  for (let i=0; i<15; i++) {
    actualTps = graphData[i]["tps"];
    actualLatency = graphData[i]["latency"];
    concurrency = graphData[i]["concurrency"];
    thinkTime = graphData[i]["thinkTime"];

    await t.expect(actualLatency).eql(EXPECTED_GRAPH_DATA_LATENCY[i]);
    await t.expect(actualTps).eql(EXPECTED_GRAPH_DATA_TPS[i]);
    logger.info("Expected and actual graph data values for concurrency " + concurrency
                 + " and thinkTime " + thinkTime + " match!"
                 + " [TPS(req/s): " + actualTps + ", "
                 + "Latency(ms): " + actualLatency + "]");
  }
  logger.info("Performance Analyzer Performance Drill Down test completed successfully!");
  await t.wait(WAIT_TIME_SHORT);

  await goBacktoAppsList(t);
  await deleteApp(t, appName);
});
