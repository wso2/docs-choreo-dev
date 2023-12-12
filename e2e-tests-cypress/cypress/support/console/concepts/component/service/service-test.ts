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

import { Enums } from "../../../../commons/enums";
import { TestHelper } from "../../../pages/component/common/test-helper";
import { TestIds } from "../../../constants/TestIds";
import { ServiceLeftMenu } from "../../../ui-elements/left-menus/service-left-menu";
import { Service } from "./service-component";
import { ServiceUtils } from "./service-utils";

export interface InvokeInfo {
  env: Enums.Environment;
  endpoint: string;
  resourcePath: string;
  method?: string;
  parentComponentId: string;
  key?: string;
  value?: string;
}

export class _ServiceTest {
  private sideMenu = new ServiceLeftMenu();

  testConsole(component: Service, invokeInfo: InvokeInfo) {
    this.sideMenu.navigateToTest();

    ServiceUtils.validateDeploymentTrack(component);

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
}
