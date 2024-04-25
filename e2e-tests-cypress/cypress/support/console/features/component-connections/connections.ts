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

import { Types } from "../../../commons/types";
import { VERY_SHORT_TIME } from "../../../commons/timeouts";
import { TestIds } from "../../constants/TestIds";
import { Service } from "../../entities/component/service-component";
import { WebApp } from "../../entities/component/webapp-component";
import { ServiceLeftMenu } from "../../ui-elements/left-menus/service-left-menu";


const managedAuthBackendServiceName = Cypress.env("managedAuthBackendServiceName");
const connectionSearchRequestRegex = new RegExp(`.*query=${managedAuthBackendServiceName}.*`);

export interface ConnectionsFeature {
  _createConnections(component: Service | WebApp): void;
  _copyConnectionUrl(): Cypress.Chainable<string>;
}

export function mixinConnections<T extends Types.Constructor>(
  base: T
): Types.Constructor<ConnectionsFeature> & T {
  return class extends base {
    private sideMenu = new ServiceLeftMenu();

    _createConnections(component: Service | WebApp) {
      this.sideMenu.navigateToDependencies();
      this.addConnection(component, "TestConnection");
    }

    _copyConnectionUrl(): Cypress.Chainable<string>{
      this.sideMenu.navigateToDependencies();
      cy.contains("TestConnection").should("be.visible").click();
      return cy.get(TestIds.copyConnectionUrlBox).should("be.visible").find("input").invoke("val").then((val) => {
        cy.log("copied connecton url: " + val);
        if (typeof val === "string") {
          return cy.wrap(val)
        }
        return cy.then(() => { throw new Error("Connection URL is not a string"); });
      });
    }

    private addConnection(component: Service | WebApp, connectionName: string) {
      cy.get(TestIds.addConnectionButton).should("be.visible")
      cy.get(TestIds.addConnectionButton).click();
      cy.intercept(connectionSearchRequestRegex).as('getReadingListSvc');
      cy.get(TestIds.connectionSearchBar).type(managedAuthBackendServiceName);
      cy.wait('@getReadingListSvc', VERY_SHORT_TIME);
      cy.get(TestIds.ConnectionCard).contains(managedAuthBackendServiceName).should("be.visible").click();
      cy.get(TestIds.connectionNameInput)
        .should("be.visible")
        .type(connectionName);
      cy.get(TestIds.connectionCreateButton).click();
      cy.get(TestIds.runNowNotification).contains(
        "Connection configuration added successfully"
      );
    }
  };
}
