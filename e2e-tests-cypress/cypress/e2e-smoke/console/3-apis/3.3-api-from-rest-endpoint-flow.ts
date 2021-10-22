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

import { generateApiName, getSelectedOrgHandle } from "../../../support/common/utils";
import { API } from '../../../support/console/common/component/apis';
import { APIDeployment } from '../../../support/console/common/component/apis/api-deployment';
import { APIDevelop } from '../../../support/console/common/component/apis/api-develop';
import { APIPublish } from '../../../support/console/common/component/apis/api-publish';
import { APITest } from '../../../support/console/common/component/apis/api-test';
import { HTTPMethod } from '../../../support/console/common/component/enums/http-method-enum';
import { SubscriptionsPlan } from '../../../support/console/common/component/enums/subscription-plans';
import { Home } from '../../../support/console/common/component/home';
import { SwaggerUI } from '../../../support/console/common/swagger-ui-component';

describe("API creation from an existing endpoint", () => {
    const API_NAME = generateApiName('CYE2E');
    const API_VERSION = 'V0.0.1';
    const API_ENDPOINT = 'https://jsonplaceholder.typicode.com';
    const OPERATION_TARGET = '/users';
    const ALLOWED_ORIGINS = ["https://127.0.0.1"]
    const ALLOWED_HEADERS = ["tenantId"]
    const ALLOWED_METHODS = [HTTPMethod.TRACE, HTTPMethod.HEAD]

    before(() => {
        cy.consoleUserLogin().then((user) => {
            const org = user?.orgs.find((org) => org.handle === getSelectedOrgHandle(user));
            cy.clearAllTestData(org);
        });

    });

    it("Create API from existing endpoint and deploy and publish", () => {
        cy.log("Visiting API listing");
        Home.selectAPI()
        API.createAPI()
        API.createProxyAPI()
        API.designANewRESTAPI(API_NAME, API_VERSION, "", API_ENDPOINT)
        cy.verifyAppName(`${API_NAME}(${API_VERSION})`)
        APIDevelop.configureRuntimeOptions(true, false, ALLOWED_ORIGINS, ALLOWED_HEADERS, ALLOWED_METHODS)
        APIDevelop.addResources(OPERATION_TARGET, HTTPMethod.GET)
        APIDevelop.updateSubscriptionPlan(SubscriptionsPlan.GOLD, SubscriptionsPlan.BRONZE)
        APIDeployment.navigateToDeployment()
        APIDeployment.createRevisionAndDeploy()
        APITest.testAPI()
        SwaggerUI.SelectResource(HTTPMethod.GET, OPERATION_TARGET)
        SwaggerUI.TryoutAPI()
        SwaggerUI.ExecuteResourceFunction()
        SwaggerUI.GetResponse()
        APIPublish.navigateToAPIPublish()
        APIPublish.publishAPI()
    });

    // TODO: add test case to check API delete flow

    after(() => {
        cy.userLogout();
    });
});
