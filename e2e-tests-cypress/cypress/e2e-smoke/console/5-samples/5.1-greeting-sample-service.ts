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

import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentDevelopPage } from "../../../support/console/pages/component/component-develop-page";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ComponentTestPage } from "../../../support/console/pages/component/component-test-page";
import { SwaggerUI } from "../../../support/console/pages/component/UI-components/swagger-UI-component";
import { Environment } from "../../../support/console/pages/enum/environment";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { GreetingSample } from "../../../support/console/pages/samples/greeting";
import { Utils } from "../../../support/console/utils";

describe("Create Greeting sample in Choreo", () => {
  const FILE_ID = "oasflow";
  const PROJECT_DESCRIPTION = "sample oas flow scenario";
  const PROJECT_NAME = Utils.generateProjectName();
;
  before(() => {
    LoginPage.login();
  });
  after(() => {
    ChoreoHomePage.logout();
  });
  it("Creating a project and add Greeting sample", () => {
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      FILE_ID
    );
    ProjectOverviewPage.addNewComponent();
    GreetingSample.selectSample()
    ComponentDevelopPage.getComponentURL();
  });


  it("Verify component deployment", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deploy();
    ComponentDeployPage.verifyDevInvokeURL().should("not.eq", "");
  });

  it("Verify component promote to prod", () => {
    ComponentDeployPage.promoteToProd();
    ComponentDeployPage.verifyProdInvokeURL().should("not.eq", "");
  });


  it("Verify test functionality of sample resource in dev on swagger", () => {
    ComponentOverviewPage.navigateToTest();
    ComponentTestPage.selectEnvironment(Environment.DEVELOPMENT);
    ComponentTestPage.getTestKey();
    SwaggerUI.SelectResource("/");
    SwaggerUI.TryoutAPI();
    SwaggerUI.enterValue("name", "dasun");
    SwaggerUI.ExecuteResourceFunction();
    // SwaggerUI.GetResponse().should("eq", "true");
    // SwaggerUI.getResponseCode().should("eq", "200");
  });

  it("Verify suspending deployed componen", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
  });

  it("Verify component deletion",()=>{
    ComponentOverviewPage.goBack();
    ComponentListingPage.deleteComponent("Greetings")
  })

});
