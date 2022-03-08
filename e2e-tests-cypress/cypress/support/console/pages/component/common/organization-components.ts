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

export class OrganizationComponent {
  static invitationEmail = Cypress.env("invitationUserEmail");

  static navigateToMembers() {
    cy.get('[data-testid="/user-settings/organization/members"]').click();
  }

  static navigateToGroups() {
    cy.get('[data-testid="/user-settings/organization/groups"]').click();
    cy.wait(2000);
  }

  static navigateToCustomDomains() {
    cy.get(
      '[data-testid="/user-settings/organization/custom-domains"]'
    ).click();
  }

  static navigateToRoles() {
    cy.get('[data-cyid="nav-link-roles"]').click({ force: true });
  }

  static inviteMembers(email: string, ...roles) {
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

  static deleteMember(email: string) {
    cy.contains("td", email).trigger("mouseover");
    cy.get(
      '[class="MuiButtonBase-root MuiIconButton-root sc-hKwDye iZMHze"]'
    ).click();
    // cy.get('[data-testid="Delete User"]').should("be.visible");
    cy.get('[data-cyid="btn-confirmation-dialog-blue"]')
      .contains("Delete")
      .click();
    cy.contains("td", email).should("not.exist");
    cy.log("Member deleted successfully");
  }

  static selectPendingInvitation() {
    cy.get('[data-cyid="pending-invitation"]').click();
  }

  static selectMembers() {
    cy.get('data-testid="members"').click();
  }

  static deleteRecord(email: string) {
    cy.contains("td", email).trigger("mouseover");
    cy.get(
      '[class="MuiButtonBase-root MuiIconButton-root sc-hKwDye iZMHze"]'
    ).click();
    cy.get("button > span > h5").should("be.visible");
    cy.get("button > span > h5").contains("Delete").click();
  }

  private static addRoles(roles: string[]) {
    roles.forEach((v) => {
      cy.get('ul[class="MuiList-root MuiMenu-list MuiList-padding"] >li').each(
        ($e) => {
          if ($e.text().toLocaleLowerCase() === v.toLocaleLowerCase()) {
            cy.wrap($e)
              .invoke("attr", "aria-selected")
              .then((attr) => {
                if (!attr) {
                  cy.wrap($e).click();
                }
              });
          } else {
            cy.wrap($e)
              .invoke("attr", "aria-selected")
              .then((attr) => {
                if (attr) {
                  cy.wrap($e).click();
                }
              });
          }
        }
      );
    });
  }

  static createRole(
    roleName: string,
    roleDescription: string,
    roleTag: string
  ) {
    cy.get('[data-cyid="btn-create-role"]').click();
    cy.contains("Create Role").should("be.visible");
    cy.log("Creating a Role");
    cy.get('[data-cyid="text-field-role-name"]').type(roleName);
    cy.get('[data-cyid="text-field-role-description"]').type(roleDescription);
    cy.get('[data-cyid="chip-role-tag"]').type(roleTag + "{enter}");
    cy.get('[data-cyid="btn-role-create"]').click({ force: true });
    cy.get('[data-cyid="checkbox-role-permission-APIM-PUBLISHER"]').click();
    cy.get('[data-cyid="checkbox-role-permission-APIM-SUBSCRIBER"]').click();
    cy.log("Created Roles APIM-PUBLISHER and APIM-SUBSCRIBER");
    cy.get('[data-cyid="btn-create"]').click();
    cy.get('[data-cyid="btn-create"]').should("not.exist");
  }

  static addMembertoRole(roleName: string) {
    cy.get('[data-cyid="search-app"]').clear().type(roleName);
    cy.get(
      '[class="MuiTableCell-root MuiTableCell-body MuiTableCell-alignLeft"]'
    ).should("contain", roleName);
    cy.contains("td", roleName).click();
    cy.get('[data-cyid="btn-add-member-to-role"]').click();
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
    cy.get('[data-cyid="search-app"]').clear().type(roleName);
    cy.contains("td", roleName).should("be.visible");
    this.deleteSelectedRole(roleName);
  }

  static deleteRoleIfExists(roleName: string) {
    cy.get('[data-cyid="search-app"]').clear().type(roleName);
    cy.wait(2000);
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
}
