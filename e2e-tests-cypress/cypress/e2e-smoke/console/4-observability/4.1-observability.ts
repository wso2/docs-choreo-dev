/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { ComponentObservePage } from "../../../support/console/pages/component/component-observe-page";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";

describe("Observability tests", () => {
  const FILE_ID = "observability";


  after(()=>{
    ChoreoHomePage.navigateToHome()
  })

  it("Deploy sample App", () => {
    const obsUrlRegexMatch = ComponentObservePage.deploySampleApp();
  });

  it("Test logs view", () => {
    ComponentObservePage.navigateToSampleApp();
    ComponentObservePage.verifyLogsView();
  });

  it("Test observability overview", () => {
    ComponentObservePage.navigateToSampleApp();
    ComponentObservePage.verifyObserveOverview();
  });

  it("Test diagnostics view", () => {
    ComponentObservePage.navigateToSampleApp();
    ComponentObservePage.verifyDiagnosticView();
  });


});
