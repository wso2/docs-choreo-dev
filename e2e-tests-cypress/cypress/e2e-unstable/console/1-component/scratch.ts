import { Enums } from "../../../support/commons/enums";
import { Utils } from "../../../support/commons/utils";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";

before(() => {
  LoginPage.login();
});

/*
after(() => {
  ChoreoHomePage.logout();
});
*/

describe(`Verify proxy api functionality`, () => {
  const PROJECT_NAME = " autotest1686288260053";
  const COMPONENT_NAME = "autotest1689670747731";

  const HEADER_KEY = "x-header-test";
  const HEADER_VALUE = "test";
  const RESOURCE = "store/inventory";

  it("Navigate to component", () => {
    //ProjectListingPage.selectProject(PROJECT_NAME);
    //ComponentListingPage.visitToAComponent(COMPONENT_NAME);
    cy.visit(
      "https://consolev2.preview-dv.choreo.dev/organizations/uvindradiasjayasinha/projects/f9c10910-d9c2-49a8-901c-db33b22671da/components/yduvem/overview"
    );
    cy.get('[id="backdrop-loader"]').should("not.exist");
    cy.wait(8000);
  });

  it("Component delete", () => {
    ComponentOverviewPage.goBackToProject();
    ComponentListingPage.deleteComponent(COMPONENT_NAME);
  });
});
