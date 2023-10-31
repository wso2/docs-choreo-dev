import { cyGet } from "../../../commons/cy";
import { Enums } from "../../../commons/enums";
import { TryOut } from "../../../devportal/pages/apis/try-out";
import { TestIds } from "../../constants/TestIds";
import { DevPortalLeftMenu } from "../../ui-elements/left-menus/dev-portal-left-menu";

export abstract class Component {
  name: string;
  versions: string[] = [];
  id: string;
  handler: string;
  projectId: string;
  componentUrl: string;

  private devPortalMenu = new DevPortalLeftMenu();

  constructor(
    name: string,
    version: string,
    componentUrl: string,
    id?: string,
    handler?: string,
    projectId?: string
  ) {
    this.name = name;
    this.versions.push(version);
    this.componentUrl = componentUrl;

    if (id) {
      this.id = id;
    }

    if (handler) {
      this.handler = handler;
    }

    if (projectId) {
      this.projectId = projectId;
    }
  }

  getName() {
    return this.name;
  }

  getId() {
    return this.id;
  }

  getHandler() {
    return this.handler;
  }

  getProjectId() {
    return this.projectId;
  }

  getLatestVersion() {
    return this.versions[this.versions.length - 1];
  }

  updateVersionList(version: string) {
    this.versions.push(version);
  }

  getComponentUrl() {
    return this.componentUrl;
  }

  generateCredentials_DevPortal(
    env: Enums.Environment.PRODUCTION | Enums.Environment.SANDBOX
  ) {
    if (env === Enums.Environment.PRODUCTION) {
      this.devPortalMenu.navigateToProductionCredentials();
    } else {
      this.devPortalMenu.navigateToSandboxCredentials();
    }

    cy.get(TestIds.generateCredentials).should("be.visible").click();
    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(TestIds.generateCredentials).should("not.exist");
    cy.get(TestIds.removeCredentials).should("be.visible");
    cy.get("#copy-textfield").invoke("val").should("not.be.empty");
    cy.get(TestIds.generateAccessToken).should("be.visible");
  }

  testSwaggerConsole_DevPortal(resource: string) {
    this.devPortalMenu.navigateToTryOut();
    // Add slight wait to account for rerendering of elements without any visible indication
    cyGet(TestIds.getTestKey).should("be.enabled").wait(5000).click();
    cy.get(TestIds.getTestKey).within(() => {
      cy.get(TestIds.progressBar).should("not.exist");
    });
    cy.get(TestIds.accessToken).should("not.be.empty");

    TryOut.SelectResource(resource);
    TryOut.TryoutAPI();
    TryOut.ExecuteResourceFunction();
    TryOut.GetResponse();
  }

  navigateToComponentInConsole() {
    cy.log(this.getComponentUrl());
    cy.visit(this.getComponentUrl()).then(() => {
      cy.get(TestIds.backdropLoader).should("not.exist");
      cy.get(TestIds.createTime).should("be.visible");
    });
  }

  protected visitComponent(name: string): string {
    cy.get('[data-cyid="listing"]').should("be.visible").click();

    cy.get('[data-cyid="project-components-multi-select"]').should(
      "be.visible"
    );

    cy.get('[data-cyid="component-table"]')
      .contains(name)
      .should("be.visible")
      .click();

    cy.get('[data-cyid="home"]').should("be.visible");

    cy.get('[id="backdrop-loader"]').should("not.exist");
    cy.get("[data-cyid=create-time]").should("be.visible");
    cy.log("Successfully visited to the component");

    cy.url().then((url) => {
      return url;
    });

    return "";
  }
}
