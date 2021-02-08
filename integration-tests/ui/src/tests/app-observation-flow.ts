import {Selector, RequestLogger} from "testcafe";
import {screen} from "@testing-library/testcafe";
import {getLocation} from "../utils/login-utils";
import page from "../model/page";
import * as config from "../../testcafe-run-config.json";
import {
    createNewApp,
    clearAppsIfExists,
    createProperty,
    createRespond,
    WAIT_TIME_SHORT,
    WAIT_TIME_MEDIUM,
    WAIT_TIME_EX_LONG,
    WAIT_TIME_LONG,
    saveLogs,
    enableDetailedLogs,
    createHttpConnector, callDeployedApp, createLog, deployToChoreo, selectTrigger
} from "../utils/choreo-utils";
import {logger} from '../utils/logger'


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

const appName = "sampleapi-" + Math.random().toString(36).substr(2, 5);

fixture("Application observability")
    .page(config.testURL)
    .beforeEach(async t => {
        await page.login();
        await enableDetailedLogs();
    })
    .requestHooks(httpLogger)
    .afterEach(async t => {
        const {log, error}: BrowserConsoleMessages = await t.getBrowserConsoleMessages();
        const httpRequests = httpLogger.requests;
        const data = [...log, ...error];
        await saveLogs(t, data, httpRequests)
        httpLogger.clear();
    });

async function deployApp(t: TestController) {
    await clearAppsIfExists(t);
    await createNewApp(t, appName);

    await t.expect(await getLocation()).contains("app/" + appName + "/develop", {timeout: WAIT_TIME_SHORT})
    await selectTrigger(t, "API", "hello");
    await createProperty(t, "res", '"hello world"');
    await t.click(Selector("#SmallPlus"), {speed: 0.5})
        .click(Selector("#Plus_a"), {speed: 0.5})
    await createHttpConnector(t, "https://postman-echo.com/get?foo1=bar1&foo2=bar2", "GET", "response");
    await createLog(t, 'Info', 'Special test Log for App');
    await createRespond(t, "res");
    const appURL = await deployToChoreo(t, appName)

    await callDeployedApp(t, `${appURL}/hello`, 3, 3)
    // TODO : Observability logs view refresh is not working, this time out is a work around
    await t.wait(WAIT_TIME_MEDIUM)

    await t.click(screen.getByTestId("observe"))
    await t.expect(Selector("#backdrop-loader").exists).notOk({timeout: WAIT_TIME_MEDIUM});

    logger.info("Succesfully Navigated to Deploy view")
}


test("test run observe overview hello world service ", async (t) => {
    await deployApp(t);
    await t.expect(Selector(".diagram-canvas").exists).ok("Diagram should be visible", {timeout: WAIT_TIME_SHORT})
        .expect(Selector(".worker-line").exists).ok("Diagram should be drawn", {timeout: WAIT_TIME_SHORT})
        .expect(Selector('#resource-path').innerText).contains('resource: /hello')
        //Check Refresh button works
        .click(Selector('[data-testid="refresh-btn"]'))
        .expect(screen.findByTestId("preloader").exists).notOk({timeout: WAIT_TIME_LONG})
        // Check Refresh interval works
        .hover(Selector('#refresh-interval'))
        .click(Selector('#refresh-interval'))
        .click(screen.getAllByText('10 Seconds').nth(1))
        .expect(screen.findAllByTestId("diagram-loader").exists).notOk({timeout: WAIT_TIME_MEDIUM})


        // Test Diagram status
        .expect(Selector('.metrics-text').withText('100% Success').exists).ok({timeout: WAIT_TIME_EX_LONG})
        .expect(Selector('#CounterLeft').exists).ok({timeout: WAIT_TIME_MEDIUM})

        .expect(screen.getByTestId('histogram-throughput').find('g.recharts-layer.recharts-area').exists).ok({timeout: WAIT_TIME_EX_LONG})
        .expect(screen.getByTestId('histogram-response-time').find('g.recharts-layer.recharts-area').exists).ok({timeout: WAIT_TIME_EX_LONG})

        // Disable refresh
        .hover(Selector('#refresh-interval'))
        .click(Selector('#refresh-interval'))
        .click(screen.getAllByText('Off'))

    // Check for log panel
    await t.expect(screen.getByTestId('log-panel').find('div>span').withText("Special test Log for App").count).gte(1, 'Log exists', {timeout: WAIT_TIME_EX_LONG})
        .click(screen.getByText('Past 24 hours'))
        .click(screen.getByText('Past 10 minutes'))

    // Enable the status bar
    let d = await screen.getByTestId('histogram-response-time').find('g.recharts-layer.recharts-area').find('path').getAttribute('d')
    d = d.replace('Z', '')
    const newD = d.split("L")
    let prevY
    let finalX
    let finalY
    for (const v of newD) {
        const arr = v.split(',')
        if (prevY !== undefined && prevY !== arr[1]) {
            finalX = arr[0]
            finalY = arr[1]
            break
        }
        prevY = arr[1]
    }

    await t.hover(screen.getByTestId('histogram-response-time').find('svg'), {
        offsetX: Math.round(finalX),
        offsetY: Math.round(finalY),
    })
        .click(screen.getByTestId('histogram-response-time').find('svg'), {
            offsetX: Math.round(finalX),
            offsetY: Math.round(finalY),
        })
        .expect(screen.findByTestId("preloader").exists).notOk({timeout: WAIT_TIME_LONG})
        .expect(Selector('[data-testid="request-table"]').visible).ok({timeout: WAIT_TIME_EX_LONG})
        .expect(screen.getAllByTestId("request-information").count).gte(1, {timeout: WAIT_TIME_SHORT})
        .expect(screen.getAllByTestId("request-information").find('div>div:nth-child(1').innerText).contains('ms', {timeout: WAIT_TIME_SHORT})
        .expect(screen.getAllByTestId("request-information").find('div>div:nth-child(2)').innerText).notEql('', {timeout: WAIT_TIME_SHORT})
        .expect(screen.getAllByTestId("request-information").find('div>div:nth-child(3)').getStyleProperty('background-color')).eql("rgb(54, 180, 117)", {timeout: WAIT_TIME_SHORT})
        // Check for hide options
        .hover(screen.getByText('Hide Options'))
        .click(Selector('[data-testid="hide-options-btn"]'))
        .expect(screen.getByText('Show Options').exists).ok({timeout: WAIT_TIME_SHORT})
});

test("test run observe log view hello world service ", async (t) => {
    await deployApp(t);
    await t.expect(Selector(".diagram-canvas").exists).ok("Diagram should be visible", {timeout: WAIT_TIME_SHORT})
    await t.expect(Selector('[data-testid="panel-Logs-btn"]').exists).ok({timeout: WAIT_TIME_MEDIUM})
        .click(Selector('[data-testid="panel-Logs-btn"]'))
        //Check for the given log
        .expect(Selector('[data-testid="panel-Logs-btn"]').exists).ok({timeout: WAIT_TIME_LONG})
    logger.info("TEST LOG")

    await t.expect(Selector('span').withText("Special test Log for App").count).eql(3, 'Not exists', {timeout: WAIT_TIME_LONG})
        // Search for log key word
        .typeText(
            Selector('#log-search').nth(0),
            "Special",
            {speed: 0.5}
        )
        .expect(Selector("#backdrop-loader").exists).notOk({timeout: WAIT_TIME_SHORT})
        .expect(Selector('span').withText("Special test Log for App").count).gte(3, 'Not exists', {timeout: WAIT_TIME_MEDIUM})
});

