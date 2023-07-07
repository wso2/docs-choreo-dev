/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

export class DomainsComponents {
  static navigateToDomainsSettings() {
    cy.get('[data-cyid="nav-link-domains-link-tabs-link-tab"]').click();
  }

  static navigateToDevPortalCustomDomain() {
    cy.get('[data-cy="/domains/devportal"]').click();
  }

  static deleteDevportalDomainIfExists(domainName: string) {
    cy.get('[data-cyid="devportal-custom-domain-search-field"]').trigger(
      "mouseover"
    );
    cy.get('[data-cyid="search-app"]').clear().type(domainName);
    cy.wait(2000);
    cy.get("td").then(($domain) => {
      if (!$domain.text().includes("No records to display")) {
        cy.contains("td", "Managed by Choreo").should("be.visible");
        this.deleteSelectedDomain(domainName);
      }
    });
  }

  private static deleteSelectedDomain(domainName: string) {
    cy.get("tbody>tr").dblclick();
    cy.contains("Delete").click({ force: true });
    cy.log("Deleting the created Domain");
    cy.get('[data-cyid="btn-confirmation-dialog-blue"]').click();
    cy.get('[data-cyid="search-app"]').should("be.visible");
    cy.contains("td", domainName).should("not.exist");
    cy.log("Domain deleted successfully");
  }

  static deleteCreatedCustomDomain(domainName: string) {
    cy.get('[data-cyid="devportal-custom-domain-search-field"]').trigger(
      "mouseover"
    );
    cy.get('[data-cyid="search-app"]').clear().type(domainName);
    cy.contains("td", domainName).should("be.visible");
    this.deleteSelectedDomain(domainName);
  }

  static createDevportalDomain(domainName: string) {
    cy.get('[data-cyid="btn-add-domain-button"]').click();
    cy.contains("Create Custom Domain").should("be.visible");
    cy.log("Creating a dev portal custom domain");
    cy.get('[data-cyid="text-field-domain-name"]').type(domainName);
    cy.get('[data-cyid="btn-verify-domain"]').click({ force: true });
    cy.get('[data-cyid="btn-verify-domain"]').should("not.exist");
    cy.get('[data-cyid="btn-next"]').should("exist");
    cy.log("Verified the CNAME mapping availability");
    cy.get('[data-cyid="btn-next"]').click({ force: true });
    cy.contains("Select TLS Certificate Provider").should("be.visible");
    cy.get('[data-cyid="lets-encrypt-certificate-tile"]').click();
    cy.get('[data-cyid="btn-add"]').should("be.visible").click({ force: true });
    cy.contains("td", domainName).should("be.visible");
    cy.log("Created the custom domain successfully");
  }
}
