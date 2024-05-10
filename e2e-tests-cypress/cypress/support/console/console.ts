/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { Project } from "./entities/project/project";
import { login } from "./entities/login/login";
import { OnPremKeyService } from "./apis/on-prem-key-service";
import { Utils } from "../commons/utils";
import { AUTH_HEADER2, OK } from "../commons/http";
import { GraphQL } from "./apis/graphql";
import { ApiDevPortalService } from "./apis/api-devportal-service";
import { TestIds } from "./constants/TestIds";
import { OrganizationSettings } from "./features/org-settings/org-settings";
import { CustomDomainType, Enums } from "../commons/enums";
import { DOMAIN_URL_MGT } from "../commons/urls";
import { VERY_SHORT_TIME } from "../commons/timeouts";

/**
 * Represents the Choreo Console, the entry point for all tests.
 */
class Console {
  private _orgSettings = new OrganizationSettings();

  static projectNamePrefix = "autotest";

  login() {
    login.login();
    return cy.wrap({});
  }

  enterpriseLogin() {
    login.enterpriseLogin();
    return cy.wrap({});
  }

  getDevPortalUrl(): Cypress.Chainable<string> {
    return cy
      .get(TestIds.choreoHomeDevPortalLink)
      .should("be.visible")
      .invoke("attr", "href")
      .then((href) => {
        if (href) {
          return href;
        } else {
          throw new Error("Devportal link not found");
        }
      });
  }

  navigateToEnterpriseDevPortal(url: string) {
    cy.visit(url).then(() => {
      cy.get(TestIds.devPortalHome, VERY_SHORT_TIME).should("be.visible");
      cy.get(TestIds.devPortalSignedInUser).should("be.visible");
    });
  }

  logout() {
    cy.request(login.getSignOutUrl()).then(() => {
      cy.clearAllSessionStorage();
      cy.clearLocalStorage();
      cy.clearAllCookies();
      cy.clearAllLocalStorage();
    });
  }

  addUserStore(userStoreFile: string, env: Enums.Environment) {
    this.navigateToHome();
    this.navigateToSettings();
    this._orgSettings.addUserStore(userStoreFile, env);
  }

  deleteRoleIfExists(roleName: string) {
    this.navigateToHome();
    this.navigateToSettings();
    this.navigateToRoles();
    this._orgSettings.deleteRoleIfExists(roleName);
  }

  deleteRole(roleName: string) {
    this.navigateToHome();
    this.navigateToSettings();
    this.navigateToRoles();
    this._orgSettings.deleteRole(roleName);
  }

  addRole(roleName: string, roleDescription: string, roleTag: string) {
    this.navigateToHome();
    this.navigateToSettings();
    this.navigateToRoles();
    this._orgSettings.addRole(roleName, roleDescription, roleTag);
  }

  checkCurrentUserIsInGroup(group: string) {
    this.navigateToHome();
    this.navigateToSettings();
    this.navigateToUsers();
    this._orgSettings.checkUserIsInGroup(login.getUserEmail(), group);
  }

  addGroup(group: string, description: string) {
    this.navigateToHome();
    this.navigateToSettings();
    this.navigateToGroups();
    this._orgSettings.addGroup(group, description);
  }

  addRolesToGroup(roles: string[], group: string) {
    this.navigateToHome();
    this.navigateToSettings();
    this.navigateToGroups();
    this._orgSettings.addRolesToGroup(roles, group);
  }

  removeRolesFromGroup(roles: string[], group: string) {
    this.navigateToHome();
    this.navigateToSettings();
    this.navigateToGroups();
    this._orgSettings.removeRolesFromGroup(roles, group);
  }

  checkRolesInGroup(roles: string[], group: string) {
    this.navigateToHome();
    this.navigateToSettings();
    this.navigateToGroups();
    this._orgSettings.checkRolesInGroup(roles, group);
  }

  deleteGroupIfExists(group: string) {
    this.navigateToHome();
    this.navigateToSettings();
    this.navigateToGroups();
    this._orgSettings.deleteGroupIfExists(group);
  }

  deleteGroup(groupName: string) {
    this.navigateToHome();
    this.navigateToSettings();
    this.navigateToGroups();
    this._orgSettings.deleteGroup(groupName);
  }

  addCurrentUserToGroup(roleName: string) {
    this.navigateToHome();
    this.navigateToSettings();
    this.navigateToGroups();
    this._orgSettings.addUserToGroup(login.getUserEmail(), roleName);
  }

  addOrReplaceCustomDomain(domainName: string, type: CustomDomainType) {
    this.navigateToHome();
    this.navigateToSettings();

    cy.intercept({ method: "GET", url: DOMAIN_URL_MGT, times: 1 }).as(
      "getDomains"
    );

    this.navigateToUrlSettings();

    cy.wait("@getDomains").then((interception) => {
      cy.get(TestIds.searchIcon).should("be.visible").click().wait(2000);
      cy.get(TestIds.searchDomain)
        .should("be.visible")
        .within(() => {
          cy.get("input").click().clear().type(domainName);
        });

      cy.get(TestIds.domainTable).within(() => {
        cy.get("tbody").then((tbody) => {
          if (tbody.find(TestIds.noDataAvailable).length == 0) {
            cy.contains("td", domainName).should("be.visible");
            interception.response?.body.forEach((domain) => {
              if (domain.name === domainName) {
                this.deleteSelectedDomain(domain.id);
              }
            });
            // Refresh the page to get the updated domain list since we doing the deletion through an API call
            cy.reload();
          }
        });
      });
    });

    cy.get(TestIds.domainTable).should("be.visible");
    cy.get(TestIds.addDomain).should("be.visible").click();
    cy.get(TestIds.domainName).should("be.visible").type(domainName);

    if (type === CustomDomainType.DevPortal) {
      cy.get(TestIds.devPortalDomainOption).click().wait(2000);
    } else {
      throw new Error("Unhandled domain type");
    }

    cy.get(TestIds.nextButtonV2).contains("Verify").click();
    cy.get(TestIds.nextButtonV2).contains("Next").click();

    cy.get(TestIds.letsEncrypt).should("be.visible").click();
    cy.get(TestIds.nextButtonV2).should("be.enabled").click();

    cy.get(TestIds.addDomain).should("be.visible");

    cy.get(TestIds.domainTable).within(() => {
      cy.contains("td", domainName).should("be.visible");
    });
  }

  removeCustomDomain(domainName: string, type: CustomDomainType) {
    this.navigateToHome();
    this.navigateToSettings();

    cy.intercept({ method: "GET", url: DOMAIN_URL_MGT, times: 1 }).as(
      "getDomains"
    );

    this.navigateToUrlSettings();

    cy.wait("@getDomains").then((interception) => {
      cy.get(TestIds.searchIcon).should("be.visible").click().wait(2000);
      cy.get(TestIds.searchDomain)
        .should("be.visible")
        .within(() => {
          cy.get("input").click().clear().type(domainName);
        });

      cy.get(TestIds.domainTable).within(() => {
        cy.contains("td", domainName).should("be.visible");
        interception.response?.body.forEach((domain) => {
          if (domain.name === domainName) {
            this.deleteSelectedDomain(domain.id);
          } else {
            throw new Error("Domain not found");
          }
        });
        // Refresh the page to get the updated domain list since we doing the deletion through an API call
        cy.reload();
      });
    });
  }

  searchProject(projectName: string): Project {
    this.navigateToHome();
    cy.get(TestIds.searchIcon).click();
    cy.get(TestIds.projectSearch)
      .should("be.visible")
      .type(`${projectName}{enter}`);

    let projectDescription = "";
    cy.contains(projectName)
      .next()
      .then((description) => {
        projectDescription = description.text();
      });

    cy.contains(projectName).click();
    cy.get(TestIds.backdropLoader).should("not.exist");
    return new Project(projectName, projectDescription, true);
  }

  navigateToHome() {
    cy.get('[data-cyid="organization-home"]').click();
    cy.get(TestIds.projectCard).should("be.visible");
  }

  private navigateToSettings() {
    cy.get('[data-cyid="settings"]').should("be.visible").click();
  }

  private navigateToRoles() {
    cy.get('[data-cyid="nav-link-system-roles-link-tabs-link-tab"]').click();
  }

  private navigateToUsers() {
    cy.get('[data-cyid="nav-link-system-users-link-tabs-link-tab"]').click();
  }

  private navigateToGroups() {
    cy.get('[data-cyid="nav-link-groups-link-tabs-link-tab"]').click();
  }

  private navigateToUrlSettings() {
    cy.get('[data-cyid="nav-link-urls-settings-link-tabs-link-tab"]')
      .should("be.visible")
      .click();
    cy.get(TestIds.progressBar).should("not.exist");
    cy.get(TestIds.addDomain).should("be.visible");
  }

  private deleteSelectedDomain(id: string) {
    // Cypress is having a problem with locating the delete confirmation button in the popup
    // So, we are using an API call to delete the domain as a workaround
    Utils.sendDeleteRequest(`${DOMAIN_URL_MGT}/${id}`, AUTH_HEADER2());
  }

  createNewProject(description: string): Project {
    const projectName =
      description === "Default Project"
        ? description
        : this.generateProjectName();
    return new Project(projectName, description);
  }

  cleanUpData() {
    let orgId = login.getOrgId();
    let token = login.getAccessToken();
    let handle = login.getOrgHandle();

    ApiDevPortalService.deleteApplications();
    GraphQL.deleteProjectsCreatedByTestsV2(orgId, handle, token);
    this.deleteOnPremKeys(handle);
  }

  private generateProjectName() {
    return `${Console.projectNamePrefix}${Date.now()}`;
  }

  private deleteOnPremKeys(handle: string) {
    this.getOnPremKeys(handle, AUTH_HEADER2()).then((keys) => {
      keys.forEach((key) => {
        Utils.sendPostRequest(
          OnPremKeyService.deleteOnPremKey(handle, key.handle),
          AUTH_HEADER2(),
          ""
        );
      });
    });
  }

  private getOnPremKeys(handle: string, header: any) {
    const url = OnPremKeyService.getOnPremKeys(handle);
    return Utils.sendGetRequest(url, header).then((res) => {
      if (res.status == OK) {
        return res.body as { handle: string }[];
      } else {
        throw new Error("Error While Getting On Prem Keys");
      }
    });
  }
}

// Singleton instance of Choreo Console
export const console = new Console();
