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
import { SHORT_TIME } from "../../../commons/timeouts";

export class _ProxyCreationWizard {
  createFromOASUrl(url: string) {
    cy.getUnstable(TestIds.OASUploadProxy).should("be.visible").click();
    cy.get(TestIds.oasUrlEntry).should("be.visible").type(url);
  }

  createFromOASFile(filepath: string) {
    cy.getUnstable(TestIds.OASUploadProxy).should("be.visible").click();
    cy.get(TestIds.upload).click();

    cy.fixture(filepath).as("oasFile");
    cy.get(TestIds.filepathEntry).selectFile("@oasFile", {
      force: true,
    });
  }

 
  enterProxyDetails(
    name: string,
    basePath: string,
    proxyInfo: ProxyInfo
  ): string {
    cy.get(TestIds.proxyName)
      .eq(0)
      .within(() => cy.get("input").clear().type(name));
      
    cy.get(TestIds.apiBasePath).within(() =>
      cy.get("input").clear().type(basePath)
    );

    let endpointUrl: string | undefined;

    if (proxyInfo.endpointUrl !== "") {
      cy.get(TestIds.Endpoint).within(() =>
        cy.get("input").clear().type(proxyInfo.endpointUrl)
      );
    } else {
      cy.get(TestIds.Endpoint).within(() =>
        cy
          .get("input")
          .invoke("val")
          .then((val) => {
            endpointUrl = val?.toString();
          })
      );
    }

    cy.getUnstable(TestIds.ProxyCreateButton).should("be.enabled").eq(1).click();

    cy.get(TestIds.backdropLoader).should("not.exist");
    cy.get(TestIds.progressBar, SHORT_TIME).should("not.exist").then(() => {
      // If an error occurs during creation the Create Proxy page will be displayed
      // which contains the Skip Source button. Therefore detect that the Proxy 
      // creation has failed.
      cy.get(TestIds.skipSource).should("not.exist");
    });

    if (endpointUrl !== undefined) {
      return endpointUrl;
    }

    return proxyInfo.endpointUrl;
  }
}
