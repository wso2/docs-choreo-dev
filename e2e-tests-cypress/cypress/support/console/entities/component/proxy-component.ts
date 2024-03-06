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

import { ApiVisibility, Enums, UsagePlan } from "../../../commons/enums";
import { Types } from "../../../commons/types";
import { Component } from "./component";
import { mixinProxyDeploy } from "../../features/deploy/deploy-proxy";
import { mixinDevelop } from "../../features/develop/develop";
import { mixinTestProxy } from "../../features/test/test-proxy";
import { mixinManage } from "../../features/manage/manage";
import { Utils } from "../../../commons/utils";

export class Proxy extends mixinDevelop(
  mixinManage(mixinProxyDeploy(mixinTestProxy(Component)))
) {
  private endpointUrl: string;
  private basePath: string;
  private hasPolicy: boolean = false;

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
    this._removeResources(this, resourceIds);
  }

  removeDefaultResources() {
    this._removeDefaultResources(this);
  }

  deploy() {
    this._deploy(this);
  }

  promote() {
    this._promote(this);
  }

  testSwaggerConsole(
    environment: Enums.Environment,
    resource: string,
    key?: string,
    value?: string
  ) {
    return this._testSwaggerConsole(this, environment, resource, key, value);
  }

  testCurl(
    environment: Enums.Environment,
    method: Enums.HTTPMethod,
    resource: string
  ) {
    return this._testCurl(this, environment, method, resource);
  }

  updateUsagePlans(plans: UsagePlan[]) {
    this._updateUsagePlans(this, plans);
  }

  addVersion() {
    this._addNewVersion(this, "1.1");
  }

  addResources(resourcePaths: Types.ResourcePath[]) {
    this._addResources(this, resourcePaths);
  }

  addResponseFlowHeaderPolicy(
    resourcePath: string,
    verb: string,
    headerName: string,
    headerValue: string
  ) {
    this._addPolicy(
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
    this._editPolicy(
      resourcePath,
      verb,
      Enums.Flow.RESPONSE,
      policyIndex,
      headerValue
    );
  }

  publish() {
    this._changeLifeCycleState(this, Enums.LifeCycleState.Publish);
  }

  navigateToDevPortal() {
    this._navigateToDevPortal("choreoe2etest");
  }

  enableCors(environment: Enums.Environment) {
    this._enableCors(this, environment);
  }

  addPermissions(permissions: string[]) {
    this._addPermissions(this, permissions);
  }

  applyAllPermissionsToResources(permissions: string[]) {
    this._applyAllPermissionsToResources(this, permissions);
  }

  applyPermissionToResources(permission: string) {
    this._applyPermissionToResources(this, permission);
  }

  deleteAllPermissionsFromResources(permissions: string[]) {
    this._deleteAllPermissionsFromResources(this, permissions);
  }

  verifyConsumer(appName: string) {
    this._verifyConsumer(appName);
  }

  disableSecurity(method: Enums.HTTPMethod, resource: string) {
    this._disableSecurity(this, Enums.Environment.PRODUCTION, method, resource);
  }

  disableSecurityInDev(method: Enums.HTTPMethod, resource: string) {
    this._disableSecurity(
      this,
      Enums.Environment.DEVELOPMENT,
      method,
      resource
    );
  }

  disableSecurityInProd(method: Enums.HTTPMethod, resource: string) {
    this._disableSecurity(this, Enums.Environment.PRODUCTION, method, resource);
  }

  updateAccessMode(accessMode: Enums.Accessibility) {
    this._updateAccessMode(this, accessMode);
  }

  updateAccessModeAndDeploy(accessMode: Enums.Accessibility) {
    this._deploy(this, accessMode);
  }

  updateApiVisibility(visibility: ApiVisibility) {
    this._updateApiVisibility(this, visibility);
  }
}
