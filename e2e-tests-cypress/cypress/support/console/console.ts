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
import { DOMAIN_URL_MGT, ORG_MGT_URL, ORGS_URL } from "../commons/urls";
import { VERY_SHORT_TIME } from "../commons/timeouts";

/**
 * Represents the Choreo Console, the entry point for all tests.
 */
class Console {
  private _orgSettings = new OrganizationSettings();

  static projectNamePrefix = "autotest";

  static keyName = Utils.generateKeyName("key");

  login(loadingTime?: number) {
    if (loadingTime === undefined) {
      login.login();
    } else {
      login.login(loadingTime);
    }
    return cy.wrap({});
  }

  selfSignupOrgAdminLogin() {
    login.selfSignupOrgAdminlogin();
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

  enableDevportalSelfSignupAutoApprovalConfig() {
    cy.get(TestIds.backdropLoader).should("not.exist");
    this.navigateToHome();
    this.navigateToSettings();
    this.navigateToAPIManagement();
    this.navigateToSelfSignups();

    cy.wait(5000);
    cy.get(TestIds.consoleSelfSignupConfigPageNotification).should(
      "be.visible"
    );
    cy.get(TestIds.consoleSelfSignupAutoApprovalConfigCheckbox).invoke(
      "removeAttr",
      "disabled"
    );
    cy.get(TestIds.consoleSelfSignupAutoApprovalConfigCheckbox).should(
      "not.be.disabled"
    );
    cy.get(TestIds.consoleSelfSignupAutoApprovalConfigCheckbox).check();
    cy.get(TestIds.consoleSelfSignupAutoApprovalConfigCheckbox).should(
      "be.checked"
    );
  }

  disableDevportalSelfSignupAutoApprovalConfig() {
    cy.get(TestIds.backdropLoader).should("not.exist");
    this.navigateToHome();
    this.navigateToSettings();
    this.navigateToAPIManagement();
    this.navigateToSelfSignups();

    cy.wait(5000);
    cy.get(TestIds.consoleSelfSignupConfigPageNotification).should(
      "be.visible"
    );
    cy.get(TestIds.consoleSelfSignupAutoApprovalConfigCheckbox).uncheck();
    cy.get(TestIds.consoleSelfSignupAutoApprovalConfigCheckbox).should(
      "not.be.checked"
    );
  }

  approveDevportalSelfSignupRequest() {
    this.navigateToHome();
    this.navigateToSettings();
    this.navigateToAPIManagement();
    this.navigateToSelfSignups();

    cy.wait(5000);
    cy.get(TestIds.consoleSelfSignupRequestApproveButton).should("be.visible");
    cy.get(TestIds.consoleSelfSignupRequestApproveButton).click();
    cy.get(TestIds.consoleSelfSignupRequestApproveButton).should("not.exist");
  }

  rejectDevportalSelfSignupRequest() {
    this.navigateToHome();
    this.navigateToSettings();
    this.navigateToAPIManagement();
    this.navigateToSelfSignups();

    cy.wait(5000);
    cy.get(TestIds.consoleSelfSignupRequestRejectButton).should("be.visible");
    cy.get(TestIds.consoleSelfSignupRequestRejectButton).click();
    cy.get(TestIds.consoleSelfSignupRequestRejectButton).should("not.exist");
  }

  removePendingDevportalSelfSignupRequests() {
    const uuid = login.getOrgUuid();
    const token = login.getAccessToken();

    const headers = {
      authorization: `Bearer ${token}`,
    };

    const selfSignUpUrl = `${ORG_MGT_URL}/orgs/${uuid}/self-signup/approval-requests`;
    const changeStatusUrl = `${selfSignUpUrl}/change-status`

    Utils.sendGetRequest(selfSignUpUrl, headers).then((res) => {
      const list = res.body.list as [];

      cy.log("Total self signup requests: " + list.length);

      list.filter((item) => item["status"] === "pending").forEach((pendingRequest) => {
        const rejectRequest = {
          "orgUuid":pendingRequest["orgUuid"],
          "userIdpId":pendingRequest["userIdpId"],
          "status":"rejected"
        };

        Utils.sendPutRequest(changeStatusUrl, headers, rejectRequest);
        cy.wait(1000);
      });
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

  switchtOrg(orgName: string) {
    let isOrgAlreadySelected = false;
    cy.get(TestIds.orgPicker).then((orgPicker) => {
      orgPicker.find("p").each((index, element) => {
          if (element.textContent && element.textContent.includes(orgName)) {
            isOrgAlreadySelected = true;
            return false;
        }
      });

      if (isOrgAlreadySelected) {
        // If the Org reuested is already selected, force a temporary switch to another org
        // so that  requested the requested Org will trigger a re-fetch of the updated token
        // compatible with the requested Org. The updated token can be used in subsequent API
        // calls made to the requested Org.
        cy.log("Org already selected: " + orgName);

        cy.get("#org-picker").click();
        cy.getUnstable(`[data-value]`).each((org, index, orgsList) => {
          if (org.attr("data-value") !== orgName) {
            org.trigger("click");
            return false;
          }
        });
      }

      cy.get("#org-picker").should("be.visible").then(() => {
        cy.intercept({ method: "GET", url: `${ORGS_URL}/*` }).as("getOrgs");
      });

      cy.get("#org-picker").click();
      cy.getUnstable(`[data-value="${orgName}"]`).click();

      cy.wait("@getOrgs", VERY_SHORT_TIME).then((intercept) => {
        if (intercept.response === undefined || intercept.response.statusCode != 200) {
            throw new Error("Failed to receive orgs response");
        } else {
          const org = intercept.response.body.organization;
          const header = intercept.request.headers["authorization"] as string;
          const accessToken = header.replace("Bearer", "").trim();
          login.updateAccessToken(accessToken);
          login.updateOrgData(org);

          cy.log("Switched to org handle: " + login.getOrgHandle());
        }
      });
    });

    return cy.wrap({});
  }

  generateOnPremKey() {
    this.navigateToHome();
    this.navigateToSettings();
    this.navigateToOnPremKeys();

    cy.wait(5000);
    cy.get(TestIds.generateOnPremKey).should("exist").click();
    cy.get(TestIds.onPremKey).should("exist").type(Console.keyName);
    cy.get(TestIds.onPremKeyGenBtn).click();
    cy.get(TestIds.onPremKeyCopyBtn, { timeout: 120000 }).should("be.visible");
    Utils.getRenderedElement(TestIds.closeDialog).eq(0).click();
  }

  editOnPremKey() {
    cy.get("tbody").should("be.visible");
    cy.contains(Console.keyName)
      .parent()
      .find(TestIds.onPremKeyEditBtn)
      .click();
    cy.get(TestIds.onPremKey).should("exist");
    Console.keyName += "New";
    cy.get(TestIds.onPremKey).clear();
    cy.get(TestIds.onPremKey).type(Console.keyName);
    cy.get(TestIds.onPremKeySaveBtn).click();
  }

  regenerateOnPremKey() {
    cy.get("tbody").should("be.visible");
    cy.contains(Console.keyName).parent().find(TestIds.onPremKeyRegen).click();
    cy.get(TestIds.onPremKeyRegenBtn).should("exist").click();
    cy.contains("Copy on-premises key").next().click();
  }

  deleteOnPremKey() {
    cy.get("tbody").should("be.visible");
    cy.contains(Console.keyName).parent().find(TestIds.onPremKeyDelete).click();
    cy.get(TestIds.onPremKeyDeleteBtn).should("exist").click();
  }

  addUserStore(userStoreFile: string, env: Enums.Environment) {
    this.navigateToHome();
    this.navigateToSettings();
    this._orgSettings.addUserStore(userStoreFile, env);
  }

  inviteMember(email: string, roles: string[]) {
    this.navigateToHome();
    this.navigateToSettings();
    this._orgSettings.inviteMember(email, roles);
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

  private navigateToOnPremKeys() {
    cy.get('[data-cyid="nav-link-on-prem-keys-link-tabs-link-tab"]').click();
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

  private navigateToAPIManagement() {
    cy.get('[data-cyid="nav-link-api-management-link-tabs-link-tab"]')
      .should("be.visible")
      .click();
  }

  private navigateToSelfSignups() {
    cy.get('[data-cyid="nav-link-selfsignups-settings-link-tabs-link-tab"]')
      .should("be.visible")
      .click();
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
