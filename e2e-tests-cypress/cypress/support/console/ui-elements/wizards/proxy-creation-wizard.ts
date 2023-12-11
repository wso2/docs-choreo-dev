import { ProxyInfo } from "../../concepts/project/project";
import { TestIds } from "../../constants/TestIds";

export class _ProxyCreationWizard {
  createFromOASUrl(url: string) {
    cy.get(TestIds.oasUrlEntry).should("be.visible").type(url);
    cy.get(TestIds.next).should("be.visible").click();
  }

  createFromOASFile(filepath: string) {
    cy.get(TestIds.upload).click();
    cy.get(TestIds.filepathEntry).attachFile(filepath);
    cy.get(TestIds.next).should("be.visible").click();
  }

  enterProxyDetails(name: string, basePath: string, proxyInfo: ProxyInfo) {
    cy.get(TestIds.apiName).within(() => cy.get("input").clear().type(name));
    cy.get(TestIds.apiVersion).clear().type(proxyInfo.version);
    cy.get(TestIds.apiBasePath).within(() =>
      cy.get("input").clear().type(basePath)
    );

    if (proxyInfo.endpointUrl !== undefined) {
      cy.get(TestIds.apiEndpoint).within(() =>
        cy.get("input").clear().type(proxyInfo.endpointUrl)
      );
    }

    if (proxyInfo.isInternal !== undefined && proxyInfo.isInternal) {
      cy.get(TestIds.internalAccessMode).click();
    }
    cy.get(TestIds.createButton).should("be.enabled").click();

    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(TestIds.createTime).should("be.visible");
  }
}
