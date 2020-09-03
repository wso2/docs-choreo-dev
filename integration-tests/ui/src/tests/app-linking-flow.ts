import { Selector, ClientFunction, RequestLogger } from "testcafe";
import { screen } from "@testing-library/testcafe";
import Axios, { AxiosResponse } from "axios";
import { getLocation } from "../utils/login-utils";
import page from "../model/page";
import * as config from "../../testcafe-user-config.json";
import { createNewApp, clearAppsIfExists, selectWebhookType, createProperty, createRespond, callExternalEndpoint } from "../utils/choreo-utils";
import { logger } from '../utils/logger'
const util = require('util');
const exec = util.promisify(require('child_process').exec);

declare const test: TestFn;

fixture("App linking")
    .page(config.testURL)
    .beforeEach(async () => {
        await page.login();
    });

test("test app linking", async (t) => {

    await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: 20000 });
    logger.info("Page loaded successfully");
    await clearAppsIfExists(t);

    logger.info("Start connecting a running app : " + "linking-test-app");
    await t
        .click(screen.getAllByTestId("link-ballerina-app"))
        .typeText(screen.getAllByPlaceholderText("App name"), "linking-test-app")
        .click(screen.getByText("Generate Secret"));
    await t.expect(screen.queryAllByTestId("copy-btn").exists).ok({ timeout: 60000 });
    logger.info("Secret generated successfully");

    async function runBallerinaApp(secret: string) {
        const secretConfig = 'secret=\\"' + secret + '\\"';
        const { stdout, stderr } = await exec('sh src/utils/applinking_test/run_ballerina.sh ' + secretConfig);
        console.log('stdout:', stdout);
        console.log('stderr:', stderr);
    }
    const secret = (await screen.getByPlaceholderText("Application secret").value).toString();
    console.log(secret);
    await runBallerinaApp(secret);

    await t.expect(screen.findByText("Successfully Connected").exists).ok({ timeout: 120000 });
    await t.click(screen.findByText("Observe"));
    await t.expect(await getLocation()).contains("observe/", { timeout: 20000 });
    logger.info("App Linking successful");
});

test("test annonymousapp linking", async (t) => {

    await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: 20000 });
    logger.info("Page loaded successfully");
    await clearAppsIfExists(t);

    logger.info("Start connecting an annonymous app : " + "annon-linking-test-app");

    async function getAnnonAppUrl() {
        const { stdout, stderr } = await exec('sh src/utils/applinking_test/run_annonapp.sh');
        console.log('stdout:', stdout);
        console.log('stderr:', stderr);
        const url = /visit (http[^\s]+)/i.exec(stdout)[1];
        return url;
    }
    const obsUrl = await getAnnonAppUrl();

    await t.navigateTo(obsUrl)
        .expect(screen.getByText("Add to Choreo")).ok({ timeout: 10000 })
        .click(screen.getByText("Add to Choreo"))
        .typeText(screen.findByPlaceholderText("Application name"), "annon-linking-test-app")
        .click(screen.findByText("Next"))
        .expect(screen.getByTestId("copy-btn")).ok({ timeout: 20000 })

    const linkingCommand = (await screen.getByPlaceholderText("App Linking command").value).toString();
    await exec(linkingCommand);
    await t.expect(screen.getByText("Congratulations", { exact: false })).ok({ timeout: 60000 });

    logger.info("Annonymous App Linking successful");
});


