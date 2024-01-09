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

import { Enums } from "../../../commons/enums";
import { Types } from "../../../commons/types";
import { TestIds } from "../../constants/TestIds";
import { ProxyLeftMenu } from "../../ui-elements/left-menus/proxy-left-menu";
import { Proxy } from "../../concepts/component/proxy/proxy-component";
import { DeploymentTrack } from "../deployment-track/deployment-track";

export interface DevelopFeature {
  _removeResources(component: Proxy, resourceIds: string[]);

  _removeDefaultResources(component: Proxy);

  _addResources(component: Proxy, resourcePaths: Types.ResourcePath[]);

  _addPolicy(
    resourcePath: string,
    verb: string,
    policy: Enums.PolicyType,
    flow: Enums.Flow,
    name: string,
    value: string
  );

  _editPolicy(
    resourcePath: string,
    verb: string,
    flow: Enums.Flow,
    policyIndex: number,
    headerValue: string
  );
}

export function mixinDevelop<T extends Types.Constructor>(
  base: T
): Types.Constructor<DevelopFeature> & T {
  return class extends base {
    private sideMenu = new ProxyLeftMenu();
    private deploymentTrack = new DeploymentTrack();

    private httpVerbs: string[] = [
      "GET",
      "POST",
      "PUT",
      "PATCH",
      "DELETE",
      "HEAD",
      "OPTIONS",
    ];

    _removeResources(component: Proxy, resourceIds: string[]) {
      this.sideMenu.navigateToDevelop();

      this.deploymentTrack.validate(component);

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

    _removeDefaultResources(component: Proxy) {
      this.sideMenu.navigateToDevelop();

      this.deploymentTrack.validate(component);

      cy.get(TestIds.deleteAllOperations).click();
      cy.get(TestIds.undoDeleteAllOperations).should("be.visible");
    }

    _addResources(component: Proxy, resourcePaths: Types.ResourcePath[]) {
      this.sideMenu.navigateToDevelop();

      this.deploymentTrack.validate(component);

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

    _addPolicy(
      resourcePath: string,
      verb: string,
      policy: Enums.PolicyType,
      flow: Enums.Flow,
      name: string,
      value: string
    ) {
      this.sideMenu.navigateToPolicies();

      const header = this.getHeader(resourcePath, verb.toUpperCase());

      const buttons = `[id="/${resourcePath}/${verb.toUpperCase()}${flow.valueOf()}"] div[data-key] button`;

      cy.get(header).eq(0).click();
      cy.get(buttons).contains("Attach Policy").click();
      cy.get("button").contains(policy).click();
      cy.get('[name*="Name"]').should("be.visible").type(name);
      cy.get('[name*="Value"]').clear().type(value);
      cy.get("button").contains("Add").click();
      cy.get("button").contains("Save").click();
    }

    _editPolicy(
      resourcePath: string,
      verb: string,
      flow: Enums.Flow,
      policyIndex: number,
      headerValue: string
    ) {
      this.sideMenu.navigateToPolicies();

      const buttons = `[id="/${resourcePath}/${verb.toUpperCase()}${flow.valueOf()}"] div[data-key] button`;
      const header = this.getHeader(resourcePath, verb.toUpperCase());

      cy.get(header).eq(0).click();
      cy.get(buttons).eq(policyIndex).click();

      cy.get('[name*="Value"]').clear().type(headerValue);
      cy.get("button:not([disabled])").contains("Save").click();
      cy.wait(1000);
      cy.get("button").contains("Save").click();
    }

    private getHeader(resourcePath: string, verb: string) {
      return `[id="panel-/${resourcePath}/${verb}-header"]`;
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
  };
}
