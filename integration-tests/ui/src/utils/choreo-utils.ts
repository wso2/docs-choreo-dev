import { Selector } from "testcafe";
import { screen } from "@testing-library/testcafe";
import {logger} from './logger';
import Axios from "axios";

export const createNewApp = async (t: TestController, name: string) => {
  logger.info("Creating a new application with name : "+ name);
  await t
    .click(screen.getAllByTestId("create-with-choreo"))
    .typeText(screen.getAllByPlaceholderText("Application name"), name)
    .click(screen.getByText("Create"));

  await t.expect(Selector(".diagram-canvas").exists).ok({ timeout: 50000 });
  logger.info("Application created successfully with name: " + name);
};

export const undeployAllApps = async(t:TestController) => {
  let deployedApps = await screen.findAllByText("Active").exists;

  if(deployedApps){
    await t.click(screen.findAllByText("Active"));
    await t.click(screen.getByTitle("deploy"))
    await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: 10000 });
    await t.click(screen.getByText("Stop"))
    .expect(screen.findByText("Deploy").exists).ok({timeout:200000})
    .click(screen.getByText("App list"))
    .expect(Selector("#backdrop-loader").exists).ok()
    .expect(Selector("#backdrop-loader").exists).notOk({ timeout: 10000 });

    deployedApps = await screen.findAllByText("Active").exists;
  }
}

export const clearAppsIfExists = async (t: TestController) => {
  await undeployAllApps(t);
  let appExists = await screen.queryAllByText("Time to create your first application").exists;
  
  // let appExists = !(await f.exists);
  while(!appExists) {
    logger.info("An application exists, deleting the application");
    await t
        .click(screen.getAllByLabelText("more"))
        .click(screen.getByTestId("delete-btn"))
        .click(screen.getByText("Delete"))
        .expect(Selector("#backdrop-loader").exists).ok()
        .expect(Selector("#backdrop-loader").exists).notOk({ timeout: 10000 });
        appExists = await screen.queryAllByText("Time to create your first application",{timeout:5000}).exists;
  }

  await t
    .expect(screen.queryAllByText("Time to create your first application").exists).ok({ timeout: 10000 });
  logger.info("Application deleted successfully");
};

export const selectWebhookType = async (t: TestController, name: string) => {
  await t
    .click(screen.getByText("Webhook"))
    .expect(screen.findByPlaceholderText("Relative path from host").exists).ok({timeout:200000})
    .typeText(screen.queryByPlaceholderText("Relative path from host"), name, {speed: 0.5})
    .click(screen.getByText("Save"), { speed: 0.5 })
    .expect(screen.findAllByTestId("diagram-loader").exists).ok({ timeout: 20000 })
    .expect(screen.findAllByTestId("diagram-loader").exists).notOk({ timeout: 100000 });
  logger.info("selected webhook app type with name : " + name);
};

export const createProperty = async (t: TestController, expression: string) => {
  logger.info("Creating the property with expression : " + expression);
  await t
    .click(Selector("#BigPlusRectangle"), { speed: 0.5 })
    .expect(screen.getByText("Process").exists).ok()
    // .expect(screen.getByText("CONNECTOR").exists).ok()
    // .expect(screen.getByText("CONDITION").exists).ok()
    .click(screen.getByTestId("process-rect"), { speed: 0.5 })
    .hover(screen.getByText("Property"),{speed:0.5})
    .click(screen.getByTestId("addproperty"), { speed: 0.5 })
    .typeText(
      screen.getByPlaceholderText("Enter custom code (eg: var x=12;)"),
      expression,
      { speed: 0.5 }
    )
    .click(screen.getByText("Save"))
    .expect(screen.findByTestId("diagram-loader").exists).ok({ timeout: 20000 })
    .expect(screen.findByTestId("diagram-loader").exists).notOk({ timeout: 60000 });
  logger.info("Successfully created the property with expression : " + expression);
};

export const createRespond = async (t: TestController, expression: string) => {
  await t
    .expect(screen.queryAllByText("END").exists).ok({ timeout: 10000 })
    .click(Selector("#SmallPlus"), { speed: 0.5 })
    .click(Selector("#Plus_a"), { speed: 0.5 })
    .expect(screen.queryAllByText("Loading").exists).notOk({ timeout: 20000 })
    // .click(Selector("#DottedStopRectangle"), { speed: 0.5 })
    // .click(screen.getAllByText("Respond"))
    .click(screen.getByTestId("stop-oval"),{speed:0.5})
    .hover(screen.getByText("Respond"),{speed:0.5})
    .click(screen.getByTestId("addrespond"),{speed:0.5})
    .typeText(
      screen.getByPlaceholderText("Enter value to send with respond"),
      "res",
      { speed: 0.5 }
    )
    .click(screen.getByText("Save"))
    .expect(screen.findByTestId("diagram-loader").exists).ok({ timeout: 200000 })
    .expect(screen.findByTestId("diagram-loader").exists).notOk({ timeout: 60000 });
  logger.info("Created respond action with variable : " + expression);
};

export const callExternalEndpoint = async (t:TestController,URL:string,attempts:number) => {
  let res = "";
  let attempt = 0
  while(res === "" && attempt <= attempts){
    await t.wait(5000);

      try {
        logger.info("Test URL : " + (URL));
        const response = await Axios.get(URL);
        res = response.data;
      } catch (err) {
        logger.error("Error while getting the test response" ,err);
      }
      logger.info("Attempt : " + attempt + " calling the endpoint.. response: "+ res);
      attempt++;
  }

  return res;

 
}
