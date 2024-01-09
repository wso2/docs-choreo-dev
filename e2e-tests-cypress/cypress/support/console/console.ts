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

/**
 * Represents the Choreo Console, the entry point for all tests.
 */
class Console {
  static projectNamePrefix = "autotest";

  login() {
    login.login();
    return cy.wrap({});
  }

  logout() {
    cy.request(login.getSignOutUrl()).then(() => {
      cy.clearAllSessionStorage();
      cy.clearLocalStorage();
      cy.clearAllCookies();
      cy.clearAllLocalStorage();
    });
  }

  navigateToHome() {
    cy.get('[data-cyid="organization-home"]').click();
  }

  createNewProject(description: string): Project {
    return new Project(this.generateProjectName(), description);
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
