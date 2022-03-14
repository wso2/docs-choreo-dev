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

import { LoginPage } from "../../../support/console/pages/login-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { OnPremkeyComponent } from "../../../support/console/pages/component/common/onpremkey-components";

/// <reference types="cypress" />

describe("Generate on-prem keys", () => {
  const FILE_ID = "1.2-generate-onprem-key";

  before(() => {
    LoginPage.loginToChoreo(FILE_ID);
  });

  beforeEach(() => {
    ChoreoHomePage.navigateToSettings();
  });

  it("generate on-prem key", () => {
    OnPremkeyComponent.generateOnPremKey();
    OnPremkeyComponent.editOnPremKey();
    OnPremkeyComponent.regenerateOnPremKey();
    OnPremkeyComponent.deleteOnPremKey();
  });

  after(() => {
    ChoreoHomePage.logout(FILE_ID);
  });
});
