/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { TestIds } from "../../constants/TestIds";
import { Enums } from "../../../commons/enums";
import { Utils } from "../../../commons/utils";

export class OrganizationSettings {
  addUserStore(userStoreFile: string, env: Enums.Environment) {
    cy.get(TestIds.orgAppSecurity).click();

    cy.get(TestIds.builtInIdpCard).within(() => {
      cy.get(TestIds.linkBtn).click();
    });

    cy.get(TestIds.choreoIdpEnvs).should("be.visible");

    cy.get("body").then((bdy) => {
      if (bdy.find(TestIds.idpEnv(env)).length > 0) {
        cy.get(TestIds.idpEnv(env)).should("be.visible").click();
      } else {
        cy.get(TestIds.idpEnvUS(env)).should("be.visible").click();
      }
    });

    cy.get(TestIds.tableTitle).find("tbody tr").should("have.length", 1);

    cy.fixture(userStoreFile).as("users");
    cy.get(TestIds.uploadUserStoreCard).within(() => {
      cy.get('input[type="file"]').selectFile("@users", { force: true });
    });

    cy.get(TestIds.uploadUserStoreFile).click();
  }

  inviteMember(email: string, roles: string[]) {
    cy.get(TestIds.inviteUser).should("be.visible").click();

    cy.get(TestIds.tagEmails)
      .should("be.visible")
      .within(() => {
        cy.get('input[type="text"]')
          .should("be.visible")
          .type(email)
          .type("{enter}");
      });

    cy.get(TestIds.selectGroups).click();

    roles.forEach((v) => {
      cy.getUnstable(TestIds.groupSelectPopup)
        .contains(v)
        .parents("li")
        .find("input")
        .scrollIntoView()
        .click();
    });

    cy.get("body").type("{esc}");

    cy.get(TestIds.inviteUserDialog).click({ force: true });
    cy.get(TestIds.inviteUserDialog).should("not.exist");

    cy.get(TestIds.pendingInvites).should("be.visible").click();
    cy.get(TestIds.searchIcon).click();
    cy.get(TestIds.searchField).type(email);
    cy.contains(email).should("be.visible");
  }

  addRole(roleName: string, roleDescription: string, roleTag: string) {
    cy.get(TestIds.createRole).click();
    cy.get(TestIds.roleName).should("be.visible").type(roleName);
    cy.get(TestIds.roleDescription).type(roleDescription);
    cy.get(TestIds.roleTags).type(roleTag + "{enter}");

    cy.get(TestIds.publisherPermission).find("input").check();
    cy.get(TestIds.subscriberPermission).find("input").check();

    cy.get(TestIds.createRole).click();
    cy.get(TestIds.roleName).should("not.exist");
  }

  addGroup(groupName: string, groupDescription: string) {
    cy.get(TestIds.createGroup).should("be.visible").click();
    cy.get(TestIds.groupDetail).eq(0).should("be.visible").type(groupName);
    cy.get(TestIds.groupDetail).eq(1).type(groupDescription);
    cy.get(TestIds.dialogCreateBtn).should("be.enabled").click();
    cy.get(TestIds.dialogCreateBtn).should("not.exist");
  }

  checkUserIsInGroup(userEmail: string, group: string) {
    this.search(userEmail);
    cy.get(TestIds.userTable)
      .should("be.visible")
      .contains("td", userEmail)
      .parent("tr")
      .contains("td", new RegExp(`${group}`))
      .should("be.visible");
  }

  deleteGroupIfExists(groupName: string) {
    cy.get(TestIds.progressBar).should("not.exist");
    this.search(groupName);
    cy.get(TestIds.groupTable)
      .should("be.visible")
      .find("td")
      .then(($role) => {
        if (!$role.text().includes("No records to display")) {
          cy.contains("td", groupName).should("be.visible");
          this.deleteSelectedGroup(groupName);
        }
      });
  }

  deleteGroup(groupName: string) {
    cy.get(TestIds.createGroup).should("be.visible");
    this.search(groupName);
    cy.get(TestIds.groupTable)
      .should("be.visible")
      .contains("td", groupName)
      .should("be.visible");
    this.deleteSelectedGroup(groupName);
  }

  addRolesToGroup(roles: string[], groupName: string) {
    this.search(groupName);
    cy.get(TestIds.groupTable)
      .should("be.visible")
      .contains("td", groupName)
      .should("be.visible")
      .click();
    for (let i = 0; i < roles.length; i++) {
      this.addRoleToGroup(roles[i]);
    }
  }

  removeRolesFromGroup(roles: string[], groupName: string) {
    this.search(groupName);
    cy.get(TestIds.groupTable)
      .should("be.visible")
      .contains("td", groupName)
      .should("be.visible")
      .click();

    for (let i = 0; i < roles.length; i++) {
      this.removeRoleFromGroup(roles[i]);
    }
  }

  checkRolesInGroup(roles: string[], groupName: string) {
    this.search(groupName);
    cy.get(TestIds.groupTable)
      .should("be.visible")
      .contains("td", groupName)
      .should("be.visible")
      .click();
    cy.get(TestIds.groupRolesTab).click();

    for (let i = 0; i < roles.length; i++) {
      cy.get(TestIds.groupTable)
        .should("be.visible")
        .contains("td", roles[i])
        .should("be.visible");
    }
  }

  private addRoleToGroup(roleName: string) {
    cy.get(TestIds.groupRolesTab).click();
    cy.get(TestIds.addRoleToGroup).click();
    cy.get(TestIds.roleToGroupSelect).should("be.visible").click();
    cy.get(TestIds.roleToGroupSelect).find("input").type(roleName);
    cy.contains("li", roleName).should("be.visible").click();
    cy.get(TestIds.roleToGroupSelect).find("input").type("{esc}");
    cy.get(TestIds.addRoleToGroupPopup).should("be.enabled").click();
    cy.get(TestIds.roleToGroupSelect).should("not.exist");

    cy.get(TestIds.groupTable)
      .should("be.visible")
      .contains("td", roleName)
      .should("be.visible");
  }

  private removeRoleFromGroup(roleName: string) {
    cy.get(TestIds.groupRolesTab).click();

    cy.get(TestIds.groupTable)
      .should("be.visible")
      .contains("td", roleName)
      .parent("tr")
      .find(TestIds.removeRoleFromGroup)
      .click();

    cy.get(TestIds.removeRoleFromGroupPopup).should("be.visible").click();
    cy.get(TestIds.removeRoleFromGroupPopup).should("not.exist");

    this.search(roleName);
    cy.get(TestIds.groupTable)
      .should("be.visible")
      .contains("td", roleName)
      .should("not.exist");
  }

  private removeAllRolesFromGroup() {
    cy.get(TestIds.groupRolesTab).click();

    cy.get(TestIds.groupTable)
      .should("be.visible")
      .find("tbody")
      .find("tr")
      .eq(0)
      .find("td")
      .eq(0)
      .then(($role) => {
        if (!$role.text().includes("No records to display")) {
          cy.get(TestIds.groupTable)
            .find("tbody")
            .find("tr")
            .eq(0)
            .find(TestIds.removeRoleFromGroup)
            .click();
          cy.get(TestIds.removeRoleFromGroupPopup).should("be.visible").click();
          cy.get(TestIds.removeRoleFromGroupPopup).should("not.exist");
          this.removeAllRolesFromGroup();
        } else {
          cy.get(TestIds.groupDetailsBack).click();
        }
      });
  }

  addUserToGroup(userEmail: string, groupName: string) {
    this.search(groupName);
    cy.get(TestIds.groupTable)
      .should("be.visible")
      .contains("td", groupName)
      .should("be.visible")
      .click();
    cy.get(TestIds.groupUserTab).click();
    cy.get(TestIds.addUserToGroup).click();
    cy.get(TestIds.userToGroupSelect).should("be.visible").click();
    cy.get(TestIds.userToGroupSelect).find("input").type(userEmail);
    cy.get(TestIds.userSelect).within(() => {
      cy.contains(new RegExp(`${Utils.escapeRegExp(userEmail)}`))
        .should("be.visible")
        .click();
    });
    cy.get(TestIds.userToGroupSelect).find("input").type("{esc}");
    cy.get(TestIds.addUserToGroupPopup).should("be.enabled").click();
    cy.get(TestIds.userToGroupSelect).should("not.exist");
  }

  deleteRoleIfExists(roleName: string) {
    cy.get(TestIds.progressBar).should("not.exist");
    this.search(roleName);
    cy.get(TestIds.roleTable)
      .should("be.visible")
      .find("td")
      .then(($role) => {
        if (!$role.text().includes("No records to display")) {
          cy.contains("td", roleName).should("be.visible");
          this.deleteSelectedRole(roleName);
        }
      });
  }

  deleteRole(roleName: string) {
    cy.get(TestIds.createRole).should("be.visible");
    this.search(roleName);
    cy.get(TestIds.roleTable)
      .should("be.visible")
      .contains("td", roleName)
      .should("be.visible");
    this.deleteSelectedRole(roleName);
  }

  private search(searchString: string) {
    cy.get(TestIds.searchIcon).click();
    cy.get(TestIds.searchField).type(searchString);
  }

  private deleteSelectedRole(roleName: string) {
    cy.contains("td", roleName).trigger("mouseover");
    cy.get(TestIds.deleteRole).click();
    cy.get(TestIds.confirmDelete).click();
    cy.contains("td", roleName).should("not.exist");
  }

  private deleteSelectedGroup(groupName: string) {
    cy.contains("td", groupName).trigger("mouseover");
    cy.get(TestIds.editGroup).click();
    this.removeAllRolesFromGroup();
    cy.get(TestIds.deleteGroup).click();
    cy.get(TestIds.deleteGroupConfirm).click();
    cy.contains("td", groupName).should("not.exist");
  }
}
