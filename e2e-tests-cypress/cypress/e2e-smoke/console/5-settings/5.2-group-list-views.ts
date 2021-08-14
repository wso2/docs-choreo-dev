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

import { SETTINGS_TEXT } from "../../../support/common/constants";

/// <reference types="cypress" />

describe("Group List View", () => {
    let savedCookies;
    let memberEmail: string;
    let memberName: string;
    const groupName = "testGroup";
    const groupDescription = "This is a test group.";
    const groupTag = "testGroupTag"

    before(() => {
        cy.consoleUserLogin().then((user) => {
            memberName = user?.name;
            memberEmail = user?.email;
        });
        cy.getCookies().then((cookies) => {
            savedCookies = cookies;
        });
    });

    beforeEach(() => {
        cy.preserveCookiesForTest(savedCookies);
        cy.navigateFromHomePage(SETTINGS_TEXT);
        cy.get('[role="progressbar"]').should("not.exist");
        cy.get('[data-testid="/user-settings/organization/groups"]').click({ force: true });
    });

    after(() => {
        cy.userLogout();
    });

    it("Create a group", () => {
        cy.get('[data-testid="create-group-btn"]').click();
        cy.get('[data-testid="create-group-popup"]').should("be.visible");
        cy.log("Creating a group!");
        cy.get('[id="create-group-name"]').type(groupName, { force: true });
        cy.get('[id="filled-multiline-static"]').type(groupDescription, { force: true });
        cy.get('[id="create-group-tag"]').type(groupTag, { force: true });
        cy.get('[data-testid="create-group"]').click({ force: true });
        cy.contains("td", groupName).should("be.visible");
        cy.contains("td", groupTag).should("be.visible");
        cy.log("Group created successfully!");
    });

    it("Add a member to a group", () => {
        cy.get('[data-testid="/user-settings/organization/groups"]').click();
        cy.contains("td", groupName).click();
        cy.get('[id="tags-standard"]').click().type(memberName);
        cy.contains('[id="tags-standard-popup"]', memberName).should("be.visible");
        cy.contains('[id="tags-standard-popup"]', memberName).click();
        cy.get('[data-testid="add-member-btn"]').click();
        cy.contains("td", memberEmail).should("be.visible");
        cy.log("Member added to the group successfully");
        cy.log("Removing member from the group");
        cy.contains("tr", memberEmail).within(() => {
            cy.get('[data-testid="api-delete-btn"]').click();
        });
        cy.get('[data-testid="delete-member-btn"]').click();
        cy.contains("td",memberEmail).should('not.exist');
        cy.log("Member removed from the group successfully");
    });

    it("Delete created group", () => {
        cy.searchApps(groupName);
        cy.log("Removing the group");
        cy.contains("td", groupName).should("be.visible");
        cy.contains("td", groupName).trigger("mouseover");
        cy.get('[data-testid="api-delete-btn"]').click();
        cy.get('[data-testid="group-delete-btn"]').click();
        cy.contains("td",groupName).should('not.exist');
        cy.log("Group Removed successfully"!);
    });
});
