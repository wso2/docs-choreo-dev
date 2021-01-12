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
        await t.resizeWindow(1920, 1080)
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
    await createHttpConnector(t, "https://postman-echo.com/get?foo1=bar1&foo2=bar2","GET", "response");
    await createLog(t, 'Info', '"Special test Log for App"');
    await createRespond(t, "res");
    const appURL = await deployToChoreo(t, appName)

    await t.click(screen.getByTitle("observe"))
    await t.expect(Selector("#backdrop-loader").exists).notOk({timeout: WAIT_TIME_MEDIUM});
    await t.expect(await getLocation()).contains("app/" + appName + "/observe", { timeout: WAIT_TIME_SHORT })

    logger.info("Succesfully Navigated to Deploy view")

    await callDeployedApp(t,appURL,3,3)
}


test("test run observe log view hello world service ", async (t) => {
    await deployApp(t);
    await t.click(screen.getByTestId("panel-Logs-btn"))
        .wait(25000)
        // .expect(screen.getByTestId("preloader").exists).notOk({timeout: WAIT_TIME_LONG})
        //Check for the given log
        .expect(screen.getByTestId('log-panel').exists).ok({timeout: WAIT_TIME_MEDIUM})
    logger.info("TEST LOG")

    await t.expect(Selector('span').withText("[lakshankarunathilake/main-module] - Special test Log for App").count).eql(3, 'Not exists', {timeout: WAIT_TIME_MEDIUM})
        // Search for log key word
        .typeText(
            Selector('#log-search').nth(0),
            "Special",
            {speed: 0.5}
        )
        .expect(Selector("#backdrop-loader").exists).notOk({timeout: WAIT_TIME_SHORT})
        .expect(Selector('span').withText("[lakshankarunathilake/main-module] - Special test Log for App").count).gte(3, 'Not exists', {timeout: WAIT_TIME_MEDIUM})

});

test("test run observe overview hello world service ", async (t) => {
    await deployApp(t);
    await t.expect(Selector(".diagram-canvas").exists).ok("Diagram should be visible", {timeout: WAIT_TIME_SHORT})
        .expect(Selector(".worker-line").exists).ok("Diagram should be drawn", {timeout: WAIT_TIME_SHORT})
        // TODO : Fix the app name
        .expect(Selector('#service').innerText).contains(`svc:${appName}`.replace('-','_'),"Service name should be available ")
        .expect(Selector('#resource-path').innerText).contains('rs:hello')
        //Check Refresh button works
        .click(screen.getByTitle("Refresh"))
        .expect(screen.findByTestId("preloader").exists).notOk({timeout: WAIT_TIME_LONG})
        // Check Refresh interval works
        .hover(Selector('#refresh-interval'))
        .click(Selector('#refresh-interval'))
        .click(screen.getAllByText('10 Seconds').nth(1))
        .expect(screen.findAllByTestId("diagram-loader").exists).notOk({timeout: WAIT_TIME_MEDIUM})

        // Disable refresh
        .hover(Selector('#refresh-interval'))
        .click(Selector('#refresh-interval'))
        .click(screen.getAllByText('Off'))

        // Check for log panel
        .expect(Selector('span').withText("[lakshankarunathilake/main-module] - Special test Log for App").count).gte(1, 'Log exists', {timeout: WAIT_TIME_MEDIUM})

        // Test Diagram status
        .expect(Selector('.metrics-text').withText('100% Success').exists).ok({timeout: WAIT_TIME_MEDIUM})
        // TODO : Counter text
        .expect(Selector('#CounterLeft').exists).ok({timeout: WAIT_TIME_MEDIUM})

        .expect(screen.getByTestId('histogram-throughput').find('g.recharts-layer.recharts-area').exists).ok()
        .expect(screen.getByTestId('histogram-response-time').find('g.recharts-layer.recharts-area').exists).ok()

    // Enable the status bar
    let d = await screen.getByTestId('histogram-throughput').find('g.recharts-layer.recharts-area').find('path').getAttribute('d')
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


    await t.hover(screen.getByTestId('histogram-throughput').find('svg'), {
        offsetX: Math.round(finalX),
        offsetY: Math.round(finalY),
    })
        .wait(3000)
        .click(screen.getByTestId('histogram-throughput').find('svg'), {
            offsetX: Math.round(finalX[0]),
            offsetY: Math.round(finalY[0]),


        })
        .expect(screen.findByTestId("preloader").exists).notOk({timeout: WAIT_TIME_LONG})
        .wait(WAIT_TIME_SHORT)
        .expect(screen.getByTestId('request-table').exists).ok({timeout: WAIT_TIME_EX_LONG})
        .expect(screen.getAllByTestId("request-information").count).eql(3, {timeout: WAIT_TIME_SHORT})
        // TODO: Test further
        .expect(screen.getAllByTestId("request-information").find('div>div:nth-child(1').innerText).contains('ms', {timeout: WAIT_TIME_SHORT})
        .expect(screen.getAllByTestId("request-information").find('div>div:nth-child(2)').innerText).notEql('', {timeout: WAIT_TIME_SHORT})
        .expect(screen.getAllByTestId("request-information").find('div>div:nth-child(2)').getStyleProperty('background-color')).eql("#36B475", {timeout: WAIT_TIME_SHORT})
        // Check for hide options
        .hover(screen.getByText('Hide Options'))
        .click(screen.getByText('Hide Options'))
        .expect(screen.getByText('Show Options').exists).ok({timeout: WAIT_TIME_SHORT})

});
