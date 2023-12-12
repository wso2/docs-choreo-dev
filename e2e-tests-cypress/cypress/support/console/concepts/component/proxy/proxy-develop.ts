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

import { Types } from "../../../../commons/types";
import { TestIds } from "../../../constants/TestIds";
import { ProxyLeftMenu } from "../../../ui-elements/left-menus/proxy-left-menu";
import { Proxy } from "./proxy-component";
import { ProxyUtils } from "./proxy-utils";

export class _ProxyDevelop {
  private sideMenu = new ProxyLeftMenu();

  private httpVerbs: string[] = [
    "GET",
    "POST",
    "PUT",
    "PATCH",
    "DELETE",
    "HEAD",
    "OPTIONS",
  ];

  removeResources(component: Proxy, resourceIds: string[]) {
    this.sideMenu.navigateToDevelop();

    ProxyUtils.validateDeploymentTrack(component);

    cy.get("body").then((body) => {
      const opCount = body.find(TestIds.operation).length;

      for (let i = 0; i < opCount; i++) {
        cy.get(TestIds.operation)
          .eq(i)
          .invoke("attr", "id")
          .then((id) => {
            resourceIds.forEach((resourceId) => {
              if (id === resourceId) {
                cy.get(TestIds.operation)
                  .eq(i)
                  .within(() => {
                    cy.get(TestIds.deleteIcon).click();
                  });
              }
            });
          });
      }

      cy.get(TestIds.save)
        .should("be.enabled")
        .contains("Save")
        .click({ force: true });

      cy.get(TestIds.backdropLoader).should("not.exist");
    });
  }

  removeDefaultResources(component: Proxy) {
    this.sideMenu.navigateToDevelop();

    ProxyUtils.validateDeploymentTrack(component);

    const wildCardResources = [
      "panel-/*/get-header",
      "panel-/*/put-header",
      "panel-/*/post-header",
      "panel-/*/delete-header",
      "panel-/*/patch-header",
    ];

    let resourceMatches = [];

    cy.get("body").then((body) => {
      const opCount = body.find(TestIds.operation).length;

      for (let i = 0; i < opCount; i++) {
        cy.get(TestIds.operation)
          .eq(i)
          .invoke("attr", "id")
          .then((id) => {
            wildCardResources.forEach((resourceId) => {
              if (id === resourceId) {
                resourceMatches.push(id);
              }
            });
          });
      }
    });

    expect(resourceMatches).to.be.eq(wildCardResources);

    cy.get(TestIds.deleteAllOperations).click();
    cy.get(TestIds.undoDeleteAllOperations).should("be.visible").wait(3000);
    cy.get(TestIds.save).should("be.enabled");
  }

  addResources(component: Proxy, resourcePaths: Types.ResourcePath[]) {
    this.sideMenu.navigateToDevelop();

    ProxyUtils.validateDeploymentTrack(component);

    resourcePaths.forEach((resourcePath) => {
      this.addHTTPVerb(resourcePath.verbs);
      this.addResource(resourcePath.path);
    });

    cy.get(TestIds.save).should("be.enabled").click();
    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(TestIds.save).should("be.disabled");

    cy.get(
      `[id="panel-/${
        resourcePaths[0].path
      }/${resourcePaths[0].verbs[0].toLowerCase()}-header"]`
    ).should("exist");
  }

  private addResource(path: string) {
    cy.get(TestIds.uriPatternEntry).type(path, {
      parseSpecialCharSequences: false,
    });
    cy.get(TestIds.add).click({ force: true });
    cy.get(TestIds.save).should("be.enabled");
    cy.get(TestIds.uriPatternEntry).should("have.value", "");
  }

  private addHTTPVerb(verbs: string[]) {
    cy.get('[data-cyid="verb-selector-multi-select"]').click();
    verbs.forEach((verb) => {
      let id = `verb-selector-option-${this.httpVerbs.indexOf(verb)}`;
      cy.get(`#${id}`).click().wait(1000);
    });
    cy.get("body").type("{esc}");
  }
}
