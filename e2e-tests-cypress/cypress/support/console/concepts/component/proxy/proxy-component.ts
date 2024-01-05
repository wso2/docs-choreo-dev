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

import { Enums, UsagePlan } from "../../../../commons/enums";
import { VERY_SHORT_TIME } from "../../../../commons/timeouts";
import { Types } from "../../../../commons/types";
import { TestIds } from "../../../constants/TestIds";
import { Component } from "../component";
import { _ProxyDeployment } from "./proxy-deployment";
import { _ProxyDevelop } from "./proxy-develop";
import { _ProxyManagement } from "./proxy-management";
import { _ProxyOverview } from "./proxy-overview";
import { _ProxyTest } from "./proxy-test";

export class Proxy extends Component {
  private endpointUrl: string;
  private basePath: string;
  private hasPolicy: boolean = false;

  private overview = new _ProxyOverview();
  private develop = new _ProxyDevelop();
  private deployment = new _ProxyDeployment();
  private test = new _ProxyTest();
  private manage = new _ProxyManagement();

  constructor(
    name: string,
    version: string,
    basePath: string,
    endpointUrl: string,
    componentUrl: string
  ) {
    super(name, version);

    this.componentUrl = componentUrl;

    this.basePath = basePath;
    this.endpointUrl = endpointUrl;
  }

  getBasePath() {
    return this.basePath;
  }

  getEndpointUrl() {
    return this.endpointUrl;
  }

  isPolicyAdded() {
    return this.hasPolicy;
  }

  removeResources(resourceIds: string[]) {
    this.develop.removeResources(this, resourceIds);
  }

  removeDefaultResources() {
    this.develop.removeDefaultResources(this);
  }

  deploy() {
    this.deployment.deploy(this);
  }

  promote() {
    this.deployment.promote(this);
  }

  testSwaggerConsole(
    environment: Enums.Environment,
    resource: string,
    key?: string,
    value?: string
  ) {
    return this.test.testSwaggerConsole(
      this,
      environment,
      resource,
      key,
      value
    );
  }

  testCurl(
    environment: Enums.Environment,
    method: Enums.HTTPMethod,
    resource: string
  ) {
    return this.test.testCurl(this, environment, method, resource);
  }

  updateUsagePlans(plans: UsagePlan[]) {
    this.manage.updateUsagePlans(this, plans);
  }

  addVersion() {
    this.deployment.addNewVersion(this);
  }

  addResources(resourcePaths: Types.ResourcePath[]) {
    this.develop.addResources(this, resourcePaths);
  }

  addResponseFlowHeaderPolicy(
    resourcePath: string,
    verb: string,
    headerName: string,
    headerValue: string
  ) {
    this.develop.addPolicy(
      resourcePath,
      verb,
      Enums.PolicyType.setHeader,
      Enums.Flow.RESPONSE,
      headerName,
      headerValue
    );

    this.hasPolicy = true;
  }

  editResponseFlowHeaderPolicy(
    resourcePath: string,
    verb: string,
    policyIndex: number,
    headerValue: string
  ) {
    this.develop.editPolicy(
      resourcePath,
      verb,
      Enums.Flow.RESPONSE,
      policyIndex,
      headerValue
    );
  }

  publish() {
    this.manage.changeLifeCycleState(this, Enums.LifeCycleState.Publish);
  }

  navigateToDevPortal() {
    this.overview.navigateToDevPortal(this, "choreoe2etest");
  }

  enableCors() {
    this.manage.enableCors(this);
  }

  addPermissions(permissions: string[]) {
    this.manage.addPermissions(this, permissions);
  }

  applyAllPermissionsToResources(permissions: string[]) {
    this.manage.applyAllPermissionsToResources(this, permissions);
  }

  applyPermissionToResources(permission: string) {
    this.manage.applyPermissionToResources(this, permission);
  }

  deleteAllPermissionsFromResources(permissions: string[]) {
    this.manage.deleteAllPermissionsFromResources(this, permissions);
  }

  verifyConsumer(appName: string) {
    this.manage.verifyConsumer(appName);
  }
}
