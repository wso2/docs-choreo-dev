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
/// <reference types="cypress-xpath" />

import { LoginPage } from "../../support/devportal/pages/login/login-page";
import { HomePage } from "../../support/devportal/pages/home/home-page";
import { Apis } from "../../support/devportal/pages/apis/apis-home";
import { ApiOverview } from "../../support/devportal/pages/apis/api-overview";

describe("API overview comment and rating scenario", () => {
  before(() => LoginPage.loginToDevportal());
  beforeEach(() => {
    HomePage.navigateToApisPage();

    Apis.navigateToApiOverview("e2etestdevportalsample");
  });
  after(() => HomePage.logout());

  it("Add and delete comment for the API", () => {
    ApiOverview.addCommentToApi("Test comment from Cypress Test Runner");
    ApiOverview.deleteComment();
  });

  it("Add and modify ratings of the API", () => {
    ApiOverview.openRatings();
    ApiOverview.addRatings();
    ApiOverview.validateRating();
  });
});
