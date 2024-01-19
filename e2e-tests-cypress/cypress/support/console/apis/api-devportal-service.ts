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

import { ONE_HOUR } from "../../commons/constants";
import { AUTH_HEADER2 } from "../../commons/http";
import { Utils } from "../../commons/utils";
import { appNamePrefix } from "../../devportal/utils";
import { login } from "../entities/login/login";

export class ApiDevPortalService {
  private static devPortalUrl = `${Cypress.env(
    "apimSvcURL"
  )}/api/am/devportal/v2`;

  static deleteApplications() {
    const uuid = login.getOrgUuid();
    Utils.sendGetRequest(
      ApiDevPortalService.devPortalUrl +
        `/applications/?organizationId=${uuid}`,
      AUTH_HEADER2()
    ).then((response) => {
      cy.log("Application count: " + response.body.count);
      let deletableCount = 0;

      response.body.list.forEach((app) => {
        if (
          app.name.includes(appNamePrefix) ||
          app.name.includes(Utils.componentNamePrefix)
        ) {
          if (Date.now() - parseInt(app.createdTime) > ONE_HOUR) {
            Utils.sendDeleteRequest(
              `${ApiDevPortalService.devPortalUrl}/applications/${app.applicationId}?organizationId=${uuid}`,
              AUTH_HEADER2()
            );
            deletableCount++;
          }
        }
      });

      cy.log("Number of deletable applications: " + deletableCount);
    });
  }
}
