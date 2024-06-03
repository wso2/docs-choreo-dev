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

import { ProxyInfo } from "../../entities/project/project";
import { TestIds } from "../../constants/TestIds";

export class _ProxyCreationWizard {
  createFromOASUrl(url: string) {
    cy.get(TestIds.oasUrlEntry).should("be.visible").type(url);
    cy.get(TestIds.next).should("be.visible").click();
  }

  createFromOASFile(filepath: string) {
    cy.get(TestIds.upload).click();

    cy.fixture(filepath).as("oasFile");
    cy.get(TestIds.filepathEntry).selectFile("@oasFile", {
      force: true,
    });
    cy.get(TestIds.next).should("be.visible").click();
  }

  enterProxyDetails(
    name: string,
    basePath: string,
    proxyInfo: ProxyInfo
  ): string {
    cy.get(TestIds.apiName).within(() =>
      cy.get("input").clear({ force: true }).type(name, { force: true })
    );
    cy.get(TestIds.apiVersion).clear().type(proxyInfo.version);
    cy.get(TestIds.apiBasePath).within(() =>
      cy.get("input").clear({ force: true }).type(basePath, { force: true })
    );

    let endpointUrl: string | undefined;

    if (proxyInfo.endpointUrl !== "") {
      cy.get(TestIds.apiEndpoint).within(() =>
        cy.get("input").clear().type(proxyInfo.endpointUrl)
      );
    } else {
      cy.get(TestIds.apiEndpoint).within(() =>
        cy
          .get("input")
          .invoke("val")
          .then((val) => {
            endpointUrl = val?.toString();
          })
      );
    }

    if (proxyInfo.isInternal !== undefined && proxyInfo.isInternal) {
      cy.get(TestIds.internalAccessMode).click();
    }
    cy.get(TestIds.createButton).should("be.enabled").click();

    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(TestIds.createTime).should("be.visible");

    if (endpointUrl !== undefined) {
      return endpointUrl;
    }

    return proxyInfo.endpointUrl;
  }
}
