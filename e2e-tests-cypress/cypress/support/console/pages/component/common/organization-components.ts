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

/// <reference types="cypress" />

import { OK } from "../../../../commons/http";
import { Utils } from "../../../../commons/utils";

export class OrganizationComponent {
  static navigateToMembers() {
    cy.get('[data-cyid="nav-link-members"]').click();
  }

  static navigateToRoles() {
    cy.get('[data-cyid="nav-link-roles"]').click();
  }

  static navigateToRoleMapping() {
    cy.get('[data-cyid="nav-link-role-mappings"]').click({ force: true });
  }

  static verifyEmailIsNotDisplayed(email: string) {
    cy.contains(email).should("not.exist");
  }

  static verifyEmailIsDisplayed(email: string) {
    cy.contains(email).should("be.visible");
  }

  static verifyGroupNameIsDisplayed(groupName: string) {
    cy.get(`td[value="${groupName}"]`).should("exist");
  }

  static verifyGroupNameIsNotDisplayed(groupName: string) {
    cy.get(`td[value="${groupName}"]`).should("not.exist");
  }

  static inviteMembers(email: string, ...roles: string[]) {
    cy.wait(300);
    cy.get('[data-cyid="invite-members"]').click();
    cy.wait(300);
    cy.get('[data-cyid="chip-email-addresses"] div div input')
      .should("be.visible")
      .type(email);
    cy.get('[data-cyid="chip-email-addresses"] div div input')
      .should("be.visible")
      .type("{enter}");
    cy.get('[data-cyid="select-roles"]').click();
    this.addRoles(roles);
    cy.get("body").type("{esc}");
    cy.get('[data-cyid="btn-invite"]').click({ force: true });
    cy.get('[data-cyid="btn-invite"]').should("not.exist");
    cy.log("Invitation sent successfully");
  }

  static addMappings(groupName: string, roles: string[]) {
    cy.wait(300);
    cy.get('[data-cyid="add-mappings"]').click();
    cy.get('[data-cyid="text-field-add-group-name"]')
      .should("be.visible")
      .type(groupName);
    cy.get('[data-cyid="text-field-add-group-name"]').should("be.visible");
    cy.get('[data-cyid="select-roles"]').should("be.visible").click();
    cy.wait(3000);
    this.addRoles(roles);
    cy.get("body").type("{esc}");
    cy.get('[data-cyid="btn-add-mapping"]').click({ force: true });
    cy.get('[data-cyid="btn-add-mapping"]').should("not.exist");
    cy.log("Group role mapping added successfully");
  }

  static deleteMember(email: string) {
    cy.contains("td", email).trigger("mouseover");
    cy.get("tr>td>div>button").click({ force: true });
    cy.get('[data-cyid="btn-confirmation-dialog-blue"]')
      .contains("Delete")
      .click();
    cy.contains("td", email).should("not.exist");
    cy.log("Member deleted successfully");
  }

  static deleteInvitation(email: string) {
    const { handle } = Cypress.env("userData");
    const token = Cypress.env("apim_token");

    const headers = {
      authorization: `Bearer ${token}`,
    };
    const deletePendingInvitation = `${Cypress.env(
      "appSvcURL"
    )}/v2/orgs/${handle}/invitations?email=${email}`;
    const getUsers = `${Cypress.env("appSvcURL")}/v2/orgs/${handle}/users`;

    Utils.sendGetRequest(getUsers, headers).then((res) => {
      const list = res.body.list as [];
      const user = list.find((u) => u["email"] === email);
      cy.log(JSON.stringify(user));

      if (user) {
        const { idpId } = user;
        const deleteUserRequest = `${Cypress.env(
          "appSvcURL"
        )}/v2/orgs/${handle}/users/${idpId}`;

        Utils.sendDeleteRequest(deleteUserRequest, headers).then((res) => {
          if (res.status === OK) {
            cy.log("Deleted Invited User");
          } else {
            cy.log("User Has Not Invited Or Error");
          }
        });
      }
      Utils.sendDeleteRequest(deletePendingInvitation, headers).then((res) => {
        if (res.status === OK) {
          cy.log("Deleted Invited User");
        } else {
          cy.log("User Has Not Invited Or Error");
        }
      });
    });
  }

  static selectPendingInvitation() {
    cy.get('[data-testid="pending-invitation"]').click();
  }

  private static addRoles(roles: string[]) {
    roles.forEach((v) => {
      Utils.getRenderedElement("ul>li>div>span").each((e) => {
        if (e.text() === v) {
          cy.wrap(e).scrollIntoView().click();
        }
      });
    });
  }

  static createRole(
    roleName: string,
    roleDescription: string,
    roleTag: string
  ) {
    cy.get('[data-cyid="btn-create-role-button"]').click();
    cy.contains("Create Role").should("be.visible");
    cy.log("Creating a Role");
    cy.get('[data-cyid="text-field-role-name"]').type(roleName);
    cy.get('[data-cyid="text-field-role-description"]').type(roleDescription);
    cy.get('[data-cyid="chip-role-tag"]').type(roleTag + "{enter}");
    cy.get('[data-cyid="btn-role-create"]').click({ force: true });

    cy.get(
      '[data-cyid="checkbox-role-permission-APIM-PUBLISHER"]>span>input'
    ).check();
    cy.get('[data-cyid="checkbox-role-permission-APIM-SUBSCRIBER"]>span>input')
      .focus()
      .check();

    cy.log("Created Roles APIM-PUBLISHER and APIM-SUBSCRIBER");
    cy.get('[data-cyid="btn-create"]').click();
    cy.get('[data-cyid="btn-create"]').should("not.exist");
  }

  static addMembertoRole(roleName: string) {
    cy.get('[data-cyid="search-app"]').clear().type(roleName);
    cy.get('[data-cyid="roles-table-rows"]').should("contain", roleName);
    cy.get('[data-cyid="roles-table-rows"]').contains("td", roleName).click();
    cy.get('[data-cyid="btn-add-member-to-role-button"]').click();
    cy.get('[data-cyid="select_members_to_role"]').click();

    const userData = Cypress.env("userData");
    let displayName = userData["displayName"];

    if (displayName.includes("@")) {
      displayName = displayName.split("@")[0];
    }

    cy.get(`[data-cyid^="${displayName}"]`).click({ force: true });

    cy.get("body").type("{esc}");
    cy.get('[data-cyid="btn-add-member"]').click();
    cy.get('[data-cyid="btn-add-member"]').should("not.exist");
    cy.contains("td", userData["userEmail"]).should("be.visible");
    cy.log("Member added to the role successfully");
  }

  static deleteCreatedRole(roleName: string) {
    cy.get('[data-cyid="btn-create-role-button"]').should("be.visible");
    cy.get('[data-cyid="search-app"]').clear().type(roleName);
    cy.contains("td", roleName).should("be.visible");
    this.deleteSelectedRole(roleName);
  }

  static deleteRoleIfExists(roleName: string) {
    cy.get('[data-cyid="search-app"]').clear().type(roleName);
    cy.get("td").then(($role) => {
      if (!$role.text().includes("No records to display")) {
        cy.contains("td", roleName).should("be.visible");
        this.deleteSelectedRole(roleName);
      }
    });
  }

  private static deleteSelectedRole(roleName: string) {
    cy.contains("td", roleName).trigger("mouseover");
    cy.get('[data-cyid="btn-delete-role"]').click();
    cy.log("Deleting the created Role");
    cy.get('[data-cyid="btn-confirmation-dialog-blue"]').click();
    cy.contains("td", roleName).should("not.exist");
    cy.log("Role deleted successfully"!);
  }

  static deleteCreatedMapping(groupName: string) {
    cy.wait(2000);
    cy.contains("td", groupName).should("be.visible");
    this.deleteSelectedMapping(groupName);
  }

  private static deleteSelectedMapping(groupName: string) {
    cy.contains("td", groupName).trigger("mouseover");
    cy.get('[data-cyid="btn-delete-mapping"]').click();
    cy.log("Deleting the created Mapping");
    cy.get('[data-cyid="btn-confirmation-dialog-red"]').click();
    cy.contains("td", groupName).should("not.exist");
    cy.log("Group role mapping deleted successfully"!);
  }

  static updateMappings(
    groupName: string,
    oldRoles: string[],
    newRoles: string[]
  ) {
    const roles = oldRoles.concat(newRoles);
    cy.get('[data-cyid="search-app"]').clear().type(groupName);
    cy.contains("td", groupName).should("be.visible");
    this.updateSelectedMapping(groupName, roles);
  }

  private static updateSelectedMapping(groupName: string, roles: string[]) {
    cy.contains("td", groupName).trigger("mouseover");
    cy.get('[data-cyid="btn-edit-mapping"]').click();
    cy.get('[data-cyid="text-field-update-group-name"]').should("be.visible");
    cy.get('[data-cyid="select-roles"]').should("be.visible").click();
    cy.wait(3000);
    this.addRoles(roles);
    cy.get("body").type("{esc}");
    cy.get('[data-cyid="btn-update-mapping"]').click({ force: true });
    cy.get('[data-cyid="btn-update-mapping"]').should("not.exist");
    cy.log("Group role mapping updated successfully");
  }
}
