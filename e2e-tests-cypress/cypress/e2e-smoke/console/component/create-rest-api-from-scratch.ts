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
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ComponentTestPage } from "../../../support/console/pages/component/component-test-page";
import { Curl } from "../../../support/console/pages/component/UI-components/curl-component";
import { SwaggerUI } from "../../../support/console/pages/component/UI-components/swagger-UI-component";
import { Environment } from "../../../support/console/pages/enum/environment";
import { HTTPMethod } from "../../../support/console/pages/enum/http-method-enum";
import { ConnectorAudience } from "../../../support/console/pages/enum/marketplace-connector-audience";
import { HomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectOverviewPage } from "../../../support/console/pages/projects/project-overview";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { RestAPITemplate } from "../../../support/console/pages/templates/rest-api-temp";
import { VSExplorer } from "../../../support/console/pages/vscod-editor/vs-explorer";
import { VSSourceControl } from "../../../support/console/pages/vscod-editor/vs-source-control";
import { Utils } from "../../../support/console/utils";

describe("Verify project creation functionality", () => {
  const COMPONENT_NAME = "covid stat api";
  const COMPONENT_DESCRIPTION = "covid daily stats";
  const PROJECT_DESCRIPTION = "Covid stats project";
  const PROJECT_NAME = Utils.generateProjectName();
  const FILE_ID = "create-rest-api-from-scratch";
  const labels = ["IT Operations/Testing Tools", "IT Operations/Debug Tools"];
  const commitMessage = "adding user.bal file";
  const queryParameters = [{ key: "name", value: "dasun" }];

  before(() => LoginPage.loginToChoreo(FILE_ID));

  it("Verify REST API component creation", () => {
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      FILE_ID
    );
    ProjectOverviewPage.addNewComponent();
    RestAPITemplate.selectHttpAPITemplate();
    RestAPITemplate.createApiFromScratch(
      COMPONENT_NAME,
      COMPONENT_DESCRIPTION,
      FILE_ID
    );
    ComponentDevelopPage.getComponentURL(FILE_ID);
  });

  it("Edit code in VScode", () => {
    LoginPage.navigateToCodespace(FILE_ID);
    VSExplorer.typeCode("User.bal");
    VSExplorer.selectSourceControl();

    VSExplorer.enterCommandInTerminal(
      "bash /config/workspace/.githooks/pre-commit"
    );
    VSExplorer.enterCommandInTerminal(
      "rm /config/workspace/.githooks/pre-commit"
    );
    VSSourceControl.commitChanges(commitMessage);

    VSExplorer.enterCommandInTerminal("git push");
    VSExplorer.waitTillCodeSyncWithChoreo();
  });

  it("Verify component commits", () => {
    LoginPage.reloginToChoreo(FILE_ID);
    ComponentDevelopPage.addLabels(labels).then((arr) => {
      expect(arr).to.deep.eq(labels);
    });
    ComponentDevelopPage.verifyLatestCommit(commitMessage);
  });

  it("Deploy the component", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deploy();
    ComponentDeployPage.isDeploymentSuccessful(FILE_ID).should("be.visible");
    //  ComponentDeployPage.verifyDevInvokeURL().should('not.be.null');
  });

  it("Verify test functionality in dev", () => {
    ComponentOverviewPage.navigateToTest();
    ComponentTestPage.selectEnvironment(Environment.DEVELOPMENT);
    ComponentTestPage.getTestKey();
    SwaggerUI.SelectResource(HTTPMethod.GET, "/sayHello");
    SwaggerUI.TryoutAPI();
    cy.get('[placeholder="name"]').type("Dasun");
    SwaggerUI.ExecuteResourceFunction();
    SwaggerUI.GetResponse().should("eq", "Hello, Dasun");
    SwaggerUI.getResponseCode().should("eq", "200");
    ComponentTestPage.selectCurl();
    Curl.selectMethod(HTTPMethod.GET);
    Curl.addQueryParameter(queryParameters);
    Curl.sendCurlRequest();
  });

  it("Verify component promote to prod", () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.promoteToProd();
    //  ComponentDeployPage.verifyProdInvokeURL().should('not.be.null');;
  });

  it("Verify test functionality in prod", () => {
    ComponentOverviewPage.navigateToTest();
    ComponentTestPage.selectEnvironment(Environment.PRODUCTION);
    ComponentTestPage.getTestKey();
    SwaggerUI.SelectResource(HTTPMethod.GET, "/sayHello");
    SwaggerUI.TryoutAPI();
    cy.get('[placeholder="name"]').type("Dasun");
    SwaggerUI.ExecuteResourceFunction();
    SwaggerUI.GetResponse().should("eq", "Hello, Dasun");
    SwaggerUI.getResponseCode().should("eq", "200");
    ComponentTestPage.selectCurl();
    Curl.selectMethod(HTTPMethod.GET);
    Curl.addQueryParameter(queryParameters);
    Curl.sendCurlRequest();
  });

  it("Verify manage functionality", () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.publish(ConnectorAudience.PRIVATE).should(
      "be.visible"
    );
    ComponentAPILifecycle.selectUsagePlans("Bronze", "Gold");
    ComponentAPILifecycle.configureSecuritySettings(false, false, [], [], []);
  });

  it.skip("Delete created project", () => {
    HomePage.selectHomeMenu();
    HomePage.navigateToProjects(FILE_ID);
    ProjectListingPage.selectProject(FILE_ID);
  });

  after(() => {
    HomePage.logout(FILE_ID);
  });
});
