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

import { TestHelper } from "../../pages/component/common/test-helper";
import { ServiceLeftMenu } from "../../ui-elements/left-menus/service-left-menu";
import { Service } from "../../entities/component/service-component";
import { DeploymentTrack } from "../deployment-track/deployment-track";
import { Types } from "../../../commons/types";
import { Enums } from "../../../commons/enums";
import { Byoc } from "../../entities/component/byoc-component";

export interface InvokeInfo {
  env: Enums.Environment;
  endpoint: string;
  resourcePath: string;
  method?: string;
  parentComponentId: string;
  key?: string;
  value?: string;
}

export interface GraphQLInvokeInfo {
  env: Enums.Environment;
  endpoint: string;
  query: string;
}

export interface TestServiceFeature {
  _testConsole(component: Service | Byoc, invokeInfo: InvokeInfo);
  _testGQL(component: Service | Byoc, invokeInfo: GraphQLInvokeInfo);
}

export function mixinTestService<T extends Types.Constructor>(
  base: T
): Types.Constructor<TestServiceFeature> & T {
  return class extends base {
    private sideMenu = new ServiceLeftMenu();
    private deploymentTrack = new DeploymentTrack();

    _testConsole(component: Service, invokeInfo: InvokeInfo) {
      this.sideMenu.navigateToTest();

      this.deploymentTrack.validate(component);

      return TestHelper.testManagedEndpoint(
        invokeInfo.env,
        invokeInfo.endpoint,
        invokeInfo.resourcePath,
        invokeInfo.method,
        invokeInfo.parentComponentId,
        invokeInfo.key,
        invokeInfo.value
      );
    }

    _testGQL(component: Service | Byoc, invokeInfo: GraphQLInvokeInfo) {
      // As a workaround to clear any previous queries/results in the GraphQL test console,
      // we navigate to the Postman test console and then back to the GraphQL test console
      this.sideMenu.navigateToPostman();
      this.sideMenu.navigateToTest();

      this.deploymentTrack.validate(component);

      TestHelper.testGraphQL(
        invokeInfo.env,
        invokeInfo.query,
        invokeInfo.endpoint
      );

      return TestHelper.getGraphQLResult();
    }
  };
}
