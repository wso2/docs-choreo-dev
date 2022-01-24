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

export class Utils {
  static projectNamePrefix = 'e2eproject';

  static keyNamePrefix = 'e2eOnPremkey';

  /**
   * Create name for app.
   *
   * @returns true name for a new app
   */
  static generateProjectName() {
    return this.projectNamePrefix + Date.now();
  }

  /**
   * Create name for on-prem key.
   *
   * @returns true name for a new on-prem key
   */
  static generateKeyName(name: string) {
    return this.keyNamePrefix + Date.now() + name;
  }

  static sendRequest(
    method: string,
    url: string,
    headers: any={},
    body: any = {}
  ) {
    const request = {
      method,
      url,
      headers,
      body,
    };
cy.log(JSON.stringify(request))


    return cy.request(request).then((res) => {
      return cy.wrap({ body: res.body, status: res.status });
    });
  }
}
