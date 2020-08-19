import { Selector, ClientFunction, RequestLogger } from "testcafe";
import { screen } from "@testing-library/testcafe";
import Axios, { AxiosResponse } from "axios";
import { getLocation } from "../utils/login-utils";
import page from "../model/page";
import * as config from "../../testcafe-user-config.json";
import { createNewApp, clearAppsIfExists, selectWebhookType, createProperty, createRespond, callExternalEndpoint } from "../utils/choreo-utils";
import {logger} from '../utils/logger'

declare const test: TestFn;

fixture("Application test run  and deployment")
  .page(config.testURL)
  .beforeEach(async () => {
    await page.login();
  });

// test("test run hello world service ", async (t) => {

//   const appName = "sampleapi-" + Math.random().toString(36).substr(2,5);
//   await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: 10000 });
//   logger.info("Page loaded successfully");
//   await clearAppsIfExists(t);
//   await createNewApp(t,appName);
//   await t.expect(await getLocation()).contains("app/"+appName+"/develop", { timeout: 10000 })
//   await selectWebhookType(t,"hello");
//   await createProperty(t,'var res = "hello world";');
//   await createRespond(t,"res");

//   await t.wait(10000);
//   await t.click(screen.getByTestId("editor-run-btn"), { speed: 0.5 });
//   logger.info("Started test run");

//   await t
//     .expect(screen.findAllByTestId("test-url").exists).ok({ timeout: 40000 })
//     .expect(
//       screen.findAllByTestId("log-panel").withText("started HTTP/WS listener")
//         .exists
//     ).ok({ timeout: 40000 });
//   logger.info("Retrieving the test URL successful");

//   const testUrl = await screen.findAllByTestId("test-url").textContent;

//   await t.wait(20000);
//   const response = await callExternalEndpoint(t,(testUrl + "/hello"),3)
 
//   logger.info("Backend service response : " +  response);
//   await t.expect(response).eql("hello world");
//   logger.info("Hello world string recieved successfully !")
// });


test("deploy hello world service",async (t)=>{

  const appName = "sampleapi-" + Math.random().toString(36).substr(2,5);
  await clearAppsIfExists(t);
  await createNewApp(t,appName);
  await t.expect(await getLocation()).contains("app/"+appName+"/develop", { timeout: 10000 })
  await selectWebhookType(t,"hello");
  await createProperty(t,'var res = "hello world";');
  await createRespond(t,"res");

  await t.click(screen.getByTitle("deploy"))
  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: 10000 });
  await t.expect(await getLocation()).contains("app/"+appName+"/deploy", { timeout: 10000 })

  logger.info("Succesfully Navigated to Deploy view")

  logger.info("Deploying application...")
  await t.click(screen.getByTestId("deploy-btn"),{speed:0.5})
  .expect(screen.findByTestId("checkout-loading").exists).ok({timeout:20000})
  .expect(screen.findByTestId("checkout-failed").exists).notOk({timeout:10000})
  .expect(screen.findByTestId("checkout-ok").exists).ok({timeout:200000})
  logger.info("Checkout phase successful!")


  logger.info("Starting build phase...")
  await t.expect(screen.findByTestId("build-loading").exists).ok({timeout:10000})
  .expect(screen.findByTestId("build-loading").exists).notOk({timeout:200000})
  .expect(screen.findByTestId("build-failed").exists).notOk({timeout:10000})
  .expect(screen.findByTestId("build-ok").exists).ok({timeout:100000});
  logger.info("Build phase successful!");

  await t.expect(screen.findByTestId("test-ok").exists).ok({timeout:100000})
  logger.info("Test phase successful!");

  logger.info("Starting deploy phase...");
  await t
  .expect(screen.findByTestId("deploy-loading").exists).ok({timeout:10000})
  .expect(screen.findByTestId("deploy-loading").exists).notOk({timeout:200000})
  .expect(screen.findByTestId("deploy-failed").exists).notOk({timeout:10000})
  .expect(screen.findByTestId("deploy-ok").exists).ok({timeout:100000})
  logger.info("Deploy phase successful!")

  await t.wait(10000);

  const testUrl = await screen.findAllByTestId("deploy-url").find("input").value;
  logger.info("test url : " + testUrl);
  await t.expect(testUrl.includes("https://")).ok();
  const response = await callExternalEndpoint(t,testUrl + "/hello",3);

  console.log("Service response : " +  response);
  await t.expect(response).eql("hello world");
  logger.info("Hello world string recieved successfully !")

  logger.info("Stopping deployed application")
  await t.click(screen.getByText("Stop"))
  .expect(screen.findByText("Deploy").exists).ok({timeout:200000})
  logger.info("Stopped application deployment successfully!")
})
